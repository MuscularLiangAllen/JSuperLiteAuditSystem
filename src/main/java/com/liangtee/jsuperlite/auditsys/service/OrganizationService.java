package com.liangtee.jsuperlite.auditsys.service;

import com.liangtee.jsuperlite.auditsys.model.Organization;
import com.liangtee.jsuperlite.auditsys.repository.OrgRepository;
import com.liangtee.jsuperlite.auditsys.service.base.BaseService;
import com.liangtee.jsuperlite.auditsys.service.base.PageModel;
import com.liangtee.jsuperlite.auditsys.values.OrgConfs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;

/**
 * Author: LIANG Tianyi
 * Created by LIANG Tianyi on 2017/5/23.
 * All rights Reserved
 */

@Component("orgService")
@Transactional
public class OrganizationService extends BaseService<Organization, Integer> {

    private OrgRepository orgRepository;

    @Autowired
    public OrganizationService(JdbcTemplate jdbcTemplate, OrgRepository orgRepository) {
        super(jdbcTemplate);
        this.orgRepository = orgRepository;
    }

    @CacheEvict(value="hierarchicalLevel_org", allEntries = true)
    public Organization add(Organization org) {
        return orgRepository.save(org);
    }

    public boolean update(Organization org) {
        return add(org) == null ? false : true;
    }

    @CacheEvict(value="hierarchicalLevel_org", allEntries = true)
    public List<Organization> getAll() {

        List<Organization> result = new LinkedList<Organization>();

        List<Organization> rootNodes = findAll("BELONG_TO_ID = ? AND IS_HIDDEN = ?", OrgConfs.NO_PARENT_ORG, 0);
        Queue<Organization> queue = new LinkedList<Organization>();
        queue.addAll(rootNodes);

        Set<Organization> IDs = new HashSet<Organization>();

        while (!queue.isEmpty()) {
            Organization org = queue.poll();
            if(!IDs.contains(org)) {
                result.add(org);
                IDs.add(org);
            }
            List<Organization> childNodes = findAll("BELONG_TO_ID = ? AND IS_HIDDEN = ?", org.getID(), 0);
            queue.addAll(childNodes);
            int parentIndex = result.indexOf(org);
            result.addAll(parentIndex+1, childNodes);
            IDs.addAll(childNodes);
        }

        return result;
    }

    @CacheEvict(value="hierarchicalLevel_org", allEntries = true)
    public List<Organization> getAll(PageModel pageModel) {

        List<Organization> result = new LinkedList<Organization>();

        List<Organization> rootNodes = findByPage(pageModel,"ID", ASC,"BELONG_TO_ID = ? AND IS_HIDDEN = ?", OrgConfs.NO_PARENT_ORG, 0);

        Queue<Organization> queue = new LinkedList<>();
        queue.addAll(rootNodes);

        Set<Organization> IDs = new HashSet<Organization>();

        while (!queue.isEmpty()) {
            Organization org = queue.poll();
            if(!IDs.contains(org)) {
                result.add(org);
                IDs.add(org);
            }
            List<Organization> childNodes = findAll("BELONG_TO_ID = ? AND IS_HIDDEN = ?", org.getID(), 0);
            queue.addAll(childNodes);
            int parentIndex = result.indexOf(org);
            result.addAll(parentIndex+1, childNodes);
            IDs.addAll(childNodes);
        }

        return result;
    }

    @CacheEvict(value="hierarchicalLevel_org", allEntries = true)
    public List<Organization> getAll(PageModel pageModel, String keywordAboutName) {

        List<Organization> result = new LinkedList<Organization>();

//        List<Organization> rootNodes = findAll("IS_HIDDEN = ? AND AND ORG_NAME LIKE ?", 0, keywordAboutName);
        List<Organization> rootNodes = findByPage(pageModel,"ID", ASC,"IS_HIDDEN = ? AND ORG_NAME LIKE ?", 0, keywordAboutName);
        if(rootNodes == null || rootNodes.size() == 0) return result;

        Queue<Organization> queue = new LinkedList<>();
        queue.addAll(rootNodes);


//        List<Organization> rootNodes = findAll("BELONG_TO_ID = ? AND IS_HIDDEN = ? AND ORG_NAME LIKE ?",
//                OrgConfs.NO_PARENT_ORG, 0, "%"+keywordAboutName+"%");
//        Queue<Organization> queue = new LinkedList<Organization>();
//        queue.addAll(rootNodes);
//
        Set<Organization> IDs = new HashSet<Organization>();

        while (!queue.isEmpty()) {
            Organization org = queue.poll();
            if(!IDs.contains(org)) {
                result.add(org);
                IDs.add(org);
            }
            List<Organization> childNodes = findAll("BELONG_TO_ID = ? AND IS_HIDDEN = ?", org.getID(), 0);
            queue.addAll(childNodes);
            int parentIndex = result.indexOf(org);
            result.addAll(parentIndex+1, childNodes);
            IDs.addAll(childNodes);
        }

        return result;
    }

    @CacheEvict(value="hierarchicalLevel_org", allEntries = true)
    public void delete(int orgID) {

        List<Object[]> paramsList = new ArrayList<Object[]>();

        Queue<Organization> queue = new LinkedList<Organization>();

        queue.offer(orgRepository.findOne(orgID));

        while (!queue.isEmpty()) {
            Organization org = queue.poll();
            paramsList.add(new Object[]{1, org.getID()});
            queue.addAll(findAll("BELONG_TO_ID = ?", org.getID()));
        }

        batchUpdate("IS_HIDDEN = ?", "ID = ?", paramsList);

    }

    @Cacheable("hierarchicalLevel_org")
    public List<Organization> getOrgbyHierachicalLevel(int hierarchicalLevel) {
        return findAll("HIERACHICAL_LEVEL = ? AND IS_HIDDEN = ?", hierarchicalLevel, 0);
    }

    public int countHierarchicalLevel(int parentOrgID) {

        int hierarchicalLevel = 1;

        if(parentOrgID == -1 || parentOrgID == 0) return hierarchicalLevel;

        hierarchicalLevel++;

        for (Organization node = orgRepository.findOne(parentOrgID); node.getHierarchicalLevel() > 1; hierarchicalLevel++)
            node = orgRepository.findOne(node.getBelongTo());

        return  hierarchicalLevel;
    }

}
