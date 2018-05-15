/**
 * Copyright (C), 2018-2022, Allen LIANG
 * FileName: ProjectVisaService
 * Author:   Allen
 * Date:     2018/5/12 22:34
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.liangtee.jsuperlite.auditsys.service;

import com.liangtee.jsuperlite.auditsys.model.ProjectVisa;
import com.liangtee.jsuperlite.auditsys.repository.ProjectVisaRepository;
import com.liangtee.jsuperlite.auditsys.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 〈〉
 *
 * @author Allen
 * @create 2018/5/12
 * @since 0.0.1
 */

@Component("projectVisaService")
public class ProjectVisaService extends BaseService<ProjectVisa, String> {

    @Autowired
    private ProjectVisaRepository projectVisaRepository;

    @Autowired
    public ProjectVisaService(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public ProjectVisa add(ProjectVisa projectVisa) {
        return projectVisaRepository.save(projectVisa);
    }

    public void delete(ProjectVisa projectVisa) {
        projectVisaRepository.delete(projectVisa);
    }

    public void delete(String visaID) {
        projectVisaRepository.delete(visaID);
    }

    public ProjectVisa findOne(String visaID) {
        return projectVisaRepository.findOne(visaID);
    }

    public List<ProjectVisa> getProjectVisaByProjectID(String projectID) {
        return projectVisaRepository.findProjectVisaByProjectID(projectID);
    }
}