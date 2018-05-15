/**
 * Copyright (C), 2018-2022, Allen LIANG
 * FileName: ProjectNodeController
 * Author:   Allen
 * Date:     2018/5/11 21:44
 * Description: node
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.liangtee.jsuperlite.auditsys.web.internal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.liangtee.jsuperlite.auditsys.Annotation.AccessControl;
import com.liangtee.jsuperlite.auditsys.model.FileInfo;
import com.liangtee.jsuperlite.auditsys.model.Project;
import com.liangtee.jsuperlite.auditsys.model.ProjectNode;
import com.liangtee.jsuperlite.auditsys.model.User;
import com.liangtee.jsuperlite.auditsys.service.FileService;
import com.liangtee.jsuperlite.auditsys.service.ProjectNodeService;
import com.liangtee.jsuperlite.auditsys.service.UserService;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 〈node〉
 *
 * @author Allen
 * @create 2018/5/11
 * @since 0.0.1
 */

@Controller
@RequestMapping("/auditsys/project-nodes")
public class ProjectNodeController extends BaseController {

    @Autowired
    private ProjectNodeService projectNodeService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_EXTERNAL_AUDITER)
    @RequestMapping(path = "show", method = RequestMethod.GET)
    public String show(@RequestParam(name="projectID", required = true) String projectID,
                       HttpServletRequest request, Model model) {

        model.addAttribute("projectID", projectID);

        return "content_pages/sys-project-nodes";

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

        List<ProjectNode> projectNodeList = null;
        if(keyword != null && !keyword.isEmpty()) {
            keyword = "%" + keyword.trim() + "%";
            projectNodeList = projectNodeService.findByPage(pageModel, sort, order.equalsIgnoreCase("ASC") ? ASC : DESC,
                    "NAME like ? OR NODE_DESC like ?", keyword, keyword);
        } else {
            projectNodeList = projectNodeService.findByPage(pageModel, sort, order.equalsIgnoreCase("ASC") ? ASC : DESC, "1 = ?", 1);
        }

        int totalSize = projectNodeService.count("1 = ?", 1);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rows", projectNodeList);
        jsonObject.put("total", totalSize);

        return jsonObject.toJSONString();
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

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_PROJ_CONTROCTOR)
    @RequestMapping(path = "node-list-json", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String getNodeJsonList(@RequestParam(name="projectID", required = true) String projectID, HttpServletRequest request) {

        System.out.println("node-list");

        List<ProjectNode> nodeList = projectNodeService.findAll("BELONG_TO = ?", projectID);
        Collections.sort(nodeList);

        return JSON.toJSONString(nodeList, SerializerFeature.DisableCircularReferenceDetect);
    }

}