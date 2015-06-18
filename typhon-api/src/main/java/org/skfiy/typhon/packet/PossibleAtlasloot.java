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
package org.skfiy.typhon.packet;

import com.alibaba.fastjson.annotation.JSONType;

/**
 *
 * @author Kevin
 */
@JSONType(ignores = "attachment")
public class PossibleAtlasloot {

    private String itemId;
    private int count;
    private Object isSoul;

    public PossibleAtlasloot() {

    }

    public PossibleAtlasloot(String itemId, int count) {
        this.itemId = itemId;
        this.count = count;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Object getIsSoul() {
        return isSoul;
    }

    public void setIsSoul(Object isSoul) {
        this.isSoul = isSoul;
    }

}
