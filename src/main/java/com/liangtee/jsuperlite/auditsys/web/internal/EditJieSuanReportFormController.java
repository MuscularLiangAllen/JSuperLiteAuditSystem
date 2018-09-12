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
import com.liangtee.jsuperlite.auditsys.model.*;
import com.liangtee.jsuperlite.auditsys.service.*;
import com.liangtee.jsuperlite.auditsys.service.base.PageModel;
import com.liangtee.jsuperlite.auditsys.utils.TimeFormater;
import com.liangtee.jsuperlite.auditsys.values.UserConfs;
import com.liangtee.jsuperlite.auditsys.values.json.ReturnMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private JieSuanAttachmentService jieSuanAttachmentService;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

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

//            List<JieSuanAttachment> attachmentList = jieSuanAttachmentService.findAll("BELONG_TO_PROJECT_ID = ? ORDER BY SEQ ASC", projectID);
            Map<Integer, JieSuanAttachment> attachmentMap = new HashMap<>();
            jieSuanAttachmentService.findAll("BELONG_TO_PROJECT_ID = ?", projectID).stream().forEach(a -> attachmentMap.put(a.getSeq(), a));
//            model.addAttribute("attachmentList", attachmentList);
            model.addAttribute("attachmentMap", attachmentMap);

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
    @RequestMapping(path = "upload-attachment", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String uploadAttachment(@RequestParam(name="projectID", required = true) String projectID,
                                                  @RequestParam(name="filename", required = true) String attachmentName,
                                                  @RequestParam(name="copies", required = true) int copies,
                                                  @RequestParam(name="pages", required = true) int pages,
                                                  @RequestParam(name="seq", required = true) int seq,
                                                  @RequestParam(name="tmpFileID", required = true) String tmpFileID,
                                                  HttpServletRequest request, Model model) {

        JSONObject msg = new JSONObject();

        FileInfo jieSuanAttachmentHomeFolder = null;

        StringBuffer grantedUserIDs = new StringBuffer();
        userService.findByDeptID(super.getOperator().getDeptID()).forEach(u -> grantedUserIDs.append(u.getUID()).append(","));

        List<FileInfo> result = fileService.findAll("BELONG_TO_PROJECT = ? AND FILE_NAME = ?", projectID, "结算审计提交资料");
        if(result != null && result.size() != 0)
            jieSuanAttachmentHomeFolder = result.get(0);
        else {
            FileInfo projectRootFolder = fileService.findAll("belong_to_project = ? and parent_folder_id = ?", projectID, FileInfo.NO_PARENT_FOLDER).get(0);
            jieSuanAttachmentHomeFolder = fileService.createFile(super.getOperator(), "结算审计提交资料", FileInfo.FOLDER_TYPE, projectRootFolder.getUUID(),
                    "", grantedUserIDs.toString(), "", projectID, FileInfo.NON_EDITABLE);
        }

        FileInfo attachment = null;
        if(tmpFileID != null && !tmpFileID.isEmpty()) {
            attachment = fileService.createFile(super.getOperator(), attachmentName, FileInfo.FILE_TYPE, jieSuanAttachmentHomeFolder.getUUID(),
                    "", grantedUserIDs.toString(), tmpFileID, projectID, FileInfo.NON_EDITABLE);
            if(attachment == null) {
                msg.put("message", "上传文件失败");
                return msg.toJSONString();
            }

            String id = UUID.randomUUID().toString().replace("-", "");
            String belongToFolderID = jieSuanAttachmentHomeFolder.getUUID();
            String belongToProjectID = projectID;
            String attachmentFileID = attachment.getUUID();

            JieSuanAttachment jieSuanAttachment = new JieSuanAttachment(id, belongToFolderID, belongToProjectID, attachmentName, attachmentFileID, copies, pages, seq);

            if(jieSuanAttachmentService.save(jieSuanAttachment) == null) {
                msg.put("message", "保存文件信息失败");
                return msg.toJSONString();
            }

        }

        msg.put("message", "上传文件成功");
        msg.put("attachmentFileID", attachment.getUUID());
        msg.put("qty", String.format("%d 份 / %d 页", copies, pages));

        return msg.toJSONString();
    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF)
    @RequestMapping(path = "delete-attachment", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String deleteAttachment(@RequestParam(name="projectID", required = true) String projectID,
                                                 @RequestParam(name="fileID", required = true) String fileID,
                                                 @RequestParam(name="seq", required = true) int seq,
                                                 HttpServletRequest request, Model model) {

        JSONObject msg = new JSONObject();
        if(fileService.delete(fileID) && jieSuanAttachmentService.delete("ATTACHMENT_FILE_ID = ?", fileID)) {
            msg.put("message", "删除文件成功");
            return msg.toJSONString();
        }

        msg.put("message", "删除文件失败");
        return msg.toJSONString();
    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF)
    @RequestMapping(path = "submit-form", method = RequestMethod.POST)
    public @ResponseBody String submitForm(@RequestParam(name="projectID", required = true) String projectID,
                      @RequestParam(name="bsbm", required = true) String bsbm,
                      @RequestParam(name="submitCost", required = true) double bsje,
                      @RequestParam(name="paidCost", required = true) double yfje,
                      HttpServletRequest request, Model model) {

        JSONObject jsonObject = new JSONObject();

        if(jieSuanAttachmentService.count("BELONG_TO_PROJECT_ID = ?", projectID) < 9) {
            jsonObject.put("message", "除附件10外其他附件都必须上传");
            return jsonObject.toJSONString();
        }

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

        return jsonObject.toJSONString();
    }

}