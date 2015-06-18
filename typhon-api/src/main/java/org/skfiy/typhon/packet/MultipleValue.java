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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class MultipleValue extends Packet {

    private final List<Object> vals = new ArrayList<>();

    public List<Object> getVals() {
        return vals;
    }

    public void setVals(List<Object> vals) {
        addAllVal(vals);
    }

    public void addVal(Object val) {
        vals.add(val);
    }

    public void addAllVal(Collection<Object> vals) {
        this.vals.addAll(vals);
    }

    /**
     *
     * @param packet
     * @return
     */
    public static MultipleValue createResult(Packet packet) {
        MultipleValue result = new MultipleValue();
        result.setId(packet.getId());
        result.setNs(packet.getNs());
        return result;
    }
}
