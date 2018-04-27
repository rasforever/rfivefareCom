/**
 * File name : SearchController.java
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


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
//import com.hanatour.gmair.util.CookieUtils;
//import com.hanatour.util.SymmetricKeyUtils;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanatour.fareCompare.dao.FareDao;
import com.hanatour.fareCompare.dao.MemberDao;
import com.hanatour.fareCompare.model.SystemDefinition;
import com.hanatour.fareCompare.service.FareSchdSvc;

/**
 * Class name : SearchController
 * Description : 메인페이지 컨트롤러
 * @author sthan
 * @author 2017. 10. 17.
 * @version 1.0
 */
@Controller
public class SearchController {
	private static Logger commonLogger = LogManager.getLogger("COMMONLOGGER");
	
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	SystemDefinition systemDefinition;
	
	@Autowired
	ReloadableResourceBundleMessageSource messageSource;
	
	@Autowired
	FareSchdSvc fareSchdSvc;
	
	@Autowired
	FareDao fareDao;
	
	/**
	 * 실시간 운임비교 리스트 페이지
	 */
	
	@RequestMapping(value={"/farecompare/searchFare.hnt"}, produces="application/text; charset=utf-8")
	@ResponseBody
	public String searchFare(HttpServletRequest request,  @RequestBody String requestBody, Model model) throws Exception{
		String resultStr = "";
		int hnt_cnt = 0;
		int mode_cnt = 0;
		int interpark_cnt = 0;
		int onlinetour_cnt = 0;
		int hntRT_cnt = 0;
		int interparkRT_cnt = 0;
		int onlinetourRT_cnt = 0;
		
		String search_num = "0";
		String search_seq = "0";
		
		String sessionkey = "";
		String dep_apo = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("dep_apo"), ""));
		String rtn_apo = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("rtn_apo"), ""));
		String dep_dt = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("dep_dt"), ""));
		String rtn_dt = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("rtn_dt"), ""));
		
		String h_dep_dt = StringUtils.replaceAll(dep_dt, "-", "");
		String h_rtn_dt = StringUtils.replaceAll(rtn_dt, "-", "");
		
		Map dataInfo = new HashMap();
		dataInfo.put("sch_seq","0");
		
		search_num = fareDao.selectFare_nvl_sch_num(dataInfo);

		if(request != null){
			sessionkey = request.getRequestedSessionId();
		}
		
		try{
			//mode_cnt = fareSchdSvc.searchMode(sessionkey, dep_apo, rtn_apo, dep_dt, rtn_dt , search_num, search_seq);
			//hnt_cnt = fareSchdSvc.searchHana(sessionkey, dep_apo, rtn_apo, h_dep_dt, h_rtn_dt, search_num , search_seq);
			//hntRT_cnt = fareSchdSvc.searchHanaRT(sessionkey, dep_apo, rtn_apo, h_dep_dt, h_rtn_dt, search_num , search_seq);
			interpark_cnt = fareSchdSvc.searchInPark(sessionkey, dep_apo, rtn_apo, h_dep_dt, h_rtn_dt, search_num , search_seq);
			//interparkRT_cnt = fareSchdSvc.searchInParkRT(sessionkey, dep_apo, rtn_apo, h_dep_dt, h_rtn_dt, search_num , search_seq);
			//onlinetour_cnt = fareSchdSvc.searchOnLine(sessionkey, dep_apo, rtn_apo, h_dep_dt, h_rtn_dt , search_num , search_seq);
			//onlinetourRT_cnt = fareSchdSvc.searchOnLineRT(sessionkey, dep_apo, rtn_apo, h_dep_dt, h_rtn_dt , search_num , search_seq);
					
			HashMap<String,Object> schOption = new HashMap<String,Object>();
			
			Date nowDate = new Date();
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	        String current_dt = sdf.format(nowDate);

			schOption.put("dep_dt",h_dep_dt);
			schOption.put("rtn_dt",h_rtn_dt);
			schOption.put("dep_apo",dep_apo);
			schOption.put("rtn_apo",rtn_apo);
			schOption.put("sch_num",search_num);
			
			List<HashMap<String, Object>> fareList = null;
				
			fareList = fareDao.selectFare(schOption);
			System.out.println("fareList size =============="+ fareList.size());
			
			if(fareList.size() > 0){
				ObjectMapper mapper = new ObjectMapper();	
				resultStr = mapper.writeValueAsString(fareList);
			}
			
			System.out.println("========================================================");
			System.out.println("resultStr=========="+resultStr);
			
		}catch(Exception e){
			commonLogger.error("SearchController.searchFare Exception :"+ ExceptionUtils.getMessage(e));
		}
		
		
		return resultStr;
	}
	
	
	/**
	 * 실시간 운임비교 리스트 페이지
	 */
	
	@RequestMapping(value={"/search/searchRealTime.hnt"})
	@ResponseBody
	public String searchRealTime(HttpServletRequest request, Model model) throws Exception{
		String resultStr = "";
		
		String seq = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("seq"), ""));
		
		System.out.println("seq==="+seq);
		
		if(StringUtils.isNotBlank(seq)){
			resultStr = fareSchdSvc.fareSearchRealTime(seq);
		}else{
			resultStr = "{\"ErrorCode\":\"-99\"}";
		}
		
		return resultStr;
	}
	
	
	
	
	/**
	 * 실시간 운임비교 리스트 페이지
	 */
	
	@RequestMapping(value={"/search/searchFareBatch.hnt"})
	public String searchFareBatch(HttpServletRequest request, Model model) throws Exception{
		String resultStr = "";
		
		resultStr = fareSchdSvc.fareSearchBatch();
		
		return resultStr;
	}

}
