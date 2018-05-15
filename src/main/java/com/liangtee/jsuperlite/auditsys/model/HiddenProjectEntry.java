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

import javax.persistence.*;

/**
 * 〈Hidden Project〉
 *
 * @author Allen
 * @create 2018/5/15
 * @since 0.0.1
 */

@Entity
@Table(name = "t_hidden_project_entry")
public class HiddenProjectEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "qty", nullable = false)
    private String qty;

    public HiddenProjectEntry() {
    }

    public HiddenProjectEntry(String content, String qty) {
        this.content = content;
        this.qty = qty;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }
}