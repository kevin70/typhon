package org.skfiy.typhon.spi.ranking;

import javax.inject.Inject;

import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.session.SessionUtils;

public class UpdateRankingList {

    @Inject
    private PowerguessRankingProvider powerguessRankingProvider;
    @Inject
    private LevelRankingProvider levelRankingProvider;
    @Inject
    private PveDifficultRankingProvider pveDifficultRankingProvider;
    @Inject
    private HeroStarRankingProvider heroStarRankingProvider;
    @Inject
    private PveRankingProvider pveRankingProvider;

    public void updatePowerguessRanking() {
        Player player = SessionUtils.getPlayer();
        if (powerguessRankingProvider.updatePowerguessRanking()) {
            levelRankingProvider.updateOtherRanking(player);
            pveDifficultRankingProvider.updateOtherRanking(player);
            heroStarRankingProvider.updateOtherRanking(player);
            pveRankingProvider.updateOtherRanking(player);
        }
    }

    public void updateLevelRanking() {
        Player player = SessionUtils.getPlayer();
        if (levelRankingProvider.updateRanking(player)) {
            powerguessRankingProvider.levleUpdateOtherRanking(player);
            pveDifficultRankingProvider.levleUpdateOtherRanking(player);
            heroStarRankingProvider.levleUpdateOtherRanking(player);
            pveRankingProvider.levleUpdateOtherRanking(player);
        }
    }

    public void updatePveDifficultRanking() {
        Player player = SessionUtils.getPlayer();
        if (pveDifficultRankingProvider.updateRanking(player)) {
            levelRankingProvider.updateOtherRanking(player);
            powerguessRankingProvider.updateOtherRanking(player);
            heroStarRankingProvider.updateOtherRanking(player);
            pveRankingProvider.updateOtherRanking(player);
        }
    }

    public void updateAllRanking() {
        Player player = SessionUtils.getPlayer();
        powerguessRankingProvider.updateOtherRanking(player);
        levelRankingProvider.updateOtherRanking(player);
        pveDifficultRankingProvider.updateOtherRanking(player);
        heroStarRankingProvider.updateOtherRanking(player);
        pveRankingProvider.updateOtherRanking(player);
    }
    
    public void updateHeroStarRanking() {
        Player player = SessionUtils.getPlayer();
        if (heroStarRankingProvider.updateRanking(player)) {
            powerguessRankingProvider.updateOtherRanking(player);
            levelRankingProvider.updateOtherRanking(player);
            pveDifficultRankingProvider.updateOtherRanking(player);
            pveRankingProvider.updateOtherRanking(player);
        }
    }
    
    public void updatePveRanking() {
        Player player = SessionUtils.getPlayer();
        if (pveRankingProvider.updateRanking(player)) {
            powerguessRankingProvider.updateOtherRanking(player);
            levelRankingProvider.updateOtherRanking(player);
            pveDifficultRankingProvider.updateOtherRanking(player);
            pveRankingProvider.updateOtherRanking(player);
            heroStarRankingProvider.updateOtherRanking(player);
        }
    }

}
