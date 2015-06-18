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
public class AttackEntry {

    private int lab;
    private Object val;
    private Object annex;
    private Object crit;
    private Object parry;

    public AttackEntry() {
        
    }
    
    public AttackEntry(int lab) {
        this.lab = lab;
    }
    
    public int getLab() {
        return lab;
    }

    public void setLab(int lab) {
        this.lab = lab;
    }

    public Object getVal() {
        return val;
    }

    public void setVal(Object val) {
        this.val = val;
    }

    public Object getAnnex() {
        return annex;
    }

    public void setAnnex(Object annex) {
        this.annex = annex;
    }

    public Object getCrit() {
        return crit;
    }

    public void setCrit(Object crit) {
        this.crit = crit;
    }

    public Object getParry() {
        return parry;
    }

    public void setParry(Object parry) {
        this.parry = parry;
    }

    //==============================================================================================
    public void crited(double c) {
        if (c > 1D) {
            crit = 1;
        }
    }

    public void parried(double p) {
        if (p > 0D) {
            parry = 1;
        }
    }
    //==============================================================================================
}
