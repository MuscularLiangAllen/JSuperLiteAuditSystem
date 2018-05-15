package com.liangtee.jsuperlite.auditsys.AOP;

import com.liangtee.jsuperlite.auditsys.model.Organization;
import com.liangtee.jsuperlite.auditsys.service.OrganizationService;
import com.liangtee.jsuperlite.auditsys.utils.TimeFormater;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Author: LIANG Tianyi
 * Created by LIANG Tianyi on 2017/6/2.
 * All rights Reserved
 */

@Aspect
@Component
public class ControllerAspect {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private OrganizationService organizationService;

    @Pointcut("execution(* com.liangtee.jsuperlite.auditsys.web.internal.UserController.show(..)) || " +
            "execution(* com.liangtee.jsuperlite.auditsys.web.internal.UserController.getUserJsonList(..)) || " +
            "execution(* com.liangtee.jsuperlite.auditsys.web.internal.OrganizationController.getOrgJsonList(..))")
    public void loadOrganizationInfo() {}

    @Around("loadOrganizationInfo()")
    public String getOrganizationInfo(ProceedingJoinPoint pjp) throws Throwable {
        List<Organization> orgList = organizationService.getAll();
        request.setAttribute("orgList", orgList);

        return pjp.proceed().toString();
    }


}
