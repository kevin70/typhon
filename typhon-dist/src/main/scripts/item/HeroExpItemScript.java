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

package item;

import javax.inject.Inject;
import org.skfiy.typhon.Container;
import org.skfiy.typhon.domain.Bag.Node;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.item.HeroItem;
import org.skfiy.typhon.domain.item.SimpleItem;
import org.skfiy.typhon.script.Script;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.hero.HeroProvider;

/**
 *
 * @author Administrator
 */
public class HeroExpItemScript implements Script {

    @Inject
    private Container container;
    
    @Override
    public Object invoke(Session session, Object obj) {
        Player player = SessionUtils.getPlayer(session);
        Object[] params = (Object[]) obj;
        Node node = player.getHeroBag().findNode((int) params[1]);
        SimpleItem simpleItem = (SimpleItem) params[0];
        int count = (int) params[2];
        
        HeroProvider heroProvider = container.getInstance(HeroProvider.class);
        heroProvider.pushExp(player.getNormal().getLevel(),
                (HeroItem) node.getItem(), (int) simpleItem.getItemDobj().getAnnex() * count);
        return null;
    }
    
}
