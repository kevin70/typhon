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
package org.skfiy.typhon.spi.pve;

import com.alibaba.fastjson.annotation.JSONType;
import org.apache.commons.lang3.ArrayUtils;
import org.skfiy.typhon.dobj.ItemDobj;
import org.skfiy.typhon.spi.war.Terrain;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@JSONType(ignores = {"parent"})
public class Part {

    private String pid;
    private Terrain terrain;
    private int minLevel;
    private int previgor;
    private int postvigor;
    private int count;
    private int copper;
    private int exp;
    private int heroExp;
    private int hcidx;
    private int hpidx;
    private ItemDobj fruItem1;
    private ItemDobj fruItem2;
    private ItemDobj fruItem3;
    private String[] atlasloots;
    private Step[] steps;

    private Chapter parent;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public int getPrevigor() {
        return previgor;
    }

    public void setPrevigor(int previgor) {
        this.previgor = previgor;
    }

    public int getPostvigor() {
        return postvigor;
    }

    public void setPostvigor(int postvigor) {
        this.postvigor = postvigor;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCopper() {
        return copper;
    }

    public void setCopper(int copper) {
        this.copper = copper;
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

    public int getHcidx() {
        return hcidx;
    }

    public void setHcidx(int hcidx) {
        this.hcidx = hcidx;
    }

    public int getHpidx() {
        return hpidx;
    }

    public void setHpidx(int hpidx) {
        this.hpidx = hpidx;
    }

    public ItemDobj getFruItem1() {
        return fruItem1;
    }

    public void setFruItem1(ItemDobj fruItem1) {
        this.fruItem1 = fruItem1;
    }

    public ItemDobj getFruItem2() {
        return fruItem2;
    }

    public void setFruItem2(ItemDobj fruItem2) {
        this.fruItem2 = fruItem2;
    }

    public ItemDobj getFruItem3() {
        return fruItem3;
    }

    public void setFruItem3(ItemDobj fruItem3) {
        this.fruItem3 = fruItem3;
    }

    public String[] getAtlasloots() {
        if (atlasloots == null) {
            return (new String[]{});
        }
        return ArrayUtils.clone(atlasloots);
    }

    public void setAtlasloots(String[] atlasloots) {
        this.atlasloots = atlasloots;
    }
    
    public Step[] getSteps() {
        return steps;
    }

    public Step getStep(int index) {
        return steps[index];
    }
    
    public int getStepSize() {
        return steps.length;
    }

    public void setSteps(Step[] steps) {
        this.steps = steps;

        for (Step s : steps) {
            s.setParent(this);
        }
    }

    public Chapter getParent() {
        return parent;
    }

    public void setParent(Chapter parent) {
        this.parent = parent;
    }

}
