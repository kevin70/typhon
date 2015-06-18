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
package org.skfiy.typhon.domain;

import java.util.List;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public interface ITroop {

    /**
     * 
     */
    int MAX_TROOP_SIZE = 5;

    /**
     *
     * @return
     */
    public List<Troop> getTroops();

    /**
     *
     * @param type
     * @return
     */
    public Troop getTroop(Type type);

    /**
     *
     * @param troops
     */
    public void setTroops(List<Troop> troops);

    /**
     *
     */
    public enum Type {

        XF(0), JS(1), ZJ(2), ZZ(3), YB(4);

        private final int pos;

        Type(int pos) {
            this.pos = pos;
        }

        /**
         *
         * @return
         */
        public int getPos() {
            return pos;
        }

        /**
         * 
         * @param pos
         * @return 
         */
        public static Type valueOf(int pos) {
            return values()[pos];
        }
    }
}
