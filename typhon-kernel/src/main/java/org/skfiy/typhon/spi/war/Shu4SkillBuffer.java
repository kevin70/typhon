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
public class Shu4SkillBuffer extends FightObjectBufferSkill {

    private int pdef;
    private int pmdef;

    public Shu4SkillBuffer(WarInfo warInfo, Direction dire, FightObject fobj) {
        super(warInfo, dire, fobj);
    }

    @Override
    protected int getTotalRound() {
        return 2;
    }

    @Override
    protected Object begin() {
        pdef = (int) (fobj.getMaxDef() * 0.2);
        pmdef = (int) (fobj.getMaxMdef() * 0.2);

        fobj.setDef(fobj.getDef() + pdef);
        fobj.setMdef(fobj.getMdef() + pmdef);
        return null;
    }

    @Override
    protected Object end() {
        fobj.setDef(fobj.getDef() - pdef);
        fobj.setMdef(fobj.getMdef() - pmdef);
        return null;
    }

    @Override
    public Type getType() {
        return Type.BUFF;
    }

}
