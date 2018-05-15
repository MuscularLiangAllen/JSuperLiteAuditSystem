package com.liangtee.jsuperlite.auditsys.values;

import javax.persistence.Transient;

/**
 * Author: LIANG Tianyi
 * Created by LIANG Tianyi on 2017/5/18.
 * All rights Reserved
 */
public class UserConfs {

    public class RoleCode {

        public static final int USER_TYPE_ROOT_ADMIN = 12;

        public static final int USER_TYPE_ADMIN = 10;

        public static final int USER_TYPE_VIEWER = 9;

        public static final int USER_TYPE_INTERNAL_AUDITER = 8;

        public static final int USER_TYPE_EXTERNAL_AUDITER = 7;

        public static final int USER_TYPE_DEPT_SENIOR = 6;

        public static final int USER_TYPE_DEPT_STUFF = 5;

        public static final int USER_TYPE_PROJ_CONTROCTOR = 4;

    }

    public static String getRoleDesc(int roleCode) {

        String roleDesc = null;

        switch (roleCode) {
            case RoleCode.USER_TYPE_ROOT_ADMIN:
                roleDesc = "超级系统管理员";
                break;
            case RoleCode.USER_TYPE_ADMIN:
                roleDesc = "系统管理员";
                break;
            case RoleCode.USER_TYPE_VIEWER:
                roleDesc = "体验用户";
                break;
            case RoleCode.USER_TYPE_INTERNAL_AUDITER:
                roleDesc = "内部审计员";
                break;
            case RoleCode.USER_TYPE_EXTERNAL_AUDITER:
                roleDesc = "外部审计员";
                break;
            case RoleCode.USER_TYPE_DEPT_SENIOR:
                roleDesc = "部门主管";
                break;
            case RoleCode.USER_TYPE_DEPT_STUFF:
                roleDesc = "部门员工";
                break;
            case RoleCode.USER_TYPE_PROJ_CONTROCTOR:
                roleDesc = "合同承包方";
                break;

            default:
                break;
        }
        return roleDesc;
    }
}
