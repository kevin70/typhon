package org.skfiy.typhon.spi.ranking;

import org.skfiy.typhon.domain.GlobalData;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.GlobalData.Type;

public class PveRankingProvider extends AbstractRankingProvider {
    @Override
    protected Type getGlobalDataType() {
        return GlobalData.Type.pve_data;
    }
    @Override
    protected boolean compare(Player player, RankingObject rankingObject) {
        return player.getNormal().getHpveProgresses().size() > rankingObject.getPveProgresses();
    }
}
