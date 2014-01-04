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

import org.skfiy.typhon.annotation.Action;

/**
 * Typhon 常量属性定义.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public interface Constants {

    /**
     * 通过{@link #SCAN_ANNOTATION_PACKAGES}扫描指定的{@link Action }注解.
     * 通过{@link System#setProperty(java.lang.String, java.lang.String) }
     * 设置属性值通过","分隔多个不同的包名.
     */
    @Deprecated
    String SCAN_ANNOTATION_PACKAGES = "typhon.scan.annotationPackages";
    /**
     * 移除对指定类扫描{@link Action}注解.
     * 通过{@link System#setProperty(java.lang.String, java.lang.String)}
     * 设置属性值通过","分隔多个不同的包名.
     *
     * @see #SCAN_ANNOTATION_PACKAGES
     */
    @Deprecated
    String SCAN_EXECUTE_CLASSES = "typhon.scan.executeClasses";
    
    /**
     * 应用脚本目录.
     */
    String SCRIPTS_DIR = "typhon.scripts.dir";
    
    /**
     * 应用脚本编译文件输出目录.
     */
    String SCRIPTS_OUT_DIR = "typhon.scripts.out.dir";
    
    /**
     * 代理JAR路径.
     */
    String AGENT_JAR_PATH = "typhon.agent.jar.path";
    
    /**
     * 是否自动升级数据库.
     */
    String AUTO_UPGRADE_DATABASE = "typhon.auto.upgrade.database";
    
    /**
     * 数据库脚本目录.
     */
    String DATABASE_SCRIPTS_DIR = "typhon.database.scripts.dir";
    
    /**
     * 基础数据存放目录.
     */
    String COMPONENT_DATAS_DIR = "typhon.component.datas.dir";
}
