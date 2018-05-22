package com.liangtee.jsuperlite.auditsys.web.internal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.liangtee.jsuperlite.auditsys.Annotation.AccessControl;
import com.liangtee.jsuperlite.auditsys.model.Organization;
import com.liangtee.jsuperlite.auditsys.model.ProjectNode;
import com.liangtee.jsuperlite.auditsys.model.User;
import com.liangtee.jsuperlite.auditsys.service.OrganizationService;
import com.liangtee.jsuperlite.auditsys.service.UserService;
import com.liangtee.jsuperlite.auditsys.service.base.PageModel;
import com.liangtee.jsuperlite.auditsys.utils.InjectionFilter;
import com.liangtee.jsuperlite.auditsys.utils.MD5Encoder;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: LIANG Tianyi
 * Created by LIANG Tianyi on 2017/5/18.
 * All rights Reserved
 */

@Controller
@RequestMapping("/auditsys/users")
public class UserController {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private UserService userService;

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_VIEWER)
    @RequestMapping(path = "show", method = RequestMethod.GET)
    public String show(HttpServletRequest request, Model model) {

        Map<Integer, List<User>> userMap = userService.getAll();
        model.addAttribute("userMap", userMap);

        return "content_pages/sys-users";
    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_DEPT_STUFF)
    @RequestMapping(path = "load", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody String load(@RequestParam(name="limit", required = true) int limit,
                                     @RequestParam(name="offset", required = true) int offset,
                                     @RequestParam(name="keyword", required = false) String keyword,
                                     HttpServletRequest request, Model model) {

        PageModel pageModel = new PageModel(limit, (limit + offset)/limit);

        List<Object> userTreeList = null;
        if(keyword != null && !keyword.isEmpty()) {
            keyword = "%" + keyword.trim() + "%";
            userTreeList = userService.getUserTree(pageModel, organizationService.getAll(), "USER_NAME LIKE ? OR EMAIL LIKE ? OR PHONE_NUMBER LIKE ?",
                    keyword, keyword, keyword);
        } else {
            userTreeList = userService.getUserTree(pageModel, organizationService.getAll(), "1 = ?", 1);
        }

        int totalSize = userTreeList.size();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rows", userTreeList);
        jsonObject.put("total", totalSize);

        return jsonObject.toJSONString();
    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_ADMIN)
    @RequestMapping(path = "add", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String add(@RequestParam(value = "userName", required = true) String userName,
                      @RequestParam(value = "passwd", required = true) String passwd,
                      @RequestParam(value = "orgID", required = true) Integer orgID,
                      @RequestParam(value = "roleCode", required = true) Integer roleCode,
                      @RequestParam(value = "userTel", required = true) String userTel,
                      @RequestParam(value = "userMail", required = true) String userMail,
                      @RequestParam(value = "isActive", required = true) boolean isActive) {

        String message = "";

        if(InjectionFilter.filter(userName, passwd, userTel, userMail)) {
            message = "\"(警告)拒绝访问\"";
            return "{\"message\":" + message + "}";
        }

        if(userService.isExist("USER_NAME = ? AND DEPT_ID = ?", userName, orgID)) {
            message = "\"用户名已存在\"";
            return "{\"message\":" + message + "}";
        }

        return userService.add(userName, MD5Encoder.get2RoundsHash(passwd, "MD5"), orgID, roleCode, userMail, userTel, isActive) != null ?
                "{\"message\": \"添加用户成功\"}":"{\"message\": \"添加用户失败\"}";

    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_ADMIN)
    @RequestMapping(path = "update", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String update(@RequestParam(value = "userID", required = true) Long userID,
                                    @RequestParam(value = "userName", required = true) String userName,
                                    @RequestParam(value = "passwd", required = true) String passwd,
                                    @RequestParam(value = "orgID", required = true) Integer orgID,
                                    @RequestParam(value = "roleCode", required = true) Integer roleCode,
                                    @RequestParam(value = "userTel", required = true) String userTel,
                                    @RequestParam(value = "userMail", required = true) String userMail,
                                    @RequestParam(value = "isActive", required = true) boolean isActive) {

        String message = "";

        if(InjectionFilter.filter(userName, passwd, userTel, userMail)) {
            message = "\"(警告)拒绝访问\"";
            return "{\"message\":" + message + "}";
        }

        if(userService.isExist("USER_NAME = ? AND DEPT_ID = ? AND UID <> ?", userName, orgID, userID)) {
            message = "\"用户名已存在\"";
            return "{\"message\":" + message + "}";
        }

        return userService.update(userID, userName, passwd, orgID, roleCode, userMail, userTel, isActive) != null ?
                "{\"message\": \"修改用户信息成功\"}":"{\"message\": \"修改用户信息失败\"}";
    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_ADMIN)
    @RequestMapping(path = "get", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String get(@RequestParam(value = "userID", required = true) Long userID) {

        User user = userService.get(userID);

        System.out.println(JSON.toJSONString(user));
        return JSON.toJSONString(user);

    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_ADMIN)
    @RequestMapping(path = "delete", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String delete(@RequestParam(value = "UID", required = true) Long UID,
                  HttpServletRequest request) {

        String message = "";

        User currentUser = (User) request.getSession().getAttribute("user");
        User deletedUser = userService.get(UID);

        if(currentUser.getUserType() <= deletedUser.getUserType()) {
            message = "\"无法删除同级别和更高级别用户\"";
            return "{\"message\":" + message + "}";
        }

        return userService.delete("UID = ?", UID) == true ? "{\"message\": \"删除成功\"}" : "{\"message\": \"删除失败\"}";

    }

    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_PROJ_CONTROCTOR)
    @RequestMapping(path = "user-list-json", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody String getUserJsonList(HttpServletRequest request) {

        Map<Integer, List<User>> userMap = userService.getAll();
        List<Organization> orgList = (List<Organization>) request.getAttribute("orgList");

        List<Node> zTreeNodeList = new ArrayList<Node>();

        orgList.forEach(o -> {zTreeNodeList.add(new Node(o.getID(), o.getBelongTo(), o.getOrgName()));
            if(userMap.containsKey(o.getID())) {
                userMap.get(o.getID()).forEach(u->zTreeNodeList.add(new Node(u.getUID(), u.getDeptID(), u.getName())));
            }
        });

        System.out.println(JSON.toJSONString(zTreeNodeList, SerializerFeature.DisableCircularReferenceDetect));

        return JSON.toJSONString(zTreeNodeList, SerializerFeature.DisableCircularReferenceDetect);
    }


    class Node {
        @JSONField(name="id")
        long ID;
        @JSONField(name="pId")
        long pID;
        @JSONField(name="name")
        String name;

        public Node() {}

        public Node(long ID, long pID, String name) {
            this.ID = ID;
            this.pID = pID;
            this.name = name;
        }

        public long getID() {
            return ID;
        }

        public void setID(long ID) {
            this.ID = ID;
        }

        public long getpID() {
            return pID;
        }

        public void setpID(long pID) {
            this.pID = pID;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
