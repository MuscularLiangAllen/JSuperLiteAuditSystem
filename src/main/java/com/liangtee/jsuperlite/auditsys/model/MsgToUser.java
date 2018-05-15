package com.liangtee.jsuperlite.auditsys.model;

import javax.persistence.*;

/**
 * Author: LIANG Tianyi
 * Created by LIANG Tianyi on 2017/4/24.
 * All rights Reserved
 */

@Entity
@Table(name = "T_MSG_TO_USER")
public class MsgToUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private int ID;

    @Column(name = "MSG_CODE", nullable = false)
    private long msgCode;

    @Column(name = "SENT_USER_ID", nullable = false)
    private int sentUID;

    @Column(name = "RECV_USER_ID", nullable = false)
    private int recvUID;

    //status = 0 means the MSG has not been read
    //status = 1 means the MSG has been read
    @Column(name = "STATUS", nullable = false)
    private int status;

    public MsgToUser() {

    }

    public MsgToUser(long msgCode, int sentUID, int recvUID, int status) {
        this.msgCode = msgCode;
        this.sentUID = sentUID;
        this.recvUID = recvUID;
        this.status = status;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public long getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(long msgCode) {
        this.msgCode = msgCode;
    }

    public int getSentUID() {
        return sentUID;
    }

    public void setSentUID(int sentUID) {
        this.sentUID = sentUID;
    }

    public int getRecvUID() {
        return recvUID;
    }

    public void setRecvUID(int recvUID) {
        this.recvUID = recvUID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
