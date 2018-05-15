package com.liangtee.jsuperlite.auditsys.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Author: LIANG Tianyi
 * Created by LIANG Tianyi on 2017/4/24.
 * All rights Reserved
 */

@Entity
@Table(name = "T_INTERNAL_MSG")
public class Message {

    @Id
    @Column(name = "ID")
    private String UUID;

    @Column(name = "CODE", nullable = false, unique = true)
    private long code;

    @Column(name = "TITLE", nullable = false)
    private String msgTitle;

    @Column(name = "CREATOR_ID", nullable = false)
    private String creatorID;

    @Column(name = "PUB_TIME", nullable = false)
    private String pubTime;

    @Column(name = "CONTENT", nullable = false)
    private String content;

    @Column(name = "RECEIVER_IDS", nullable = false)
    private String receiverIDs;

    //status = 1 means the MSG is visible. status = 0 means the MSG is invisible(deleted).
    @Column(name = "STATUS", nullable = false)
    private int status;

    public Message() {

    }

    public Message(String UUID, long code, String msgTitle, String creatorID, String pubTime, String content, String receiverIDs, int status) {
        this.UUID = UUID;
        this.code = code;
        this.msgTitle = msgTitle;
        this.creatorID = creatorID;
        this.pubTime = pubTime;
        this.content = content;
        this.receiverIDs = receiverIDs;
        this.status = status;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getMsgTitle() {
        return msgTitle;
    }

    public void setMsgTitle(String msgTitle) {
        this.msgTitle = msgTitle;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public String getPubTime() {
        return pubTime;
    }

    public void setPubTime(String pubTime) {
        this.pubTime = pubTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReceiverIDs() {
        return receiverIDs;
    }

    public void setReceiverIDs(String receiverIDs) {
        this.receiverIDs = receiverIDs;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
