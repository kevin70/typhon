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
package org.skfiy.typhon.domain;

import org.skfiy.typhon.dobj.SimpleItemDobj;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class BagTest {

    @Test
    public void execute() {
        Bag big = new Bag();

        SimpleItemDobj si1 = new SimpleItemDobj();
        si1.setId("a001");
        si1.setAutoOpen(false);
        si1.setOverlapping(1);
        si1.setPrice(1000);
        
        big.intoItem(si1);
        big.intoItem(si1);
        
        //
        big.swap(2, 5);
        
        big.intoItem(si1);
        Assert.assertEquals(big.size(), 3);
    }

}
