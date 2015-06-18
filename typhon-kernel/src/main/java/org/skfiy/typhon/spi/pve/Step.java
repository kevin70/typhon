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
package org.skfiy.typhon.spi.pve;

import com.alibaba.fastjson.annotation.JSONType;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@JSONType(ignores = {"parent", "members"})
public class Step {

    private String sid;
    private Member[] members;
    //
    private Part parent;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public Member[] getMembers() {
        return ArrayUtils.clone(members);
    }
    
    public Member getMember(int index) {
        return members[index];
    }

    public void setMembers(Member[] members) {
        this.members = members;
        
        for (Member member : members) {
            member.setParent(this);
        }
    }


    public Part getParent() {
        return parent;
    }

    public void setParent(Part parent) {
        this.parent = parent;
    }

}
