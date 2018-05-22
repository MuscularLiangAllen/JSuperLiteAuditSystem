package com.liangtee.jsuperlite.auditsys.values;

/**
 * Author: LIANG Tianyi
 * Created by LIANG Tianyi on 2017/5/21.
 * All rights Reserved
 */
public class OrgConfs {

    public static final int NO_PARENT_ORG = 0;

    public class TypeCode {

        public static final int ORG_TYPE_UNIVERSITY = 1;

        public static final int ORG_TYPE_DEPT = 2;

        public static final int ORG_TYPE_AUDIT_COMPANY = 3;

        public static final int ORG_TYPE_CONSTRUCTOR = 4;

    }

    public static String getTypeDesc(int typeCode) {

        String orgDesc = null;

        switch (typeCode) {
            case TypeCode.ORG_TYPE_UNIVERSITY:
                orgDesc = "学校";
                break;
            case TypeCode.ORG_TYPE_DEPT:
                orgDesc = "处室/院系";
                break;
            case TypeCode.ORG_TYPE_AUDIT_COMPANY:
                orgDesc = "第三方审计单位";
                break;
            case TypeCode.ORG_TYPE_CONSTRUCTOR:
                orgDesc = "施工单位";
                break;

            default:
                break;
        }
        return orgDesc;
    }

}
