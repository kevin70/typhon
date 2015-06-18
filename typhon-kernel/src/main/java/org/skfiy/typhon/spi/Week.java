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
package org.skfiy.typhon.spi;

import java.util.Calendar;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public enum Week {

    /**
     * 星期天.
     */
    Sun,
    /**
     * 星期一.
     */
    Mon,
    /**
     * 星期二.
     */
    Tue,
    /**
     * 星期三.
     */
    Wed,
    /**
     * 星期四.
     */
    Thu,
    /**
     * 星期五.
     */
    Fri,
    /**
     * 星期六.
     */
    Sat;

    /**
     *
     * @param dayOfWeek
     * @return
     */
    public static Week valueOf(int dayOfWeek) {
        return values()[dayOfWeek - 1];
    }

    /**
     * 获取当前周的天数.
     * @return 
     */
    public static Week currentDayOfWeek() {
        Calendar c = Calendar.getInstance();
        return valueOf(c.get(Calendar.DAY_OF_WEEK));
    }
}
