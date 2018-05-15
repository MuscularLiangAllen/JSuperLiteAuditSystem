package com.liangtee.jsuperlite.auditsys.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.liangtee.jsuperlite.auditsys.utils.TimeFormater;
import com.liangtee.jsuperlite.auditsys.values.OrgConfs;

import javax.persistence.*;

/**
 * Author: LIANG Tianyi
 * Created by LIANG Tianyi on 2017/4/25.
 * All rights Reserved
 */

@Entity
@Table(name = "T_ORGANIZATION_UNIT")
public class Organization {

    @JSONField(name="id")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private int ID;

    @JSONField(name="pid")
    @Column(name = "BELONG_TO_ID")
    private int belongTo;

    @JSONField(serialize=false)
    @Column(name = "HIERACHICAL_LEVEL", nullable = false)
    private int hierarchicalLevel;

    @JSONField(name="name")
    @Column(name = "ORG_NAME", nullable = false)
    private String orgName;

    @Column(name = "ORG_TYPE_CODE", nullable = false)
    private int orgTypeCode;

    @JSONField(serialize=false)
    @Column(name = "ORG_TYPE", nullable = false)
    private String orgType;

    @Column(name = "ORG_DESC")
    private String orgDesc;

    @Column(name = "ORG_LOCATION")
    private String location;

    @Column(name = "ORG_TEL")
    private String tel;

    @JSONField(serialize=false)
    @Column(name = "ORG_CREATE_TIME")
    private String createTime;

    @JSONField(serialize=false)
    @Column(name = "IS_HIDDEN")
    private int isHidden;

    public Organization() {

    }

    public Organization(int orgID, int belongTo, int hierarchicalLevel, String orgName, int orgTypeCode, String tel) {
        this.ID = orgID;
        this.belongTo = belongTo;
        this.hierarchicalLevel = hierarchicalLevel;
        this.orgName = orgName;
        this.orgTypeCode = orgTypeCode;
        this.orgType = OrgConfs.getTypeDesc(orgTypeCode);
        this.tel = tel;
        this.createTime = TimeFormater.format("yyyy/MM/dd");
        this.isHidden = 0;
    }

    public Organization(int belongTo, int hierarchicalLevel, String orgName, int orgTypeCode, String tel) {
        this.belongTo = belongTo;
        this.hierarchicalLevel = hierarchicalLevel;
        this.orgName = orgName;
        this.orgTypeCode = orgTypeCode;
        this.orgType = OrgConfs.getTypeDesc(orgTypeCode);
        this.tel = tel;
        this.createTime = TimeFormater.format("yyyy/MM/dd");
        this.isHidden = 0;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getBelongTo() {
        return belongTo;
    }

    public void setBelongTo(int belongTo) {
        this.belongTo = belongTo;
    }

    public int getHierarchicalLevel() {
        return hierarchicalLevel;
    }

    public void setHierarchicalLevel(int hierarchicalLevel) {
        this.hierarchicalLevel = hierarchicalLevel;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgDesc() {
        return orgDesc;
    }

    public void setOrgDesc(String orgDesc) {
        this.orgDesc = orgDesc;
    }

    public int getOrgTypeCode() {
        return orgTypeCode;
    }

    public void setOrgTypeCode(int orgTypeCode) {
        this.orgTypeCode = orgTypeCode;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(int isHidden) {
        this.isHidden = isHidden;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Organization that = (Organization) o;

        return ID == that.ID;
    }

    @Override
    public int hashCode() {
        return ID;
    }
}
