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
import org.skfiy.typhon.packet.Namespaces;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.skfiy.typhon.annotation.Action;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.auth.Authenticator;
import org.skfiy.typhon.spi.auth.PasswordNotMatchedException;
import org.skfiy.typhon.spi.auth.UserNotFoundException;
import org.skfiy.typhon.packet.Auth;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionContext;
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

    @Action(Namespaces.NS_AUTH)
    public void authentic(Auth auth) {
        Session session = SessionContext.getSession();
        
        if (StringUtils.isEmpty(auth.getUsername())) {
            PacketError error = PacketError.createResult(auth,
                    PacketError.Condition.bat_request);
            error.setText("illegal username");
            session.write(error);
            return;
        }
        
        if (StringUtils.isEmpty(auth.getPassword())) {
            PacketError error = PacketError.createResult(auth,
                    PacketError.Condition.bat_request);
            error.setText("illegal user password");
            session.write(error);
            return;
        }

        // set context attachment
        try {

            authenticator.authentic(auth);
            
            // auth successful
            JSONObject result = new JSONObject();
            result.put("id", auth.getId());
            result.put("uid", SessionUtils.getUser().getUid());
            session.write(Namespaces.NS_USER_INFO, result);
            
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
