package com.xlk.takstarpaperlessmanage.model.bean;

/**
 * @author Created by xlk on 2021/5/25.
 * @desc
 */
public class ChatMessage {
    /**
     * =0收到的信息，=1自己发送的信息,=2新的未读消息提示
     */
    private int type;
    private String memberName;
    private int memberId;
    private long utcSecond;
    private String content;

    public ChatMessage(int type, String memberName, int memberId, long utcSecond, String content) {
        this.type = type;
        this.memberName = memberName;
        this.memberId = memberId;
        this.utcSecond = utcSecond;
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public long getUtcSecond() {
        return utcSecond;
    }

    public void setUtcSecond(long utcSecond) {
        this.utcSecond = utcSecond;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
