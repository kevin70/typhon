package org.skfiy.typhon.spi.hero;

import java.util.List;

public class ExclusiveBuild {
    private String id;
    private String soulId;
    private List<ExclusiveBuildInformation> attribute;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSoulId() {
        return soulId;
    }

    public void setSoulId(String soulId) {
        this.soulId = soulId;
    }

    public List<ExclusiveBuildInformation> getAttribute() {
        return attribute;
    }

    public void setAttribute(List<ExclusiveBuildInformation> attribute) {
        this.attribute = attribute;
    }

}
