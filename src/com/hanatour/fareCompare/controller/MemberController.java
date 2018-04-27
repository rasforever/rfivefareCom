/**
 * File name : MainController.java
 * Package : com.hanatour.fareCompare.controller
 * Description : 
 *
 * <pre>
 * <변경이력>
 * 수정일			수정자				수정내용
 * ------------------------------------------------------
 * 2017. 10. 17.		sthan	최초 작성
 * </pre>
 */
package com.hanatour.fareCompare.controller;


import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana.security.cryptography.StringEncrypter;
import com.hanatour.fareCompare.dao.FareInfoDao;
import com.hanatour.fareCompare.dao.MemberDao;
import com.hanatour.fareCompare.model.FareInfoVo;
import com.hanatour.fareCompare.model.MemberVo;
import com.hanatour.fareCompare.model.UserVo;
import com.hanatour.fareCompare.util.CommonUtil;
import com.hanatour.fareCompare.util.CookieUtils;
import com.hanatour.fareCompare.util.SiteUtils;
import com.hanatour.util.EncryptUtil;
import com.hanatour.util.SymmetricKeyUtils;

/**
 * Class name : MainController
 * Description : 메인페이지 컨트롤러
 * @author sthan
 * @author 2017. 10. 17.
 * @version 1.0
 */
@Controller
public class MemberController {
	private static Logger commonLogger = LogManager.getLogger("COMMONLOGGER");
	
	@Autowired
	ReloadableResourceBundleMessageSource messageSource;
	
	@Autowired
	MemberDao memberdao;
	
	
	/**
	 * 메인페이지 호출
	 */
	@RequestMapping({"/", "/main/index.hnt"})
	public ModelAndView main(HttpServletRequest request, HttpServletResponse response, ModelAndView model) throws Exception{
		/*
		String month = "4";
		String week = "1";
		String dayOfWeek = "1"; //1일요일 ~ 7월요일
		String strDate = "";
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		
 		Calendar c = Calendar.getInstance(); 		
 		c.setTime(new Date());
 		
 		c.add(Calendar.MONTH, Integer.parseInt(month)); //월을 더한다
 		int schMonth = c.get(c.MONTH)+1;	// 조회 월 설정
 		
 		c.set(Calendar.WEEK_OF_MONTH, 1); 	// 해당 월의 첫주차를 설정 
	    c.set(Calendar.DAY_OF_WEEK, 5);		// 조회 월의 첫째주의 목요일 설정
	    
	    System.out.println("해당 월의 첫째주 목요일이 몇일인지 ======="+formatter.format(c.getTime()));
	    
	    int tmpMonth = c.get(c.MONTH)+1; // 조회월의 첫째주 목요일이 이전 달이면 조회 주는 +1 을 한다.
	    
	    if( schMonth > tmpMonth  ){
	    	c.setTime(new Date());
	 		c.add(Calendar.MONTH, Integer.parseInt(month)); //월을 더한다
		    c.set(Calendar.WEEK_OF_MONTH, Integer.parseInt(week)+1); // 주차를 더한다 
		    c.set(Calendar.DAY_OF_WEEK, Integer.parseInt(dayOfWeek));
	    }else{
	    	c.setTime(new Date());
	 		c.add(Calendar.MONTH, Integer.parseInt(month)); //월을 더한다
		    c.set(Calendar.WEEK_OF_MONTH, Integer.parseInt(week)); // 주차를 더한다 
		    c.set(Calendar.DAY_OF_WEEK, Integer.parseInt(dayOfWeek));
	    }
	    
 		strDate = formatter.format(c.getTime());
		System.out.println("strDate======="+strDate);
		*/
		model.setViewName("/main/index");
		return model;
	}
	
	
	
