package org.skfiy.typhon.spi.ranking;

import org.skfiy.typhon.domain.GlobalData;
import org.skfiy.typhon.domain.GlobalData.Type;
import org.skfiy.typhon.domain.Player;

public class HeroStarRankingProvider extends AbstractRankingProvider {
    @Override
    protected Type getGlobalDataType() {
        return GlobalData.Type.heroStar_data;
    }

    @Override
    protected boolean compare(Player player, RankingObject rankingObject) {
        return player.getNormal().getPveStarCounts() > rankingObject.getStar();
    }
}
