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
package org.skfiy.typhon;

import com.alibaba.fastjson.parser.ParserConfig;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.com>
 */
public final class Globals {

    /**
     * 'typhon.devMode' in system prperties.
     */
    public static final String PROP_DEV_MODE = "typhon.devMode";
    
    /**
     * 'typhon.version' in system prperties.
     */
    public static final String PROP_VERSION = "typhon.version";
    
    /**
     * Typhon默认MBean Domain.
     */
    public static final String DEFAULT_MBEAN_DOMAIN = "org.skfiy.typhon";
    
    /**
     * 打印日志到控制台的名称.
     * <pre>
     * e.g.
     * Logger LOG = LoggerFactory.getLogger({@link #CONSOLE_LOG_NAME});
     * </pre>
     */
    public static final String CONSOLE_LOG_NAME = "console.out";
    
    /**
     * 关闭Asm解析的Json Parse Config.
     */
    public static final ParserConfig NO_ENABLED_ASM_PARSE_CONFIG;
    
    static {
        NO_ENABLED_ASM_PARSE_CONFIG = new ParserConfig();
        NO_ENABLED_ASM_PARSE_CONFIG.setAsmEnable(false);
    }
}
