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
package org.skfiy.typhon.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.skfiy.typhon.util.DomainUtils;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class Mail extends AbstractIndexable {

    // 邮件状态
    public static final int READ_STATE = 1;
    public static final int UNREAD_STATE = 2;

    // 邮件类型
    /**
     * 更新通知.
     */
    public static final int UPDATED_NOTICE_TYPE = 1;
    /**
     * 活动通知.
     */
    public static final int ACTIVITY_NOTICE_TYPE = 2;
    /**
     * 维护通知.
     */
    public static final int MAINTAIN_NOTICE_TYPE = 3;
    /**
     * 补偿通知.
     */
    public static final int REPARATION_NOTICE_TYPE = 4;
    /**
     * 奖励通知.
     */
    public static final int REWARD_NOTICE_TYPE = 5;
    /**
     * 工会boss奖励通知.
     */
    public static final int SOCIETY_REWARD_TYPE = 6;

    private String title;
    private String content;
    private String appendix;
    private int count = 1;
    private int state = UNREAD_STATE;
    private int type;
    private long creationTime = System.currentTimeMillis();
    private long expiredTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAppendix() {
        return appendix;
    }

    public void setAppendix(String appendix) {
        this.appendix = appendix;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        DomainUtils.firePropertyChange(this, "state", this.state);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(long expiredTime) {
        this.expiredTime = expiredTime;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("title", title);
        builder.append("content", content);
        builder.append("appendix", appendix);
        builder.append("count", count);
        builder.append("state", state);
        builder.append("type", type);
        builder.append("creationTime", creationTime);
        builder.append("expiredTime", expiredTime);
        // FIXME
        builder.append("index", index());
        return builder.toString();
    }

}
