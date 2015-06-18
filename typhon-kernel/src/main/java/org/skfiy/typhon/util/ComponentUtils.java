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
package org.skfiy.typhon.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.skfiy.typhon.ComponentException;
import org.skfiy.typhon.Constants;
import org.skfiy.util.StreamUtils;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class ComponentUtils {

    /**
     * 
     * @param pathname
     * @return 
     */
    public static String readDataFile(String pathname) {
        File file = new File(System.getProperty(Constants.COMPONENT_DATAS_DIR), pathname);
        return readDataFile(file);
    }
    
    /**
     * 
     * @param file
     * @return 
     */
    public static String readDataFile(File file) {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            String text = StreamUtils.copyToString(in, StandardCharsets.UTF_8);
            return text;
        } catch (IOException ex) {
            throw new ComponentException(ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
            }
        }
    }
    
}
