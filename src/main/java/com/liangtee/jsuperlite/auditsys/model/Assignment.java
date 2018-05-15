package com.liangtee.jsuperlite.auditsys.model;

import javax.persistence.*;

/**
 * Created by Allen on 2017/8/6.
 */

@Entity
@Table(name = "T_ASSIGNMENT")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private int id;

    @Column(name = "AUDIT_COMPANY_ID", nullable = false)
    private int auditCompanyId;

    @Column(name = "AUDIT_COMPANY_NAME", nullable = false)
    private String auditCompanyName;

    @Column(name = "PROJECT_ID", nullable = false)
    private String projectID;

    @Column(name = "PROJECT_NAME", nullable = false)
    private String projectName;

    @Column(name = "AUDIT_SCOPE_CODE", nullable = false)
    private int auditScopeCode;

    @Transient
    public static final int TOTAL_AUDIT = 1;

    @Transient
    public static final int CLOSING_AUDIT = 0;

    public Assignment() {
    }

    public Assignment(int auditCompanyId, String projectID, int auditScopeCode) {
        this.auditCompanyId = auditCompanyId;
        this.auditCompanyName = "";
        this.projectID = projectID;
        this.projectName = "";
        this.auditScopeCode = auditScopeCode;
    }

    public Assignment(int auditCompanyId, String auditCompanyName, String projectID, String projectName, int auditScopeCode) {
        this.auditCompanyId = auditCompanyId;
        this.auditCompanyName = auditCompanyName;
        this.projectID = projectID;
        this.projectName = projectName;
        this.auditScopeCode = auditScopeCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAuditCompanyId() {
        return auditCompanyId;
    }

    public void setAuditCompanyId(int auditCompanyId) {
        this.auditCompanyId = auditCompanyId;
    }

    public String getAuditCompanyName() {
        return auditCompanyName;
    }

    public void setAuditCompanyName(String auditCompanyName) {
        this.auditCompanyName = auditCompanyName;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getAuditScopeCode() {
        return auditScopeCode;
    }

    public void setAuditScopeCode(int auditScopeCode) {
        this.auditScopeCode = auditScopeCode;
    }
}
