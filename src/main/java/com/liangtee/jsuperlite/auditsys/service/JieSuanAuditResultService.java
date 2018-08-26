/**
 * Copyright (C), 2018-2022, Allen LIANG
 * FileName: JieSuanAuditResultService
 * Author:   Allen
 * Date:     2018/8/18 9:58
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.liangtee.jsuperlite.auditsys.service;

import com.liangtee.jsuperlite.auditsys.model.JieSuanAuditResult;
import com.liangtee.jsuperlite.auditsys.repository.JieSuanAuditResultRepository;
import com.liangtee.jsuperlite.auditsys.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 〈〉
 *
 * @author Allen
 * @create 2018/8/18
 * @since 0.0.1
 */

@Component("jieSuanAuditResultService")
public class JieSuanAuditResultService extends BaseService<JieSuanAuditResult, String> {

    @Autowired
    private JieSuanAuditResultRepository jieSuanAuditResultRepository;

    public JieSuanAuditResultService(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public JieSuanAuditResult saveOrUpdate(JieSuanAuditResult jieSuanAuditResult) {
        System.out.println("====save audit====");
        return this.jieSuanAuditResultRepository.save(jieSuanAuditResult);
    }
}