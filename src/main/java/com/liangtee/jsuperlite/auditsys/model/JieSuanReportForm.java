/**
 * Copyright (C), 2018-2022, Allen LIANG
 * FileName: JieSuanReportForm
 * Author:   Allen
 * Date:     2018/7/31 22:05
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.liangtee.jsuperlite.auditsys.model;

import javax.persistence.*;

/**
 *
 * @author Allen
 * @create 2018/7/31
 * @since 0.0.1
 */

@Entity
@Table(name = "T_JIESUAN_REPORT_FORM")
public class JieSuanReportForm {

    @Id
    private String id;

    @Column(name = "PROJECT_ID", nullable = false)
    private String projectID;

    //报审部门
    @Column(nullable = false)
    private String bsbm;

    //报审金额
    @Column(nullable = false)
    private double bsje;

    //已付工程款
    @Column(nullable = false)
    private double yfje;

    @Column(nullable = false)
    private String time;

    //报审人
    @Column(nullable = false)
    private String submitter;

    @Column(name = "REPORT_FORM_PATH", nullable = true)
    private String reportFormPath;

    @Column(name = "FILE_ID", nullable = true)
    private String fileID;

    @Column(name = "IS_GRANTED", nullable = false)
    private int isGranted;

    @Transient
    public static final int GRANTED = 1;

    @Transient
    public static final int AUDITING = 0;

    @Transient
    public static final int NOT_GRANTED = -1;

    public JieSuanReportForm() {
    }

    public JieSuanReportForm(String id, String projectID, String bsbm, double bsje, double yfje, String time, String submitter) {
        this.id = id;
        this.projectID = projectID;
        this.bsbm = bsbm;
        this.bsje = bsje;
        this.yfje = yfje;
        this.time = time;
        this.submitter = submitter;
        this.isGranted = AUDITING;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getBsbm() {
        return bsbm;
    }

    public void setBsbm(String bsbm) {
        this.bsbm = bsbm;
    }

    public double getBsje() {
        return bsje;
    }

    public void setBsje(double bsje) {
        this.bsje = bsje;
    }

    public double getYfje() {
        return yfje;
    }

    public void setYfje(double yfje) {
        this.yfje = yfje;
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

    public String getReportFormPath() {
        return reportFormPath;
    }

    public void setReportFormPath(String reportFormPath) {
        this.reportFormPath = reportFormPath;
    }

    public int getIsGranted() {
        return isGranted;
    }

    public void setIsGranted(int isGranted) {
        this.isGranted = isGranted;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }
}