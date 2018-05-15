package com.liangtee.jsuperlite.auditsys.model;

import javax.persistence.*;

/**
 * Created by liangtee on 2017/6/15.
 */

@Entity
@Table(name = "T_FILE_TO_USER")
public class FileToUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private int ID;

    @Column(name = "FILE_ID", nullable = false)
    private String fileID;

    @Column(name = "USER_ID", nullable = false)
    private  long userID;

    public FileToUser() {}

    public FileToUser(String fileID, long userID) {
        this.fileID = fileID;
        this.userID = userID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }
}
