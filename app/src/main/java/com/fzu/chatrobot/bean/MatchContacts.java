package com.fzu.chatrobot.bean;

/**
 * 根据用户的输入匹配到的联系人实体类
 * Created by yury on 2016/8/28.
 */
public class MatchContacts implements Comparable<Object> {

    /** 联系人姓名 */
    private String displayName;

    /** 联系人手机号 */
    private String phoneNum;

    /** 匹配程度，如用户输入给我爸打个电话，电话簿里面正好有老爸这个联系人，那么"我爸"和"老爸"的匹配程度即为0.5 */
    private double matchDegree;

    public MatchContacts(String displayName, String phoneNum, double matchDegree) {
        this.displayName = displayName;
        this.phoneNum = phoneNum;
        this.matchDegree = matchDegree;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public double getMatchDegree() {
        return matchDegree;
    }

    public void setMatchDegree(double matchDegree) {
        this.matchDegree = matchDegree;
    }

    @Override
    public int compareTo(Object another) {
        if (this == another) {
            return 0;
        }

        if (another != null && another instanceof MatchContacts) {
            MatchContacts m = (MatchContacts) another;
            if (this.getMatchDegree() >= m.getMatchDegree()) {
                return -1;
            } else {
                return 1;
            }
        } else {
            return -1;
        }
    }

    @Override
    public int hashCode() {
        return this.displayName.hashCode() * 37 + this.phoneNum.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o != null && o instanceof MatchContacts) {
            MatchContacts m = (MatchContacts)o;
            return this.displayName.equals(m.displayName) && this.phoneNum.equals(m.phoneNum);
        } else {
            return false;
        }

    }
}
