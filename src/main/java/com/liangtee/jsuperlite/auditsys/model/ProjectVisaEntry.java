package com.liangtee.jsuperlite.auditsys.model;

import javax.persistence.*;

/**
 * Created by Allen on 2017/8/12.
 */

@Entity
@Table(name = "T_PROJECT_VISA_ENTRY")
public class ProjectVisaEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private int ID;

    @Column(name = "ITEM_CONTENT", nullable = false)
    private String itemContent;

    @Column(name = "ITEM_UNIT", nullable = false)
    private String itemUnit;

    @Column(name = "ITEM_QTY", nullable = false)
    private int itemQty;

    @Column(name = "ITEM_REASON", nullable = false)
    private String reason;

    public ProjectVisaEntry() {
    }

    public ProjectVisaEntry(String itemContent, String itemUnit, int itemQty, String reason) {
        this.itemContent = itemContent;
        this.itemUnit = itemUnit;
        this.itemQty = itemQty;
        this.reason = reason;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getItemContent() {
        return itemContent;
    }

    public void setItemContent(String itemContent) {
        this.itemContent = itemContent;
    }

    public String getItemUnit() {
        return itemUnit;
    }

    public void setItemUnit(String itemUnit) {
        this.itemUnit = itemUnit;
    }

    public int getItemQty() {
        return itemQty;
    }

    public void setItemQty(int itemQty) {
        this.itemQty = itemQty;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
