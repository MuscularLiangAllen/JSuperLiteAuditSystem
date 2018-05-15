package com.liangtee.jsuperlite.auditsys.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeFormater {

	public static String format(String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(new Date());
	}
	
}
