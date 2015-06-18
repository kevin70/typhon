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
package org.skfiy.typhon.dobj;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import org.skfiy.typhon.domain.item.AbstractItem;
import org.skfiy.typhon.domain.item.Area;
import org.skfiy.typhon.domain.item.Gender;
import org.skfiy.typhon.domain.item.HeroItem;
import org.skfiy.typhon.spi.hero.HeroProvider;
import org.skfiy.typhon.util.MBeanUtils;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@JSONType(shortType = ComplexItemDobj.JSON_SHORT_TYPE)
public class HeroItemDobj extends AbstractHeroItemDobj {

    private static ObjectName heroProviderObjectName;
    public static final String JSON_SHORT_TYPE = "S$HeroItem";

    private Area area;
    private Gender gender;
    
    @JSONField(name = "BSaSkill")
    private String bsaSkill;
    @JSONField(name = "LeaderSkill")
    private String leaderSkill;
    private int tong;
    private int wu;
    private int zhi;
    private double tongUp;
    private double wuUp;
    private double zhiUp;
    private double potentialUp;
    private double atkX;
    private double defY;
    private double hpZ;
    private double atkA;
    private double defB;
    private double hpC;
    private Soul soul;
    private Shot[] shots1;
    private Shot[] shots2;
    private Shot[] shots3;
    private Shot[] shots4;
    private Shot[] shots5;

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getBsaSkill() {
        return bsaSkill;
    }

    public void setBsaSkill(String bsaSkill) {
        this.bsaSkill = bsaSkill;
    }

    public String getLeaderSkill() {
        return leaderSkill;
    }

    public void setLeaderSkill(String leaderSkill) {
        this.leaderSkill = leaderSkill;
    }

    public int getTong() {
        return tong;
    }

    public void setTong(int tong) {
        this.tong = tong;
    }

    public int getWu() {
        return wu;
    }

    public void setWu(int wu) {
        this.wu = wu;
    }

    public int getZhi() {
        return zhi;
    }

    public void setZhi(int zhi) {
        this.zhi = zhi;
    }

    public double getTongUp() {
        return tongUp;
    }

    public void setTongUp(double tongUp) {
        this.tongUp = tongUp;
    }

    public double getWuUp() {
        return wuUp;
    }

    public void setWuUp(double wuUp) {
        this.wuUp = wuUp;
    }

    public double getZhiUp() {
        return zhiUp;
    }

    public void setZhiUp(double zhiUp) {
        this.zhiUp = zhiUp;
    }

    public double getPotentialUp() {
        return potentialUp;
    }

    public void setPotentialUp(double potentialUp) {
        this.potentialUp = potentialUp;
    }

    public double getAtkX() {
        return atkX;
    }

    public void setAtkX(double atkX) {
        this.atkX = atkX;
    }

    public double getDefY() {
        return defY;
    }

    public void setDefY(double defY) {
        this.defY = defY;
    }

    public double getHpZ() {
        return hpZ;
    }

    public void setHpZ(double hpZ) {
        this.hpZ = hpZ;
    }

    public double getAtkA() {
        return atkA;
    }

    public void setAtkA(double atkA) {
        this.atkA = atkA;
    }

    public double getDefB() {
        return defB;
    }

    public void setDefB(double defB) {
        this.defB = defB;
    }

    public double getHpC() {
        return hpC;
    }

    public void setHpC(double hpC) {
        this.hpC = hpC;
    }

    public Soul getSoul() {
        return soul;
    }

    public void setSoul(Soul soul) {
        this.soul = soul;
    }

    public Shot[] getShots1() {
        return shots1;
    }

    public void setShots1(Shot[] shots1) {
        this.shots1 = shots1;
    }

    public Shot[] getShots2() {
        return shots2;
    }

    public void setShots2(Shot[] shots2) {
        this.shots2 = shots2;
    }

    public Shot[] getShots3() {
        return shots3;
    }

    public void setShots3(Shot[] shots3) {
        this.shots3 = shots3;
    }

    public Shot[] getShots4() {
        return shots4;
    }

    public void setShots4(Shot[] shots4) {
        this.shots4 = shots4;
    }

    public Shot[] getShots5() {
        return shots5;
    }

    public void setShots5(Shot[] shots5) {
        this.shots5 = shots5;
    }

    @Override
    public AbstractItem toDomainItem() {
        HeroItem item = new HeroItem();
        item.setId(getId());
        item.setStar(getStar());
        item.setItemDobj(this);
//        item.calculatorPowerGuess();

        try {
            MBeanUtils.REGISTRY.getMBeanServer().invoke(getHeroProviderObjectName(),
                    "calculatorPowerGuess", new Object[]{item},
                    new String[]{HeroItem.class.getName()});
        } catch (Exception ex) {
            // System.err.println(ex);
            ex.printStackTrace(System.err);
        }
        return item;
    }

    private synchronized ObjectName getHeroProviderObjectName() {
        if (heroProviderObjectName == null) {
            heroProviderObjectName = MBeanUtils.newObjectName(
                    MBeanUtils.findManagedBean(HeroProvider.class));
        }
        return heroProviderObjectName;
    }
}
