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
package org.skfiy.typhon.util;

import java.util.List;
import org.skfiy.typhon.domain.Indexable;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketIndexPropertyChange;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.util.Assert;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public final class DomainUtils {

    public static final int STRING = 0;
    public static final int BOOLEAN = 1;
    public static final int CHAR = 2;
    public static final int BYTE = 3;
    public static final int SHORT = 4;
    public static final int INT = 5;
    public static final int FLOAT = 6;
    public static final int LONG = 7;
    public static final int DOUBLE = 8;
    public static final int ARRAY = 9;
    public static final int OBJECT = 10;
    
    private DomainUtils() {
        
    }
    
    /**
     * 
     * @param datas
     * @param obj
     * @param propertyName 
     */
    public static void add(List<Indexable> datas, Indexable obj, String propertyName) {
        Assert.notNull(datas);
        Assert.notNull(obj);
        Assert.notNull(propertyName);
        
        int idx = datas.size();
        datas.add(obj);
        obj.setIndex(idx);
        
        PacketIndexPropertyChange pipc = new PacketIndexPropertyChange();
        pipc.setNs(obj.getNs());
        pipc.setType(Packet.Type.ad);
        pipc.setPn(propertyName);
        pipc.setPt(OBJECT);
        pipc.setVal(obj);
        Session session = SessionContext.getSession();
        session.write(pipc);
    }

    /**
     * 
     * @param datas
     * @param idx
     * @param propertyName 
     */
    public static void remove(List<Indexable> datas, int idx, String propertyName) {
        Assert.notNull(datas);
        Assert.notNull(propertyName);
        
        Indexable obj = datas.remove(idx);
        Indexable obj2;
        for (int i = (idx - 1); i < datas.size(); i++) {
            obj2 = datas.get(i);
            obj2.setIndex(i);
        }

        PacketIndexPropertyChange pipc = new PacketIndexPropertyChange();
        pipc.setNs(obj.getNs());
        pipc.setType(Packet.Type.rm);
        pipc.setPn(propertyName);
        pipc.setPt(OBJECT);
        pipc.setVal(obj);
        
        Session session = SessionContext.getSession();
        session.write(pipc);
    }
    
}
