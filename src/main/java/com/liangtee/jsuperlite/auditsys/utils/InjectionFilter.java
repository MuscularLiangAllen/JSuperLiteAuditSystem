package com.liangtee.jsuperlite.auditsys.utils;

import java.util.regex.Pattern;


/**
 * Author : LIANG Tianyi
 * Created by LIANG Tianyi on 2017/4/4.
 */

public class InjectionFilter {

	private static final String REGEX = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|"  
            + "(\\b(select|update|and|or|delete|insert|truncate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";
	
	private static Pattern pattern = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);  
	
	public static boolean filter(String input) {
		return pattern.matcher(input).find();
	}
	
	public static boolean filter(String ...inputs) {
		for(String input : inputs) {
			if (pattern.matcher(input).find()) return true;
		}
		
		return false;
	}
	
}
