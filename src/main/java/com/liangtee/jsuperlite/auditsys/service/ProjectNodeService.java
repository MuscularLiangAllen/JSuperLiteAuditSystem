package com.liangtee.jsuperlite.auditsys.service;

import com.liangtee.jsuperlite.auditsys.model.ProjectNode;
import com.liangtee.jsuperlite.auditsys.repository.ProjectNodeRepository;
import com.liangtee.jsuperlite.auditsys.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by Allen on 2018/2/22.
 */
@Component("projectNodeService")
public class ProjectNodeService extends BaseService<ProjectNode, String> {

    private ProjectNodeRepository projectNodeRepository;

    @Autowired
    public ProjectNodeService(JdbcTemplate jdbcTemplate, ProjectNodeRepository projectNodeRepository) {
        super(jdbcTemplate);
        this.projectNodeRepository = projectNodeRepository;
    }

    public ProjectNode add(ProjectNode projectNode) {return projectNodeRepository.save(projectNode);}

    public ProjectNode findOne(String nodeID) {return projectNodeRepository.findOne(nodeID);}

    public ProjectNode update(ProjectNode projectNode) {return projectNodeRepository.save(projectNode);}
}
