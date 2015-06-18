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
package org.skfiy.typhon.spi.store;

import javax.inject.Inject;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.spi.Event;
import org.skfiy.typhon.spi.RoleProvider;
import org.skfiy.typhon.spi.Vip;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class EverydayStoreEvent implements Event<Player> {

    @Inject
    private RoleProvider roleProvider;
    @Inject
    private MarketStoreProvider marketStoreProvider;
    @Inject
    private WesternStoreProvider westernStoreProvider;

    @Override
    public void invoke(Player obj) {
        Normal normal = obj.getNormal();
        
        normal.setRefreshCount(0);
        normal.setPvpStoreRecount(0);
        normal.setRefreshDargonStore(0);
        Vip vip = roleProvider.getVip(normal.getVipLevel());

        if (vip.privileged.market_store_enabled) {
            marketStoreProvider.refreshCommodity(obj);
            normal.getMarketStore().setCount(0);
        } else {
            normal.setMarketStore(null);
        }

        if (vip.privileged.western_store_enabled) {
            westernStoreProvider.refreshCommodity(obj);
            normal.getWesternStore().setCount(0);
        } else {
            normal.setWesternStore(null);
        }
    }

}
