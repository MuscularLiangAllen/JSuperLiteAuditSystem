package com.liangtee.jsuperlite.auditsys.model;

import com.liangtee.jsuperlite.auditsys.utils.TimeFormater;

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
@Table(name = "T_PROJECT_INFO")
//@Document(indexName = "jsuperliteauditsys",type = "projectinfo")
public class Project {

    @Id
//    @org.springframework.data.annotation.Id
    @Column(name = "ID")
    private String ID;      //ID template: XM20170721164011

    @Column(name = "PROJECT_NAME", nullable = false)
//    @Field
    private String projectName;

    @Column(name = "PROJECT_LOC", nullable = false)
//    @Field
    private String projectLoc;

    @Column(name = "EXPECTED_START_DATE", nullable = false)
//    @Field
    private String expectedStartDate;

    @Column(name = "EXPECTED_END_DATE", nullable = false)
//    @Field
    private String expectedEndDate;

    @Column(name = "START_TIME", nullable = false)
//    @Field
    private String startDate;

    @Column(name = "END_TIME", nullable = false)
//    @Field
    private String endDate;

    @Column(name = "DURATION", nullable = false)
    private int projectDuration;

    /**
     * 招标形式
     *
     */
    @Column(name = "BID_TYPE", nullable = false)
    private String bidType;

    @Column(name = "CTRL_PRICE", nullable = false)
    private double ctrlPrice;

    /**
     * 合同形式
     *
     */
    @Column(name = "CONTRACT_TYPE", nullable = false)
    private String contractType;

    @Column(name = "CONTRACT_PRICE", nullable = false)
    private double contractPrice;

    @Column(name = "LEADING_ORG", nullable = false)
    private String leadingOrg;

    @Column(name = "LEADING_ORG_ID", nullable = false)
    private String leadingOrgIDs;

    @Column(name = "PROJECT_LEADER_NAME", nullable = false)
    private String projectLeaderName;

    @Column(name = "PROJECT_LEADER_TEL", nullable = false)
    private String projectLeaderTel;

    @Column(name = "PROJECT_CONTRACTOR_NAME", nullable = false)
    private String projectContractorName;

    @Column(name = "CONTRACTOR_MANAGER_NAME", nullable = false)
    private String contractorManagerName;

    @Column(name = "CONTRACTOR_MANAGER_TEL", nullable = false)
    private String contractorManagerTel;

    @Column(name = "SUPERVISION_COMPANY_NAME", nullable = false)
    private String supervisionName;

    @Column(name = "SUPERVISOR_NAME", nullable = false)
    private String supervisorName;

    @Column(name = "SUPERVISOR_TEL", nullable = false)
    private String supervisorTel;

    @Column(name = "PROJECT_OBJECTIVES", nullable = false)
    private String projectObjectives;

    @Column(name = "PROJECT_MAIN_CONTENT", nullable = false)
    private String projectMainContent;

    @Column(name = "MATERIAL_LIST")
    private String materialList;

    public Project(){}

    public Project(String projectName, String projectLoc, String expectedStartDate, String expectedEndDate, String startDate, String endDate, int projectDuration, String bidType, double ctrlPrice, String contractType, double contractPrice, String projectLeaderName, String projectLeaderTel, String projectContractorName, String contractorManagerName, String contractorManagerTel, String supervisionName, String supervisorName, String supervisorTel, String projectObjectives, String projectMainContent, String leadingOrg, String leadingOrgIDs) {
        this.ID = String.format("XM%s", TimeFormater.format("yyyyMMddHHmmss"));
        this.projectName = projectName;
        this.projectLoc = projectLoc;
        this.expectedStartDate = expectedStartDate;
        this.expectedEndDate = expectedEndDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectDuration = projectDuration;
        this.bidType = bidType;
        this.ctrlPrice = ctrlPrice;
        this.contractType = contractType;
        this.contractPrice = contractPrice;
        this.projectLeaderName = projectLeaderName;
        this.projectLeaderTel = projectLeaderTel;
        this.projectContractorName = projectContractorName;
        this.contractorManagerName = contractorManagerName;
        this.contractorManagerTel = contractorManagerTel;
        this.supervisionName = supervisionName;
        this.supervisorName = supervisorName;
        this.supervisorTel = supervisorTel;
        this.projectObjectives = projectObjectives;
        this.projectMainContent = projectMainContent;
        this.leadingOrg = leadingOrg;
        this.leadingOrgIDs = leadingOrgIDs;
    }

