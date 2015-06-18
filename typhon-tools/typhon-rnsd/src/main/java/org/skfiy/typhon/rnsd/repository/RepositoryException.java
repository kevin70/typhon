/*
 * Copyright 2015 The Skfiy Open Association.
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
package org.skfiy.typhon.rnsd.repository;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class RepositoryException extends RuntimeException {

    /**
     * 
     */
    public static final int SIGNAL_INSERTING = 1;
    /**
     * 
     */
    public static final int SIGNAL_DELETING = 2;
    /**
     * 
     */
    public static final int SIGNAL_UPDATING = 3;
    /**
     * 
     */
    public static final int SIGNAL_SELECTING = 4;

    private int signal;

    /**
     *
     * @param message
     */
    public RepositoryException(String message) {
        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     *
     * @param signal
     * @param message
     */
    public RepositoryException(int signal, String message) {
        super(message);
        this.signal = signal;
        
        this.printStackTrace();
    }

    /**
     *
     * @param signal
     * @param cause
     */
    public RepositoryException(int signal, Throwable cause) {
        super(cause);
        this.signal = signal;
    }

    /**
     *
     * @param signal
     * @param message
     * @param cause
     */
    public RepositoryException(int signal, String message, Throwable cause) {
        super(message, cause);
        this.signal = signal;
    }

    /**
     *
     * @return
     */
    public int getSignal() {
        return signal;
    }

    /**
     *
     * @param signal
     */
    public void setSignal(int signal) {
        this.signal = signal;
    }

}
