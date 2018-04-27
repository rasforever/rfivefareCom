/**
 * File name : SiteUtils.java
 * Package : com.hanatour.fareCompare.util
 * Description : 
 *
 * <pre>
 * <변경이력>
 * 수정일			수정자				수정내용
 * ------------------------------------------------------
 * 2018. 1. 8.		hana	최초 작성
 * </pre>
 */
package com.hanatour.fareCompare.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.hana.security.cryptography.StringEncrypter;
import com.hanatour.fareCompare.dao.MemberDao;
import com.hanatour.util.EncryptUtil;

/**
 * Class name : SiteUtils
 * Description :
 * @author hana
 * @author 2018. 1. 8.
 * @version 1.0
 */
public class SiteUtils {
	
	
	public static Boolean isLogin(String authCookie) throws Exception{
		Boolean isLogin = false;
		MemberDao memberdao = new MemberDao();
		
		Map dataInfo = new HashMap();
		dataInfo.put("userId",authCookie);
		int userCount = memberdao.getLoingDataCount(dataInfo);
		
		if( userCount > 0){
			isLogin = true;
		}
		
		return isLogin;
	}
}
