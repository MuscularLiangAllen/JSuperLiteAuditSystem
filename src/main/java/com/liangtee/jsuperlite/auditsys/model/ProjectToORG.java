package com.liangtee.jsuperlite.auditsys.model;

import javax.persistence.*;

/**
 * Created by Allen on 2017/7/22.
 */

@Entity
@Table(name = "T_PROJECT_TO_ORG")
public class ProjectToORG {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private int ID;

    @Column(name = "PROJECT_ID", nullable = false)
    private String projectID;

    @Column(name = "ORG_ID", nullable = false)
    private int orgID;

    public ProjectToORG() {
    }

    public ProjectToORG(String projectID, int orgID) {
        this.projectID = projectID;
        this.orgID = orgID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public int getOrgID() {
        return orgID;
    }

    public void setOrgID(int orgID) {
        this.orgID = orgID;
    }
}
