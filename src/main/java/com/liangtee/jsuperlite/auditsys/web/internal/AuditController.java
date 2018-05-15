package com.liangtee.jsuperlite.auditsys.web.internal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.liangtee.jsuperlite.auditsys.Annotation.AccessControl;
import com.liangtee.jsuperlite.auditsys.model.*;
import com.liangtee.jsuperlite.auditsys.service.*;
import com.liangtee.jsuperlite.auditsys.service.base.PageModel;
import com.liangtee.jsuperlite.auditsys.utils.DocxGenerator;
import com.liangtee.jsuperlite.auditsys.utils.TimeFormater;
import com.liangtee.jsuperlite.auditsys.utils.wordtemplate.WordTemplate;
import com.liangtee.jsuperlite.auditsys.values.UserConfs;
import com.liangtee.jsuperlite.auditsys.values.json.ReturnMessage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Allen on 2017/8/10.
 */

@Controller
@RequestMapping("/auditsys/audits")
public class AuditController extends BaseController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectNodeService projectNodeService;

    @Autowired
    private AuditService auditService;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF)
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
//        model.addAttribute("auditCompanyList", projectService.findAll(Organization.class, "ORG_TYPE_CODE = ?", OrgConfs.TypeCode.ORG_TYPE_AUDIT_COMPANY));

        return "content_pages/sys-audits";
    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF)
    @RequestMapping(path = "show2", method = RequestMethod.GET)
    public String show2(@RequestParam(name="category", required = true) String category,
                       HttpServletRequest request, Model model) {

        if(category.equalsIgnoreCase("projectNode")) {
            model.addAttribute("categoryName", "工程节点");
        }
        if(category.equalsIgnoreCase("projectVisa")) {
            model.addAttribute("categoryName", "工程签证");
        }
        if(category.equalsIgnoreCase("hiddenProject")) {
            model.addAttribute("categoryName", "隐蔽工程");
        }

        return "content_pages/sys-audits2";
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

        int totalSize = projectService.count("1 = ?", 1);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rows", projectList);
        jsonObject.put("total", totalSize);

        return jsonObject.toJSONString();
    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_EXTERNAL_AUDITER)
    @RequestMapping(path = "process/{projectID}", method = RequestMethod.GET)
    public String showPanel(@PathVariable String projectID,
                       HttpServletRequest request, Model model) {

        if(projectID == null) return "content_pages/sys-audits";

        Project project = projectService.findOne(projectID);

        model.addAttribute("project", project);
        model.addAttribute("projectVisaList", auditService.getProjectVisaByProjectID(projectID));

        return "content_pages/sys-audit-process";

    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF, excludeLevel = {UserConfs.RoleCode.USER_TYPE_EXTERNAL_AUDITER})
    @RequestMapping(path = "createProjectNode", method = RequestMethod.POST)
    public @ResponseBody String createProjectNode(@RequestParam(name="projectID", required = true) String projectID,
                                                  @RequestParam(name="projectNodeName", required = true) String projectNodeName,
                                                  @RequestParam(name="startDate", required = true) String startDate,
                                                  @RequestParam(name="endDate", required = true) String endDate,
                                                  @RequestParam(name="desc", required = true) String desc,
                                                  @RequestParam(name="tmpFileID", required = true) String tmpFileID,
                                                  HttpServletRequest request, Model model) {

        if(projectNodeService.isExist("BELONG_TO = ? AND NAME = ?", projectID, projectNodeName)) {
            JSON.toJSONString(new ReturnMessage("该节点名已存在, 同一工程下不能存在相同名称节点!"));
        }

        int seq = (int) (projectNodeService.count("BELONG_TO = ?", projectID)+1);

        ProjectNode projectNode = null;

        if((projectNode = projectNodeService.add(new ProjectNode(UUID.randomUUID().toString(), projectID, seq, projectNodeName, startDate, endDate, desc, tmpFileID))) != null) {
            FileInfo projectNodeHomeFolder = null;
            List<FileInfo> result = fileService.findAll("BELONG_TO_PROJECT = ? AND FILE_NAME = ?", projectID, "工程节点");
            if(result != null && result.size() > 0) projectNodeHomeFolder = result.get(0);
            else {
                return JSON.toJSONString(new ReturnMessage("创建新工程节点目录失败: 找不到工程节点根目录"));
            }

            StringBuffer grantedUserIDs = new StringBuffer();
            userService.findByDeptID(super.getOperator().getDeptID()).forEach(u -> grantedUserIDs.append(u.getUID()).append(","));

            FileInfo newNodeFolder = fileService.createFile(super.getOperator(), String.format("%s%s", TimeFormater.format("yyyyMMdd"), projectNodeName), FileInfo.FOLDER_TYPE, projectNodeHomeFolder.getUUID(),
                    "", grantedUserIDs.toString(), "", projectID, FileInfo.NON_EDITABLE);
            if(newNodeFolder == null) {
                return JSON.toJSONString(new ReturnMessage("创建新工程节点目录失败"));
            }

            if(tmpFileID != null && !tmpFileID.isEmpty()) {
                FileInfo photo = fileService.createFile(super.getOperator(), String.format("%s", TimeFormater.format("yyyyMMddHHmmss")), FileInfo.FILE_TYPE, newNodeFolder.getUUID(),
                        "", grantedUserIDs.toString(), tmpFileID, projectID, FileInfo.NON_EDITABLE);
                if(photo == null)
                    return JSON.toJSONString(new ReturnMessage("#ERROR# 创建 工程节点图片 失败"));
            }

            projectNode.setNodeFolderID(newNodeFolder.getUUID());
            projectNodeService.update(projectNode);

            return JSON.toJSONString(new ReturnMessage("创建新工程节点成功!"));
        }


        return JSON.toJSONString(new ReturnMessage("创建新工程节点失败"));
    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF, excludeLevel = {UserConfs.RoleCode.USER_TYPE_EXTERNAL_AUDITER})
    @RequestMapping(path = "addNodePic", method = RequestMethod.POST)
    public @ResponseBody String addNodePic(@RequestParam(name="nodeID", required = true) String nodeID,
                                                  @RequestParam(name="tmpFileID", required = true) String tmpFileID,
                                                  HttpServletRequest request, Model model) {

        ProjectNode projectNode = projectNodeService.findOne(nodeID);

        System.out.println("node folder id: " + projectNode.getNodeFolderID());

        StringBuffer grantedUserIDs = new StringBuffer();
        userService.findByDeptID(super.getOperator().getDeptID()).forEach(u -> grantedUserIDs.append(u.getUID()).append(","));

        if(tmpFileID != null) {
            FileInfo photo = fileService.createFile(super.getOperator(), String.format("%s", TimeFormater.format("yyyyMMddHHmmss")), FileInfo.FILE_TYPE, projectNode.getNodeFolderID(),
                    "", grantedUserIDs.toString(), tmpFileID, projectNode.getBelongTo(), FileInfo.NON_EDITABLE);
            if(photo == null)
                return JSON.toJSONString(new ReturnMessage("#ERROR# 创建 工程节点图片 失败"));

            projectNode.setPhotos(projectNode.getPhotos().isEmpty() ? photo.getUUID() : String.format("%s,%s", projectNode.getPhotos(), photo.getUUID()));
            return projectNodeService.update(projectNode) != null ? JSON.toJSONString(new ReturnMessage("上传 工程节点图片 成功")) : JSON.toJSONString(new ReturnMessage("上传 工程节点图片 失败"));
        }


        return JSON.toJSONString(new ReturnMessage("未上传图片"));
    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF)
    @RequestMapping(path = "get/{nodeID}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody String getOneNode(@PathVariable String nodeID, HttpServletRequest request, Model model) {

        return JSON.toJSONString(projectNodeService.findOne(nodeID));
    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_PROJ_CONTROCTOR)
    @RequestMapping(path = "node-list-json", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String getNodeJsonList(@RequestParam(name="projectID", required = true) String projectID, HttpServletRequest request) {

        List<ProjectNode> nodeList = projectNodeService.findAll("BELONG_TO = ?", projectID);
        Collections.sort(nodeList);

        return JSON.toJSONString(nodeList, SerializerFeature.DisableCircularReferenceDetect);
    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF, excludeLevel = {UserConfs.RoleCode.USER_TYPE_EXTERNAL_AUDITER})
    @RequestMapping(path = "createProjectVisa", method = RequestMethod.POST)
    public @ResponseBody String createProjectVisa(@RequestParam(name="projectID", required = true) String projectID,
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

        createWordFiles(super.getOperator(), project, projectVisa, request.getSession().getServletContext().getRealPath(DEFAULT_TEMPLATE_FILE_PATH), grantedUserIDs.toString());

        return auditService.add(projectVisa) == null ? JSON.toJSONString(new ReturnMessage("创建工程签证失败"))
                : JSON.toJSONString(new ReturnMessage("创建工程签证成功"));

    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF, excludeLevel = {UserConfs.RoleCode.USER_TYPE_EXTERNAL_AUDITER})
    @RequestMapping(path = "deleteProjectVisa", method = RequestMethod.POST)
    public @ResponseBody String deleteProjectVisa(@RequestParam(name="projectVisaID", required = true) String projectVisaID,
                                                  HttpServletRequest request, Model model) {

        try {
            ProjectVisa deleted = auditService.get(projectVisaID);

            if(!fileService.delete(deleted.getVisaFolderID())) throw new Exception("删除工程签证出现异常");

            auditService.delete(deleted);
            logger.info(String.format("%s deleted Project Visa: %s", super.getOperator().getName(), deleted.getVisaName()));
            deleted = null;
            return JSON.toJSONString(new ReturnMessage("删除工程签证成功"));
        } catch (Exception e) {
            e.printStackTrace();
            return JSON.toJSONString(new ReturnMessage("删除工程签证失败"));
        }

    }

    private boolean createWordFiles(User user, Project project, ProjectVisa projectVisa, String templateFolderPath, String grantedUserIDs) {

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
                BufferedOutputStream bos = null;
                try {
                    fileInputStream = new FileInputStream(new File(templateFolderPath+PATH_SEPARATOR+"工程签证模板.docx"));
                    template = new WordTemplate(fileInputStream);
                    template.replaceTag(params);
//                    out = new FileOutputStream(new File(tmpFolder+PATH_SEPARATOR+projectVisa.getVisaName()+String.format("-工程签证-%d页.docx", pageNum)));
//                    bos = new BufferedOutputStream(out);
//                    template.write(bos);

                    Path visaFile = Paths.get(tmpFolder+PATH_SEPARATOR+projectVisa.getVisaName()+String.format("-工程签证-%d页.docx", pageNum));
                    out = Files.newOutputStream(visaFile);
                    template.write(out);

                    InputStream in = Files.newInputStream(visaFile);
                    fileService.createFile(user, visaFile.getFileName().toString().split("\\.")[0], "docx", FileInfo.FILE_TYPE, projectVisa.getVisaFolderID(),
                            "", grantedUserIDs, in, project.getID(), FileInfo.NON_EDITABLE);

                    out.close();
//                    bos.close();
                    in.close();
                } catch(IOException exception){
                    exception.printStackTrace();
                    return false;
                } finally {
                    try {
                        out.close();
//                        bos.close();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            }

        }

        return true;
    }
}
