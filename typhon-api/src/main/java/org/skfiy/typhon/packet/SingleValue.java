/*
 * Copyright 2013 The Skfiy Open Association.
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

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class SingleValue extends Packet {

    public static final String SUCCESS = "S";
    public static final String FAILURE = "F";
    
    private Object val;

    public Object getVal() {
        return val;
    }

    public void setVal(Object val) {
        this.val = val;
    }

    /**
     * 
     * @param packet
     * @param val
     * @return 
     */
    public static SingleValue createResult(Packet packet, Object val) {
        SingleValue result = new SingleValue();
        result.setId(packet.getId());
        result.setVal(val);
        return result;
    }
    
}
