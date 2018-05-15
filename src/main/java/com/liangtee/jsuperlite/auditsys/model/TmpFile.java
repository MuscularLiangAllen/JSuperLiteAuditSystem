package com.liangtee.jsuperlite.auditsys.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Author: LIANG Tianyi
 * Created by LIANG Tianyi on 2017/6/23.
 * All rights Reserved
 */

@Entity
@Table(name = "T_TMP_FILE")
public class TmpFile {

    @Id
    @Column(name = "CODE")
    private String code;

    @Column(name = "FILE_NAME", nullable = false)
    private String fileName;

    @Column(name = "FILE_TYPE", nullable = false)
    private String fileType;

    @Column(name = "FILE_SIZE", nullable = false)
    private long fileSize;

    @Column(name = "FILE_PATH", nullable = false)
    private String filePath;

    public TmpFile() {}

    public TmpFile(String code, String fileName, String fileType, long fileSize, String filePath) {
        this.code = code;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.filePath = filePath;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
