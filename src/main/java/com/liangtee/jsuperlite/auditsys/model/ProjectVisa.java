package com.liangtee.jsuperlite.auditsys.model;

import com.liangtee.jsuperlite.auditsys.utils.TimeFormater;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Allen on 2017/8/12.
 */

@Entity
@Table(name = "T_PROJECT_VISA")
public class ProjectVisa {

    @Id
    @Column(name = "VISA_ID")
    private String visaID;

    @Column(name = "VISA_NAME", nullable = false)
    private String visaName;

    @Column(name = "PROJECT_ID", nullable = false)
    private String projectID;

    @Column(name = "CREATE_DATE", nullable = false)
    private String createDate;

    @Column(name = "CREATE_USER_ID", nullable = false)
    private long createUserID;

    @Column(name = "CREATE_USER_NAME", nullable = false)
    private String createUserName;

    @Column(name = "VISA_FOLDER_ID", nullable = false)
    private String visaFolderID;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinColumn(name = "VISA_ID")
    @Fetch(FetchMode.SUBSELECT)
    private List<ProjectVisaEntry> visaEntryList;

    public ProjectVisa() {
    }

    public ProjectVisa(String visaName, String projectID, String createDate, User user, String visaFolderID, List<ProjectVisaEntry> visaEntryList) {
        this.visaID = String.format("VISA%s", TimeFormater.format("yyyyMMddHHmmss"));
        this.visaName = visaName;
        this.projectID = projectID;
        this.createDate = createDate;
        this.createUserID = user.getUID();
        this.createUserName = user.getName();
        this.visaFolderID = visaFolderID;
        this.visaEntryList = visaEntryList;
    }

    public String getVisaID() {
        return visaID;
    }

    public void setVisaID(String visaID) {
        this.visaID = visaID;
    }

    public String getVisaName() {
        return visaName;
    }

    public void setVisaName(String visaName) {
        this.visaName = visaName;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public long getCreateUserID() {
        return createUserID;
    }

    public void setCreateUserID(long createUserID) {
        this.createUserID = createUserID;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getVisaFolderID() {
        return visaFolderID;
    }

    public void setVisaFolderID(String visaFolderID) {
        this.visaFolderID = visaFolderID;
    }

    public List<ProjectVisaEntry> getVisaEntryList() {
        return visaEntryList;
    }

    public void setVisaEntryList(List<ProjectVisaEntry> visaEntryList) {
        this.visaEntryList = visaEntryList;
    }
}
