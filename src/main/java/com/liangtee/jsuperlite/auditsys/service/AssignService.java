package com.liangtee.jsuperlite.auditsys.service;

import com.liangtee.jsuperlite.auditsys.model.Assignment;
import com.liangtee.jsuperlite.auditsys.repository.AssignRepository;
import com.liangtee.jsuperlite.auditsys.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by Allen on 2017/8/6.
 */

@Component("assigntService")
public class AssignService extends BaseService<Assignment, Integer> {

    private AssignRepository assignRepository;

    @Autowired
    public AssignService(JdbcTemplate jdbcTemplate, AssignRepository assignRepository) {
        super(jdbcTemplate);
        this.assignRepository = assignRepository;
    }

    public Assignment add(Assignment assignment) {
        return assignRepository.save(assignment);
    }

    public Assignment getByProjectID(String projectID) {
        return assignRepository.findAssignmentByProjectID(projectID);
    }

    public boolean update(Assignment assignment) {
        return assignRepository.save(assignment) == null ? false : true;
    }
}
