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
package org.skfiy.typhon.session;

import org.skfiy.typhon.dobj.HeroItemDobj;
import org.skfiy.typhon.dobj.ItemDobj;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.repository.UserRepository;

/**
 *
 * @author Kevin
 */
public final class BagUtils {

    
    private static UserRepository userRepository;
    /**
     * 
     * @param itemDobj 
     */
    public static void intoItem(ItemDobj itemDobj) {
        intoItem(itemDobj, 1);
    }

    static void setUserRepository(UserRepository userRepository) {
        BagUtils.userRepository = userRepository;
    }

    /**
     * 
     * @param itemDobj
     * @param count 
     */
    public static void intoItem(ItemDobj itemDobj, int count) {
        if (itemDobj.isAutoOpen() && itemDobj.getScript() != null) {
            itemDobj.getScript().invoke(SessionContext.getSession(),
                    new Object[]{itemDobj, count});
            return;
        }
        
        Player player = SessionUtils.getPlayer();
        
        if (itemDobj instanceof HeroItemDobj) {
            if (player.getHeroBag().findNode(itemDobj.getId()) != null) {
                HeroItemDobj heroItemDobj = (HeroItemDobj) itemDobj;
                intoItem(heroItemDobj.getSoul().getItemDobj(), heroItemDobj.getSoul().getCount()
                        * count);
                return;
            }
            player.getHeroBag().intoItem(itemDobj, count);
        } else {
            player.getBag().intoItem(itemDobj, count);
        }
    }

}
