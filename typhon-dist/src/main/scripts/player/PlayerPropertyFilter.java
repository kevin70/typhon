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
package player;

import com.alibaba.fastjson.serializer.PropertyFilter;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.script.Script;
import org.skfiy.typhon.session.Session;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class PlayerPropertyFilter implements Script, PropertyFilter {

    @Override
    public boolean apply(Object source, String name, Object value) {
        if (source instanceof Normal) {
            if ("level".equals(name)) {
                return false;
            } else if ("diamond".equals(name)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object invoke(Session session, Object obj) {
        return this;
    }

}
