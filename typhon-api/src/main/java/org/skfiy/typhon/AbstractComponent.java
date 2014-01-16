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
package org.skfiy.typhon;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public abstract class AbstractComponent implements Component {

    private Status status;

    @Override
    public final void init() {
        checkStatus(null);
        doInit();
        status = Status.INITIALIZED;
    }

    @Override
    public final void reload() {
        checkStatus(Status.INITIALIZED);
        doReload();
    }

    @Override
    public final void destroy() {
        checkStatus(Status.INITIALIZED);
        doDestroy();
        status = Status.DESTROYED;
    }

    /**
     * 
     * @return 
     */
    protected Status getStatus() {
        return status;
    }
    
    /**
     *
     */
    protected abstract void doInit();

    /**
     *
     */
    protected abstract void doReload();

    /**
     *
     */
    protected abstract void doDestroy();

    private void checkStatus(Status expected) {
        if (status != expected) {
            throw new ComponentStatusException(
                    "current status [" + status + "] exected status [" + expected + "]");
        }
    }
}
