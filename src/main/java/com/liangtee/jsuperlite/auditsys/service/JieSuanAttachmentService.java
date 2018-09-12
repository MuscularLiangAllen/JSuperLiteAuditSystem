/**
 * Copyright (C), 2018-2022, Allen LIANG
 * FileName: JieSuanAttachmentService
 * Author:   Allen
 * Date:     2018/9/5 10:19
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.liangtee.jsuperlite.auditsys.service;

import com.liangtee.jsuperlite.auditsys.model.JieSuanAttachment;
import com.liangtee.jsuperlite.auditsys.repository.JieSuanAttachmentRepository;
import com.liangtee.jsuperlite.auditsys.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 〈〉
 *
 * @author Allen
 * @create 2018/9/5
 * @since 0.0.1
 */

@Component("jieSuanAttachmentService")
public class JieSuanAttachmentService extends BaseService<JieSuanAttachment, String> {

    @Autowired
    private JieSuanAttachmentRepository jieSuanAttachmentRepository;

    @Autowired
    public JieSuanAttachmentService(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public JieSuanAttachment save(JieSuanAttachment jieSuanAttachment) {
        return this.jieSuanAttachmentRepository.save(jieSuanAttachment);
    }
}