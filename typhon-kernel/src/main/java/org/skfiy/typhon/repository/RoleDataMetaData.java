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
package org.skfiy.typhon.repository;

import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
class RoleDataMetaData {

    static final String PK_NAME = "RID";
    private RoleDataColumn[] columns = new RoleDataColumn[0];

    RoleDataColumn[] getColumns() {
        return columns;
    }

    RoleDataColumn getColumn(int i) {
        return columns[i];
    }

    void addColumn(String name, int type) {
        addColumn(name, type, columns.length);
    }

    void addColumn(String name, int type, int index) {
        ArrayUtils.add(columns, new RoleDataColumn(
                name.toUpperCase(), type, columns.length));
    }

    boolean isPK(String name) {
        return PK_NAME.equalsIgnoreCase(name);
    }

    static class RoleDataColumn {

        String name;
        int index;
        int type;

        RoleDataColumn(String name, int type, int idx) {
            this.name = name;
            this.type = type;
            this.index = idx;
        }

        String getName() {
            return name;
        }

        int getType() {
            return type;
        }

        int getIndex() {
            return index;
        }
    }
}
