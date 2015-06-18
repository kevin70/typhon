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

import java.util.List;
import javax.inject.Inject;
import org.skfiy.typhon.annotation.Action;
import org.skfiy.typhon.domain.Bag;
import org.skfiy.typhon.domain.Bag.Node;
import org.skfiy.typhon.domain.ITroop;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.Troop;
import org.skfiy.typhon.domain.item.TroopItem;
import org.skfiy.typhon.packet.MultipleValue;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.packet.TroopPacket;
import org.skfiy.typhon.packet.TroopStrengPacket;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.troop.TroopProvider;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class TroopAction {

    @Inject
    private TroopProvider troopProvider;

    @Action(Namespaces.TROOP_STRENG)
    public void troopStreng(TroopStrengPacket packet) {
        Player player = SessionUtils.getPlayer();
        List<Integer> intensities = packet.getIntensities();
        for (int pos : intensities) {
            Node node = player.getBag().findNode(pos);
            if (node == null) {
                PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
                error.setText("Not found item[" + pos + "]/count is not enough");
                player.getSession().write(error);
                return;
            }
        }

        troopProvider.rise(packet);
    }

    @Action(Namespaces.TROOP_HARDEN)
    public void harden(SingleValue packet) {
        troopProvider.harden(packet);
    }

    @Action(Namespaces.TROOP_RESOLVE)
    public void resolve(MultipleValue packet) {
        troopProvider.resolve(packet);
    }

    @Action(Namespaces.TROOP_EQUIP)
    public void equip(TroopPacket packet) {
        Player player = SessionUtils.getPlayer();
        Node node = player.getBag().findNode(packet.getPos());
        if (node == null) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.item_not_found);
            player.getSession().write(error);
            return;
        }

        TroopItem troopItem = node.getItem();
        if (troopItem.getActiveType() != null) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Actived");
            player.getSession().write(error);
            return;
        }

        ITroop.Type troopType = ITroop.Type.valueOf(packet.getCasernType());
        Troop troop = player.getNormal().getTroop(troopType);

        if (troop.isFull()) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("IsFull");
            player.getSession().write(error);
            return;
        }

        boolean sameType = isSameType(troopItem, player.getBag(), troop.getFirst())
                || isSameType(troopItem, player.getBag(), troop.getSecond())
                || isSameType(troopItem, player.getBag(), troop.getThird())
                || isSameType(troopItem, player.getBag(), troop.getFour())
                || isSameType(troopItem, player.getBag(), troop.getFive());
        if (sameType) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Same Type");
            player.getSession().write(error);
            return;
        }

        //==========================================================================================
        boolean success = false;
        switch (packet.getTarget()) {
            case 1:
                if (troop.getFirst() <= 0) {
                    troop.setFirst(packet.getPos());
                    success = true;
                }
                break;
            case 2:
                if (troop.getSecond() <= 0) {
                    troop.setSecond(packet.getPos());
                    success = true;
                }
                break;
            case 3:
                if (troop.getThird() <= 0) {
                    troop.setThird(packet.getPos());
                    success = true;
                }
                break;
            case 4:
                if (troop.getFour() <= 0) {
                    troop.setFour(packet.getPos());
                    success = true;
                }
                break;
            case 5:
                if (troop.getFive() <= 0) {
                    troop.setFive(packet.getPos());
                    success = true;
                }
                break;
        }

        if (!success) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Not success");
            player.getSession().write(error);
            return;
        }

        troopItem.setActiveType(troopType);

        player.getSession().write(Packet.createResult(packet));

        troopProvider.calculateTroopProps(player);
    }

    /**
     *
     * @param packet
     */
    @Action(Namespaces.TROOP_UNEQUIP)
    public void unequip(TroopPacket packet) {
        Player player = SessionUtils.getPlayer();
        Troop troop = player.getNormal().getTroop(ITroop.Type.valueOf(packet.getCasernType()));

        int pos = 0;
        boolean success = false;
        switch (packet.getTarget()) {
            case 1:
                if (troop.getFirst() > 0) {
                    pos = troop.getFirst();
                    troop.setFirst(0);
                    success = true;
                }
                break;
            case 2:
                if (troop.getSecond() > 0) {
                    pos = troop.getSecond();
                    troop.setSecond(0);
                    success = true;
                }
                break;
            case 3:
                if (troop.getThird() > 0) {
                    pos = troop.getThird();
                    troop.setThird(0);
                    success = true;
                }
                break;
            case 4:
                if (troop.getFour() > 0) {
                    pos = troop.getFour();
                    troop.setFour(0);
                    success = true;
                }
                break;
            case 5:
                if (troop.getFive() > 0) {
                    pos = troop.getFive();
                    troop.setFive(0);
                    success = true;
                }
                break;
        }

        if (!success) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Not success");
            player.getSession().write(error);
            return;
        }

        Node node = player.getBag().findNode(pos);
        TroopItem troopItem = node.getItem();
        troopItem.setActiveType(null);

        player.getSession().write(Packet.createResult(packet));

        troopProvider.calculateTroopProps(player);
    }

    private boolean isSameType(TroopItem troopItem, Bag bag, int pos) {
        Node n = bag.findNode(pos);
        return (n != null && troopItem.getItemDobj().getPrimary()
                == ((TroopItem) n.getItem()).getItemDobj().getPrimary());
    }

}
