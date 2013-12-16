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

/**
 * 系统部件接口定义.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public interface Component {

    /**
     * 初始化实例所需要的数据. 在调用实例的其它任何公共接口之前被调用.
     */
    void init();

    /**
     * 
     */
    void reload();
    
    /**
     * 销毁实例数据. 在销毁实例之前被调用.
     */
    void destroy();
    
    /**
     * 
     */
    enum Status {

        INITIALIZED, DESTROYED
    }
}
