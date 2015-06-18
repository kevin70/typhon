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

import javax.inject.Inject;
import org.skfiy.typhon.annotation.Action;
import org.skfiy.typhon.packet.AttackPacket;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.OnlinePvpPacket;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.spi.pvp.OnlinePvpProvider;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class OnlinePvpAction {

    @Inject
    private OnlinePvpProvider onlinePvpProvider;

    @Action(Namespaces.OPVP_INVITE)
    public void invite(SingleValue packet) {
        onlinePvpProvider.invite(packet);
    }

    @Action(Namespaces.OPVP_ACCEPT)
    public void accept(SingleValue packet) {
        onlinePvpProvider.accept(packet);
    }

    @Action(Namespaces.OPVP_REJECT)
    public void reject(SingleValue packet) {
        onlinePvpProvider.reject(packet);
    }

    @Action(Namespaces.OPVP_PREPARE)
    public void prepare(OnlinePvpPacket packet) {
        onlinePvpProvider.prepare(packet);
    }

    @Action(Namespaces.OPVP_ATTACK)
    public void attack(AttackPacket packet) {
        onlinePvpProvider.attack(packet);
    }
}
