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
package org.skfiy.typhon;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class VersionTest {
    
    @Test
    public void execute() {
        System.setProperty(Globals.PROP_VERSION, "1.0-SNAPSHOT");
        Version version = new Version(1, 0, 0, null);
        Assert.assertEquals(Version.currentVersion(), version);
    }

    @Test
    public void execute1() {
        System.setProperty(Globals.PROP_VERSION, "1.0-SNAPSHOT");
        Version version = new Version(0, 7, 0, null);
        Assert.assertTrue(Version.currentVersion().compareTo(version) > 0);
    }

    @Test
    public void execute2() {
        System.setProperty(Globals.PROP_VERSION, "1.0-SNAPSHOT");
        Version version = new Version(1, 7, 0, null);
        Assert.assertTrue(Version.currentVersion().compareTo(version) < 0);
    }
}
