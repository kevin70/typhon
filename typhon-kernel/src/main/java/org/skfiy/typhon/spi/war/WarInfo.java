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
package org.skfiy.typhon.spi.war;

import java.util.List;
import org.skfiy.typhon.domain.FightGroup;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class WarInfo {

    private Terrain terrain;
    // 回合记数
    private int round = 1;
    //
    private Entity attackerEntity;
    private Entity defenderEntity;
    private Direction nextDire = Direction.S;

    private WarReport warReport;

    public Terrain getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public Entity getAttackerEntity() {
        return attackerEntity;
    }

    public void setAttackerEntity(Entity attackerEntity) {
        this.attackerEntity = attackerEntity;
    }

    public Entity getDefenderEntity() {
        return defenderEntity;
    }

    public void setDefenderEntity(Entity defenderEntity) {
        this.defenderEntity = defenderEntity;
    }

    public Direction getNextDire() {
        return nextDire;
    }

    public void setNextDire(Direction nextDire) {
        this.nextDire = nextDire;
    }

    /**
     *
     * @return
     */
    public WarReport getWarReport() {
        return warReport;
    }

    /**
     *
     * @param warReport
     */
    void setWarReport(WarReport warReport) {
        this.warReport = warReport;
    }

    public static class Entity {

        // 攻击组
        private List<FightObject> fightObjects;
        // 援助
        private FightObject succor;
        // 方向
        private Direction dire;
        // 出手次数
        private int atkCount;
        //
        private IFindAttackGoal findAttackGoal;

        private int captain = FightGroup.PRIMARY_POS;

        private int rid;
        private String roleName;
        private int level;
        private int powerGuess;
        private String societyName;

        public Entity(Direction dire) {
            this.dire = dire;
        }

        public Entity(Direction dire, List<FightObject> fightObjects) {
            this.dire = dire;
            this.fightObjects = fightObjects;
        }

        public List<FightObject> getFightObjects() {
            return fightObjects;
        }

        public FightObject getFightObject(int i) {
            return fightObjects.get(i);
        }

        public void setFightObjects(List<FightObject> fightObjects) {
            this.fightObjects = fightObjects;
        }

        public FightObject getSuccor() {
            return succor;
        }

        public void setSuccor(FightObject succor) {
            this.succor = succor;
        }

        //
        public Direction getDire() {
            return dire;
        }

        public int getAtkCount() {
            return atkCount;
        }

        public int incrementAtkCount() {
            return (atkCount++);
        }

        public IFindAttackGoal getFindAttackGoal() {
            return findAttackGoal;
        }

        public void setFindAttackGoal(IFindAttackGoal findAttackGoal) {
            this.findAttackGoal = findAttackGoal;
        }

        /**
         *
         * @return
         */
        public boolean isOver() {
            for (FightObject fo : fightObjects) {
                if (!fo.isDead()) {
                    return false;
                }
            }
            return true;
        }

        public FightObject findFightGoal() {
            return findAttackGoal.nextGoal(fightObjects);
        }

        public FightObject findFightGoal(boolean ran) {
            return findAttackGoal.nextGoal(fightObjects, ran);
        }

        public List<FightObject> findFightGoals(int num) {
            return findAttackGoal.nextGoals(fightObjects, num);
        }

        public int getCaptain() {
            return captain;
        }

        public void setCaptain(int captain) {
            this.captain = captain;
        }

        //
        public int getRid() {
            return rid;
        }

        public void setRid(int rid) {
            this.rid = rid;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public int getPowerGuess() {
            return powerGuess;
        }

        public void setPowerGuess(int powerGuess) {
            this.powerGuess = powerGuess;
        }

        public String getSocietyName() {
            return societyName;
        }

        public void setSocietyName(String societyName) {
            this.societyName = societyName;
        }

    }

    public interface IFindAttackGoal {

        /**
         *
         * @param fightObjects
         * @return
         */
        FightObject nextGoal(List<FightObject> fightObjects);

        /**
         *
         * @param fightObjects
         * @param ran
         * @return
         */
        FightObject nextGoal(List<FightObject> fightObjects, boolean ran);

        /**
         *
         * @param fightObjects
         * @param num
         * @return
         */
        List<FightObject> nextGoals(List<FightObject> fightObjects, int num);

    }

}
