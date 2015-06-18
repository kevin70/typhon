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

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public abstract class FightObjectBufferSkill implements BufferSkill {

    private int beginRound;
    protected final Direction dire;
    protected final WarInfo warInfo;
    protected final FightObject fobj;

    /**
     * 
     * @param warInfo
     * @param dire
     * @param fobj 
     */
    public FightObjectBufferSkill(WarInfo warInfo, Direction dire, FightObject fobj) {
        this.beginRound = warInfo.getRound();
        this.dire = dire;
        this.warInfo = warInfo;
        this.fobj = fobj;

        if (this.dire == Direction.N) {
            beginRound += 1;
        }
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    /**
     * 
     * @return 
     */
    @Override
    public Object onBefore() {
        Object rs = begin();
        fobj.addBufferSkill(this);
        return rs;
    }

    /**
     * 
     * @return 
     */
    @Override
    public Object onAfter() {
        if (warInfo.getRound() - beginRound >= getTotalRound()) {
            return onFinish();
        }
        return null;
    }

    /**
     * 
     * @return 
     */
    @Override
    public Object onFinish() {
        Object rs = end();
        fobj.removeBufferSkill(this);
        return rs;
    }
    
    private int totalRound() {
        if (dire == Direction.N) {
            return (getTotalRound() + 1);
        }
        return getTotalRound();
    }

    /**
     *
     * @return
     */
    protected abstract int getTotalRound();

    /**
     *
     * @return
     */
    protected abstract Object begin();

    /**
     *
     * @return
     */
    protected abstract Object end();
}
