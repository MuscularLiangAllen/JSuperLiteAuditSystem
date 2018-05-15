package com.liangtee.jsuperlite.auditsys;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import java.util.Collections;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(JSuperLiteAuditSystemApplication.class);
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);

		// This will set to use COOKIE only
		servletContext.setSessionTrackingModes(
				Collections.singleton(SessionTrackingMode.COOKIE)
		);
		// This will prevent any JS on the page from accessing the
		// cookie - it will only be used/accessed by the HTTP transport
		// mechanism in use
		SessionCookieConfig sessionCookieConfig =
				servletContext.getSessionCookieConfig();
		sessionCookieConfig.setHttpOnly(true);
	}
}
