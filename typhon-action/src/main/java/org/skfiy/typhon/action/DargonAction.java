package org.skfiy.typhon.action;

import javax.inject.Inject;

import org.skfiy.typhon.annotation.Action;
import org.skfiy.typhon.packet.DargonPacket;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.spi.dargon.DargonProvider;
import org.skfiy.typhon.spi.dargon.DargonStoreProvider;

public class DargonAction {

    @Inject
    private DargonProvider dargonProvider;
    @Inject
    private DargonStoreProvider dargonStoreProvider;

    @Action(Namespaces.DARGON_LIST)
    public void hyrovalveList(SingleValue packet) {
        dargonProvider.dargonPrepare(packet);
    }

    @Action(Namespaces.DARGON_START)
    public void dargonStart(DargonPacket packet) {
        dargonProvider.dargonStart(packet);
    }

    @Action(Namespaces.DARGON_RESET)
    public void reset(SingleValue packet) {
        dargonProvider.reset(packet);
    }

    @Action(Namespaces.DARGON_AGAIN)
    public void again(SingleValue packet) {
        dargonProvider.comeBack(packet);
    }

    @Action(Namespaces.DARGON_REFRESH)
    public void refresh(SingleValue packet) {
        dargonStoreProvider.refreshCommodity(packet);
    }

    @Action(Namespaces.DARGON_BUY)
    public void buy(SingleValue packet) {
        dargonStoreProvider.buyCommodities(packet);
    }

    @Action(Namespaces.DARGON_WAR_RESULT)
    public void warResults(DargonPacket packet) {
        dargonProvider.warResults(packet);
    }

    @Action(Namespaces.DARGON_BUY_COUNTS)
    public void buyDargonCounts(SingleValue packet) {
        dargonProvider.buyDargonCounts(packet);

    }
}
