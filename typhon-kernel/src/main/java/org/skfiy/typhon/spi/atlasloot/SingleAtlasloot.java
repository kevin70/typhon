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
package org.skfiy.typhon.spi.atlasloot;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math.util.MathUtils;
import org.skfiy.typhon.domain.Lootable;
import org.skfiy.typhon.domain.Lootable.Record;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionUtils;

/**
 *
 * @author Kevin
 */
public class SingleAtlasloot extends AtlaslootBean {

    // 必定不掉, 不掉的次数
    private static final int SURE_NO_ATLASLOOT_COUNT = 1;
    
    private String id;
    private int factor;
    
    private double p_prob;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getFactor() {
        return factor;
    }

    public void setFactor(int factor) {
        this.factor = factor;
    }

    @Override
    void prepare() {
        p_prob = MathUtils.round((1 - getProb()) / factor, 3, BigDecimal.ROUND_UP);
    }

    @Override
    AtlaslootBean calculate(Session session, Lootable lootable) {
        double p = getProb();
        if (lootable != null && factor > 0) {
            // 计算新的概率
            Record record = lootable.findRecord(id);
            if (record != null) {
                int in = lootable.getTotal() - record.getLastTime();
                // 距离上一次掉落必须超过2次
                if (in < SURE_NO_ATLASLOOT_COUNT) {
                    p = 0;
                } else {
                    p = p + p_prob * (in - 1);
                }
            } else {
                p = p + p_prob * lootable.getTotal();
            }
        }
        
        AtlaslootBean ab =  calculate(p);
        if (ab != null) {
            List<String> list = SessionUtils.getPlayer(session).getInvisible().getSingleAtlasloots();
            if (list == null) {
                list = new ArrayList<>();
                SessionUtils.getPlayer(session).getInvisible().setSingleAtlasloots(list);
            }
            list.add(id); // 增加掉落ID
        }
        
        return ab;
    }

}
