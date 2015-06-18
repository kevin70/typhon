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
package org.skfiy.typhon.spi;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.management.MBeanException;
import javax.management.ObjectName;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.modeler.ManagedBean;
import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.ComponentException;
import org.skfiy.typhon.ConnectionProvider;
import org.skfiy.typhon.DbException;
import org.skfiy.typhon.TyphonException;
import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.dobj.HeroItemDobj;
import org.skfiy.typhon.dobj.ItemDobj;
import org.skfiy.typhon.domain.Bag.Node;
import org.skfiy.typhon.domain.Mail;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.PveProgress;
import org.skfiy.typhon.domain.User;
import org.skfiy.typhon.domain.item.HeroItem;
import org.skfiy.typhon.repository.UserRepository;
import org.skfiy.typhon.session.BagUtils;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionConstants;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.session.SessionManager;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.pve.Chapter;
import org.skfiy.typhon.spi.pve.PveProvider;
import org.skfiy.typhon.util.DbUtils;
import org.skfiy.typhon.util.MBeanUtils;
import org.skfiy.util.Assert;
import org.skfiy.util.ReflectionUtils;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public class GMConsoleProvider extends AbstractComponent {

    private static final int[] MAIL_TYPES = {Mail.UPDATED_NOTICE_TYPE,
        Mail.ACTIVITY_NOTICE_TYPE,
        Mail.MAINTAIN_NOTICE_TYPE,
        Mail.REPARATION_NOTICE_TYPE,
        Mail.REWARD_NOTICE_TYPE};

    private ThreadLocal<Session> _local_session;

    @Inject
    private SessionManager sessionManager;
    @Inject
    private ItemProvider itemProvider;
    @Inject
    private PveProvider pveProvider;
    @Inject
    private RoleProvider roleProvider;
    @Inject
    private UserRepository userRepository;
    @Inject
    private ConnectionProvider connectionProvider;
    private ObjectName oname;

    @Override
    protected void doInit() {
        ManagedBean managedBean = MBeanUtils.findManagedBean(getClass());
        oname = MBeanUtils.registerComponent(this, managedBean);

        //
        initSessionThreadLocal();
    }

    @Override
    protected void doReload() {
    }

    @Override
    protected void doDestroy() {
        if (oname != null) {
            MBeanUtils.REGISTRY.unregisterComponent(oname);
        }
    }

    /**
     *
     * @param uid
     * @param propertyName
     * @param val
     * @throws javax.management.MBeanException
     */
    public void changeProperty(final String uid, final String propertyName, final String val)
            throws MBeanException {
        try {
            invoke(transcoding(uid), new Handler() {

                @Override
                void execute() {
                    Player player = SessionUtils.getPlayer();
                    try {
                        Class<?> propertyType = PropertyUtils.getPropertyType(player, propertyName);
                        if (propertyType == null) { // Not found property
                            throw new IllegalArgumentException("Not found property[" + propertyName + "]");
                        }

                        if (propertyType == Byte.class || propertyType == Byte.TYPE) {
                            BeanUtils.setProperty(player, propertyName, Byte.valueOf(val));
                        } else if (propertyType == Character.class || propertyType == Character.TYPE) {
                            BeanUtils.setProperty(player, propertyName, val.charAt(0));
                        } else if (propertyType == Boolean.class || propertyType == Boolean.TYPE) {
                            BeanUtils.setProperty(player, propertyName, Boolean.valueOf(val));
                        } else if (propertyType == Integer.class || propertyType == Integer.TYPE) {
                            BeanUtils.setProperty(player, propertyName, Integer.valueOf(val));
                        } else if (propertyType == Long.class || propertyType == Long.TYPE) {
                            BeanUtils.setProperty(player, propertyName, Long.valueOf(val));
                        } else if (propertyType == Float.class || propertyType == Float.TYPE) {
                            BeanUtils.setProperty(player, propertyName, Float.valueOf(val));
                        } else if (propertyType == Double.class || propertyType == Double.TYPE) {
                            BeanUtils.setProperty(player, propertyName, Double.valueOf(val));
                        } else {
                            BeanUtils.setProperty(player, propertyName, val);
                        }

                    } catch (Exception ex) {
                        throw new IllegalArgumentException(ex.getMessage());
                    }
                }

            });
        } catch (Exception e) {
            throw new MBeanException(e, e.getMessage());
        }
    }

    /**
     *
     * @param key
     * @param value
     * @throws javax.management.MBeanException
     */
    public void changeSystemProperties(final String key, final String value) throws MBeanException {
        try {
            Typhons.setProperty(key, value);
            Typhons.refresh();
        } catch (Exception e) {
            throw new MBeanException(e, e.getMessage());
        }
    }

    /**
     *
     * @param uid
     * @param iid
     * @param level
     * @throws MBeanException
     */
    public void changeHeroLevel(final String uid, final String iid, final String level)
            throws MBeanException {
        try {
            invoke(transcoding(uid), new Handler() {

                @Override
                void execute() {
                    Player player = SessionUtils.getPlayer();
                    Node node = player.getHeroBag().findNode(iid);
                    if (node == null) {
                        HeroItemDobj itemDobj = itemProvider.getItem(iid);
                        int pos = player.getHeroBag().intoItem(itemDobj);
                        node = player.getHeroBag().findNode(pos);
                    }

                    HeroItem heroItem = node.getItem();
                    int lv = Integer.valueOf(level);
                    if (lv > 150) {
                        lv = 150;
                    }
                    heroItem.setLevel(lv);
                }

            });
        } catch (Exception e) {
            throw new MBeanException(e, e.getMessage());
        }
    }

    /**
     * 为玩家增加一个英雄.
     *
     * @param uid 用户名称
     * @param iid Item ID
     * @throws javax.management.MBeanException
     */
    public void pushHero(final String uid, final String iid) throws MBeanException {
        try {
            invoke(transcoding(uid), new Handler() {

                @Override
                void execute() {
//                    Player player = SessionUtils.getPlayer();
                    HeroItemDobj itemDobj = itemProvider.getItem(iid);
                    BagUtils.intoItem(itemDobj);
                }
            });
        } catch (Exception e) {
            throw new MBeanException(e, e.getMessage());
        }
    }

    /**
     *
     * @param uid
     * @param iid
     * @throws javax.management.MBeanException
     */
    public void pushItem(final String uid, final String iid) throws MBeanException {
        pushItem(uid, iid, "1");
    }

    /**
     *
     * @param uid
     * @param iid
     * @param count
     * @throws javax.management.MBeanException
     */
    public void pushItem(final String uid, final String iid, final String count)
            throws MBeanException {
        try {
            invoke(transcoding(uid), new Handler() {

                @Override
                void execute() {
//                    Player player = SessionUtils.getPlayer();
                    ItemDobj itemDobj = itemProvider.getItem(iid);
                    if (itemDobj instanceof HeroItemDobj) {
                        throw new TyphonException("不能在通过背包中添加英雄");
                    }
                    BagUtils.intoItem(itemDobj, Integer.valueOf(count));
                }
            });
        } catch (Exception e) {
            throw new MBeanException(e, e.getMessage());
        }
    }

    /**
     *
     * @param uid
     * @throws javax.management.MBeanException
     */
    public void openAllPve(final String uid) throws MBeanException {
        try {
            invoke(transcoding(uid), new Handler() {

                @Override
                void execute() {
                    Player player = SessionUtils.getPlayer();
                    Normal normal = player.getNormal();

                    // 史实篇
                    Field field = ReflectionUtils.findField(PveProvider.class, "historyChapters");
                    normal.setHpveProgresses(newPveProgressList(field));

                    // 史实篇(困难)
                    field = ReflectionUtils.findField(PveProvider.class, "historyDifficultChapters");
                    normal.setHdpveProgresses(newPveProgressList(field));
                }

                List<PveProgress> newPveProgressList(Field field) {
                    field.setAccessible(true);
                    List<Chapter> chapters = (List<Chapter>) ReflectionUtils.getField(field, pveProvider);
                    List<PveProgress> list = new ArrayList<>();

                    for (int i = 0; i < chapters.size(); i++) {
                        Chapter c = chapters.get(i);
                        for (int j = 0; j < c.getParts().length; j++) {
                            PveProgress pp = new PveProgress(i, j);
                            list.add(pp);
                        }
                    }

                    return list;
                }
            });
        } catch (Exception e) {
            throw new MBeanException(e, e.getMessage());
        }
    }

    /**
     *
     * @param uid
     * @param title
     * @param content
     * @param appendix
     * @param count
     * @param type
     * @throws java.lang.Exception
     */
    public void sendMail(final String uid, final String title, final String content,
            final String appendix, final String count, final String type) throws Exception {
        try {
            User user = userRepository.findByUid(Integer.parseInt(uid));
            if (user == null) {
                throw new RuntimeException("Not found user[" + uid + "]");
            }

            roleProvider.sendMail(user.getUid(), newMail(new String(title.getBytes("ISO-8859-1"), "UTF-8"),
                    new String(content.getBytes("ISO-8859-1"), "UTF-8"), appendix,
                    Integer.valueOf(count), Integer.valueOf(type)));
        } catch (Exception e) {
            throw new MBeanException(e);
        }
    }

    /**
     *
     * @param title
     * @param content
     * @param appendix
     * @param count
     * @param type
     * @throws java.lang.Exception
     */
    public void sendAllMail(final String title, final String content, final String appendix,
            final String count, final String type) throws Exception {
        try {
            String _title = new String(title.getBytes("ISO-8859-1"), "UTF-8");
            String _content = new String(content.getBytes("ISO-8859-1"), "UTF-8");

            List<Integer> rids = loadAllRids();
            for (int rid : rids) {
                roleProvider.sendMail(rid, newMail(_title, _content, appendix,
                        Integer.valueOf(count), Integer.valueOf(type)));
            }
        } catch (Exception e) {
            throw new MBeanException(e);
        }
    }

    /**
     *
     * @param rid
     * @param cash
     */
    public void recharge(final String rid, final String cash) {
        Recharging recharging = new Recharging();

        recharging.setRid(Integer.valueOf(rid));
        recharging.setCash(Integer.valueOf(cash));
        roleProvider.recharge(recharging);
    }

    private List<Integer> loadAllRids() {
        List<Integer> rids = new ArrayList<>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = connectionProvider.getConnection();
            ps = conn.prepareStatement("select t.rid from t_role t");
            rs = ps.executeQuery();
            while (rs.next()) {
                rids.add(rs.getInt("rid"));
            }

            DbUtils.commitQuietly(conn);
        } catch (SQLException e) {
            DbUtils.rollbackQuietly(conn);
            throw new DbException(e);
        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }

        return rids;
    }

    private Mail newMail(final String title, final String content, final String appendix,
            final int count, final int type) {
        // 查检邮件的标题
        Assert.hasLength(title, "mail title not empty");
        // 检查邮件的内容
        Assert.hasLength(content, "mail content not empty");
        // 检查类型是否存在
        if (ArrayUtils.indexOf(MAIL_TYPES, type) < 0) {
            throw new ComponentException("No mail type [" + type + "], optional" + ArrayUtils.toString(MAIL_TYPES));
        }

        if (StringUtils.isNotEmpty(appendix)) {
            // 检查道具是否存在
            itemProvider.getItem(appendix);
        }

        if (count <= 0) {
            throw new ComponentException("appendix count[" + count + "]");
        }

        Mail mail = new Mail();
        mail.setTitle(title);
        mail.setContent(content);

        if (StringUtils.isNotEmpty(appendix)) {
            mail.setAppendix(appendix);
            mail.setCount(count);
        }
        mail.setType(type);

        if (type == Mail.UPDATED_NOTICE_TYPE) {
            mail.setExpiredTime(mail.getCreationTime() + 7 * 24 * 60 * 60 * 1000);
        }

        return mail;
    }

    private void invoke(final String uid, final Handler handler) {
        for (Session session : sessionManager.findSessions()) {
            User user = (User) session.getAttribute(SessionConstants.ATTR_USER);
            if (user.getUid() == Integer.valueOf(uid).intValue()) {
                synchronized (session) {
                    _local_session.set(session);
                    handler.execute();
                }
                return;
            }
        }

        throw new TyphonException("[" + uid + "]用户不存在或不在线");
    }

    private void initSessionThreadLocal() {
        try {
            Field field = SessionContext.class.getDeclaredField("LOCAL_SESSION");
            field.setAccessible(true);
            _local_session = (ThreadLocal<Session>) field.get(SessionContext.class);
        } catch (NoSuchFieldException ex) {
            throw new ComponentException("SessionContext中不存在[LOCAL_SESSION]属性", ex);
        } catch (SecurityException ex) {
            throw new ComponentException(
                    getClass() + " 缺少访问SessionContext [LOCAL_SESSION] 属性的权限", ex);
        } catch (Exception ex) {
            throw new ComponentException(ex.getMessage(), ex);
        }
    }

    private abstract class Handler {

        /**
         * SessionContext.getSession();
         */
        abstract void execute();
    }

    // 转换
    private String transcoding(String userName) throws Exception {
        try {
            return new String(userName.getBytes("ISO-8859-1"), "UTF-8");
        } catch (Exception e) {
            throw new MBeanException(e);
        }
    }
}
