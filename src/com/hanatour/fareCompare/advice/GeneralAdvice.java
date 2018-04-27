/**
 * File name : GeneralAdvice.java
 * Package : com.hanatour.fareCompare.advice
 * Description : 클라이언트 view 에서 사용할 model advice
 *
 * <pre>
 * <변경이력>
 * 수정일			수정자				수정내용
 * ------------------------------------------------------
 * 2017. 10. 25.		sthan	최초 작성
 * </pre>
 */
package com.hanatour.fareCompare.advice;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

import com.hana.security.cryptography.StringEncrypter;
import com.hanatour.fareCompare.model.MemberVo;
import com.hanatour.fareCompare.model.SystemDefinition;
import com.hanatour.fareCompare.util.CookieUtils;
import com.hanatour.util.EncryptUtil;
/**
 * Class name : GeneralAdvice
 * Description :
 * @author sthan
 * @author 2017. 10. 25.
 * @version 1.0
 */
@ControllerAdvice
public class GeneralAdvice {
	
	@Autowired 
	ResourceUrlProvider resourceUrlProvider;
	
	@Autowired 
	SystemDefinition systemDefinition;
	
	@ModelAttribute("resourceUrlProvider")
	public ResourceUrlProvider resourceUrlProvider() {
		return this.resourceUrlProvider;
	}
	
	@ModelAttribute("systemDefinition")
	public SystemDefinition systemDefinition() {
		return this.systemDefinition;
	}
	
	@ModelAttribute("member")
	public MemberVo member( HttpServletRequest request, HttpServletResponse response, @CookieValue(value="c_id", required=false, defaultValue="") String authCookie  ) throws Exception {
		MemberVo mData = new MemberVo();
		if(!StringUtils.isEmpty(authCookie)){
			authCookie = CookieUtils.getCookieValueByAttrName(request, "c_id");
			String c_nm = CookieUtils.getCookieValueByAttrName(request, "c_nm");
			String c_auth = CookieUtils.getCookieValueByAttrName(request, "c_auth");
			
			if( !StringUtils.isBlank(authCookie) && !StringUtils.isBlank(c_nm) && !StringUtils.isBlank(c_auth) ){
				
				EncryptUtil keyloader = null;
				keyloader = new EncryptUtil("C:\\HanaEnc\\Hanatour.Bin");   //하나어스 파일 읽어오기
				String key = keyloader.getKey();
				String vector = keyloader.getVector();
				StringEncrypter hanaAuth = new StringEncrypter(key,vector);    //입력된 암호 암호화 하여 기존 암호와 비교
				
				String dec_id = hanaAuth.decrypt(authCookie);
				String dec_nm = hanaAuth.decrypt(c_nm);
				String dec_auth = hanaAuth.decrypt(c_auth);
				
				mData.setMem_id(dec_id);
				mData.setMem_nm(dec_nm);
				mData.setAuth_cd(dec_auth);
				mData.setIsLogin("Y");
				
			}else{
				mData.setIsLogin("N");
			}
		}
		
		return mData;
	}
	
}
