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
package org.skfiy.typhon.action;

import javax.inject.Singleton;

import org.skfiy.typhon.annotation.Action;
import org.skfiy.typhon.domain.Bag.Node;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.item.TroopItem;
import org.skfiy.typhon.packet.ItemApplied;
import org.skfiy.typhon.packet.ItemSell;
import org.skfiy.typhon.packet.MultipleItemApplied;
import org.skfiy.typhon.packet.MultipleItemApplied.Element;
import org.skfiy.typhon.packet.MultipleValue;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.session.SessionUtils;

/**
 *
 * @author Administrator
 */
@Singleton
public class ItemAction {

    @Action(Namespaces.ITEM_APPLY)
    public void apply(ItemApplied packet) {
        Player player = SessionUtils.getPlayer();
        Node node = player.getBag().findNode(packet.getPos());
        if (node == null) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Not found item[" + packet.getPos() + "]");
            player.getSession().write(error);
            return;
        }

        node.getItem().getScript().invoke(player.getSession(),
                new Object[]{node.getItem(), packet.getTargetPos()});
        player.getBag().decrementTotal(node, 1);

        player.getSession().write(Packet.createResult(packet));
    }

    @Action(Namespaces.ITEM_APPLIES)
    public void apply(MultipleItemApplied packet) {
        Player player = SessionUtils.getPlayer();
        for (Element e : packet.getElements()) {
            Node node = player.getBag().findNode(e.getPos());
            if (node == null || node.getTotal() < e.getCount()) {
                PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
                error.setText("Not found item[" + e.getPos() + "]/not enough count");
                player.getSession().write(error);
                return;
            }

            node.getItem().getScript().invoke(player.getSession(),
                    new Object[]{node.getItem(), e.getTargetPos(), e.getCount()});
            player.getBag().decrementTotal(node, e.getCount());

        }
        player.getSession().write(Packet.createResult(packet));
    }

    @Action(Namespaces.ITEM_SELL)
    public void sell(ItemSell packet) {
        Player player = SessionUtils.getPlayer();
        Node node = player.getBag().findNode(packet.getPos());
        if (node == null || packet.getCount() > node.getTotal()) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Not found item[" + packet.getPos() + "]/count is not enough");
            player.getSession().write(error);
            return;
        }

        if (node.getItem() instanceof TroopItem) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Not sell");
            player.getSession().write(error);
            return;
        }

        int price = node.getItem().getPrice();
        int count = packet.getCount();
        player.getBag().decrementTotal(node, count);
        SessionUtils.incrementCopper(count * price);
        player.getSession().write(Packet.createResult(packet));
    }

    @Action(Namespaces.ITEM_SELLS)
    public void sell(MultipleValue packet) {
        Player player = SessionUtils.getPlayer();
        for (Object v : packet.getVals()) {
            int pos = (int) v;
            Node node = player.getBag().findNode(pos);

            if (node.getItem() instanceof TroopItem) {
                PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
                error.setText("Not sell");
                player.getSession().write(error);
                return;
            }

            int price = node.getItem().getPrice();
            player.getBag().decrementTotal(node, node.getTotal());
            SessionUtils.incrementCopper(node.getTotal() * price);
        }

        player.getSession().write(Packet.createResult(packet));
    }

}
