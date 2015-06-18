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
package item;

import org.skfiy.typhon.dobj.ComplexItemDobj;
import org.skfiy.typhon.domain.item.Subitem;
import org.skfiy.typhon.script.Script;
import org.skfiy.typhon.session.BagUtils;
import org.skfiy.typhon.session.Session;

/**
 *
 * @author Administrator
 */
public class ComplexItemScript implements Script {

    @Override
    public Object invoke(Session session, Object obj) {
        Object[] params = (Object[]) obj;
        ComplexItemDobj itemDobj = (ComplexItemDobj) params[0];
        int count = (int) params[1];
        for (Subitem subitem : itemDobj.getSubitems()) {
            BagUtils.intoItem(subitem.getItemDobj(), count * subitem.getCount());
        }
        return null;
    }

}
