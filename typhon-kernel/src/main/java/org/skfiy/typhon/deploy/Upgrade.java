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
package org.skfiy.typhon.deploy;

import java.io.File;
import java.io.FilenameFilter;
import org.skfiy.typhon.Version;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
class Upgrade implements Comparable<Upgrade> {

    private final File dir;
    private final Version version;

    Upgrade(File dir) {
        this.dir = dir;
        version = new Version(dir.getName());
    }

    File getDir() {
        return dir;
    }
    
    File[] getSqlFiles() {
        return getDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".sql");
            }
        });
    }
    
    Version getVersion() {
        return version;
    }

    @Override
    public int compareTo(Upgrade o) {
        return version.compareTo(o.version);
    }
}
