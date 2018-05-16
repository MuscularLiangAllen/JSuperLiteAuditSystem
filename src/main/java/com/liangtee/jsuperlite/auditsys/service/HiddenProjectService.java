/**
 * Copyright (C), 2018-2022, Allen LIANG
 * FileName: HiddenProjectService
 * Author:   Allen
 * Date:     2018/5/15 14:44
 * Description: Hidden Project Service
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.liangtee.jsuperlite.auditsys.service;

import com.liangtee.jsuperlite.auditsys.model.HiddenProject;
import com.liangtee.jsuperlite.auditsys.repository.HiddenProjectRepository;
import com.liangtee.jsuperlite.auditsys.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

/**
 * 〈Hidden Project Service〉
 *
 * @author Allen
 * @create 2018/5/15
 * @since 0.0.1
 */

@Component("hiddenProjectService")
@Transactional
public class HiddenProjectService extends BaseService<HiddenProject, Integer> {

    @Autowired
    private HiddenProjectRepository hiddenProjectRepository;

    @Autowired
    public HiddenProjectService(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public HiddenProject add(HiddenProject hiddenProject) {return hiddenProjectRepository.save(hiddenProject);}

    public HiddenProject findOne(Integer id) {return hiddenProjectRepository.findOne(id);}

}