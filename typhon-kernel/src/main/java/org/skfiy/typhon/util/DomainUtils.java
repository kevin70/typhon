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

import java.util.Collection;
import org.skfiy.typhon.domain.Bag;
import org.skfiy.typhon.domain.Changeable;
import org.skfiy.typhon.domain.Indexable;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketPropertyChange;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionUtils;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public final class DomainUtils {

    public static final int TYPE_STRING = 0;
    public static final int TYPE_BOOLEAN = 1;
    public static final int TYPE_CHAR = 2;
    public static final int TYPE_BYTE = 3;
    public static final int TYPE_SHORT = 4;
    public static final int TYPE_INT = 5;
    public static final int TYPE_FLOAT = 6;
    public static final int TYPE_LONG = 7;
    public static final int TYPE_DOUBLE = 8;
    public static final int TYPE_ARRAY = 9;
    public static final int TYPE_OBJECT = 10;

    private DomainUtils() {
    }

    /**
     *
     * @param source
     * @param propertyName
     * @param val
     */
    public static void firePropertyChange(Changeable source,
            String propertyName, String val) {
        firePropertyChange(source, propertyName, val, TYPE_STRING, Packet.Type.st);
    }

    /**
     *
     * @param source
     * @param propertyName
     * @param val
     */
    public static void firePropertyChange(Changeable source,
            String propertyName, boolean val) {
        firePropertyChange(source, propertyName, val, TYPE_BOOLEAN, Packet.Type.st);
    }

    /**
     *
     * @param source
     * @param propertyName
     * @param val
     */
    public static void firePropertyChange(Changeable source,
            String propertyName, char val) {
        firePropertyChange(source, propertyName, val, TYPE_CHAR, Packet.Type.st);
    }

    /**
     *
     * @param source
     * @param propertyName
     * @param val
     */
    public static void firePropertyChange(Changeable source,
            String propertyName, byte val) {
        firePropertyChange(source, propertyName, val, TYPE_BYTE, Packet.Type.st);
    }

    /**
     *
     * @param source
     * @param propertyName
     * @param val
     */
    public static void firePropertyChange(Changeable source,
            String propertyName, short val) {
        firePropertyChange(source, propertyName, val, TYPE_SHORT, Packet.Type.st);
    }

    /**
     *
     * @param source
     * @param propertyName
     * @param val
     */
    public static void firePropertyChange(Changeable source,
            String propertyName, int val) {
        firePropertyChange(source, propertyName, val, TYPE_INT, Packet.Type.st);
    }

    /**
     *
     * @param source
     * @param propertyName
     * @param val
     */
    public static void firePropertyChange(Changeable source,
            String propertyName, float val) {
        firePropertyChange(source, propertyName, val, TYPE_FLOAT, Packet.Type.st);
    }

    /**
     *
     * @param source
     * @param propertyName
     * @param val
     */
    public static void firePropertyChange(Changeable source,
            String propertyName, long val) {
        firePropertyChange(source, propertyName, val, TYPE_LONG, Packet.Type.st);
    }

    /**
     *
     * @param source
     * @param propertyName
     * @param val
     */
    public static void firePropertyChange(Changeable source,
            String propertyName, double val) {
        firePropertyChange(source, propertyName, val, TYPE_DOUBLE, Packet.Type.st);
    }

    /**
     *
     * @param source
     * @param propertyName
     * @param val
     */
    public static void firePropertyChange(Changeable source,
            String propertyName, Object val) {
//        if (val == null) {
//            return;
//        }
        
        if (val != null && (val.getClass().isArray()
                || Collection.class.isAssignableFrom(val.getClass()))) {
            firePropertyChange(source, propertyName, val, TYPE_ARRAY, Packet.Type.st);
        } else {
            firePropertyChange(source, propertyName, val, TYPE_OBJECT, Packet.Type.st);
        }
    }

    /**
     *
     * @param source
     * @param propertyName
     * @param val
     */
    public static void fireIndexPropertyAdd(Changeable source,
            String propertyName, String val) {
        firePropertyChange(source, propertyName, val, TYPE_STRING, Packet.Type.ad);
    }

    /**
     *
     * @param source
     * @param propertyName
     * @param val
     */
    public static void fireIndexPropertyAdd(Changeable source,
            String propertyName, boolean val) {
        firePropertyChange(source, propertyName, val, TYPE_BOOLEAN, Packet.Type.ad);
    }

    /**
     *
     * @param source
     * @param propertyName
     * @param val
     */
    public static void fireIndexPropertyAdd(Changeable source,
            String propertyName, char val) {
        firePropertyChange(source, propertyName, val, TYPE_CHAR, Packet.Type.ad);
    }

    /**
     *
     * @param source
     * @param propertyName
     * @param val
     */
    public static void fireIndexPropertyAdd(Changeable source,
            String propertyName, byte val) {
        firePropertyChange(source, propertyName, val, TYPE_BYTE, Packet.Type.ad);
    }

    /**
     *
     * @param source
     * @param propertyName
     * @param val
     */
    public static void fireIndexPropertyAdd(Changeable source,
            String propertyName, short val) {
        firePropertyChange(source, propertyName, val, TYPE_SHORT, Packet.Type.ad);
    }

    /**
     *
     * @param source
     * @param propertyName
     * @param val
     */
    public static void fireIndexPropertyAdd(Changeable source,
            String propertyName, int val) {
        firePropertyChange(source, propertyName, val, TYPE_INT, Packet.Type.ad);
    }

    /**
     *
     * @param source
     * @param propertyName
     * @param val
     */
    public static void fireIndexPropertyAdd(Changeable source,
            String propertyName, float val) {
        firePropertyChange(source, propertyName, val, TYPE_FLOAT, Packet.Type.ad);
    }

    /**
     *
     * @param source
     * @param propertyName
     * @param val
     */
    public static void fireIndexPropertyAdd(Changeable source,
            String propertyName, long val) {
        firePropertyChange(source, propertyName, val, TYPE_LONG, Packet.Type.ad);
    }

    /**
     *
     * @param source
     * @param propertyName
     * @param val
     */
    public static void fireIndexPropertyAdd(Changeable source,
            String propertyName, double val) {
        firePropertyChange(source, propertyName, val, TYPE_DOUBLE, Packet.Type.ad);
    }

    /**
     *
     * @param source
     * @param propertyName
     * @param val
     */
    public static void fireIndexPropertyAdd(Changeable source,
            String propertyName, Object val) {
        firePropertyChange(source, propertyName, val, TYPE_OBJECT, Packet.Type.ad);
    }
    
    /**
     * 
     * @param source
     */
    public static void fireIndexPropertyRemove(Indexable source) {
        firePropertyChange(source, null, null, 0, Packet.Type.rm);
    }
    
    private static void firePropertyChange(Changeable source,
            String propertyName, Object val, int type, Packet.Type pt) {
        if (source == null
                || source.parent() == null
                || source.player() == null
                || source.player().getSession() == null) {
            return;
        }

        Session session = source.player().getSession();
        if (session.getAttribute("player.send.enabled") != Boolean.TRUE) {
            return;
        }

        StringBuilder fullName = new StringBuilder();
        if (Packet.Type.rm != pt) {
            fullName.append(propertyName);
        }
        while (source.parentPropertyName() != null) {
            fullName.insert(0, ".");
            if (source instanceof Indexable) {
                int j = 0;
                fullName.insert(j++, "[");
                if (source.parent() instanceof Bag) {
                    fullName.insert(j++, "\\");
                }
                
                int idx = ((Indexable) source).index();
                fullName.insert(j++, idx);
                j += String.valueOf(idx).length() - 1;
                fullName.insert(j, "]");
            }
            fullName.insert(0, source.parentPropertyName());
            source = source.parent();
        }

        if (Packet.Type.rm == pt) {
            fullName.deleteCharAt(fullName.length() - 1);
        }
        
        // 数据修改包
        PacketPropertyChange packet = new PacketPropertyChange();
        packet.setNs(Namespaces.SET_PLAYER);
        packet.setPn(fullName.toString());
        packet.setPt(type);
        packet.setVal(val);
        packet.setType(pt);

        session.write(packet);
    }

}
