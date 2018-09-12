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

import com.alibaba.fastjson.JSONObject;
import com.liangtee.jsuperlite.auditsys.Annotation.AccessControl;
import com.liangtee.jsuperlite.auditsys.model.*;
import com.liangtee.jsuperlite.auditsys.service.*;
import com.liangtee.jsuperlite.auditsys.service.base.PageModel;
import com.liangtee.jsuperlite.auditsys.utils.TimeFormater;
import com.liangtee.jsuperlite.auditsys.utils.wordtemplate.WordTemplate;
import com.liangtee.jsuperlite.auditsys.values.UserConfs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 〈Audit ReportForm Controller〉
 *
 * @author Allen
 * @create 2018/7/30
 * @since 0.0.1
 */


@Controller
@RequestMapping("/auditsys/audit-jiesuan-forms")
public class AuditJieSuanReportFormController extends BaseController  {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private JieSuanReportService jieSuanReportService;

    @Autowired
    private JieSuanAuditResultService jieSuanAuditResultService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @Autowired
    private JieSuanAttachmentService jieSuanAttachmentService;

    @Autowired
    private ShenJianReasonService shenJianReasonService;

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF)
    @RequestMapping(path = "show", method = RequestMethod.GET)
    public String show(HttpServletRequest request, Model model) {

        return "content_pages/sys-audit-jiesuan";
    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF)
    @RequestMapping(path = "audit-form", method = RequestMethod.GET)
    public String showAuditPanel(@RequestParam(name="projectID", required = true) String projectID,
                        HttpServletRequest request, Model model) {

        if(jieSuanReportService.isExist("PROJECT_ID = ?", projectID)) {
            JieSuanReportForm jieSuanReportForm = jieSuanReportService.findAll("PROJECT_ID = ?", projectID).get(0);
            model.addAttribute("jieSuanReportForm", jieSuanReportForm);

            Map<Integer, JieSuanAttachment> attachmentMap = new HashMap<>();
            jieSuanAttachmentService.findAll("BELONG_TO_PROJECT_ID = ?", projectID).stream().forEach(a -> attachmentMap.put(a.getSeq(), a));
            model.addAttribute("attachmentMap", attachmentMap);
        }

        Project project = projectService.findOne(projectID);
        model.addAttribute("project", project);

        return "content_pages/sys-audit-jiesuan-form";
    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF)
    @RequestMapping(path = "upload-attachment", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String uploadAttachment(@RequestParam(name="projectID", required = true) String projectID,
                                                 @RequestParam(name="tmpFileID", required = true) String tmpFileID,
                                                 HttpServletRequest request, Model model) {

        JSONObject msg = new JSONObject();

        FileInfo jieSuanAttachmentHomeFolder = null;

        StringBuffer grantedUserIDs = new StringBuffer();
        userService.findByDeptID(super.getOperator().getDeptID()).forEach(u -> grantedUserIDs.append(u.getUID()).append(","));

        List<FileInfo> result = fileService.findAll("BELONG_TO_PROJECT = ? AND FILE_NAME = ?", projectID, "审减反馈意见");
        if(result != null && result.size() != 0)
            jieSuanAttachmentHomeFolder = result.get(0);
        else {
            FileInfo projectRootFolder = fileService.findAll("belong_to_project = ? and parent_folder_id = ?", projectID, FileInfo.NO_PARENT_FOLDER).get(0);
            jieSuanAttachmentHomeFolder = fileService.createFile(super.getOperator(), "审减反馈意见", FileInfo.FOLDER_TYPE, projectRootFolder.getUUID(),
                    "", grantedUserIDs.toString(), "", projectID, FileInfo.NON_EDITABLE);
        }

        FileInfo attachment = null;
        if(tmpFileID != null && !tmpFileID.isEmpty()) {
            attachment = fileService.createFile(super.getOperator(), TimeFormater.format("yyMMddHHmmss"), FileInfo.FILE_TYPE, jieSuanAttachmentHomeFolder.getUUID(),
                    "", grantedUserIDs.toString(), tmpFileID, projectID, FileInfo.NON_EDITABLE);
            if(attachment == null) {
                msg.put("message", "上传文件失败");
                return msg.toJSONString();
            }

            String id = UUID.randomUUID().toString().replace("-", "");
            String belongToProjectID = projectID;
            String attachmentFileID = attachment.getUUID();
            String time = TimeFormater.format("yyyy-MM-dd HH:mm:ss");

            ShenJianReasonFile shenJianReasonFile = new ShenJianReasonFile(id, belongToProjectID, attachmentFileID, time, getOperator().getName());

            if(shenJianReasonService.save(shenJianReasonFile) == null) {
                msg.put("message", "保存文件信息失败");
                return msg.toJSONString();
            }

        }

        msg.put("message", "上传文件成功");
        msg.put("uploaded", attachment.getUUID());

        return msg.toJSONString();
    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF)
    @RequestMapping(path = "submit-audits", method = RequestMethod.POST)
    public @ResponseBody String submitForm(@RequestParam(name="projectID", required = true) String projectID,
                      @RequestParam(name="audit_result", required = true) String audit_result,
                      @RequestParam(name="shending_val", required = true) double shending_val,
                      @RequestParam(name="shenjian_val", required = true) double shenjian_val,
                      @RequestParam(name="reasons", required = true) String reasons,
                      @RequestParam(name="uploadedID", required = true) String uploadedID,
                      HttpServletRequest request, Model model) {

        JSONObject jsonObject = new JSONObject();

        if(audit_result.toLowerCase().equals("banned")) {
            if(jieSuanAuditResultService.saveOrUpdate(new JieSuanAuditResult(UUID.randomUUID().toString().replace("-", ""),
                    projectID, JieSuanAuditResult.NOT_GRANTED, shending_val, shenjian_val, reasons,
                    TimeFormater.format("yyyy-MM-dd HH:mm:ss"), "", uploadedID)) == null) {

                jsonObject.put("message", "保存审计结果失败");
                return jsonObject.toJSONString();
            }
            JieSuanReportForm jieSuanReportForm = jieSuanReportService.findAll("PROJECT_ID = ?", projectID).get(0);
            jieSuanReportForm.setIsGranted(JieSuanReportForm.NOT_GRANTED);
            jieSuanReportService.saveOrUpdate(jieSuanReportForm);
        } else {
            if(jieSuanAuditResultService.saveOrUpdate(new JieSuanAuditResult(UUID.randomUUID().toString().replace("-", ""),
                    projectID, JieSuanAuditResult.GRANTED, 0D, 0D, "",
                    TimeFormater.format("yyyy-MM-dd HH:mm:ss"), "", uploadedID)) == null) {

                jsonObject.put("message", "保存审计结果失败");
                return jsonObject.toJSONString();
            }
            JieSuanReportForm jieSuanReportForm = jieSuanReportService.findAll("PROJECT_ID = ?", projectID).get(0);
            jieSuanReportForm.setIsGranted(JieSuanReportForm.GRANTED);
            jieSuanReportService.saveOrUpdate(jieSuanReportForm);

            Project project = projectService.findOne(projectID);
            StringBuffer grantedUserIDs = new StringBuffer();
            userService.findAll("DEPT_ID = ?", project.getLeadingOrgIDs().split(",")[0]).
                    forEach(u -> grantedUserIDs.append(u.getUID()).append(","));

            FileInfo projectRootFolder = fileService.findAll("BELONG_TO_PROJECT = ? AND PARENT_FOLDER_ID = ?", projectID, FileInfo.NO_PARENT_FOLDER).get(0);
            FileInfo jieSuanDocxFile = createJieSuanReportDocx(getOperator(), project, jieSuanReportForm, projectRootFolder, request.getSession().getServletContext().getRealPath(DEFAULT_TEMPLATE_FILE_PATH), grantedUserIDs.toString());
            if(jieSuanDocxFile == null) jsonObject.put("message", "生成审计结算表失败");
            else {
                jieSuanReportForm.setFileID(jieSuanDocxFile.getUUID());
                jieSuanReportService.saveOrUpdate(jieSuanReportForm);
            }
        }

        jsonObject.put("message", "提交审计结果成功");

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
            jieSuanReportFormList.forEach(js -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("iD", project.getID());
                jsonObject.put("projectName", project.getProjectName());
                jsonObject.put("projectLoc", project.getProjectLoc());
                jsonObject.put("bidType", project.getBidType());
                jsonObject.put("expectedStartDate", project.getExpectedStartDate());
                jsonObject.put("contractType", project.getContractType());
                jsonObject.put("contractPrice", project.getContractPrice());
                jsonObject.put("projectObjectives", project.getProjectObjectives());
                if(js.getProjectID().equalsIgnoreCase(project.getID())) jsonObject.put("status", js.getIsGranted() > 0 ? "报表审批通过" : (js.getIsGranted() == 0 ? "审计中" : "报表审批失败"));
                else jsonObject.put("status", "未提交报表");
                result.add(jsonObject);
            });
        }

        int totalSize = projectService.count("1 = ?", 1);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rows", result);
        jsonObject.put("total", totalSize);

        return jsonObject.toJSONString();
    }

    private FileInfo createJieSuanReportDocx(User user, Project project, JieSuanReportForm jieSuanReportForm, FileInfo projRootFolder, String templateFolderPath, String grantedUserIDs) {

        String tmpFolder = System.getProperty("sys.tmp.upload");
        String[] date = TimeFormater.format("yyyy-MM-dd").split("-");

        Map<String, String> params = new HashMap<String, String>();
        params.put("bsbm", jieSuanReportForm.getBsbm());
        params.put("yy", date[0]);
        params.put("mm", date[1]);
        params.put("dd", date[2]);
        params.put("projectName", project.getProjectName());
        params.put("projectLoc", project.getProjectLoc());
        params.put("projectLeader", project.getProjectLeaderName());
        params.put("projectLeaderTel", project.getProjectLeaderTel());
        params.put("contractPrice", String.valueOf(project.getContractPrice()));
        params.put("bsje", String.valueOf(jieSuanReportForm.getBsje()));
        params.put("yfje", String.valueOf(jieSuanReportForm.getYfje()));
        params.put("bidType", project.getBidType());
        params.put("contractorName", project.getProjectContractorName());
        params.put("manager", project.getContractorManagerName());
        params.put("managerTel", project.getContractorManagerTel());

        WordTemplate template = null;
        FileInputStream fileInputStream = null;
        OutputStream out = null;
        try {
            fileInputStream = new FileInputStream(new File(templateFolderPath+PATH_SEPARATOR+"结算报审表模板.docx"));
            template = new WordTemplate(fileInputStream);
            template.replaceTag(params);

            Path jieSuanFile = Paths.get(tmpFolder+PATH_SEPARATOR+project.getProjectName()+"-结算审计表.docx");
            out = Files.newOutputStream(jieSuanFile);
            template.write(out);

            InputStream in = Files.newInputStream(jieSuanFile);
            FileInfo jieSuanDocxFile = fileService.createFile(user, jieSuanFile.getFileName().toString().split("\\.")[0], "docx", FileInfo.FILE_TYPE, projRootFolder.getUUID(),
                    "", grantedUserIDs, in, project.getID(), FileInfo.NON_EDITABLE);

            out.close();
            in.close();

            return jieSuanDocxFile;
        } catch(IOException exception){
            exception.printStackTrace();
            return null;
        } finally {
            try {
                out.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

    }

}