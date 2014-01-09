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
public abstract class Packet {

    private String ns;
    private String id;
    private Type type;

    public String getNs() {
        return ns;
    }

    public void setNs(String ns) {
        this.ns = ns;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    /**
     *
     * @param in
     * @param out
     */
    protected void assignIdAndType(Packet in, Packet out) {
        out.setId(in.getId());
        out.setType(Type.rs);
    }

    /**
     *
     */
    public enum Type {

        /**
         * The full name is "set".
         */
        st,
        /**
         * The full name is "get".
         */
        gt,
        /**
         * The full name is "add".
         */
        ad,
        /**
         * The full name is "remove".
         */
        rm,
        /**
         * The full name is "result".
         */
        rs
    }
}
