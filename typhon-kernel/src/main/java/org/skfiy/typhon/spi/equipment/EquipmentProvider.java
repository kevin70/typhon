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
package org.skfiy.typhon.spi.equipment;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.commons.lang3.ArrayUtils;
import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.dobj.EquipmentItemDobj;
import org.skfiy.typhon.domain.Bag.Node;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.item.Subitem;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.ItemProvider;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public class EquipmentProvider extends AbstractComponent {

    // mingle
    @Inject
    private ItemProvider itemProvider;

    @Override
    protected void doInit() {
    }

    @Override
    protected void doReload() {
    }

    @Override
    protected void doDestroy() {
    }

    /**
     * 
     * @param packet 
     */
    public void mingle(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        
        EquipmentItemDobj itemDobj = itemProvider.getItem(String.valueOf(packet.getVal()));
        if (ArrayUtils.isEmpty(itemDobj.getSubitems())) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.bat_request);
            error.setText("No mingle");
            player.getSession().write(error);
            return;
        }
        
        Node node;
        for (Subitem subitem : itemDobj.getSubitems()) {
            node = player.getBag().findNode(subitem.getItemDobj().getId());
            if (node == null || node.getTotal() < subitem.getCount()) {
                PacketError error = PacketError.createResult(packet, PacketError.Condition.item_not_found);
                error.setText("No item[" + subitem.getItemDobj().getId() + "]");
                player.getSession().write(error);
                return;
            }
        }

        SessionUtils.decrementCopper(itemDobj.getExpense());

        for (Subitem subitem : itemDobj.getSubitems()) {
            player.getBag().decrementTotal(subitem.getItemDobj().getId(), subitem.getCount());
        }

        player.getBag().intoItem(itemDobj);
        
        player.getSession().write(Packet.createResult(packet));
    }

}
