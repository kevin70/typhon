package org.skfiy.typhon.action;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.skfiy.typhon.annotation.Action;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.spi.store.MarketStoreProvider;
import org.skfiy.typhon.spi.store.StoreProvider;
import org.skfiy.typhon.spi.store.WesternStoreProvider;

/**
 *
 * @author Administrator
 */
@Singleton
public class StoreAction {

    @Inject
    private StoreProvider storeProvider;
    @Inject
    private MarketStoreProvider marketStoreProvider;
    @Inject
    private WesternStoreProvider westernStoreProvider;

    @Action(Namespaces.STORE_REFRESH)
    public void storeRefresh(SingleValue packet) {
        storeProvider.refreshCommodity(packet);
    }

    @Action(Namespaces.BUY_COMMODITY)
    public void buyStore(SingleValue packet) {
        storeProvider.buyCommodities(packet);
    }

    @Action(Namespaces.BUY_COMMODITY4M)
    public void buyCommodity4Market(SingleValue packet) {
        marketStoreProvider.buyCommodities(packet);
    }

    @Action(Namespaces.REFRESH_MARKET)
    public void refreshMarket(SingleValue packet) {
        marketStoreProvider.refreshCommodity(packet);
    }

    @Action(Namespaces.BUY_COMMODITY4W)
    public void buyCommodity4Western(SingleValue packet) {
        westernStoreProvider.buyCommodities(packet);
    }

    @Action(Namespaces.REFRESH_WESTERN)
    public void refreshWestern(SingleValue packet) {
        westernStoreProvider.refreshCommodity(packet);
    }
}
