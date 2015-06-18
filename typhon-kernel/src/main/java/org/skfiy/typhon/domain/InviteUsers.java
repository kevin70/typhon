package org.skfiy.typhon.domain;

public class InviteUsers {

    private String avatar;
    private String avatarBorder;
    private String userName;
    private int level;
    private int uid;

     public InviteUsers() {}

    public InviteUsers(String avatar, String avatarBorder, String userName, int level, int uid) {
        this.avatar = avatar;
        this.avatarBorder = avatarBorder;
        this.level = level;
        this.userName = userName;
        this.uid = uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatarBorder() {
        return avatarBorder;
    }

    public void setAvatarBorder(String avatarBorder) {
        this.avatarBorder = avatarBorder;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

}
