package com.liangtee.jsuperlite.auditsys.service;

import com.liangtee.jsuperlite.auditsys.model.Project;
import com.liangtee.jsuperlite.auditsys.model.ProjectVisa;
import com.liangtee.jsuperlite.auditsys.repository.ProjectVisaRepository;
import com.liangtee.jsuperlite.auditsys.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Allen on 2017/8/12.
 */

@Component("auditService")
public class AuditService extends BaseService<ProjectVisa, String> {

    private ProjectVisaRepository projectVisaRepository;

    @Autowired
    public AuditService(JdbcTemplate jdbcTemplate, ProjectVisaRepository projectVisaRepository) {
        super(jdbcTemplate);
        this.projectVisaRepository = projectVisaRepository;
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

    public ProjectVisa get(String visaID) {
        return projectVisaRepository.findOne(visaID);
    }

    public List<ProjectVisa> getProjectVisaByProjectID(String projectID) {
        return projectVisaRepository.findProjectVisaByProjectID(projectID);
    }
}
