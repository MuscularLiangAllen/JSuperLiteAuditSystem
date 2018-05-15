/**
 * Copyright (C), 2018-2022, Allen LIANG
 * FileName: HiddenProject
 * Author:   Allen
 * Date:     2018/5/15 10:35
 * Description: Hidden Project
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.liangtee.jsuperlite.auditsys.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

/**
 * 〈Hidden Project〉
 *
 * @author Allen
 * @create 2018/5/15
 * @since 0.0.1
 */

@Entity
@Table(name = "t_hidden_project")
public class HiddenProject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "construction_position", nullable = false)
    private String constructionPosition;

    @Column(name = "project_id", nullable = false)
    private String projectID;

    @Column(name = "start_date", nullable = false)
    private String startDate;

    @Column(name = "create_user_id", nullable = false)
    private long createUserID;

    @Column(name = "create_user_name", nullable = false)
    private String createUserName;

    @Column(name = "hidden_project_folder_id", nullable = false)
    private String hiddenProjectFolderID;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinColumn(name = "hidden_project_id")
    @Fetch(FetchMode.SUBSELECT)
    private List<HiddenProjectEntry> hiddenProjectEntryList;

    public HiddenProject() {
    }

    public HiddenProject(String constructionPosition, String projectID, String startDate, long createUserID, String createUserName, String hiddenProjectFolderID, List<HiddenProjectEntry> hiddenProjectEntryList) {
        this.constructionPosition = constructionPosition;
        this.projectID = projectID;
        this.startDate = startDate;
        this.createUserID = createUserID;
        this.createUserName = createUserName;
        this.hiddenProjectFolderID = hiddenProjectFolderID;
        this.hiddenProjectEntryList = hiddenProjectEntryList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getConstructionPosition() {
        return constructionPosition;
    }

    public void setConstructionPosition(String constructionPosition) {
        this.constructionPosition = constructionPosition;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
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

    public String getHiddenProjectFolderID() {
        return hiddenProjectFolderID;
    }

    public void setHiddenProjectFolderID(String hiddenProjectFolderID) {
        this.hiddenProjectFolderID = hiddenProjectFolderID;
    }

    public List<HiddenProjectEntry> getHiddenProjectEntryList() {
        return hiddenProjectEntryList;
    }

    public void setHiddenProjectEntryList(List<HiddenProjectEntry> hiddenProjectEntryList) {
        this.hiddenProjectEntryList = hiddenProjectEntryList;
    }
}