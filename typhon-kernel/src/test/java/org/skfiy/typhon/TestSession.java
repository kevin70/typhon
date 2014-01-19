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

import org.skfiy.typhon.session.AbstractSession;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class TestSession extends AbstractSession {

    private long creationTime;
    private long lastAccessedTime;

    public TestSession() {
        creationTime = lastAccessedTime = System.currentTimeMillis();
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public int getId() {
        // FIXME
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    @Override
    protected void write(byte[] buf, int off, int len) {
        // nothing
    }

    @Override
    public void close() {
    }
    
}
