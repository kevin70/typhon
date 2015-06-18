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

import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.RoleData;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.spi.pvp.PvpStoreProvider;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class PvpStoreDatable extends StoreDatable {

    private static final String SESSION_SRT_KEY = "__pvp__store__refresh__task";

    @Inject
    private PvpStoreProvider pvpStoreProvider;

    @Override
    public void initialize(Player player) {
        PvpStoreRefreshTask psrt = new PvpStoreRefreshTask(player);
        psrt.run();

        SessionContext.getSession().setAttribute(SESSION_SRT_KEY, psrt);
    }

    @Override
    public void serialize(Player player, RoleData roleData) {
    }

    @Override
    public void deserialize(RoleData roleData, Player player) {
        initialize(player);
    }

    private class PvpStoreRefreshTask implements Runnable {

        private final Player player;

        PvpStoreRefreshTask(Player player) {
            this.player = player;
        }

        @Override
        public void run() {
            if (!player.getSession().isAvailable()) {
                return;
            }

            boolean isRefresh = false;
            long cdTime = 0;
            Calendar nextRefreshTime0;
            // 当前时间
            Calendar curCal = Calendar.getInstance();
            //上次自动刷新时间
            Calendar lastCal = Calendar.getInstance();
            lastCal.setTimeInMillis(player.getNormal().getLastPvpRestoreTime());

            //初始帐号
            if (lastCal.getTimeInMillis() == 0) {
                isRefresh = true;
            } else {
                //下次刷新时间点
                nextRefreshTime0 = nextRefreshTime(getRefreshTimes(), lastCal.getTimeInMillis());
//                isRefresh = !(lastCal.get(Calendar.DAY_OF_YEAR) == curCal.get(Calendar.DAY_OF_YEAR));
                if (nextRefreshTime0.getTimeInMillis() <= curCal.getTimeInMillis()) {
                    isRefresh = true;
                } else {
                    cdTime = nextRefreshTime0.getTimeInMillis() - curCal.getTimeInMillis();
                }
            }

            if (isRefresh) {
                pvpStoreProvider.refreshCommodity(player);
                nextRefreshTime0 = nextRefreshTime(getRefreshTimes(), curCal.getTimeInMillis());
                cdTime = nextRefreshTime0.getTimeInMillis() - curCal.getTimeInMillis();
                player.getNormal().setLastPvpRestoreTime(System.currentTimeMillis());
            }

            scheduler.schedule(new PvpStoreRefreshTask(player), cdTime, TimeUnit.MILLISECONDS);
        }

        private List<Integer> getRefreshTimes() {
            return pvpStoreProvider.getRefreshTimes();
        }
    }
}
