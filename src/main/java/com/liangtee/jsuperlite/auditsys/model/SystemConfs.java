package com.liangtee.jsuperlite.auditsys.model;

import javax.persistence.*;

/**
 * Created by liangtee on 2017/6/15.
 */

@Entity
@Table(name = "T_SYS_CONF_INFO")
public class SystemConfs {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private int ID;

    @Column(name = "CONF_NAME")
    private String confName;

    @Column(name = "CONF_VALUE")
    private String confValue;

    public SystemConfs(String confName, String confValue) {
        this.confName = confName;
        this.confValue = confValue;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getConfName() {
        return confName;
    }

    public void setConfName(String confName) {
        this.confName = confName;
    }

    public String getConfValue() {
        return confValue;
    }

    public void setConfValue(String confValue) {
        this.confValue = confValue;
    }
}
