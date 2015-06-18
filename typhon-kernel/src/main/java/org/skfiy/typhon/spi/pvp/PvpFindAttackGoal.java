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
package org.skfiy.typhon.spi.pvp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.ArrayUtils;
import org.skfiy.typhon.domain.FightGroup;
import org.skfiy.typhon.spi.war.FightObject;
import org.skfiy.typhon.spi.war.WarInfo;
import org.skfiy.typhon.util.FastRandom;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
class PvpFindAttackGoal implements WarInfo.IFindAttackGoal {

    private static final Random RANDOM = new FastRandom();

    /**
     *
     */
    static final PvpFindAttackGoal INSTANCE = new PvpFindAttackGoal();

    private final int[] ATTACKER_GOALS = {FightGroup.LEFT_PIONEER_POS,
        FightGroup.RIGHT_PIONEER_POS,
        FightGroup.COUNSELLOR_POS,
        FightGroup.FORAGE_POS,
        FightGroup.PRIMARY_POS};

    @Override
    public FightObject nextGoal(List<FightObject> fightObjects) {
        return nextGoal(fightObjects, false);
    }

    @Override
    public FightObject nextGoal(List<FightObject> fightObjects, boolean ran) {
        if (ran) {
            int t, s;
            t = s = RANDOM.nextInt(fightObjects.size());
            FightObject rs = null;
            for (;;) {
                rs = fightObjects.get(t);
                if (!rs.isDead()) {
                    break;
                }

                t++;
                if (t >= fightObjects.size()) {
                    t = 0;
                }

                if (t == (s - 1)) {
                    break;
                }
            }
            return rs;
        }

        FightObject rs = null;
        for (int goal : ATTACKER_GOALS) {
            rs = fightObjects.get(goal);
            if (!rs.isDead()) {
                break;
            }
        }
        return rs;
    }

    @Override
    public List<FightObject> nextGoals(List<FightObject> fightObjects, int num) {
        List<FightObject> results = new ArrayList<>(num);

        int first = next(fightObjects);
        results.add(fightObjects.get(first));

        if (num >= 2) {
            int[] indexes = nextIndexes(first);
            shuffle(indexes);

            FightObject fo;
            for (int i : indexes) {
                fo = fightObjects.get(i);
                if (!fo.isDead()) {
                    results.add(fo);

                    if (results.size() >= num) {
                        break;
                    }
                }
            }
        }
        return results;
    }

    private int next(List<FightObject> fightObjects) {
        FightObject fo;
        for (int goal : ATTACKER_GOALS) {
            fo = fightObjects.get(goal);
            if (!fo.isDead()) {
                return goal;
            }
        }
        return -1;
    }

    private int[] nextIndexes(int first) {
        int[] rs = new int[ATTACKER_GOALS.length - 1];

        int i = 0;
        for (int x : ATTACKER_GOALS) {
            if (x != first) {
                rs[i] = x;
                i++;
            }
        }
        return rs;
    }

    void shuffle(final int[] array) {
        final Random r = new Random();
        final int limit = array.length;
        for (int i = 0; i < limit; ++i) {
            swap(array, i, r.nextInt(limit));
        }
    }

    void swap(final int[] array, final int i, final int j) {
        int o = array[i];
        array[i] = array[j];
        array[j] = o;
    }
}
