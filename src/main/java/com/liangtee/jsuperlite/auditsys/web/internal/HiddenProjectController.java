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
import com.liangtee.jsuperlite.auditsys.service.*;
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
import java.util.stream.Collectors;

/**
 * 〈Project Visa〉
 *
 * @author Allen
 * @create 2018/5/12
 * @since 0.0.1
 */

@Controller
@RequestMapping("/auditsys/hidden-project")
public class HiddenProjectController extends BaseController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private HiddenProjectService hiddenProjectService;

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

        return "content_pages/sys-hidden-project";
    }

//    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF)
//    @RequestMapping(path = "load", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
//    public @ResponseBody String load(@RequestParam(name="limit", required = true) int limit,
//                                     @RequestParam(name="offset", required = true) int offset,
//                                     @RequestParam(name="sort", required = false) String sort,
//                                     @RequestParam(name="order", required = false) String order,
//                                     @RequestParam(name="keyword", required = false) String keyword,
//                                     HttpServletRequest request, Model model) {
//
//        User user = (User) request.getSession().getAttribute("user");
//        PageModel pageModel = new PageModel(limit, (limit + offset)/limit);
//
//        List<ProjectVisa> projectVisaList = null;
//        if(keyword != null && !keyword.isEmpty()) {
//            keyword = "%" + keyword.trim() + "%";
//            projectVisaList = projectVisaService.findByPage(pageModel, sort, order.equalsIgnoreCase("ASC") ? ASC : DESC,
//                    "VISA_NAME like ? OR CREATE_DATE like ?", keyword, keyword);
//        } else {
//            projectVisaList = projectVisaService.findByPage(pageModel, sort, order.equalsIgnoreCase("ASC") ? ASC : DESC, "1 = ?", 1);
//        }
//
//        int totalSize = projectVisaService.count("1 = ?", 1);
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("rows", projectVisaList);
//        jsonObject.put("total", totalSize);
//
//        return jsonObject.toJSONString();
//    }

//    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF)
//    @RequestMapping(path = "loadVisaItems", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
//    public @ResponseBody String loadVisaItems(@RequestParam(name="visaID", required = true) String visaID,
//                                     HttpServletRequest request, Model model) {
//
//        ProjectVisa projectVisa = projectVisaService.findOne(visaID);
////        List<ProjectVisaEntry> projectVisaEntryList = projectVisa.getVisaEntryList();
//        model.addAttribute("projectVisa", projectVisa);
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("message", "success");
//        jsonObject.put("visaEntryList", projectVisa.getVisaEntryList());
//
//        return JSON.toJSONString(jsonObject, SerializerFeature.DisableCircularReferenceDetect);
//    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF, excludeLevel = {UserConfs.RoleCode.USER_TYPE_EXTERNAL_AUDITER})
    @RequestMapping(path = "createHiddenProject", method = RequestMethod.POST)
    public @ResponseBody
    String createProjectVisa(@RequestParam(name="projectID", required = true) String projectID,
                             @RequestParam(name="hiddenProjectName", required = true) String hiddenProjectName,
                             @RequestParam(name="contentArray[]", required = true) String[] contentArray,
                             @RequestParam(name="qtyArray[]", required = true) String[] qtyArray,
                             HttpServletRequest request, Model model) {

        List<HiddenProjectEntry> hiddenProjectEntryList = new ArrayList<HiddenProjectEntry>(contentArray.length);

        for(int i=0; i<contentArray.length; i++) {
            hiddenProjectEntryList.add(new HiddenProjectEntry(contentArray[i], qtyArray[i]));
        }

        FileInfo hiddenProjectHomeFolder = null;
        List<FileInfo> result = fileService.findAll("BELONG_TO_PROJECT = ? AND FILE_NAME = ?", projectID, "隐蔽工程");
        if(result != null && result.size() > 0) hiddenProjectHomeFolder = result.get(0);
        else {
            return JSON.toJSONString(new ReturnMessage("创建新隐蔽工程目录失败: 找不到隐蔽工程根目录"));
        }

        StringBuffer grantedUserIDs = new StringBuffer();
        userService.findByDeptID(super.getOperator().getDeptID()).forEach(u -> grantedUserIDs.append(u.getUID()).append(","));

        FileInfo newHiddenProjectFolder = fileService.createFile(super.getOperator(), String.format("%s-%s", TimeFormater.format("yyyyMMdd"), hiddenProjectName), FileInfo.FOLDER_TYPE, hiddenProjectHomeFolder.getUUID(),
                "", grantedUserIDs.toString(), "", projectID, FileInfo.NON_EDITABLE);
        if(newHiddenProjectFolder == null) {
            return JSON.toJSONString(new ReturnMessage("创建新工程签证目录失败"));
        }

        HiddenProject hiddenProject = new HiddenProject(hiddenProjectName, projectID, TimeFormater.format("yyyy/MM/dd"), super.getOperator().getUID(), super.getOperator().getName(), newHiddenProjectFolder.getUUID(), hiddenProjectEntryList);

        Project project = projectService.findOne(projectID);

        createWordFiles(super.getOperator(), project, hiddenProject, request.getSession().getServletContext().getRealPath(DEFAULT_TEMPLATE_FILE_PATH), grantedUserIDs.toString());

        return hiddenProjectService.add(hiddenProject) == null ? JSON.toJSONString(new ReturnMessage("创建隐蔽工程失败"))
                : JSON.toJSONString(new ReturnMessage("创建隐蔽工程成功"));

    }

    private boolean createWordFiles(User user, Project project, HiddenProject hiddenProject, String templateFolderPath, String grantedUserIDs) {

        String tmpFolder = System.getProperty("sys.tmp.upload");

        Map<String, String> params = new HashMap<String, String>();
        params.put("contractor", project.getProjectContractorName());
        params.put("supervision", project.getSupervisionName());
        params.put("hiddenPosition", hiddenProject.getConstructionPosition());
        params.put("contentList", hiddenProject.getHiddenProjectEntryList().stream().
                map(hiddenProjectEntry -> String.format("%s: %s", hiddenProjectEntry.getContent(), hiddenProjectEntry.getQty())).collect(Collectors.joining("<wbr>")));
        params.put("projectName", project.getProjectName());

        WordTemplate template = null;
        FileInputStream fileInputStream = null;
        OutputStream out = null;
        try {
            fileInputStream = new FileInputStream(new File(templateFolderPath + PATH_SEPARATOR + "隐蔽工程现场收方计量记录表模板.docx"));
            template = new WordTemplate(fileInputStream);
            template.replaceTag(params);

            Path hiddenProjectFile = Paths.get(tmpFolder + PATH_SEPARATOR + String.format("%s-隐蔽工程现场收方计量记录表.docx", hiddenProject.getConstructionPosition()));
            out = Files.newOutputStream(hiddenProjectFile);
            template.write(out);

            InputStream in = Files.newInputStream(hiddenProjectFile);
            fileService.createFile(user, hiddenProjectFile.getFileName().toString().split("\\.")[0], "docx", FileInfo.FILE_TYPE, hiddenProject.getHiddenProjectFolderID(),
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

        return true;
    }


}