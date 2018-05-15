package com.liangtee.jsuperlite.auditsys.repository;

import com.liangtee.jsuperlite.auditsys.model.ProjectVisa;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Allen on 2017/8/12.
 */

public interface ProjectVisaRepository extends CrudRepository<ProjectVisa, String> {

    public List<ProjectVisa> findProjectVisaByProjectID(String projectID);

    public void deleteByVisaID(String visaID);

}
