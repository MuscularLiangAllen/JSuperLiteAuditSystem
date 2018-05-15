package com.liangtee.jsuperlite.auditsys.init;

import com.liangtee.jsuperlite.auditsys.model.Organization;
import com.liangtee.jsuperlite.auditsys.model.User;
import com.liangtee.jsuperlite.auditsys.service.OrganizationService;
import com.liangtee.jsuperlite.auditsys.service.UserService;
import com.liangtee.jsuperlite.auditsys.utils.FileUtils;
import com.liangtee.jsuperlite.auditsys.utils.MD5Encoder;
import com.liangtee.jsuperlite.auditsys.values.OrgConfs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Author: LIANG Tianyi
 * Created by LIANG Tianyi on 2017/4/18.
 * All rights Reserved
 */

@Component
public class SystemStartupInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SystemStartupInitializer.class);

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private UserService userService;

    @Override
    public void run(String... strings) throws Exception {

        System.out.println("===================================================");
        System.out.println("DeepInsight Audit System (DeepInsight 审计管理系统)  ");
        System.out.println("      Author: LIANG Tianyi (Allen LIANG)           ");
        System.out.println("            All Rights Reserved                    ");
        System.out.println("===================================================");

        Organization org = null;
        if(!organizationService.isExist("ORG_NAME = ?", "运营维护中心"))
            org = organizationService.add(new Organization(OrgConfs.NO_PARENT_ORG, 1, "运营维护中心",
                    OrgConfs.TypeCode.ORG_TYPE_DEPT, "18660163087"));
        else org = organizationService.findAll("ORG_NAME = ?", "运营维护中心").get(0);

        userService.add("kingroot", "504B2BA8E6B0B74FE61AF448D6C3772D",
                org.getID(), User.USER_TYPE_ROOT_ADMIN, "liangtee@126.com", "13000000000", true);

        log.info("###### The Super Admin User has been created ... ######");

//        System.setProperty("user.home", System.getProperty("user.home")+System.getProperty("file.separator")+"JSuperLiteFS");
        System.setProperty("sys.home", String.format("%s%sAuditSys_FS", System.getProperty("user.home"), System.getProperty("file.separator")));
        System.setProperty("sys.workplace", String.format("%s%sworkplace", System.getProperty("sys.home"), System.getProperty("file.separator")));
        System.setProperty("sys.tmp.upload", String.format("%s%stmp_upload", System.getProperty("sys.home"), System.getProperty("file.separator")));
        System.setProperty("sys.trash",String.format("%s%strash", System.getProperty("sys.home"), System.getProperty("file.separator")));

        FileUtils.mkdir(System.getProperty("sys.home"));
        FileUtils.mkdir(System.getProperty("sys.workplace"));
        FileUtils.mkdir(System.getProperty("sys.tmp.upload"));
        FileUtils.mkdir(System.getProperty("sys.trash"));

        log.info("###### System initialized successfully ... ######");

    }
}
