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

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
class PvpHighestRank {

    final int beginRanking;
    final int endRanking;
    final int count;

    PvpHighestRank(int beginRanking, int endRanking, int count) {
        this.beginRanking = beginRanking;
        this.endRanking = endRanking;
        this.count = count;
    }

}
