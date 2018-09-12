/**
 * Copyright (C), 2018-2022, Allen LIANG
 * FileName: ShenJianReasonFile
 * Author:   Allen
 * Date:     2018/9/8 19:30
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.liangtee.jsuperlite.auditsys.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 〈〉
 *
 * @author Allen
 * @create 2018/9/8
 * @since 0.0.1
 */

@Entity
@Table(name = "T_SHENJIAN_REASON_FILE")
public class ShenJianReasonFile {

    @Id
    private String id;

    @Column(name = "BELONG_TO_PROJECT_ID", nullable = false)
    private String belongToProjectID;

    @Column(name = "FILE_ID", nullable = false)
    private String fileID;

    private String time;

    private String submitter;

    public ShenJianReasonFile() {
    }

    public ShenJianReasonFile(String id, String belongToProjectID, String fileID, String time, String submitter) {
        this.id = id;
        this.belongToProjectID = belongToProjectID;
        this.fileID = fileID;
        this.time = time;
        this.submitter = submitter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBelongToProjectID() {
        return belongToProjectID;
    }

    public void setBelongToProjectID(String belongToProjectID) {
        this.belongToProjectID = belongToProjectID;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSubmitter() {
        return submitter;
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }
}