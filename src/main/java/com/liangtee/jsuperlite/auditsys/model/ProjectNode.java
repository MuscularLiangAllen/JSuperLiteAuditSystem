package com.liangtee.jsuperlite.auditsys.model;

import javax.persistence.*;

/**
 * Created by Allen on 2018/2/21.
 */

@Entity
@Table(name = "T_PROJECT_NODE")
public class ProjectNode implements Comparable<ProjectNode> {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "BELONG_TO", nullable = false)
    private String belongTo;

    @Column(name = "SEQ", nullable = false)
    private int seq;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "START_DATE", nullable = false)
    private String startDate;

    @Column(name = "END_DATE", nullable = false)
    private String endDate;

    @Column(name = "NODE_DESC", nullable = false)
    private String nodeDesc;

    @Column(name = "PHOTOS", nullable = true)
    private String photos;

    @Column(name = "NODE_FOLDER_ID", nullable = true, unique = true)
    private String nodeFolderID;

    public ProjectNode() {}

    public ProjectNode(String id, String belongTo, int seq, String name, String startDate, String endDate, String nodeDesc, String photos) {
        this.id = id;
        this.belongTo = belongTo;
        this.seq = seq;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.nodeDesc = nodeDesc;
        this.photos = photos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBelongTo() {
        return belongTo;
    }

    public void setBelongTo(String belongTo) {
        this.belongTo = belongTo;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getNodeDesc() {
        return nodeDesc;
    }

    public void setNodeDesc(String nodeDesc) {
        this.nodeDesc = nodeDesc;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public String getNodeFolderID() {
        return nodeFolderID;
    }

    public void setNodeFolderID(String nodeFolderID) {
        this.nodeFolderID = nodeFolderID;
    }

    //降序
    @Override
    public int compareTo(ProjectNode o) {
        if(o.seq == this.seq) return 0;
        else if (o.seq > this.seq) return -1;
        else return 1;
    }
}
