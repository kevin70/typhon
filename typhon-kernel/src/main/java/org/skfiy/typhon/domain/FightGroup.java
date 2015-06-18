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

import com.alibaba.fastjson.annotation.JSONType;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.domain.item.HeroItem;
import org.skfiy.typhon.util.DomainUtils;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@JSONType(ignores = {"heroItems, heroItemIds"})
public class FightGroup extends AbstractIndexable {

    /**
     * PVP攻击组索引.
     */
    public static final int PVP_FG_IDX = 3;

    /**
     * 先锋营.
     */
    public static final int LEFT_PIONEER_POS = 0;
    /**
     * 军师营.
     */
    public static final int COUNSELLOR_POS = 1;
    /**
     * 主将营.
     */
    public static final int PRIMARY_POS = 2;
    /**
     * 辎重营.
     */
    public static final int FORAGE_POS = 3;
    /**
     * 疑兵营.
     */
    public static final int RIGHT_PIONEER_POS = 4;

    private int captain = PRIMARY_POS;
    private int succor; // 挥军位索引
    private String succorIid;
    private int[] heroPoses = new int[Typhons.getInteger("typhon.spi.fightGroup.heroCount", 5)];
    private HeroItem[] heroItems = new HeroItem[heroPoses.length];

    public FightGroup() {
        Arrays.fill(heroPoses, -1);
    }

    /**
     * 
     * @return 
     */
    public int getCaptain() {
        return captain;
    }

    /**
     * 
     * @param captain 
     */
    public void setCaptain(int captain) {
        this.captain = captain;

        DomainUtils.firePropertyChange(this, "captain", this.captain);
    }

    public int getSuccor() {
        return succor;
    }

    public void setSuccor(int succor) {
        this.succor = succor;
        DomainUtils.firePropertyChange(this, "succor", this.succor);
    }

    public String getSuccorIid() {
        return succorIid;
    }

    public void setSuccorIid(String succorIid) {
        this.succorIid = succorIid;
        DomainUtils.firePropertyChange(this, "succorIid", this.succorIid);
    }

    /**
     *
     * @return
     */
    public int[] getHeroPoses() {
        return heroPoses;
    }

    /**
     *
     * @param heroPoses
     */
    public void setHeroPoses(int[] heroPoses) {
        this.heroPoses = heroPoses;

        DomainUtils.firePropertyChange(this, "heroPoses", this.heroPoses);
    }

    /**
     *
     * @return
     */
    public HeroItem[] getHeroItems() {
        return heroItems;
    }

    /**
     *
     * @param i
     * @return
     */
    public HeroItem getHeroItem(int i) {
        return heroItems[i];
    }

    /**
     *
     * @return
     */
    public String[] getHeroItemIds() {
        String[] ids = new String[heroItems.length];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = heroItems[i].getId();
        }
        return ids;
    }

    /**
     *
     * @param heroItems
     */
    public void setHeroItems(HeroItem[] heroItems) {
        this.heroItems = heroItems;
    }

    /**
     *
     * @param idx
     * @param toIdx
     */
    public void swap(int idx, int toIdx) {
        int tmp = heroPoses[toIdx];
        heroPoses[toIdx] = heroPoses[idx];
        heroPoses[idx] = tmp;

        HeroItem tmpHero = heroItems[toIdx];
        heroItems[toIdx] = heroItems[idx];
        heroItems[idx] = tmpHero;

        DomainUtils.firePropertyChange(this, "heroPoses", this.heroPoses);
    }

    /**
     *
     * @param i
     * @param node
     */
    public void setHero(int i, Bag.Node node) {
        if (ArrayUtils.indexOf(heroPoses, node.getPos()) >= 0) {
            // FIXME
            return;
        }

        this.heroPoses[i] = node.getPos();
        this.heroItems[i] = (HeroItem) node.getItem();

        // 通知客户端更新武将组
        DomainUtils.firePropertyChange(this, "heroPoses", this.heroPoses);
    }

}
