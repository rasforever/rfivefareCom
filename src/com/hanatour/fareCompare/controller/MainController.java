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


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;






import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;




import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanatour.fareCompare.dao.FareDao;
import com.hanatour.fareCompare.dao.FareInfoDao;
import com.hanatour.fareCompare.dao.MemberDao;
import com.hanatour.fareCompare.model.FareInfoVo;
import com.hanatour.fareCompare.util.CommonUtil;
import com.hanatour.fareCompare.util.CookieUtils;
import com.hanatour.fareCompare.util.SiteUtils;
import com.hanatour.util.SymmetricKeyUtils;

/**
 * Class name : MainController
 * Description : 메인페이지 컨트롤러
 * @author sthan
 * @author 2017. 10. 17.
 * @version 1.0
 */
@Controller
public class MainController {
	private static Logger commonLogger = LogManager.getLogger("COMMONLOGGER");
	//@Autowired
	//ApiController apiController;
	
	@Autowired
	ReloadableResourceBundleMessageSource messageSource;
	
	@Autowired
	MemberDao memberdao;
	
	@Autowired
	FareInfoDao fareInfodao;
	
	@Autowired
	FareDao fareDao;
			
	/**
	 * 범용 hnt->jsp 컨트롤러
	 */
	@RequestMapping("/**/*.hnt")
	public String template(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		
		return StringUtils.replace(request.getRequestURI(), ".hnt", "");
	}
	
	
	/**
	 * 운임 스케줄 배치 정보 검색 
	 * @throws Exception 
	 */
	@RequestMapping("/search/fareInfoSearchList.hnt")
	public String fareInfoSearchList(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, ModelMap modelMap) throws Exception {
		Map dataInfo = new HashMap();
		String depApo = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("depApo"), ""));   //출발지
		String arrApo = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("arrApo"), ""));   //도착지
		String carr = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("carr"), ""));    //
		/*
		String userId = CookieUtils.getCookieValueByAttrName(request,"c_id");
		
		if( StringUtils.isBlank(userId) ){
			return "redirect:/";
		}
		*/
		dataInfo.put("depApo",depApo);
		dataInfo.put("arrApo",arrApo);
		dataInfo.put("carr",carr);
		
		List<HashMap<String, Object>> fareSearchList = null;
		try {
			fareSearchList = fareInfodao.getFareSearchInfo(dataInfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		modelMap.put("fareSearchList", fareSearchList);
		modelMap.put("depApo",depApo);
		modelMap.put("arrApo",arrApo);
		modelMap.put("carr",carr);
		return "/search/fare_info_search_list";
	}
	/**
	 * 운임 스케줄 배치 정보 입력폼
	 */
	@RequestMapping("/search/fareInfoInsertFormAction.hnt")
	public String fareInfoInsertFormAction(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, ModelMap modelMap) {
		return "/search/fare_info_insertForm";
	}
	/**
	 * 운임 스케줄 배치 정보 입력 
	 * @throws Exception 
	 */
	@RequestMapping("/search/fareInfoInsertAction.hnt")
	public String fareInfoInsertAction(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, ModelMap modelMap) throws Exception {
		String url = "/search/fareInfoSearchList.hnt";
		try{
			String depApo = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("depApo"), ""));    //출발공항코드
			String arrApo = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("arrApo"), ""));    //도착공항코드
			String carr = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("carr"), ""));    //항공사코드
			String tripType = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("tripType"), ""));    //여정구분
			String depFixYn = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("depFixYn"), ""));    //출발일고정여부  Y:고정
			String chgMonth = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("chgMonth"), ""));    //월
			String chgWeek = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("chgWeek"), ""));    //주
			String chgDay = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("chgDay"), ""));    //날
			String depDt = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("depDt"), ""));    //고정출발일
			String chgRtnDay = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("chgRtnDay"), ""));    //변동귀국일
			String alwaysYn = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("alwaysYn"), ""));    //상시수집여부
			String schEndDt = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("schEndDt"), ""));    //수집종료일
			String useYn = "Y";
			
			String userId = CookieUtils.getCookieValueByAttrName(request,"c_id");
			
			if( StringUtils.isBlank(userId) ){
				model.addAttribute("message", messageSource.getMessage("MESSAGE.NONLOGIN", null, null));
				return "/common/message.hnt";
			}else{
				userId = SymmetricKeyUtils.decryptBase64(userId);
			}
			
			Map dataInfo = new HashMap();
			dataInfo.put("depApo",depApo);
			dataInfo.put("arrApo",arrApo);
			dataInfo.put("carr",carr);
			dataInfo.put("tripType",tripType);
			dataInfo.put("depFixYn",depFixYn);
			dataInfo.put("chgMonth",chgMonth);
			dataInfo.put("chgWeek",chgWeek);
			dataInfo.put("chgDay",chgDay);
			dataInfo.put("chgRtnDay",chgRtnDay);
			dataInfo.put("depDt",depDt);
			dataInfo.put("alwaysYn",alwaysYn);
			dataInfo.put("schEndDt",schEndDt);
			dataInfo.put("userId",userId);
			dataInfo.put("useYn",useYn);
			
			fareInfodao.fareInfoInsert(dataInfo);
			
		}catch(Exception e){
			commonLogger.error("MainController.fareInfoInsertAction Exception :"+ ExceptionUtils.getMessage(e));
		}
		
		return "redirect:" + url;
	}
	/**
	 * 운임 스케줄 배치 정보 수정 폼 
	 * @throws Exception 
	 */
	@RequestMapping("/search/fareInfoUpdateFormAction.hnt")
	public String fareInfoUpdateFormAction(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, ModelMap modelMap) throws Exception {
		String upSeq = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("upSeq"), ""));
		HashMap<String, Object> fareUpdateList = null;
		Map dataInfo = new HashMap();
		dataInfo.put("seq",upSeq);
		fareUpdateList = fareInfodao.getFareUpdateInfo(dataInfo);
		modelMap.put("fareUpdateList", fareUpdateList);
		return "/search/fare_info_updateForm";
	}
	
	/**
	 * 운임 스케줄 배치 정보 수정 
	 * @throws Exception 
	 */
	@RequestMapping("/search/fareInfoUpdateAction.hnt")
	public String fareInfoUpdateAction(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, ModelMap modelMap) throws Exception {
		String url = "/search/fareInfoSearchList.hnt";
		
		try{
			String upSeq = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("upSeq"), ""));      //수정할 Seq
			String depApo = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("depApo"), ""));    //출발공항코드
			String arrApo = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("arrApo"), ""));    //도착공항코드
			String carr = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("carr"), ""));    //항공사코드
			String tripType = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("tripType"), ""));    //여정구분
			String depFixYn = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("depFixYn"), ""));    //출발일고정여부  Y:고정
			String chgMonth = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("chgMonth"), ""));    //월
			String chgWeek = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("chgWeek"), ""));    //주
			String chgDay = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("chgDay"), ""));    //날
			String depDt = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("depDt"), ""));    //고정출발일
			String chgRtnDay = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("chgRtnDay"), ""));    //변동귀국일
			String alwaysYn = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("alwaysYn"), ""));    //상시수집여부
			String schEndDt = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("schEndDt"), ""));    //수집종료일
			//String userId = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("userId"), ""));
			
			String userId = CookieUtils.getCookieValueByAttrName(request,"c_id");
			
			if( StringUtils.isBlank(userId) ){
				model.addAttribute("message", messageSource.getMessage("MESSAGE.NONLOGIN", null, null));
				return "/common/message.hnt";
			}else{
				userId = SymmetricKeyUtils.decryptBase64(userId);
			}


			Map dataInfo = new HashMap();
			dataInfo.put("depApo",depApo);
			dataInfo.put("arrApo",arrApo);
			dataInfo.put("carr",carr);
			dataInfo.put("tripType",tripType);
			dataInfo.put("depFixYn",depFixYn);
			dataInfo.put("chgMonth",chgMonth);
			dataInfo.put("chgWeek",chgWeek);
			dataInfo.put("chgDay",chgDay);
			dataInfo.put("chgRtnDay",chgRtnDay);
			dataInfo.put("depDt",depDt);
			dataInfo.put("alwaysYn",alwaysYn);
			dataInfo.put("schEndDt",schEndDt);
			dataInfo.put("userId",userId);
			dataInfo.put("seq",upSeq);
			
			fareInfodao.fareInfoUpdate(dataInfo);
			
		}catch(Exception e){
			commonLogger.error("MainController.fareInfoUpdateAction Exception :"+ ExceptionUtils.getMessage(e));
		}
		
		
		return url;
	}
	/**
	 * 운임 스케줄 배치 정보 수정 (삭제 UseYn -> N)
	 * @throws DecoderException 
	 */
	@RequestMapping("/search/fareInfoDeleteAction.hnt")
	public String fareInfoDeleteAction(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, ModelMap modelMap) throws DecoderException {
		String url = "/search/fareInfoSearchList.hnt";
		try{
			Map dataInfo = new HashMap();
			String deSeq = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("deSeq"), ""));
			
			String userId = CookieUtils.getCookieValueByAttrName(request,"c_id");
			
			if( StringUtils.isBlank(userId) ){
				model.addAttribute("message", messageSource.getMessage("MESSAGE.NONLOGIN", null, null));
				return "/common/message.hnt";
			}else{
				userId = SymmetricKeyUtils.decryptBase64(userId);
			}
			
			
			String[] deSeqArr = deSeq.split("/");
			for (int i = 0; i < deSeqArr.length ; i++) {
				dataInfo.put("useYn","N");
				dataInfo.put("seq",deSeqArr[i]);
				dataInfo.put("userId",userId);
				fareInfodao.fareInfoDelete(dataInfo);
			}
			
		}catch(Exception e){
			commonLogger.error("MainController.fareInfoDeleteAction Exception :"+ ExceptionUtils.getMessage(e));
		}
		return "redirect:" + url;
	}
	
	/**
	 * 운임 수집 룰 리스트 카운트 
	 * @throws Exception 
	 */
	@RequestMapping(value={"/search/searchInfoCnt.hnt"}, produces="application/text; charset=utf-8")
	@ResponseBody
	public String searchInfoCnt(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seq_cnt = "0";		
		try {
			seq_cnt  = fareInfodao.getSearchInfoCnt();
		} catch (Exception e) {
			commonLogger.error("MainController.searchInfoCnt Exception :"+ ExceptionUtils.getMessage(e));
		}
		
		return seq_cnt;
	}
	
	
	/**
	 * 운임 수집 데이터 리스트
	 * @throws Exception 
	 */
	@RequestMapping(value="/search/compareList.hnt", method={RequestMethod.GET, RequestMethod.POST})
	public ModelAndView compareList(HttpServletRequest request, ModelAndView model) throws Exception {
		model.setViewName("/search/compareList");
		
		String air_cd = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("air_cd"), ""));   //항공사
		String promoArr = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("promoArr"), "")); //프로모션명
		
		String sch_seq = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("sch_seq"), ""));  //운임수집 룰 조건 SEQ
		String sch_num = ""; // 운임 수집 룰 조건 중 최근 수집된 데이터를 조회 하기 위해 MAX SCH_NUM 값  
		
		try{
			if(StringUtils.isBlank(sch_seq)){
				model.addObject("ErrorCode", "-1");
				model.addObject("ErrorDesc", "잘못된 파라미터 정보입니다.");
				return model;
			}
			
			Map dataInfo = new HashMap();
			dataInfo.put("sch_seq",sch_seq);
			
			sch_num = fareDao.selectFare_max_sch_num(dataInfo);
			
			HashMap<String,Object> schOption = new HashMap<String,Object>();
			List<String> promoList = new ArrayList<String>();
			
			if(StringUtils.isNoneBlank(promoArr)){
				
				String[] arrPromolist = promoArr.split(",");
				for (int i = 0; i < arrPromolist.length ; i++) {
					promoList.add(arrPromolist[i]);
				}
			}
			

			schOption.put("sch_seq",sch_seq);
			schOption.put("sch_num",sch_num);
			schOption.put("air_cd", air_cd);
			schOption.put("promo_list", promoList);
			
			List<HashMap<String, Object>> fareList = null;
			List<HashMap<String, Object>> airList = null;
			List<HashMap<String, Object>> promotionList = null;
				
			fareList = fareDao.selectCollectFare(schOption);
			airList = fareDao.selectAirList(schOption);
			promotionList = fareDao.selectPromotionList(schOption);
			
			ObjectMapper mapper = new ObjectMapper();	
			String strFareList = mapper.writeValueAsString(fareList);
			String strAirList = mapper.writeValueAsString(airList);
			String strPromotionList = mapper.writeValueAsString(promotionList);
			String strPromoList = mapper.writeValueAsString(promoList);
			
			model.addObject("fareList", strFareList);
			model.addObject("airList", strAirList);
			model.addObject("promotionList", strPromotionList);
			model.addObject("ErrorCode", "0");
			model.addObject("ErrorDesc", "정상입니다.");
			model.addObject("air_cd", air_cd);
			model.addObject("promoList", strPromoList);
			
		}catch(Exception e){
			model.addObject("ErrorCode", "-1");
			model.addObject("ErrorDesc", "운임조회 실패");
			commonLogger.error("MainController.compareList Exception :"+ ExceptionUtils.getMessage(e));
		}
		return model;
	}
	
	/**
	 * 실시간 운임비교 리스트 페이지
	 */
	
	@RequestMapping(value={"/search/getFareInfo.hnt"}, produces="application/text; charset=utf-8")
	@ResponseBody
	public String getFareInfo(HttpServletRequest request,  @RequestBody String requestBody, Model model) throws Exception{
		String resultStr = "";
		int hnt_cnt = 0;
		int mode_cnt = 0;
		int interpark_cnt = 0;
		int onlinetour_cnt = 0;
		String search_num = "0";
		String search_seq = "0";
		
		String sessionkey = "";
		String dep_apo = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("dep_apo"), ""));
		String rtn_apo = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("rtn_apo"), ""));
		String dep_dt = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("dep_dt"), ""));
		String rtn_dt = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("rtn_dt"), ""));
		
		String h_dep_dt = StringUtils.replaceAll(dep_dt, "-", "");
		String h_rtn_dt = StringUtils.replaceAll(rtn_dt, "-", "");
		


		if(request != null){
			sessionkey = request.getRequestedSessionId();
		}
		
		try{
			
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
				
			ObjectMapper mapper = new ObjectMapper();	
			resultStr = mapper.writeValueAsString(fareList);
			
			
			System.out.println("========================================================");
			System.out.println("resultStr=========="+resultStr);
			
		}catch(Exception e){
			commonLogger.error("MainController.getFareInfo Exception :"+ ExceptionUtils.getMessage(e));
		}
		
		
		return resultStr;
	}
	
	
	/**
	 * 운임 수집 데이터 리스트 ExcelDown
	 * @throws Exception 
	 */
	@RequestMapping(value="/search/excelDown.hnt", method={RequestMethod.GET, RequestMethod.POST})
	public void excelDown(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String air_cd = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("air_cd"), ""));   //항공사
		String promoArr = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("promoArr"), "")); //프로모션명
		
		
		String sch_seq = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("sch_seq"), ""));  //운임수집 룰 조건 SEQ
		String sch_num = ""; // 운임 수집 룰 조건 중 최근 수집된 데이터를 조회 하기 위해 MAX SCH_NUM 값  
		
		try{
			Map dataInfo = new HashMap();
			dataInfo.put("sch_seq",sch_seq);
			
			sch_num = fareDao.selectFare_max_sch_num(dataInfo);
			
			HashMap<String,Object> schOption = new HashMap<String,Object>();
			
			List<String> promoList = new ArrayList<String>();
			
			if(StringUtils.isNoneBlank(promoArr)){
				
				String[] arrPromolist = promoArr.split(",");
				for (int i = 0; i < arrPromolist.length ; i++) {
					promoList.add(arrPromolist[i]);
				}
			}

			schOption.put("sch_seq",sch_seq);
			schOption.put("sch_num",sch_num);
			schOption.put("air_cd", air_cd);
			schOption.put("promo_list", promoList);
			
			List<HashMap<String, Object>> fareList = null;
			List<HashMap<String, Object>> airList = null;
			List<HashMap<String, Object>> promotionList = null;
				
			fareList = fareDao.selectCollectFare(schOption);
			
			// 워크북 생성
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("게시판");
			Row row = null;
			Cell cell = null;
			int rowNo = 1;

			// 테이블 헤더용 스타일
			CellStyle headStyle = wb.createCellStyle();
			// 가는 경계선을 가집니다.
			headStyle.setBorderTop(BorderStyle.THIN);
			headStyle.setBorderBottom(BorderStyle.THIN);
			headStyle.setBorderLeft(BorderStyle.THIN);
			headStyle.setBorderRight(BorderStyle.THIN);
			// 배경색은 노란색입니다.
			headStyle.setFillForegroundColor(HSSFColorPredefined.YELLOW.getIndex());
			headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			// 데이터는 가운데 정렬합니다.
			headStyle.setAlignment(HorizontalAlignment.CENTER);
			headStyle.setVerticalAlignment(VerticalAlignment.CENTER); //높이 가운데 정렬
			
			
			// Title 용 스타일 
			CellStyle titleStyle = wb.createCellStyle();
			titleStyle.setVerticalAlignment(VerticalAlignment.CENTER); //높이 가운데 정렬
			
			// 데이터용 경계 스타일
			CellStyle bodyStyle = wb.createCellStyle();
			bodyStyle.setBorderTop(BorderStyle.THIN);
			bodyStyle.setBorderBottom(BorderStyle.THIN);
			bodyStyle.setBorderLeft(BorderStyle.THIN);
			bodyStyle.setBorderRight(BorderStyle.THIN);

			// 헤더 생성
			row = sheet.createRow(rowNo++); //2row
			cell = row.createCell(0);
			cell.setCellStyle(headStyle);
			cell.setCellValue("항공사");
			cell = row.createCell(1);
			cell.setCellStyle(headStyle);
			cell.setCellValue("유형");
			cell = row.createCell(2);
			cell.setCellStyle(headStyle);
			cell.setCellValue("유류/TAX/NET");
			
			//셀병합시 보더 스타일을 위해 병합 되는 열만큼 셀을 생성함
			for(int i=5; i <= 24; i++){
				cell = row.createCell(i);
				cell.setCellStyle(headStyle);
				cell.setCellValue("자체사이트 기본운임");
			}
			
			//셀병합시 보더 스타일을 위해 병합 되는 열만큼 셀을 생성함
			for(int i=25; i <= 28; i++){
				cell = row.createCell(i);
				cell.setCellStyle(headStyle);
				cell.setCellValue("자체사이트 기본운임");
			}
			/*
			cell = row.createCell(5);
			cell.setCellStyle(headStyle);
			cell.setCellValue("자체사이트 기본운임");
			cell = row.createCell(25);
			cell.setCellStyle(headStyle);
			cell.setCellValue("자체사이트 할인율");
			*/
			
			row = sheet.createRow(rowNo++); //3row
			//셀병합시 보더 스타일을 위해 병합 되는 열만큼 셀을 생성함
			cell = row.createCell(0);
			cell.setCellStyle(headStyle);
			cell.setCellValue("항공사");
			cell = row.createCell(1);
			cell.setCellStyle(headStyle);
			cell.setCellValue("유형");
			cell = row.createCell(2);
			cell.setCellStyle(headStyle);
			cell.setCellValue("유류");
			
			
			cell = row.createCell(3);
			cell.setCellStyle(headStyle);
			cell.setCellValue("제세공과금");
			cell = row.createCell(4);
			cell.setCellStyle(headStyle);
			cell.setCellValue("NETT");
			
			for(int i=5; i <= 9; i++){
				cell = row.createCell(i);
				cell.setCellStyle(headStyle);
				cell.setCellValue("하나투어");
			}
			
			for(int i=10; i <= 14; i++){
				cell = row.createCell(i);
				cell.setCellStyle(headStyle);
				cell.setCellValue("모두투어");
			}
			
			for(int i=15; i <= 19; i++){
				cell = row.createCell(i);
				cell.setCellStyle(headStyle);
				cell.setCellValue("인터파크");
			}
			
			for(int i=20; i <= 24; i++){
				cell = row.createCell(i);
				cell.setCellStyle(headStyle);
				cell.setCellValue("온라인투어");
			}
			
			/*
			cell = row.createCell(5);
			cell.setCellStyle(headStyle);
			cell.setCellValue("하나투어");
			cell = row.createCell(10);
			cell.setCellStyle(headStyle);
			cell.setCellValue("모두투어");
			cell = row.createCell(15);
			cell.setCellStyle(headStyle);
			cell.setCellValue("인터파크");
			cell = row.createCell(20);
			cell.setCellStyle(headStyle);
			cell.setCellValue("온라인투어");
			*/
			
			cell = row.createCell(25);
			cell.setCellStyle(headStyle);
			cell.setCellValue("하나투어");
			cell = row.createCell(26);
			cell.setCellStyle(headStyle);
			cell.setCellValue("모두투어");
			cell = row.createCell(27);
			cell.setCellStyle(headStyle);
			cell.setCellValue("인터파크");
			cell = row.createCell(28);
			cell.setCellStyle(headStyle);
			cell.setCellValue("온라인투어");
			
			
			row = sheet.createRow(rowNo++); //4row
			//셀병합시 보더 스타일을 위해 병합 되는 행만큼 셀을 생성함
			cell = row.createCell(0);
			cell.setCellStyle(headStyle);
			cell.setCellValue("항공사");
			cell = row.createCell(1);
			cell.setCellStyle(headStyle);
			cell.setCellValue("유형");
			cell = row.createCell(2);
			cell.setCellStyle(headStyle);
			cell.setCellValue("유류");
			cell = row.createCell(3);
			cell.setCellStyle(headStyle);
			cell.setCellValue("제세공과금");
			cell = row.createCell(4);
			cell.setCellStyle(headStyle);
			cell.setCellValue("NETT");
			
			//하나투어
			cell = row.createCell(5);
			cell.setCellStyle(headStyle);
			cell.setCellValue("기본운임");
			cell = row.createCell(6);
			cell.setCellStyle(headStyle);
			cell.setCellValue("유효기간");
			cell = row.createCell(7);
			cell.setCellStyle(headStyle);
			cell.setCellValue("경유횟수");
			cell = row.createCell(8);
			cell.setCellStyle(headStyle);
			cell.setCellValue("출발편명");
			cell = row.createCell(9);
			cell.setCellStyle(headStyle);
			cell.setCellValue("귀국편명");
			//모두투어
			cell = row.createCell(10);
			cell.setCellStyle(headStyle);
			cell.setCellValue("기본운임");
			cell = row.createCell(11);
			cell.setCellStyle(headStyle);
			cell.setCellValue("유효기간");
			cell = row.createCell(12);
			cell.setCellStyle(headStyle);
			cell.setCellValue("경유횟수");
			cell = row.createCell(13);
			cell.setCellStyle(headStyle);
			cell.setCellValue("출발편명");
			cell = row.createCell(14);
			cell.setCellStyle(headStyle);
			cell.setCellValue("귀국편명");
			
			//인터파크
			cell = row.createCell(15);
			cell.setCellStyle(headStyle);
			cell.setCellValue("기본운임");
			cell = row.createCell(16);
			cell.setCellStyle(headStyle);
			cell.setCellValue("유효기간");
			cell = row.createCell(17);
			cell.setCellStyle(headStyle);
			cell.setCellValue("경유횟수");
			cell = row.createCell(18);
			cell.setCellStyle(headStyle);
			cell.setCellValue("출발편명");
			cell = row.createCell(19);
			cell.setCellStyle(headStyle);
			cell.setCellValue("귀국편명");
			
			//온라인투어
			cell = row.createCell(20);
			cell.setCellStyle(headStyle);
			cell.setCellValue("기본운임");
			cell = row.createCell(21);
			cell.setCellStyle(headStyle);
			cell.setCellValue("유효기간");
			cell = row.createCell(22);
			cell.setCellStyle(headStyle);
			cell.setCellValue("경유횟수");
			cell = row.createCell(23);
			cell.setCellStyle(headStyle);
			cell.setCellValue("출발편명");
			cell = row.createCell(24);
			cell.setCellStyle(headStyle);
			cell.setCellValue("귀국편명");
			
			cell = row.createCell(25);
			cell.setCellStyle(headStyle);
			cell.setCellValue("하나투어");
			
			cell = row.createCell(26);
			cell.setCellStyle(headStyle);
			cell.setCellValue("모두투어");
			
			cell = row.createCell(27);
			cell.setCellStyle(headStyle);
			cell.setCellValue("인터파크");
			
			cell = row.createCell(28);
			cell.setCellStyle(headStyle);
			cell.setCellValue("온라인투어");
			
			
			//상단 1 row
			sheet.addMergedRegion(new CellRangeAddress(1, 3, 0, 0)); //항공사	
			sheet.addMergedRegion(new CellRangeAddress(1, 3, 1, 1)); //유형
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 4)); //유류/TAX/NETT
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 5, 24)); //자체사이트기본운임
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 25, 28)); //자체 사이트 할인율
			
			//상단 2 row
			sheet.addMergedRegion(new CellRangeAddress(2, 3, 2, 2)); 	//유류
			sheet.addMergedRegion(new CellRangeAddress(2, 3, 3, 3)); 	//TAX
			sheet.addMergedRegion(new CellRangeAddress(2, 3, 4, 4)); 	//NETT
			
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 5, 9)); 	//하나투어
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 10, 14)); 	//모두트어
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 15, 19)); 	//인터파크
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 20, 24)); 	//온라인투어
			
			sheet.addMergedRegion(new CellRangeAddress(2, 3, 25, 25)); 	//하나투어
			sheet.addMergedRegion(new CellRangeAddress(2, 3, 26, 26)); 	//모두투어
			sheet.addMergedRegion(new CellRangeAddress(2, 3, 27, 27)); 	//인터파크
			sheet.addMergedRegion(new CellRangeAddress(2, 3, 28, 28)); 	//온라인투어
			
			
			// 데이터 부분 생성
			int list_size = fareList.size();
			
			for( int i = 0; i < list_size; i++ ){
				HashMap<String, Object> map = fareList.get(i);
				
				if(i == 0 ){
					
					String depDt = map.get("DEP_DT").toString().replaceAll("(^[0-9]{4})([0-9]{2})([0-9]{2})","$1/$2/$3");
					String rtnDt = map.get("RTN_DT").toString().replaceAll("(^[0-9]{4})([0-9]{2})([0-9]{2})","$1/$2/$3");
					
					String headerTitle = map.get("DEP_APO").toString() + "/" + map.get("RTN_APO").toString()+" 왕복 "+ depDt 
							+ " ~ " + rtnDt + ", 최종없데이트 "+map.get("REG_DATE").toString()+ " , 성인1인 기준 / 대기 제외" ;
					
					row = sheet.createRow(0); //1row
					cell = row.createCell(0);
					cell.setCellStyle(titleStyle);
					cell.setCellValue(headerTitle);
				}
				
				
				row = sheet.createRow(rowNo++);
				//항공사 코드
				cell = row.createCell(0);
				cell.setCellStyle(bodyStyle);
				cell.setCellValue(map.get("AIR_CD").toString());
				
				//카드프로모션 명
				cell = row.createCell(1);
				cell.setCellStyle(bodyStyle);
				cell.setCellValue(map.get("PROMOTION_NM").toString());
				//유류세
				cell = row.createCell(2);
				cell.setCellStyle(bodyStyle);
				String oil_fee = Integer.parseInt(map.get("OIL_FEE").toString()) >  0 ? map.get("OIL_FEE").toString() :"-";
				cell.setCellValue(oil_fee);
				//TAX
				cell = row.createCell(3);
				cell.setCellStyle(bodyStyle);
				String tax_fee = Integer.parseInt(map.get("TAX_FEE").toString()) >  0 ? map.get("TAX_FEE").toString() :"-";
				cell.setCellValue(tax_fee);
				//기본운임
				cell = row.createCell(4);
				cell.setCellStyle(bodyStyle);
				String fare_nett = Integer.parseInt(map.get("FARE_NETT").toString()) >  0 ? map.get("FARE_NETT").toString() :"-";
				cell.setCellValue(fare_nett);
				
				//하나투어 기본운임
				cell = row.createCell(5);
				cell.setCellStyle(bodyStyle);
				String h_fare_dis = Integer.parseInt(map.get("H_FARE_DIS").toString()) >  0 ? map.get("H_FARE_DIS").toString() :"-";
				cell.setCellValue(h_fare_dis);
				
				//하나투어 유효기간
				cell = row.createCell(6);
				cell.setCellStyle(bodyStyle);
				String h_exp_dt = !"0".equals(map.get("H_EXP_DT").toString()) ? map.get("H_EXP_DT").toString() :"-";
				cell.setCellValue(h_exp_dt);
				//하나투어 경유 횟수
				cell = row.createCell(7);
				cell.setCellStyle(bodyStyle);
				String h_via_cnt = Integer.parseInt(map.get("H_FARE_DIS").toString()) >  0 ? map.get("H_VIA_CNT").toString() :"-";
				cell.setCellValue(h_via_cnt);
				//하나투어 출발편명
				cell = row.createCell(8);
				cell.setCellStyle(bodyStyle);
				String h_dep_flt_no = !"0".equals(map.get("H_DEP_FLT_NO").toString()) ? map.get("H_DEP_FLT_NO").toString() :"-";
				cell.setCellValue(h_dep_flt_no);
				//하나투어 귀국편명
				cell = row.createCell(9);
				cell.setCellStyle(bodyStyle);
				String h_rtn_flt_no = !"0".equals(map.get("H_RTN_FLT_NO").toString()) ? map.get("H_RTN_FLT_NO").toString() :"-";
				cell.setCellValue(h_rtn_flt_no);
				
				//모두투어 기본운임
				cell = row.createCell(10);
				cell.setCellStyle(bodyStyle);
				String m_fare_dis = Integer.parseInt(map.get("M_FARE_DIS").toString()) >  0 ? map.get("M_FARE_DIS").toString() :"-";
				cell.setCellValue(m_fare_dis);
				//모두투어 유효기간
				cell = row.createCell(11);
				cell.setCellStyle(bodyStyle);
				String m_exp_dt = !"0".equals(map.get("M_EXP_DT").toString()) ? map.get("M_EXP_DT").toString() :"-";
				cell.setCellValue(m_exp_dt);
				//모두투어 경유 횟수
				cell = row.createCell(12);
				cell.setCellStyle(bodyStyle);
				String m_via_cnt = Integer.parseInt(map.get("M_FARE_DIS").toString()) >  0 ? map.get("M_VIA_CNT").toString() :"-";
				cell.setCellValue(m_via_cnt);
				//모두투어 출발편명
				cell = row.createCell(13);
				cell.setCellStyle(bodyStyle);
				String m_dep_flt_no = !"0".equals(map.get("M_DEP_FLT_NO").toString()) ? map.get("M_DEP_FLT_NO").toString() :"-";
				cell.setCellValue(m_dep_flt_no);
				//모두투어 귀국편명
				cell = row.createCell(14);
				cell.setCellStyle(bodyStyle);
				String m_rtn_flt_no = !"0".equals(map.get("M_RTN_FLT_NO").toString()) ? map.get("M_RTN_FLT_NO").toString() :"-";
				cell.setCellValue(m_rtn_flt_no);
				
				//인터파크 기본운임
				cell = row.createCell(15);
				cell.setCellStyle(bodyStyle);
				String i_fare_dis = Integer.parseInt(map.get("I_FARE_DIS").toString()) >  0 ? map.get("I_FARE_DIS").toString() :"-";
				cell.setCellValue(i_fare_dis);
				//인터파크 유효기간
				cell = row.createCell(16);
				cell.setCellStyle(bodyStyle);
				String i_exp_dt = !"0".equals(map.get("I_EXP_DT").toString()) ? map.get("I_EXP_DT").toString() :"-";
				cell.setCellValue(i_exp_dt);
				//인터파크 경유 횟수
				cell = row.createCell(17);
				cell.setCellStyle(bodyStyle);
				String i_via_cnt = Integer.parseInt(map.get("I_FARE_DIS").toString()) >  0 ? map.get("I_VIA_CNT").toString() :"-";
				cell.setCellValue(i_via_cnt);
				//인터파크 출발편명
				cell = row.createCell(18);
				cell.setCellStyle(bodyStyle);
				String i_dep_flt_no = !"0".equals(map.get("I_DEP_FLT_NO").toString()) ? map.get("I_DEP_FLT_NO").toString() :"-";
				cell.setCellValue(i_dep_flt_no);
				//인터파크 귀국편명
				cell = row.createCell(19);
				cell.setCellStyle(bodyStyle);
				String i_rtn_flt_no = !"0".equals(map.get("I_RTN_FLT_NO").toString()) ? map.get("I_RTN_FLT_NO").toString() :"-";
				cell.setCellValue(i_rtn_flt_no);
				
				//온라인투어 기본운임
				cell = row.createCell(20);
				cell.setCellStyle(bodyStyle);
				String o_fare_dis = Integer.parseInt(map.get("O_FARE_DIS").toString()) >  0 ? map.get("O_FARE_DIS").toString() :"-";
				cell.setCellValue(o_fare_dis);
				//온라인투어 유효기간
				cell = row.createCell(21);
				cell.setCellStyle(bodyStyle);
				String o_exp_dt = !"0".equals(map.get("O_EXP_DT").toString()) ? map.get("O_EXP_DT").toString() :"-";
				cell.setCellValue(o_exp_dt);
				//온라인투어 경유 횟수
				cell = row.createCell(22);
				cell.setCellStyle(bodyStyle);
				String o_via_cnt = Integer.parseInt(map.get("O_FARE_DIS").toString()) >  0 ? map.get("O_VIA_CNT").toString() :"-";
				cell.setCellValue(o_via_cnt);
				//온라인투어 출발편명
				cell = row.createCell(23);
				cell.setCellStyle(bodyStyle);
				String o_dep_flt_no = !"0".equals(map.get("O_DEP_FLT_NO").toString()) ? map.get("O_DEP_FLT_NO").toString() :"-";
				cell.setCellValue(o_dep_flt_no);
				//온라인투어 귀국편명
				cell = row.createCell(24);
				cell.setCellStyle(bodyStyle);
				String o_rtn_flt_no = !"0".equals(map.get("O_RTN_FLT_NO").toString()) ? map.get("O_RTN_FLT_NO").toString() :"-";
				cell.setCellValue(o_rtn_flt_no);
				
				
				int n_fare = Integer.parseInt(map.get("H_FARE_NOMAL").toString());
				String h_dis_rate = "-";
				String m_dis_rate = "-";
				String i_dis_rate = "-";
				String o_dis_rate = "-";
				
				
				//DecimalFormat dFormat = new DecimalFormat("#.#");
				
				if( n_fare > 0){
					if( Integer.parseInt(map.get("H_FARE_DIS").toString()) > 0 ){
						
						double h_rate = ( ( (n_fare - Double.parseDouble(map.get("H_FARE_DIS").toString())) / n_fare * 100 ) * -1  );
						h_dis_rate = String.format("%.1f", h_rate);
					}
					if( Integer.parseInt(map.get("M_FARE_DIS").toString()) > 0 ){
						double m_rate = ( ( (n_fare - Double.parseDouble(map.get("M_FARE_DIS").toString())) / n_fare * 100 ) * -1  );
						m_dis_rate = String.format("%.1f", m_rate);
					}
					if( Integer.parseInt(map.get("I_FARE_DIS").toString()) > 0 ){
						double i_rate = ( ( (n_fare - Double.parseDouble(map.get("I_FARE_DIS").toString())) / n_fare * 100 ) * -1  );
						i_dis_rate = String.format("%.1f", i_rate);
					}
					if( Integer.parseInt(map.get("O_FARE_DIS").toString()) > 0 ){
						double o_rate = ( ( (n_fare - Double.parseDouble(map.get("O_FARE_DIS").toString())) / n_fare * 100 ) * -1  );
						o_dis_rate = String.format("%.1f", o_rate);
					} 		
				}
				
				//하나투어 할인율
				cell = row.createCell(25);
				cell.setCellStyle(bodyStyle);
				cell.setCellValue(h_dis_rate);
				//모두투어 할인율
				cell = row.createCell(26);
				cell.setCellStyle(bodyStyle);
				cell.setCellValue(m_dis_rate);
				//인터파크 할인율
				cell = row.createCell(27);
				cell.setCellStyle(bodyStyle);
				cell.setCellValue(i_dis_rate);
				//온라인투어 할인율
				cell = row.createCell(28);
				cell.setCellStyle(bodyStyle);
				cell.setCellValue(o_dis_rate);
			}
			
			String exFileNm = "fareCompare_"+CommonUtil.getYYYYMMDD()+".xls";
			// 엑셀 출력
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment;filename="+exFileNm);
			
			wb.write(response.getOutputStream());
			wb.close();
			
		}catch(Exception e){
			commonLogger.error("MainController.excelDown Exception :"+ ExceptionUtils.getMessage(e));
		}
	}
	
}
