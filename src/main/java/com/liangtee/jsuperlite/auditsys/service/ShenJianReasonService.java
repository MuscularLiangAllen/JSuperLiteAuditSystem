/**
 * Copyright (C), 2018-2022, Allen LIANG
 * FileName: ShenJianReasonService
 * Author:   Allen
 * Date:     2018/9/9 21:12
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.liangtee.jsuperlite.auditsys.service;

import com.liangtee.jsuperlite.auditsys.model.ShenJianReasonFile;
import com.liangtee.jsuperlite.auditsys.repository.ShenJianReasonRepository;
import com.liangtee.jsuperlite.auditsys.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 〈〉
 *
 * @author Allen
 * @create 2018/9/9
 * @since 0.0.1
 */

@Component("shenJianReasonService")
public class ShenJianReasonService extends BaseService<ShenJianReasonFile, String> {

    @Autowired
    private ShenJianReasonRepository shenJianReasonRepository;

    @Autowired
    public ShenJianReasonService(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public ShenJianReasonFile save(ShenJianReasonFile shenJianReasonFile) {
        return shenJianReasonRepository.save(shenJianReasonFile);
    }
}