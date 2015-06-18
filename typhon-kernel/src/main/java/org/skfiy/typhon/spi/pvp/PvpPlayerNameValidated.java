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
package org.skfiy.typhon.spi.pvp;

import javax.inject.Inject;
import org.skfiy.typhon.spi.IPlayerNameValidated;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class PvpPlayerNameValidated implements IPlayerNameValidated {

    @Inject
    private PvpProvider pvpProvider;

    @Override
    public boolean validate(String name) {
        for (PvpRobot pr : pvpProvider.getPvpRobots()) {
            if (name.equals(pr.getName())) {
                return false;
            }
        }
        return true;
    }

}
