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

import org.skfiy.typhon.domain.Lootable;

/**
 *
 * @author Kevin
 */
public class MultipleAtlasloot extends RandomAtlaslootBean {

    private String id;
    private AtlaslootBean[] atlasloots;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AtlaslootBean[] getAtlasloots() {
        return atlasloots;
    }

    public void setAtlasloots(AtlaslootBean[] atlasloots) {
        this.atlasloots = atlasloots;

        //
        AtlaslootBean prev = null;
        for (AtlaslootBean bean : atlasloots) {
            if (prev == null) {
                prev = bean;
                continue;
            }
            bean.setProb(prev.getProb() + bean.getProb());
        }
    }

    @Override
    void prepare() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    AtlaslootBean calculate(org.skfiy.typhon.session.Session session, Lootable lootable) {
        double r = RANDOM.nextDouble();
        for (AtlaslootBean bean : atlasloots) {
            if (r <= bean.getProb()) {
                return bean;
            }
        }
        return null;
    }

}
