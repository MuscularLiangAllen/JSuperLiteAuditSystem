package com.liangtee.jsuperlite.auditsys.AOP;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.liangtee.jsuperlite.auditsys.Annotation.AccessControl;
import com.liangtee.jsuperlite.auditsys.Annotation.AccessControlJson;
import com.liangtee.jsuperlite.auditsys.model.User;
import com.liangtee.jsuperlite.auditsys.utils.TimeFormater;
import com.liangtee.jsuperlite.auditsys.values.json.ReturnMessage;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class PermissionAspect {

	@Autowired
	private HttpServletRequest request;

	@Pointcut("execution(* com.liangtee.jsuperlite.auditsys.web.internal.*.*(..))")
	public void isLogin() {}

	@Pointcut("@annotation(com.liangtee.jsuperlite.auditsys.Annotation.AccessControl)")
	public void accessControl() {}

	@Pointcut("@annotation(com.liangtee.jsuperlite.auditsys.Annotation.AccessControlJson)")
	public void accessControlJson() {}

	@Around("isLogin()")
	public String loginCheck(ProceedingJoinPoint pjp) throws Throwable {
		if(request.getSession().getAttribute("user") == null) return "redirect:/";
		request.setAttribute("currentDate", TimeFormater.format("yyyy/MM/dd"));
		return pjp.proceed().toString();
	}

	@Around(value = "accessControl()&&@annotation(accessControl)", argNames = "accessControl")
	public String permissionCheck(ProceedingJoinPoint pjp, AccessControl accessControl) throws Throwable {
		User user = (User) request.getSession().getAttribute("user");
		if(user.getUserType() < accessControl.accessLevel()) return "internal/exception/denied";

		if(accessControl.excludeLevel().length > 0) {
			int excludeLevelCode = -100;
			for(int i=0; i<accessControl.excludeLevel().length; i++) {
				if(user.getUserType() == accessControl.excludeLevel()[i]) return "internal/exception/denied";
			}

		}

		return pjp.proceed().toString();
	}

//	@Around(value = "accessControlJson()&&@annotation(accessControlJson)", argNames = "accessControlJson")
//	public String permissionCheckJson(ProceedingJoinPoint pjp, AccessControlJson accessControlJson) throws Throwable {
//		User user = (User) request.getSession().getAttribute("user");
//		if(user.getUserType() < accessControlJson.accessLevel()) return JSON.toJSONString(new ReturnMessage("权限不足"));
//
//		return pjp.proceed().toString();
//	}
}
	 