	/**
	 * 운임 스케줄 배치 정보 검색 
	 * @throws Exception 
	 */
	@CrossOrigin
	@RequestMapping("/main/login.hnt")
	@ResponseBody
	public String login(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, ModelMap modelMap) throws Exception {
		String resultStr = "";
		
		Map dataInfo = new HashMap();
		
		String userId = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("userId"), ""));   //출발지
		String passWord = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("password"), ""));   //도착지
		passWord = passWord.toUpperCase();
		
		try{
			EncryptUtil keyloader = null;
			keyloader = new EncryptUtil("C:\\HanaEnc\\Hanatour.Bin");   //하나어스 파일 읽어오기
			String key = keyloader.getKey();
			String vector = keyloader.getVector();
			StringEncrypter hanaAuth = new StringEncrypter(key,vector);    //입력된 암호 암호화 하여 기존 암호와 비교
			String enPassWord = hanaAuth.encrypt(passWord);
			
			dataInfo.put("userId",userId);
			dataInfo.put("passWord",enPassWord);
			int userCount = memberdao.getLoingDataCount(dataInfo);

			if(userCount < 1){
				resultStr = "{\"ErrorCode\":\"-1\"}";
			}else{
				
				MemberVo mVo = new MemberVo();
				mVo = memberdao.getLoingData(dataInfo);
				
				String mem_id = mVo.getMem_id();
				String mem_nm = mVo.getMem_nm();
				String auth_cd = mVo.getAuth_cd();
				
				String enc_userId = hanaAuth.encrypt(mVo.getMem_id());
				String enc_menNm = hanaAuth.encrypt(mVo.getMem_nm());
				String enc_authCd = hanaAuth.encrypt(mVo.getAuth_cd());
				
				CookieUtils.setCookieValueByAttrName(response, "c_id" , enc_userId);
				CookieUtils.setCookieValueByAttrName(response, "c_nm" , enc_menNm);
				CookieUtils.setCookieValueByAttrName(response, "c_auth" , enc_authCd);
				
				resultStr = "{\"ErrorCode\":\"0\"}";
			}
		}catch(Exception e){
			resultStr = "{\"ErrorCode\":\"-99\"}";
			commonLogger.error("MemberController.login Exception :"+ ExceptionUtils.getMessage(e));
		}
		
		return resultStr;
	}
	
	/**
	 * 사용자 등록 페이지
	 */
	@RequestMapping("/member/member_join.hnt")
	public ModelAndView member_join(HttpServletRequest request, HttpServletResponse response, ModelAndView model) throws Exception{
		
		model.setViewName("/member/member_join");
		
		String userId = CookieUtils.getCookieValueByAttrName(request,"c_id");
		
		if( StringUtils.isBlank(userId) ){
			model.addObject("message", messageSource.getMessage("MESSAGE.NONLOGIN", null, null));
			model.setViewName("/member/member_join");
			return model;
		}else{
			userId = SymmetricKeyUtils.decryptBase64(userId);
		}
		
		
		return model;
	}
	
	/**
	 * 사용자 등록 insert
	 */
	@RequestMapping("/member/member_insertProc.hnt")
	@ResponseBody
	public String member_insertProc(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception{
		String resultStr = "";
		
		try{
			UserVo user = new UserVo();
			
			String user_id = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("user_id"), ""));
			String user_nm = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("user_nm"), ""));
			String user_auth = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("user_auth"), ""));
			String passWord = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("passWord"), ""));
			user_id = user_id.toUpperCase();
			passWord = passWord.toUpperCase();
			
			
			passWord = SymmetricKeyUtils.encryptBase64(passWord);
			
			user.setMem_id(user_id);
			user.setMem_nm(user_nm);
			user.setMem_pwd(passWord);
			user.setAuth_cd(user_auth);
			user.setUse_yn("Y");
			
			int result = memberdao.insertUserInfo(user);
			
			if(result > 0 ){
				resultStr = "{\"ErrorCode\":\"0\"}";
			}else{
				resultStr = "{\"ErrorCode\":\"-1\"}";
			}
			
		}catch(Exception e){
			resultStr = "{\"ErrorCode\":\"-99\"}";
			commonLogger.error("MemberController.member_insertProc Exception :"+ ExceptionUtils.getMessage(e));
		}
		
		
		return resultStr;
	}
	
	/**
	 * 로그아웃
	 */
	@RequestMapping({"/member/signOut.hnt"})
	public String signOut(HttpServletRequest request, HttpServletResponse response, ModelAndView model) throws Exception{
		
		CookieUtils.clearCookie(request, response);
		
		return "redirect:/";
	}
}
