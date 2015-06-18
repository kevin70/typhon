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
package org.skfiy.typhon.script;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import javax.inject.Inject;
import org.skfiy.typhon.Constants;
import org.skfiy.typhon.TestBase;
import org.skfiy.typhon.TestSession;
import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.session.Session;
import org.skfiy.util.StreamUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class DebugScriptManagerTest extends TestBase {

    private static final String TEST_SCRIPT1_TEMPLATE = "package test.debug;\n"
            + "\n"
            + "import org.skfiy.typhon.script.Script;\n"
            + "import org.skfiy.typhon.session.Session;\n"
            + "\n"
            + "/*\n"
            + " * Copyright 2013 The Skfiy Open Association.\n"
            + " *\n"
            + " * Licensed under the Apache License, Version 2.0 (the \"License\");\n"
            + " * you may not use this file except in compliance with the License.\n"
            + " * You may obtain a copy of the License at\n"
            + " *\n"
            + " *      http://www.apache.org/licenses/LICENSE-2.0\n"
            + " *\n"
            + " * Unless required by applicable law or agreed to in writing, software\n"
            + " * distributed under the License is distributed on an \"AS IS\" BASIS,\n"
            + " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n"
            + " * See the License for the specific language governing permissions and\n"
            + " * limitations under the License.\n"
            + " */\n"
            + "/**\n"
            + " *\n"
            + " * @author Kevin Zou <kevinz@skfiy.org>\n"
            + " */\n"
            + "public class TestScript1 implements Script {\n"
            + "\n"
            + "    @Override\n"
            + "    public Object invoke(Session session, Object obj) {\n"
            + "        session.setAttribute(\"TEST_KEY\", ${DYNAMIC_VALUE});\n"
            + "        return null;\n"
            + "    }\n"
            + "}\n";
    
    @Inject
    private ScriptManager scriptManager;

//    @BeforeClass
//    public void setup() {
//        debugScriptManager = new DebugScriptManager();
//        debugScriptManager.init();
//    }

    @Test
    public void reload() {
        scriptManager.reload();
//        UUID uuid = UUID.randomUUID();
//        byte[] outBytes = TEST_SCRIPT1_TEMPLATE.replace("${DYNAMIC_VALUE}",
//                uuid.getLeastSignificantBits() + "L")
//                .getBytes(StandardCharsets.UTF_8);
//        
//        try {
//            File file = new File(Typhons.getProperty(Constants.SCRIPTS_DIR), "test/debug/TestScript1.java");
//            OutputStream out = new FileOutputStream(file);
//            StreamUtils.copy(outBytes, out);
//        } catch (IOException ex) {
//            Assert.fail("生成[TestScript1.java]文件失败", ex);
//        }
//        
//        scriptManager.reload();
//        
//        Script script = scriptManager.getScript("test.debug.TestScript1");
//        Session session = new TestSession();
//        script.invoke(session, null);
//        
//        Assert.assertEquals(session.getAttribute("TEST_KEY"), uuid.getLeastSignificantBits());
    }

//    @AfterClass
//    public void terndown() {
//        debugScriptManager.destroy();
//    }
}
