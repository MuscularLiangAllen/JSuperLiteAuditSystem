package com.liangtee.jsuperlite.auditsys.service;

import com.liangtee.jsuperlite.auditsys.model.Project;
import com.liangtee.jsuperlite.auditsys.model.ProjectToORG;
import com.liangtee.jsuperlite.auditsys.repository.ProjectRepository;
import com.liangtee.jsuperlite.auditsys.repository.ProjectToOrgRepository;
import com.liangtee.jsuperlite.auditsys.service.base.BaseService;
import com.liangtee.jsuperlite.auditsys.service.base.PageModel;
import com.liangtee.jsuperlite.auditsys.service.base.QueryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by Allen on 2017/7/22.
 */

@Component("projectService")
public class ProjectService extends BaseService<Project, String> {

    private ProjectRepository projectRepository;

    private ProjectToOrgRepository projectToOrgRepository;

    @Autowired
    private QueryHelper queryHelper;

    @Autowired
    public ProjectService(JdbcTemplate jdbcTemplate, ProjectRepository projectRepository, ProjectToOrgRepository projectToOrgRepository) {
        super(jdbcTemplate);
        this.projectRepository = projectRepository;
        this.projectToOrgRepository = projectToOrgRepository;
    }

    public Project add(Project project) {
        return projectRepository.save(project);
    }

    public Project findOne(String projectID) {
        return projectRepository.findOne(projectID);
    }

    public List<Project> findAll(PageModel page) {
        return queryHelper.findByPage(Project.class, page, "ID", -1, "ID != ?", "-1");
    }

    public List<Project> findByOrgID(int orgID, PageModel page) {
        Set<String> projectIDs = new HashSet<String>();
        queryHelper.findAll(ProjectToORG.class, "ORG_ID = ?", orgID).forEach(pto -> projectIDs.add(pto.getProjectID()));
        List<Project> result = new ArrayList<Project>();
        findAll(page).forEach(p -> {
            if(projectIDs.contains(p.getID()))
                result.add(p);
        });

        return result;
    }

    public Iterable<ProjectToORG> addGrantedList(String projectID, String orgIDs) {

        List<ProjectToORG> projectToOrgs = new ArrayList<ProjectToORG>();
        Arrays.stream(orgIDs.split(",")).forEach(orgID -> projectToOrgs.add(new ProjectToORG(projectID, Integer.parseInt(orgID))));

        return projectToOrgRepository.save(projectToOrgs);
    }

    public boolean update(Project project) {
        if(projectRepository.save(project) == null) return false;
        queryHelper.findAll(ProjectToORG.class, "PROJECT_ID = ?", project.getID()).forEach(pto -> {
            projectToOrgRepository.delete(pto.getID());
        });
        if(addGrantedList(project.getID(), project.getLeadingOrgIDs()) == null) return false;

        return true;
    }
}
