package org.skfiy.typhon.domain;

import java.util.ArrayList;
import java.util.List;

public class HeroAttribute extends HeroProperty{

    private List<HeroProperty> list=new ArrayList<>();

    public List<HeroProperty> getList() {
        return list;
    }

    public void setList(List<HeroProperty> list) {
        this.list = list;
    }
    
    public void addList(HeroProperty heroproperty) {
        this.list.add(heroproperty);
    }
    
}
