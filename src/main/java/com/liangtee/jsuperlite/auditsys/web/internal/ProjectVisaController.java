/**
 * Copyright (C), 2018-2022, Allen LIANG
 * FileName: ProjectVisaController
 * Author:   Allen
 * Date:     2018/5/12 22:29
 * Description: Project Visa
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.liangtee.jsuperlite.auditsys.web.internal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.liangtee.jsuperlite.auditsys.Annotation.AccessControl;
import com.liangtee.jsuperlite.auditsys.model.*;
import com.liangtee.jsuperlite.auditsys.service.FileService;
import com.liangtee.jsuperlite.auditsys.service.ProjectService;
import com.liangtee.jsuperlite.auditsys.service.ProjectVisaService;
import com.liangtee.jsuperlite.auditsys.service.UserService;
import com.liangtee.jsuperlite.auditsys.service.base.PageModel;
import com.liangtee.jsuperlite.auditsys.utils.TimeFormater;
import com.liangtee.jsuperlite.auditsys.utils.wordtemplate.WordTemplate;
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
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈Project Visa〉
 *
 * @author Allen
 * @create 2018/5/12
 * @since 0.0.1
 */

@Controller
@RequestMapping("/auditsys/project-visa")
public class ProjectVisaController extends BaseController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectVisaService projectVisaService;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_PROJ_CONTROCTOR)
    @RequestMapping(path = "show", method = RequestMethod.GET)
    public String show(@RequestParam(name="projectID", required = true) String projectID,
                       HttpServletRequest request, Model model) {

        Project project = projectService.findOne(projectID);

        model.addAttribute("projectID", projectID);
        model.addAttribute("project", project);

        return "content_pages/sys-project-visa";
    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF)
    @RequestMapping(path = "load", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody String load(@RequestParam(name="limit", required = true) int limit,
                                     @RequestParam(name="offset", required = true) int offset,
                                     @RequestParam(name="sort", required = false) String sort,
                                     @RequestParam(name="order", required = false) String order,
                                     @RequestParam(name="keyword", required = false) String keyword,
                                     @RequestParam(name="projectID", required = true) String projectID,
                                     HttpServletRequest request, Model model) {

        User user = (User) request.getSession().getAttribute("user");
        PageModel pageModel = new PageModel(limit, (limit + offset)/limit);

        List<ProjectVisa> projectVisaList = null;
        if(keyword != null && !keyword.isEmpty()) {
            keyword = "%" + keyword.trim() + "%";
            projectVisaList = projectVisaService.findByPage(pageModel, sort, order.equalsIgnoreCase("ASC") ? ASC : DESC,
                    "PROJECT_ID = ? AND (VISA_NAME like ? OR CREATE_DATE like ?)", projectID, keyword, keyword);
        } else {
            projectVisaList = projectVisaService.findByPage(pageModel, sort, order.equalsIgnoreCase("ASC") ? ASC : DESC, "PROJECT_ID = ?", projectID);
        }

        int totalSize = projectVisaService.count("PROJECT_ID = ?", projectID);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rows", projectVisaList);
        jsonObject.put("total", totalSize);

        return jsonObject.toJSONString();
    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF)
    @RequestMapping(path = "loadVisaItems", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String loadVisaItems(@RequestParam(name="visaID", required = true) String visaID,
                                     HttpServletRequest request, Model model) {

        ProjectVisa projectVisa = projectVisaService.findOne(visaID);
//        List<ProjectVisaEntry> projectVisaEntryList = projectVisa.getVisaEntryList();
        model.addAttribute("projectVisa", projectVisa);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "success");
        jsonObject.put("visaEntryList", projectVisa.getVisaEntryList());

        return JSON.toJSONString(jsonObject, SerializerFeature.DisableCircularReferenceDetect);
    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF, excludeLevel = {UserConfs.RoleCode.USER_TYPE_EXTERNAL_AUDITER})
    @RequestMapping(path = "createProjectVisa", method = RequestMethod.POST)
    public @ResponseBody
    String createProjectVisa(@RequestParam(name="projectID", required = true) String projectID,
                             @RequestParam(name="visaName", required = true) String visaName,
                             @RequestParam(name="visaItemContentArray[]", required = true) String[] visaItemContentArray,
                             @RequestParam(name="visaItemUnitArray[]", required = true) String[] visaItemUnitArray,
                             @RequestParam(name="visaItemQtyArray[]", required = true) Integer[] visaItemQtyArray,
                             @RequestParam(name="visaItemReasonArray[]", required = true) String[] visaItemReasonArray,
                             HttpServletRequest request, Model model) {

        List<ProjectVisaEntry> visaEntryList = new ArrayList<ProjectVisaEntry>(visaItemContentArray.length);

        for(int i=0; i<visaItemContentArray.length; i++) {
            visaEntryList.add(new ProjectVisaEntry(visaItemContentArray[i], visaItemUnitArray[i], visaItemQtyArray[i], visaItemReasonArray[i]));
        }

        FileInfo visaHomeFolder = null;
        List<FileInfo> result = fileService.findAll("BELONG_TO_PROJECT = ? AND FILE_NAME = ?", projectID, "工程签证");
        if(result != null && result.size() > 0) visaHomeFolder = result.get(0);
        else {
            return JSON.toJSONString(new ReturnMessage("创建新工程签证目录失败: 找不到工程签证根目录"));
        }

        StringBuffer grantedUserIDs = new StringBuffer();
        userService.findByDeptID(super.getOperator().getDeptID()).forEach(u -> grantedUserIDs.append(u.getUID()).append(","));

        FileInfo newVisaFolder = fileService.createFile(super.getOperator(), String.format("%s-%s", TimeFormater.format("yyyyMMdd"), visaName), FileInfo.FOLDER_TYPE, visaHomeFolder.getUUID(),
                "", grantedUserIDs.toString(), "", projectID, FileInfo.NON_EDITABLE);
        if(newVisaFolder == null) {
            return JSON.toJSONString(new ReturnMessage("创建新工程签证目录失败"));
        }

        ProjectVisa projectVisa = new ProjectVisa(visaName, projectID, TimeFormater.format("yyyy/MM/dd"), super.getOperator(), newVisaFolder.getUUID(), visaEntryList);

        Project project = projectService.findOne(projectID);

        createVisaWordFiles(super.getOperator(), project, projectVisa, request.getSession().getServletContext().getRealPath(DEFAULT_TEMPLATE_FILE_PATH), grantedUserIDs.toString());

        return projectVisaService.add(projectVisa) == null ? JSON.toJSONString(new ReturnMessage("创建工程签证失败"))
                : JSON.toJSONString(new ReturnMessage("创建工程签证成功"));

    }

    private boolean createVisaWordFiles(User user, Project project, ProjectVisa projectVisa, String templateFolderPath, String grantedUserIDs) {

        String tmpFolder = System.getProperty("sys.tmp.upload");

        Map<String, String> params = new HashMap<String, String>();
        params.put("contractor", project.getProjectContractorName());
        params.put("leadingOrg", project.getLeadingOrg());
        params.put("year", projectVisa.getCreateDate().split("/")[0]);
        params.put("month", projectVisa.getCreateDate().split("/")[1]);
        params.put("day", projectVisa.getCreateDate().split("/")[2]);
        params.put("projectName", projectVisa.getVisaName());
        params.put("projectLoc", project.getProjectLoc());

        int totalItemQty = projectVisa.getVisaEntryList().size();
        int pageQty = totalItemQty%7 == 0 ? totalItemQty/7 : (totalItemQty/7+1);

        params.put("pages", Integer.toString(pageQty));

        int idx = 1;
        for(; idx<=totalItemQty; idx++) {
            params.put("content"+(idx%7==0?7:idx%7), projectVisa.getVisaEntryList().get(idx-1).getItemContent());
            params.put("u"+(idx%7==0?7:idx%7), projectVisa.getVisaEntryList().get(idx-1).getItemUnit());
            params.put("q"+(idx%7==0?7:idx%7), Integer.toString(projectVisa.getVisaEntryList().get(idx-1).getItemQty()));
            params.put("reason"+(idx%7==0?7:idx%7), projectVisa.getVisaEntryList().get(idx-1).getReason());
            if(idx%7 == 0 || idx == totalItemQty) {
                if(idx == totalItemQty) {
                    int i = idx%7 + 1;
                    for(; i<=7; i++) {
                        params.put("content"+i, "");
                        params.put("u"+i, "");
                        params.put("q"+i, "");
                        params.put("reason"+i, "");
                    }
                }

                int pageNum = idx%7 == 0 ? idx/7 : (idx/7+1);
                params.put("page", Integer.toString(pageNum));

                WordTemplate template = null;
                FileInputStream fileInputStream = null;
                OutputStream out = null;
                try {
                    fileInputStream = new FileInputStream(new File(templateFolderPath+PATH_SEPARATOR+"工程签证模板.docx"));
                    template = new WordTemplate(fileInputStream);
                    template.replaceTag(params);

                    Path visaFile = Paths.get(tmpFolder+PATH_SEPARATOR+projectVisa.getVisaName()+String.format("-工程签证-%d页.docx", pageNum));
                    out = Files.newOutputStream(visaFile);
                    template.write(out);

                    InputStream in = Files.newInputStream(visaFile);
                    fileService.createFile(user, visaFile.getFileName().toString().split("\\.")[0], "docx", FileInfo.FILE_TYPE, projectVisa.getVisaFolderID(),
                            "", grantedUserIDs, in, project.getID(), FileInfo.NON_EDITABLE);

                    out.close();
                    in.close();
                } catch(IOException exception){
                    exception.printStackTrace();
                    return false;
                } finally {
                    try {
                        out.close();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            }

        }

        return true;
    }


}