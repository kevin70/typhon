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
package org.skfiy.typhon.spi.atlasloot;

import java.util.Random;
import org.skfiy.typhon.domain.Lootable;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.util.FastRandom;

/**
 *
 * @author Kevin Zou <kveinz@skfiy.org>
 */
abstract class RandomAtlaslootBean {

    final Random RANDOM = new FastRandom();

    /**
     * 
     */
    abstract void prepare();
    
    /**
     *
     * @return
     */
    abstract AtlaslootBean calculate(Session session, Lootable lootable);

}
