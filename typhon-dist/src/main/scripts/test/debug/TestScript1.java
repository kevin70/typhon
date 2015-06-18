package test.debug;

import org.skfiy.typhon.script.Script;
import org.skfiy.typhon.session.Session;

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
/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class TestScript1 implements Script {

    @Override
    public Object invoke(Session session, Object obj) {
        session.setAttribute("TEST_KEY", -6947256868250810247L);
        return null;
    }
}
