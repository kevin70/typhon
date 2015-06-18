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
package org.skfiy.typhon.spi.role;

/**
 *
 * @author Kevin
 */
public class ExpLevel {

    private int level;
    private int maxVigor;
    private int presentVigor;
    private int exp;
    private int heroExp;
    private int heroMaxLevel;
    private int troopExp;
    private int troopMaxLevel;
 
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getMaxVigor() {
        return maxVigor;
    }

    public void setMaxVigor(int maxVigor) {
        this.maxVigor = maxVigor;
    }

    public int getPresentVigor() {
        return presentVigor;
    }

    public void setPresentVigor(short presentVigor) {
        this.presentVigor = presentVigor;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getHeroExp() {
        return heroExp;
    }

    public void setHeroExp(int heroExp) {
        this.heroExp = heroExp;
    }

    public int getHeroMaxLevel() {
        return heroMaxLevel;
    }

    public void setHeroMaxLevel(int heroMaxLevel) {
        this.heroMaxLevel = heroMaxLevel;
    }

    public int getTroopExp() {
        return troopExp;
    }

    public void setTroopExp(int troopExp) {
        this.troopExp = troopExp;
    }

    public int getTroopMaxLevel() {
        return troopMaxLevel;
    }

    public void setTroopMaxLevel(int troopMaxLevel) {
        this.troopMaxLevel = troopMaxLevel;
    }

    
}
