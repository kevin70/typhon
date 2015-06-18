/*
 * Copyright 2013 The Skfiy Open Association.
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
package org.skfiy.typhon.action;

import com.alibaba.fastjson.JSONObject;
import java.util.Map;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.skfiy.typhon.annotation.Action;
import org.skfiy.typhon.domain.User;
import org.skfiy.typhon.packet.Auth;
import org.skfiy.typhon.packet.Logout;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.OAuth2;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.Platform;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.session.SessionManager;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.RoleProvider;
import org.skfiy.typhon.spi.auth.Authenticator;
import org.skfiy.typhon.spi.auth.OAuthenticator;
import org.skfiy.typhon.spi.auth.PasswordNotMatchedException;
import org.skfiy.typhon.spi.auth.UserInfo;
import org.skfiy.typhon.spi.auth.UserNotFoundException;
import org.skfiy.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kevin Zou <<kevinz@skfiy.org>>
 */
@Singleton
public class AuthenticationAction {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationAction.class);

    @Inject
    private Authenticator authenticator;
    @Resource
    private Map<Platform, OAuthenticator> oauthenticators;
    @Inject
    private RoleProvider roleProvider;
    @Inject
    private SessionManager sessionManager;

    @Action(Namespaces.AUTH)
    public void authentic(Auth auth) {
        Session session = SessionContext.getSession();
        if (session.getAuthType() != null) {
            PacketError error = PacketError.createResult(auth, PacketError.Condition.conflict);
            error.setText("repeat certification");
            session.write(error);
            return;
        }

        if (StringUtils.isEmpty(auth.getUsername())) {
            PacketError error = PacketError.createResult(auth, PacketError.Condition.bat_request);
            error.setText("illegal username");
            session.write(error);
            return;
        }

        if (StringUtils.isEmpty(auth.getPassword())) {
            PacketError error = PacketError.createResult(auth, PacketError.Condition.bat_request);
            error.setText("illegal user password");
            session.write(error);
            return;
        }

        synchronized (lockStr(auth.getUsername())) {
            LOG.debug("Authentic: <{}>", auth.getUsername());

            // set context attachment
            try {

                authenticator.authentic(auth);
                // auth successful
                JSONObject result = new JSONObject();
                result.put("id", auth.getId());
                result.put("uid", SessionUtils.getUser().getUid());
                session.write(Namespaces.USER_INFO, result);

                // 预加载角色
                roleProvider.preload();
            } catch (UserNotFoundException e) {
                LOG.debug("not found user [{}]", auth.getUsername(), e);

                PacketError error = PacketError.createResult(auth,
                        PacketError.Condition.item_not_found);
                error.setText("not found user");
                session.write(error);
            } catch (PasswordNotMatchedException e) {
                LOG.debug("password nt matched. user: [{}]", auth.getUsername(), e);

                PacketError error = PacketError.createResult(auth,
                        PacketError.Condition.item_not_found);
                error.setText("password not matched");
                session.write(error);
            }
        }
    }

    /**
     *
     * @param oauth
     */
    @Action(Namespaces.OAUTH2)
    public void authentic(OAuth2 oauth) {
        if (oauth.getPlatform() == null || StringUtils.isEmpty(oauth.getCode())) {
            PacketError error = PacketError.createResult(oauth, PacketError.Condition.conflict);
            error.setText("OAuth2: illegal arguments");
            LOG.error("OAuth2: illegal arguments {}", SessionContext.getSession());
            SessionContext.getSession().write(error);
            return;
        }

        LOG.debug("Auth Code: <{}>", oauth.getCode());

        UserInfo userInfo = oauthenticators.get(oauth.getPlatform()).authentic(oauth);
        Auth auth = new Auth();
        auth.setUsername(userInfo.getUsername());
        auth.setPassword(userInfo.getUsername());
        auth.setPlatform(userInfo.getPlatform());
        authentic(auth);
    }

    @Action(Namespaces.LOGOUT)
    public void logout(Logout packet) {
        Session session = SessionContext.getSession();
        User user = (User) session.getAttribute(SessionUtils.ATTR_USER);
        if (user == null) {
            return;
        }

        synchronized (lockStr(user.getUsername())) {
            if (sessionManager.getSession(user.getUid()) == session) {
                sessionManager.removeSession(user.getUid());
            }
        }
    }

    private String lockStr(String username) {
        String lock = AuthenticationAction.class.getCanonicalName() + "_" + username;
        return lock.intern();
    }

}
