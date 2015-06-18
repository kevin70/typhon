/*
 * Copyright 2014 The Skfiy Open Association.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.skfiy.typhon.spi.role;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.domain.DailyTask;
import org.skfiy.typhon.domain.Mail;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.Role;
import org.skfiy.typhon.domain.RoleData;
import org.skfiy.typhon.domain.item.MonthCardObject;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.RoleProvider;
import org.skfiy.util.CustomizableThreadCreator;
import org.skfiy.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class NormalRoleDatable implements RoleDatable {

    private final ScheduledExecutorService REVIVAL_SCH_EXEC = Executors.newScheduledThreadPool(1,
            new ThreadFactory() {
                CustomizableThreadCreator threadCreator = new CustomizableThreadCreator("revigor-");

                {
                    threadCreator.setDaemon(true);
                }

                @Override
                public Thread newThread(Runnable r) {
                    return threadCreator.createThread(r);
                }
            });

    private final static String RESTORE_VIGOR = "_RESTOREVIGOR";

    @Inject
    private RoleProvider roleProvider;

    @Override
    public void initialize(Player player) {
        Role role = player.getRole();
        Normal normal = new Normal();
        ExpLevel expLevel = roleProvider.getExpLevel(role.getLevel());
        normal.setVigor(expLevel.getMaxVigor());
        normal.setLastRevigorTime(System.currentTimeMillis());

        role.setDiamond(Typhons.getInteger("typhon.spi.player.initDiamond"));
        normal.setCopper(Typhons.getInteger("typhon.spi.player.initCopper"));
        normal.setLastBuyDiamondTime(System.currentTimeMillis());
        normal.setHornNum(Typhons.getInteger("typhon.spi.chat.world.hornNum"));
        normal.setFreeCount(5);
        normal.setVipLevel(Typhons.getInteger("typhon.spi.defaultVipLevel"));
        normal.setLastResetTime(System.currentTimeMillis());

        MonthCardObject monthCardObject=new MonthCardObject();
        normal.setMonthCardObject(monthCardObject);
        DailyTask dailyTask = new DailyTask();
        normal.setDailyTask(dailyTask);
        player.setNormal(normal);
        role.setCreationTime(SessionUtils.getUser().getCreationTime());

        // 注册监听器
        normal.addPropertyChangeListener("vigor", new VigorChangeListener());
    }

    @Override
    public void serialize(Player player, RoleData roleData) {
        roleData.setNormalData(JSON.toJSONString(player.getNormal(),
                //                SerializerFeature.WriteClassName,
                SerializerFeature.DisableCircularReferenceDetect));
    }

    @Override
    public void deserialize(RoleData roleData, Player player) {
        String data = roleData.getNormalData();
        Normal normal;
        if (StringUtils.isEmpty(data)) {
            initialize(player);
            return;
        } else {
            normal = JSON.parseObject(data, Normal.class);
        }
        player.setNormal(normal);
        // ==============================================================================
        ExpLevel expLevel = roleProvider.getExpLevel(player.getRole().getLevel());
        if (normal.getVigor() < expLevel.getMaxVigor()) {
            long rms = Typhons.getLong("typhon.spi.revigor.millis");
            long s = System.currentTimeMillis() - normal.getLastRevigorTime();
            int newVigor = (int) (normal.getVigor() + s / rms);

            // 如果恢复超过上限则只恢复到最大值
            if (newVigor > expLevel.getMaxVigor()) {
                newVigor = expLevel.getMaxVigor();
            }

            normal.setVigor(newVigor);
            normal.setLastRevigorTime(System.currentTimeMillis() - (s % rms));

            if (newVigor < expLevel.getMaxVigor()) {
                RevigorTimerTask revigorTimerTask = new RevigorTimerTask(player);
                revigorTimerTask.schedule();
            }
        } else {
            normal.setLastRevigorTime(System.currentTimeMillis());
        }

        // ==============================================================================
        // 清理过期邮件
        long currentTimeMillis = System.currentTimeMillis();
        for (Mail mail : new ArrayList<>(normal.getMails())) {
            if (mail.getExpiredTime() > 0 && mail.getExpiredTime() <= currentTimeMillis) {
                normal.removeMail(mail);
            }
        }

        // 如果邮件超过上限则移除
        int maxMailSize = Typhons.getInteger("typhon.spi.maxMailSize", 30);
        if (normal.getMailSize() > maxMailSize) {
            for (int i = 0; i < (normal.getMailSize() - maxMailSize); i++) {
                normal.removeMail(normal.getMail(i));
            }
        }

        player.setNormal(normal);
        // 注册监听器
        normal.addPropertyChangeListener("vigor", new VigorChangeListener());
    }

    private class VigorChangeListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            Normal normal = (Normal) evt.getSource();
            Player player = normal.player();
            ExpLevel expLevel = roleProvider.getExpLevel(normal.getLevel());

            if (expLevel.getMaxVigor() <= (int) evt.getOldValue()
                    && expLevel.getMaxVigor() > (int) evt.getNewValue()) {
                normal.setLastRevigorTime(System.currentTimeMillis());

                RevigorTimerTask revigorTimerTask = new RevigorTimerTask(player);
                revigorTimerTask.schedule();
            }
        }

    }

    private class RevigorTimerTask implements Runnable {

        private final Player player;

        RevigorTimerTask(Player player) {
            this.player = player;
        }

        @Override
        public void run() {
            if (!player.getSession().isAvailable()) {
                return;
            }

            ExpLevel expLevel = roleProvider.getExpLevel(player.getRole().getLevel());
            Normal normal = player.getNormal();
            int maxVigor = expLevel.getMaxVigor();
            int newVigor = normal.getVigor() + 1;

            if (newVigor <= maxVigor) {
                normal.setVigor(normal.getVigor() + 1);
                normal.setLastRevigorTime(System.currentTimeMillis());
            }

            if (normal.getVigor() < maxVigor) {
                schedule();
            } else {
                player.getSession().removeAttribute(RESTORE_VIGOR);
            }
        }

        public void schedule() {
            ScheduledFuture<?> future = (ScheduledFuture<?>) player.getSession().getAttribute(RESTORE_VIGOR);
            if (future != null) {
                future.cancel(true);
            }

            long rms = Typhons.getLong("typhon.spi.revigor.millis");
            long delay = rms - (System.currentTimeMillis() - player.getNormal().getLastRevigorTime());
            future = REVIVAL_SCH_EXEC.schedule(this, delay, TimeUnit.MILLISECONDS);
            player.getSession().setAttribute(RESTORE_VIGOR, future);
        }
    }
}
