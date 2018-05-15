package com.liangtee.jsuperlite.auditsys.repository;

import com.liangtee.jsuperlite.auditsys.model.Organization;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.Cacheable;
import java.util.List;

/**
 * Author: LIANG Tianyi
 * Created by LIANG Tianyi on 2017/5/23.
 * All rights Reserved
 */
public interface OrgRepository extends CrudRepository<Organization, Integer> {

    public List<Organization> findOrganizationByHierarchicalLevel(int lierarchicalLevel);

}
