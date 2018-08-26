/**
 * Copyright (C), 2018-2022, Allen LIANG
 * FileName: JieSuanAuditResult
 * Author:   Allen
 * Date:     2018/8/17 22:50
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.liangtee.jsuperlite.auditsys.model;

import javax.persistence.*;

/**
 * 〈〉
 *
 * @author Allen
 * @create 2018/8/17
 * @since 0.0.1
 */

@Entity
@Table(name = "T_JIESUAN_AUDIT_RESULT")
public class JieSuanAuditResult {

    @Id
    private String id;

    @Column(name = "PROJECT_ID", nullable = false)
    private String projectID;

    @Column(name = "IS_GRANTED", nullable = false)
    private int isGranted;

    @Column(name = "SHENDING_VAL", nullable = true)
    private double shenDing_val;

    @Column(name = "SHENJIAN_VAL", nullable = true)
    private double shenJian_val;

    @Column(name = "REASONS", nullable = true, columnDefinition = "text")
    private String reasons;

    @Column(name = "TIME", nullable = false)
    private String time;

    @Column(name = "SHENJI_COMPANY", nullable = false)
    private String shenjiCompany;

    @Transient
    public static final int GRANTED = 1;

    @Transient
    public static final int NOT_GRANTED = -1;

    public JieSuanAuditResult() {
    }

    public JieSuanAuditResult(String id, String projectID, int isGranted, double shenDing_val, double shenJian_val, String reasons, String time, String shenjiCompany) {
        this.id = id;
        this.projectID = projectID;
        this.isGranted = isGranted;
        this.shenDing_val = shenDing_val;
        this.shenJian_val = shenJian_val;
        this.reasons = reasons;
        this.time = time;
        this.shenjiCompany = shenjiCompany;
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

    public double getShenDing_val() {
        return shenDing_val;
    }

    public void setShenDing_val(double shenDing_val) {
        this.shenDing_val = shenDing_val;
    }

    public double getShenJian_val() {
        return shenJian_val;
    }

    public void setShenJian_val(double shenJian_val) {
        this.shenJian_val = shenJian_val;
    }

    public String getReasons() {
        return reasons;
    }

    public void setReasons(String reasons) {
        this.reasons = reasons;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getShenjiCompany() {
        return shenjiCompany;
    }

    public void setShenjiCompany(String shenjiCompany) {
        this.shenjiCompany = shenjiCompany;
    }
}