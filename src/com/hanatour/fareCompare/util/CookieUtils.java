package com.hanatour.fareCompare.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("javadoc")
public class CookieUtils {
	public static String getCookieValueByAttrName(HttpServletRequest request, String name){
		
		Cookie[] cookieArray = request.getCookies();
		if (cookieArray != null) {
			for (Cookie cookie : cookieArray) {
				if (StringUtils.equals(name, cookie.getName())) {
					try {
						return URLDecoder.decode(StringUtils.trim(cookie.getValue()), CharEncoding.UTF_8);
					} catch (Exception e) {}
				}
			}
		}

		return null;
	}
	
	public static void setCookieValueByAttrName(HttpServletResponse response, String attributeName, String attributeValue){
		setCookieValueByAttrName(response,attributeName,attributeValue,-1);
	}
	
	public static void setCookieValueByAttrName(HttpServletResponse response, String attributeName, String attributeValue, int expiry){//60 * 60 * 24 * 15
			
		if (!StringUtils.isEmpty(attributeValue)) {
			try {	
				Cookie cookie = new Cookie(attributeName, URLEncoder.encode(attributeValue,  CharEncoding.UTF_8));
				cookie.setPath("/");
				cookie.setMaxAge(expiry);	
				response.addCookie(cookie);
			} catch (Exception e) {}
		}
	}
	
	public static String getCookieKeyToValue(HttpServletRequest request, String cookieName, String  cookieKey){
		
		String cookieValue = getCookieValueByAttrName( request, cookieName);
		if(cookieValue == null) return null;
		String[] arCookie = cookieValue.split("&");
		//System.out.println("cookieValue 2222 ===" +cookieValue);
		try{
			String returnString = "";
			for(int i=0; i < arCookie.length; i++){
				String str = arCookie[i];
				str =  URLDecoder.decode(StringUtils.trim(str), CharEncoding.UTF_8);
				if(str.indexOf(cookieKey) > -1 ){
					returnString = str.substring( str.indexOf("=") +1, str.length());
					returnString = returnString.replace("\"", "");
					break;
				}
			}
			//System.out.println("returnString ===" + returnString);
			return returnString;	
		}catch(Exception e){
			return null;
		}
	}
	
	public static void clearCookie(HttpServletRequest request, HttpServletResponse response){
		
		Cookie[] cookieArray = request.getCookies();
		if (cookieArray != null) {
			for (Cookie cookie : cookieArray) {
				cookie.setValue("");
				cookie.setPath("/");
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
		}
	}
	
	
	
	
	
}
