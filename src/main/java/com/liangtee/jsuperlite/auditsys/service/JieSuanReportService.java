/**
 * Copyright (C), 2018-2022, Allen LIANG
 * FileName: JieSuanReportService
 * Author:   Allen
 * Date:     2018/8/14 9:18
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.liangtee.jsuperlite.auditsys.service;

import com.liangtee.jsuperlite.auditsys.model.FileInfo;
import com.liangtee.jsuperlite.auditsys.model.JieSuanReportForm;
import com.liangtee.jsuperlite.auditsys.repository.JieSuanReportRepository;
import com.liangtee.jsuperlite.auditsys.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 〈〉
 *
 * @author Allen
 * @create 2018/8/14
 * @since 0.0.1
 */

@Component("jieSuanReportService")
public class JieSuanReportService extends BaseService<JieSuanReportForm, String> {

    @Autowired
    private JieSuanReportRepository jieSuanReportRepository;

    public JieSuanReportService(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public JieSuanReportForm saveOrUpdate(JieSuanReportForm jieSuanReportForm) {
        return this.jieSuanReportRepository.save(jieSuanReportForm);
    }

    public JieSuanReportForm findOne(String ID) {
        return this.jieSuanReportRepository.findOne(ID);
    }

    public List<JieSuanReportForm> findAll() {
        return (List<JieSuanReportForm>) this.jieSuanReportRepository.findAll();
    }

    public FileInfo mkJieSuanDocxFile() {
        return null;
    }
}