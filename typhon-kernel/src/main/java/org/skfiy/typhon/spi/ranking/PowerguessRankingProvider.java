package org.skfiy.typhon.spi.ranking;

import java.util.List;

import org.skfiy.typhon.domain.GlobalData;
import org.skfiy.typhon.domain.GlobalData.Type;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.session.SessionUtils;

public class PowerguessRankingProvider extends AbstractRankingProvider {

    @Override
    protected Type getGlobalDataType() {
        return GlobalData.Type.powerGuess_data;
    }

    @Override
    protected boolean compare(Player player, RankingObject rankingObject) {
        return (returnPowerGuess(player) > rankingObject.getPowerGuess());
    }

    public boolean updatePowerguessRanking() {
        Player player = SessionUtils.getPlayer();
        int rid = player.getRole().getRid();

        List<RankingObject> rankings = returnRankings();
        int index = rankings.size();

        for (int i = index - 1; i >= 0; i--) {
            if (rankings.get(i).getRid() == rid) {
                rankings.remove(i);
                break;
            }
        }
        return updateRanking(player);
    }
}