    public Project(String projectID, String projectName, String projectLoc, String expectedStartDate, String expectedEndDate, String startDate, String endDate, int projectDuration, String bidType, double ctrlPrice, String contractType, double contractPrice, String projectLeaderName, String projectLeaderTel, String projectContractorName, String contractorManagerName, String contractorManagerTel, String supervisionName, String supervisorName, String supervisorTel, String projectObjectives, String projectMainContent, String leadingOrg, String leadingOrgIDs) {
        this.ID = projectID;
        this.projectName = projectName;
        this.projectLoc = projectLoc;
        this.expectedStartDate = expectedStartDate;
        this.expectedEndDate = expectedEndDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectDuration = projectDuration;
        this.bidType = bidType;
        this.ctrlPrice = ctrlPrice;
        this.contractType = contractType;
        this.contractPrice = contractPrice;
        this.projectLeaderName = projectLeaderName;
        this.projectLeaderTel = projectLeaderTel;
        this.projectContractorName = projectContractorName;
        this.contractorManagerName = contractorManagerName;
        this.contractorManagerTel = contractorManagerTel;
        this.supervisionName = supervisionName;
        this.supervisorName = supervisorName;
        this.supervisorTel = supervisorTel;
        this.projectObjectives = projectObjectives;
        this.projectMainContent = projectMainContent;
        this.leadingOrg = leadingOrg;
        this.leadingOrgIDs = leadingOrgIDs;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectLoc() {
        return projectLoc;
    }

    public void setProjectLoc(String projectLoc) {
        this.projectLoc = projectLoc;
    }

    public String getExpectedStartDate() {
        return expectedStartDate;
    }

    public void setExpetedStartDate(String expetedStartDate) {
        this.expectedStartDate = expetedStartDate;
    }

    public String getExpectedEndDate() {
        return expectedEndDate;
    }

    public void setExpectedEndDate(String expectedEndDate) {
        this.expectedEndDate = expectedEndDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getProjectDuration() {
        return projectDuration;
    }

    public void setProjectDuration(int projectDuration) {
        this.projectDuration = projectDuration;
    }

    public String getBidType() {
        return bidType;
    }

    public void setBidType(String bidType) {
        this.bidType = bidType;
    }

    public double getCtrlPrice() {
        return ctrlPrice;
    }

    public void setCtrlPrice(double ctrlPrice) {
        this.ctrlPrice = ctrlPrice;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public double getContractPrice() {
        return contractPrice;
    }

    public void setContractPrice(double contractPrice) {
        this.contractPrice = contractPrice;
    }

    public String getProjectLeaderName() {
        return projectLeaderName;
    }

    public void setProjectLeaderName(String projectLeaderName) {
        this.projectLeaderName = projectLeaderName;
    }

    public String getProjectLeaderTel() {
        return projectLeaderTel;
    }

    public void setProjectLeaderTel(String projectLeaderTel) {
        this.projectLeaderTel = projectLeaderTel;
    }

    public String getProjectContractorName() {
        return projectContractorName;
    }

    public void setProjectContractorName(String projectContractorName) {
        this.projectContractorName = projectContractorName;
    }

    public String getContractorManagerName() {
        return contractorManagerName;
    }

    public void setContractorManagerName(String contractorManagerName) {
        this.contractorManagerName = contractorManagerName;
    }

    public String getContractorManagerTel() {
        return contractorManagerTel;
    }

    public void setContractorManagerTel(String contractorManagerTel) {
        this.contractorManagerTel = contractorManagerTel;
    }

    public String getSupervisionName() {
        return supervisionName;
    }

    public void setSupervisionName(String supervisionName) {
        this.supervisionName = supervisionName;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public String getSupervisorTel() {
        return supervisorTel;
    }

    public void setSupervisorTel(String supervisorTel) {
        this.supervisorTel = supervisorTel;
    }

    public String getProjectObjectives() {
        return projectObjectives;
    }

    public void setProjectObjectives(String projectObjectives) {
        this.projectObjectives = projectObjectives;
    }

    public String getProjectMainContent() {
        return projectMainContent;
    }

    public void setProjectMainContent(String projectMainContent) {
        this.projectMainContent = projectMainContent;
    }

    public void setExpectedStartDate(String expectedStartDate) {
        this.expectedStartDate = expectedStartDate;
    }

    public String getLeadingOrg() {
        return leadingOrg;
    }

    public void setLeadingOrg(String leadingOrg) {
        this.leadingOrg = leadingOrg;
    }

    public String getMaterialList() {
        return materialList;
    }

    public void setMaterialList(String materialList) {
        this.materialList = materialList;
    }

    public String getLeadingOrgIDs() {
        return leadingOrgIDs;
    }

    public void setLeadingOrgIDs(String leadingOrgIDs) {
        this.leadingOrgIDs = leadingOrgIDs;
    }
}
