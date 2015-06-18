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
package war;

import org.skfiy.typhon.domain.item.IFightItem;
import org.skfiy.typhon.script.Script;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.spi.war.Direction;
import org.skfiy.typhon.spi.war.FightObject;
import org.skfiy.typhon.spi.war.WarInfo;
import war.buf.FYuBufferSkill;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class BufferSkillScriptFactory implements Script {

    @Override
    public Object invoke(Session session, Object obj) {
        Object[] params = (Object[]) obj;
        if (params[3] == IFightItem.Shot.FYu) {
            return (new FYuBufferSkill((WarInfo) params[0], (Direction) params[1], (FightObject) params[2]));
        }

        // FIXME 待删除
        return null;
    }

}
