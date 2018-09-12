/**
 * Copyright (C), 2018-2022, Allen LIANG
 * FileName: JieSuanAttachment
 * Author:   Allen
 * Date:     2018/8/30 16:57
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
 * @create 2018/8/30
 * @since 0.0.1
 */

@Entity
@Table(name = "T_JIESUAN_ACCACHMENT")
public class JieSuanAttachment {

    @Id
    private String id;

    @Column(name = "BELONG_TO_FOLDER_ID", nullable = false)
    private String belongToFolderID;

    @Column(name = "BELONG_TO_PROJECT_ID", nullable = false)
    private String belongToProjectID;

    @Column(name = "ATTACHMENT_NAME", nullable = false)
    private String attachmentName;

    @Column(name = "ATTACHMENT_FILE_ID", nullable = false)
    private String attachmentFileID;

    @Column(nullable = false)
    private int copies;

    @Column(nullable = false)
    private int pages;

    @Column(nullable = false)
    private int seq;

    public JieSuanAttachment() {
    }

    public JieSuanAttachment(String id, String belongToFolderID, String belongToProjectID, String attachmentName,
                             String attachmentFileID, int copies, int pages, int seq) {
        this.id = id;
        this.belongToFolderID = belongToFolderID;
        this.belongToProjectID = belongToProjectID;
        this.attachmentName = attachmentName;
        this.attachmentFileID = attachmentFileID;
        this.copies = copies;
        this.pages = pages;
        this.seq = seq;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBelongToFolderID() {
        return belongToFolderID;
    }

    public void setBelongToFolderID(String belongToFolderID) {
        this.belongToFolderID = belongToFolderID;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public int getCopies() {
        return copies;
    }

    public void setCopies(int copies) {
        this.copies = copies;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getBelongToProjectID() {
        return belongToProjectID;
    }

    public void setBelongToProjectID(String belongToProjectID) {
        this.belongToProjectID = belongToProjectID;
    }

    public String getAttachmentFileID() {
        return attachmentFileID;
    }

    public void setAttachmentFileID(String attachmentFileID) {
        this.attachmentFileID = attachmentFileID;
    }
}