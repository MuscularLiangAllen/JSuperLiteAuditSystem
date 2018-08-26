/**
 * Copyright (C), 2018-2022, Allen LIANG
 * FileName: AuditReportFormController
 * Author:   Allen
 * Date:     2018/7/30 10:50
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.liangtee.jsuperlite.auditsys.web.internal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liangtee.jsuperlite.auditsys.Annotation.AccessControl;
import com.liangtee.jsuperlite.auditsys.model.JieSuanAuditResult;
import com.liangtee.jsuperlite.auditsys.model.JieSuanReportForm;
import com.liangtee.jsuperlite.auditsys.model.Project;
import com.liangtee.jsuperlite.auditsys.model.User;
import com.liangtee.jsuperlite.auditsys.service.JieSuanAuditResultService;
import com.liangtee.jsuperlite.auditsys.service.JieSuanReportService;
import com.liangtee.jsuperlite.auditsys.service.ProjectService;
import com.liangtee.jsuperlite.auditsys.service.base.PageModel;
import com.liangtee.jsuperlite.auditsys.utils.TimeFormater;
import com.liangtee.jsuperlite.auditsys.values.UserConfs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 〈Audit ReportForm Controller〉
 *
 * @author Allen
 * @create 2018/7/30
 * @since 0.0.1
 */


@Controller
@RequestMapping("/auditsys/edit-jiesuan-forms")
public class EditJieSuanReportFormController extends BaseController  {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private JieSuanReportService jieSuanReportService;

    @Autowired
    private JieSuanAuditResultService jieSuanAuditResultService;

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF)
    @RequestMapping(path = "show", method = RequestMethod.GET)
    public String show(HttpServletRequest request, Model model) {

        return "content_pages/sys-jiesuan";
    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF)
    @RequestMapping(path = "edit-form", method = RequestMethod.GET)
    public String showFormEditor(@RequestParam(name="projectID", required = true) String projectID,
                        HttpServletRequest request, Model model) {

        if(jieSuanReportService.isExist("PROJECT_ID = ?", projectID)) {
            JieSuanReportForm jieSuanReportForm = jieSuanReportService.findAll("PROJECT_ID = ?", projectID).get(0);
            model.addAttribute("jieSuanReportForm", jieSuanReportForm);
            if(jieSuanReportForm.getIsGranted() == -1) {
                JieSuanAuditResult jieSuanAuditResult = jieSuanAuditResultService.findAll("PROJECT_ID = ? ORDER BY TIME DESC", projectID).get(0);
                model.addAttribute("jieSuanAuditResult", jieSuanAuditResult);
            }
        }

        Project project = projectService.findOne(projectID);
        model.addAttribute("project", project);

        return "content_pages/sys-jiesuan-form";
    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF)
    @RequestMapping(path = "submit-form", method = RequestMethod.POST)
    public @ResponseBody String submitForm(@RequestParam(name="projectID", required = true) String projectID,
                      @RequestParam(name="bsbm", required = true) String bsbm,
                      @RequestParam(name="submitCost", required = true) double bsje,
                      @RequestParam(name="paidCost", required = true) double yfje,
                      HttpServletRequest request, Model model) {

        JSONObject jsonObject = new JSONObject();
        String time = TimeFormater.format("yyyy-MM-dd HH:mm:ss");
        if(jieSuanReportService.isExist("PROJECT_ID = ?", projectID)) {
            JieSuanReportForm jieSuanReportForm = jieSuanReportService.findAll("PROJECT_ID = ?", projectID).get(0);
            jieSuanReportForm.setIsGranted(JieSuanReportForm.AUDITING);
            jieSuanReportForm.setBsje(bsje);
            jieSuanReportForm.setYfje(yfje);
            jieSuanReportForm.setTime(time);
            if(jieSuanReportService.saveOrUpdate(jieSuanReportForm) == null) {
                jsonObject.put("message", "提交失败!");
                return jsonObject.toJSONString();
            }
            jsonObject.put("message", "提交修改结算审计表成功");
        } else {
            String ID = UUID.randomUUID().toString().replace("-", "");
            String submitter = getOperator().getName();
            jieSuanReportService.saveOrUpdate(new JieSuanReportForm(ID, projectID, bsbm, bsje, yfje, time, submitter));
            jsonObject.put("message", "提交审计结算表成功");
        }

        return jsonObject.toJSONString();
    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF)
    @RequestMapping(path = "load", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody String load(@RequestParam(name="limit", required = true) int limit,
                                     @RequestParam(name="offset", required = true) int offset,
                                     @RequestParam(name="sort", required = false) String sort,
                                     @RequestParam(name="order", required = false) String order,
                                     @RequestParam(name="keyword", required = false) String keyword,
                                     HttpServletRequest request, Model model) {

        User user = (User) request.getSession().getAttribute("user");
        PageModel pageModel = new PageModel(limit, (limit + offset)/limit);

        List<Project> projectList = null;
        if(keyword != null && !keyword.isEmpty()) {
            keyword = "%" + keyword.trim() + "%";
            if(user.getUserType() >= UserConfs.RoleCode.USER_TYPE_VIEWER) {
                projectList = projectService.findByPage(pageModel, sort, order.equalsIgnoreCase("ASC") ? ASC : DESC,
                        "PROJECT_NAME like ? OR PROJECT_LOC like ?", keyword, keyword);
            } else {
                projectList = projectService.findByOrgID(user.getDeptID(), pageModel);
            }
        } else {
            if(user.getUserType() >= UserConfs.RoleCode.USER_TYPE_VIEWER) {
                projectList = projectService.findByPage(pageModel, sort, order.equalsIgnoreCase("ASC") ? ASC : DESC, "1 = ?", 1);
            } else {
                projectList = projectService.findByOrgID(user.getDeptID(), pageModel);
            }
        }

        List<JieSuanReportForm> jieSuanReportFormList = jieSuanReportService.findAll();
        List<JSONObject> result = new ArrayList<JSONObject>();
        for(Project project : projectList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("iD", project.getID());
            jsonObject.put("projectName", project.getProjectName());
            jsonObject.put("projectLoc", project.getProjectLoc());
            jsonObject.put("bidType", project.getBidType());
            jsonObject.put("expectedStartDate", project.getExpectedStartDate());
            jsonObject.put("contractor", project.getProjectContractorName());
            jsonObject.put("contractPrice", project.getContractPrice());
            jsonObject.put("projectObjectives", project.getProjectObjectives());

            jieSuanReportFormList.forEach(js -> {
                if(js.getProjectID().equalsIgnoreCase(project.getID())) jsonObject.put("status", js.getIsGranted() > 0 ? "报表审批通过" : (js.getIsGranted() == 0 ? "审计中" : "报表审批失败"));
                else jsonObject.put("status", "未提交报表");
                jsonObject.put("vstatus", js.getIsGranted());
            });

            result.add(jsonObject);
        }

        int totalSize = projectService.count("1 = ?", 1);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rows", result);
        jsonObject.put("total", totalSize);

        System.out.println(jsonObject.toJSONString());

        return jsonObject.toJSONString();
    }

}