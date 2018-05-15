package com.liangtee.jsuperlite.auditsys.web.internal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.liangtee.jsuperlite.auditsys.Annotation.AccessControl;
import com.liangtee.jsuperlite.auditsys.Annotation.AccessControlJson;
import com.liangtee.jsuperlite.auditsys.model.*;
import com.liangtee.jsuperlite.auditsys.service.AssignService;
import com.liangtee.jsuperlite.auditsys.service.FileService;
import com.liangtee.jsuperlite.auditsys.service.ProjectService;
import com.liangtee.jsuperlite.auditsys.service.base.PageModel;
import com.liangtee.jsuperlite.auditsys.service.base.QueryHelper;
import com.liangtee.jsuperlite.auditsys.utils.InjectionFilter;
import com.liangtee.jsuperlite.auditsys.utils.TimeFormater;
import com.liangtee.jsuperlite.auditsys.values.OrgConfs;
import com.liangtee.jsuperlite.auditsys.values.UserConfs;
import com.liangtee.jsuperlite.auditsys.values.json.ReturnMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;

/**
 * Created by Allen on 2017/7/18.
 */

@Controller
@RequestMapping("/auditsys/projects")
public class ProjectController extends BaseController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private FileService fileService;

    @Autowired
    private AssignService assignService;

    @Autowired
    private QueryHelper queryHelper;

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_PROJ_CONTROCTOR, excludeLevel = {UserConfs.RoleCode.USER_TYPE_EXTERNAL_AUDITER})
    @RequestMapping(path = "show", method = RequestMethod.GET)
    public String show(@RequestParam(name="pageNumber", required = false) Integer pageNumber,
                       @RequestParam(name="create", required = false) boolean create,
                       HttpServletRequest request, Model model) {

        if(create) model.addAttribute("create", "create");


        User user = (User) request.getSession().getAttribute("user");
        if(user.getUserType() >= UserConfs.RoleCode.USER_TYPE_VIEWER) {
            model.addAttribute("projectList", projectService.findAll(new PageModel(pageNumber == null ? 1 : pageNumber)));
        } else {
            model.addAttribute("projectList", projectService.findByOrgID(user.getDeptID(), new PageModel(pageNumber == null ? 1 : pageNumber)));
        }
        model.addAttribute("totalCounts", projectService.count("1 = ?", 1));

        return "content_pages/sys-projects";
    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF)
    @RequestMapping(path = "get/{projectID}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody String get(@PathVariable String projectID, HttpServletRequest request, Model model) {

        return JSON.toJSONString(projectService.findOne(projectID));
    }

    @AccessControlJson(accessLevel = UserConfs.RoleCode.USER_TYPE_INTERNAL_AUDITER)
    @RequestMapping(path = "permission", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody String permissionCheck(HttpServletRequest request, Model model) {

        return JSON.toJSONString(new ReturnMessage("允许访问"));
    }

    @AccessControlJson(accessLevel = UserConfs.RoleCode.USER_TYPE_INTERNAL_AUDITER)
    @RequestMapping(path = "getAssign/{projectID}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody String getAssignment(@PathVariable String projectID, HttpServletRequest request, Model model) {

        String result = JSON.toJSONString(assignService.getByProjectID(projectID));
        return result;
    }

    @AccessControlJson(accessLevel = UserConfs.RoleCode.USER_TYPE_INTERNAL_AUDITER)
    @RequestMapping(path = "getAuditList", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody String getAuditCompanyList(HttpServletRequest request, Model model) {

        List<Organization> result = queryHelper.findAll(Organization.class, "ORG_TYPE_CODE = ?", OrgConfs.TypeCode.ORG_TYPE_AUDIT_COMPANY);

        return JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_INTERNAL_AUDITER)
    @RequestMapping(path = "addAssign", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    String addAssign(@RequestParam(value = "projectID", required = true) String projectID,
                     @RequestParam(value = "auditCompanyID", required = true) Integer auditCompanyID,
                     @RequestParam(value = "auditScopeID", required = true) Integer auditScopeID,
               HttpServletRequest request) {

        System.out.println(projectID);
        Assignment assignment = null;
        if(assignService.isExist("PROJECT_ID = ?", projectID)) {
            assignment = assignService.getByProjectID(projectID);
            assignment.setAuditCompanyId(auditCompanyID);
            assignment.setAuditScopeCode(auditScopeID);
            return assignService.add(assignment) == null ?
                    JSON.toJSONString(new ReturnMessage("修改项目失败")) : JSON.toJSONString(new ReturnMessage("修改项目成功"));
        } else {
            assignment = new Assignment(auditCompanyID, projectID, auditScopeID);
            return assignService.add(assignment) == null ?
                    JSON.toJSONString(new ReturnMessage("创建项目失败")) : JSON.toJSONString(new ReturnMessage("创建项目成功"));
        }

    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF)
    @RequestMapping(path = "add", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    String add(@RequestParam(value = "projectName", required = true) String projectName,
               @RequestParam(value = "projectLoc", required = true) String projectLoc,
               @RequestParam(value = "expectedStartDate", required = true) String expectedStartDate,
               @RequestParam(value = "expectedEndDate", required = true) String expectedEndDate,
               @RequestParam(value = "startDate", required = true) String startDate,
               @RequestParam(value = "endDate", required = true) String endDate,
               @RequestParam(value = "duration", required = true) Integer duration,
               @RequestParam(value = "bidType", required = true) String bidType,
               @RequestParam(value = "ctrlPrice", required = true) Double ctrlPrice,
               @RequestParam(value = "contractType", required = true) String contractType,
               @RequestParam(value = "contractPrice", required = true) Double contractPrice,
               @RequestParam(value = "deptIDs", required = true) String deptIDs,
               @RequestParam(value = "projectLeader", required = true) String projectLeader,
               @RequestParam(value = "projectLeaderTel", required = true) String projectLeaderTel,
               @RequestParam(value = "contractorName", required = true) String contractorName,
               @RequestParam(value = "contractorManager", required = true) String contractorManager,
               @RequestParam(value = "contractorManagerTel", required = true) String contractorManagerTel,
               @RequestParam(value = "supervisionName", required = true) String supervisionName,
               @RequestParam(value = "supervisorName", required = true) String supervisorName,
               @RequestParam(value = "supervisorTel", required = true) String supervisorTel,
               @RequestParam(value = "projectObjectives", required = true) String projectObjectives,
               @RequestParam(value = "projectMainContent", required = true) String projectMainContent,
               @RequestParam(value = "leadingOrg", required = true) String leadingOrg,
               @RequestParam(value = "tmpFileID", required = true) String tmpFileID,
               HttpServletRequest request) {

        if(InjectionFilter.filter(projectName, projectLoc, expectedStartDate, expectedEndDate, startDate, endDate, projectLeader,
                projectLeaderTel, contractorName, contractorManagerTel, supervisionName, supervisorName, supervisorTel, projectObjectives,
                projectMainContent))
            return JSON.toJSONString(new ReturnMessage("输入不合法!"));

        if(projectService.isExist("PROJECT_NAME = ?", projectName))
            return JSON.toJSONString(new ReturnMessage("项目名称已经存在"));

        if(!queryHelper.isExist(TmpFile.class, "CODE = ?", tmpFileID)) return JSON.toJSONString(new ReturnMessage("未上传 施工主要材料明细表(模板).xls"));

        User user = (User) request.getSession().getAttribute("user");

        Project project = projectService.add(new Project(projectName, projectLoc, expectedStartDate, expectedEndDate, startDate, endDate,
                duration, bidType, ctrlPrice, contractType, contractPrice, projectLeader, projectLeaderTel, contractorName,
                contractorManager, contractorManagerTel, supervisionName, supervisorName, supervisorTel, projectObjectives, projectMainContent, leadingOrg, deptIDs));

        if(project == null) return JSON.toJSONString(new ReturnMessage("#ERROR# 创建项目失败"));

        FileInfo homeFolder = fileService.createFile(user, String.format(TimeFormater.format("yyyyMMdd")+"%s", projectName), FileInfo.FOLDER_TYPE,
                "root", projectObjectives, deptIDs, tmpFileID, project.getID(), FileInfo.NON_EDITABLE);
        if(homeFolder != null)
            logger.info(String.format("Project '%s' 's home folder '%s' has been created, saved path: %s", projectName, homeFolder.getFileName(), homeFolder.getFilePath()));
        else
            return JSON.toJSONString(new ReturnMessage("#ERROR# 创建项目主目录失败"));

        FileInfo detailList = fileService.createFile(user, projectName+"-施工主要材料明细表", FileInfo.FILE_TYPE, homeFolder.getUUID(), projectObjectives, deptIDs, tmpFileID, project.getID(), FileInfo.NON_EDITABLE);
        if(detailList == null)
            return JSON.toJSONString(new ReturnMessage("#ERROR# 创建 施工主要材料明细表 失败"));

        if(projectService.update("MATERIAL_LIST = ?","ID = ?", detailList.getUUID(), project.getID()) == 0)
            logger.info("#ERROR# 项目关联 施工主要材料明细表 失败");

        FileInfo projectVisaFolder = fileService.createFile(super.getOperator(), "工程签证",  FileInfo.FOLDER_TYPE, homeFolder.getUUID(), "用于存放本工程全部工程签证", deptIDs, "", project.getID(), FileInfo.NON_EDITABLE);
        if(projectVisaFolder == null)
            logger.info("#ERROR# 项目创建 施工签证目录 失败");

        FileInfo projectNodeFolder = fileService.createFile(super.getOperator(), "工程节点",  FileInfo.FOLDER_TYPE, homeFolder.getUUID(), "用于存放本工程全部工程节点材料", deptIDs, "", project.getID(), FileInfo.NON_EDITABLE);
        if(projectVisaFolder == null)
            logger.info("#ERROR# 项目创建 工程节点目录 失败");

        if(projectService.addGrantedList(project.getID(), deptIDs) == null)
            logger.info("#ERROR# 项目与部门关联 失败");

        return JSON.toJSONString(new ReturnMessage("创建项目成功"));
    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF)
    @RequestMapping(path = "delete", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String delete(@RequestParam(value = "projectID", required = true) String projectID) {

        if(projectService.delete("ID = ?", projectID))
            return JSON.toJSONString(new ReturnMessage("删除项目成功"));

        return JSON.toJSONString(new ReturnMessage("删除项目失败"));
    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF)
    @RequestMapping(path = "update", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String update(
               @RequestParam(value = "projectID", required = true) String projectID,
               @RequestParam(value = "projectName", required = true) String projectName,
               @RequestParam(value = "projectLoc", required = true) String projectLoc,
               @RequestParam(value = "expectedStartDate", required = true) String expectedStartDate,
               @RequestParam(value = "expectedEndDate", required = true) String expectedEndDate,
               @RequestParam(value = "startDate", required = true) String startDate,
               @RequestParam(value = "endDate", required = true) String endDate,
               @RequestParam(value = "duration", required = true) Integer duration,
               @RequestParam(value = "bidType", required = true) String bidType,
               @RequestParam(value = "ctrlPrice", required = true) Double ctrlPrice,
               @RequestParam(value = "contractType", required = true) String contractType,
               @RequestParam(value = "contractPrice", required = true) Double contractPrice,
               @RequestParam(value = "deptIDs", required = true) String deptIDs,
               @RequestParam(value = "projectLeader", required = true) String projectLeader,
               @RequestParam(value = "projectLeaderTel", required = true) String projectLeaderTel,
               @RequestParam(value = "contractorName", required = true) String contractorName,
               @RequestParam(value = "contractorManager", required = true) String contractorManager,
               @RequestParam(value = "contractorManagerTel", required = true) String contractorManagerTel,
               @RequestParam(value = "supervisionName", required = true) String supervisionName,
               @RequestParam(value = "supervisorName", required = true) String supervisorName,
               @RequestParam(value = "supervisorTel", required = true) String supervisorTel,
               @RequestParam(value = "projectObjectives", required = true) String projectObjectives,
               @RequestParam(value = "projectMainContent", required = true) String projectMainContent,
               @RequestParam(value = "leadingOrg", required = true) String leadingOrg,
               HttpServletRequest request) {

        if(InjectionFilter.filter(projectName, projectLoc, expectedStartDate, expectedEndDate, startDate, endDate, projectLeader,
                projectLeaderTel, contractorName, contractorManagerTel, supervisionName, supervisorName, supervisorTel, projectObjectives,
                projectMainContent))
            return JSON.toJSONString(new ReturnMessage("输入不合法!"));

        if(projectService.update(new Project(projectID, projectName, projectLoc, expectedStartDate, expectedEndDate, startDate, endDate,
                duration, bidType, ctrlPrice, contractType, contractPrice, projectLeader, projectLeaderTel, contractorName,
                contractorManager, contractorManagerTel, supervisionName, supervisorName, supervisorTel, projectObjectives, projectMainContent, leadingOrg, deptIDs)))
            return JSON.toJSONString(new ReturnMessage("项目信息修改成功!"));

        return JSON.toJSONString(new ReturnMessage("项目信息修改失败!"));
    }


}
