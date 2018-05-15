package com.liangtee.jsuperlite.auditsys.repository;

import com.liangtee.jsuperlite.auditsys.model.Assignment;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Allen on 2017/8/6.
 */
public interface AssignRepository extends CrudRepository<Assignment, Integer> {
    public Assignment findAssignmentByProjectID(String projectID);
}
