package com.liangtee.jsuperlite.auditsys.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.liangtee.jsuperlite.auditsys.utils.TimeFormater;

import javax.persistence.*;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * Author: LIANG Tianyi
 * Created by LIANG Tianyi on 2017/5/29.
 * All rights Reserved
 */

@Entity
@Table(name = "T_FILE_INFO")
//@Document(indexName = "jsuperliteauditsys", type = "fileinfo")
public class FileInfo {

    @Id
    @Column(name = "ID")
    private String UUID;

    @JSONField(name = "id")
    @Transient
    private int seq;

    @JSONField(name = "pid")
    @Transient
    private int pSeq;

    @Column(name = "FILE_NAME", nullable = false)
//    @Field
    private String fileName;

    @Column(name = "FILE_TYPE", nullable = false)
    private String fileType;

    @Column(name = "FILE_SIZE", nullable = false)
    private long fileSize;

    @Column(name = "FILE_DESC", nullable = false)
//    @Field
    private String fileDesc;

    @Column(name = "FILE_SUBMITTER_ID", nullable = false)
    private long submitterID;

    @Column(name = "FILE_SUBMITTER_NAME", nullable = false)
    private String submitterName;

    @Column(name = "SUBMIT_DATE", nullable = false)
//    @Field
    private String subDate;

    @Column(name = "BELONG_TO_PROJECT")
    private String belongToProjectID;

    @Column(name = "FILE_PATH", nullable = false)
    private String filePath;

    @Column(name = "IS_FOLDER", nullable = false)
    private int isFolder;

    @Column(name = "PARENT_FOLDER_ID", nullable = false)
//    @JSONField(name = "pid")
    private String parentFolder;

    @Transient
    private int childQty = 0;

    @Column(name = "EDITABLE", nullable = false)
    private int editable = EDITABLE;

    @Transient
    public static final int FOLDER_TYPE = 1;

    @Transient
    public static final int FILE_TYPE = 0;

    @Transient
    public static final String NO_PARENT_FOLDER = "NPF";

    @Transient
    public static final int NON_EDITABLE = 0;

    @Transient
    public static final int EDITABLE = 1;

    public FileInfo() {
    }

    public FileInfo(String fileName, String fileType, long fileSize, String fileDesc, long submitterID,
                    String submitterName, String belongToProjectID, String filePath, int isFolder, String parentFolder, int editable) {
        this.UUID = java.util.UUID.randomUUID().toString().replace("-", "");
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.fileDesc = fileDesc;
        this.submitterID = submitterID;
        this.submitterName = submitterName;
        this.belongToProjectID = belongToProjectID;
        this.filePath = filePath;
        this.subDate = TimeFormater.format("yyyy/MM/dd HH:mm:ss");
        this.isFolder = isFolder;
        this.parentFolder = parentFolder;
        this.editable = editable;
    }

    public FileInfo(String fileID, String fileName, String fileType, long fileSize, String fileDesc, long submitterID,
                    String submitterName, String belongToProjectID, String filePath, int isFolder, String parentFolder) {
        this.UUID = fileID;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.fileDesc = fileDesc;
        this.submitterID = submitterID;
        this.submitterName = submitterName;
        this.belongToProjectID = belongToProjectID;
        this.filePath = filePath;
        this.subDate = TimeFormater.format("yyyy/MM/dd HH:mm:ss");
        this.isFolder = isFolder;
        this.parentFolder = parentFolder;

    }

    public FileInfo(String fileName, String fileType, long fileSize, String fileDesc, long submitterID,
                    String submitterName, String belongToProjectID, String filePath, int isFolder, String parentFolder) {
        this.UUID = java.util.UUID.randomUUID().toString();
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.fileDesc = fileDesc;
        this.submitterID = submitterID;
        this.submitterName = submitterName;
        this.belongToProjectID = belongToProjectID;
        this.filePath = filePath;
        this.subDate = TimeFormater.format("yyyy/MM/dd HH:mm:ss");
        this.isFolder = isFolder;
        this.parentFolder = parentFolder;

    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileDesc() {
        return fileDesc;
    }

    public void setFileDesc(String fileDesc) {
        this.fileDesc = fileDesc;
    }

    public long getSubmitterID() {
        return submitterID;
    }

    public void setSubmitterID(long submitterID) {
        this.submitterID = submitterID;
    }

    public String getSubmitterName() {
        return submitterName;
    }

    public void setSubmitterName(String submitterName) {
        this.submitterName = submitterName;
    }

    public String getSubDate() {
        return subDate;
    }

    public void setSubDate(String subDate) {
        this.subDate = subDate;
    }

    public String getBelongToProjectID() {
        return belongToProjectID;
    }

    public void setBelongToProjectID(String belongToProjectID) {
        this.belongToProjectID = belongToProjectID;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getIsFolder() {
        return isFolder;
    }

    public void setIsFolder(int isFolder) {
        this.isFolder = isFolder;
    }

    public String getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(String parentFolder) {
        this.parentFolder = parentFolder;
    }

    public int getEditable() {
        return editable;
    }

    public void setEditable(int editable) {
        this.editable = editable;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getpSeq() {
        return pSeq;
    }

    public void setpSeq(int pSeq) {
        this.pSeq = pSeq;
    }

    public int getChildQty() {
        return childQty;
    }

    public void setChildQty(int childQty) {
        this.childQty = childQty;
    }
}
