/**
 * File name : Scheduler.java
 * Package : com.hanatour.fareCompare.config
 * Description : batch Scheduler 관련 파일 
 *
 * <pre>
 * <변경이력>
 * 수정일			수정자				수정내용
 * ------------------------------------------------------
 * 2017. 10. 31.		sthan	최초 작성
 * </pre>
 */
package com.hanatour.fareCompare.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.hanatour.fareCompare.controller.ApiController;
import com.hanatour.fareCompare.dao.FareDao;
import com.hanatour.fareCompare.dao.FareInfoDao;
import com.hanatour.fareCompare.model.FareInfoVo;
import com.hanatour.fareCompare.service.FareSchdSvc;
import com.hanatour.fareCompare.util.CommonUtil;


/**
 * Class name : Scheduler
 * Description :
 * @author sthan
 * @author 2017. 10. 31.
 * @version 1.0
 */
@Configuration
@EnableScheduling
public class Scheduler {
	
	@Autowired
	ApiController apiController;
	
	@Autowired
	FareDao fareDao;
	
	@Autowired
	FareInfoDao fareInfodao;
	
	@Autowired
	FareSchdSvc fareSchdSvc;
	
	private static Logger commonLogger = LogManager.getLogger("COMMONLOGGER");
	
	@Scheduled(cron="0 0 02 * * ?")
	public void searchFareBatch() throws Exception{
		List<FareInfoVo> fareSearchList = null;
		try {
			
			commonLogger.info("Scheduler.searchFareBatch Start ["+ CommonUtil.getYYYYMMDDHHMMss()+"]");
			
			String currentDay = CommonUtil.getYYYYMMDD();
			Map dataInfo = new HashMap();
			dataInfo.put("seq", "");
			fareSearchList = fareInfodao.getSelectSearchInfo(dataInfo);
			
			System.out.println("fareSearchList==="+ fareSearchList.toString());
			
			if( fareSearchList.size() > 0){
				for(int i = 0; i < fareSearchList.size(); i++){
					FareInfoVo map = new FareInfoVo();
					map = fareSearchList.get(i);
					
					boolean schFlag = false; // 운임수집여부 flag
					String useYn = map.getUseYn(); // 운임수집 조건 사용여부 
					String alwaysYn = map.getAlwaysYn();	// 상시 수집 여부
					String schEndDt = map.getSchEndDt();	// 상시 수집여부가 N일 경우 수집 종료일   
					String dep_apo = map.getDepApo();		// 출발 항공
					String rtn_apo = map.getRtnApo();		// 도착 항공
					String tripType = "RT";					// 여정 타입 OW:편도, RT:왕복
					String depfixYn = map.getDepFixYn();	// 출발일자 고정 여부 Y,N
					String chgMonth = map.getChgMonth();	// 출발일 변동일 경우 변동 월 EX) 현재 월 + 설정 개월  
					String chgWeek = map.getChgWeek();		// 출발일 변동일 경우 변동 주 EX) 변동 월의 설정 주 
					String chgDay = map.getChgDay();		// 출발일 변동일 경우 변동 요일 EX) 변동 주의 설정 요일
					String dep_dt = map.getDepDt();			// 출발일 고정일 경우 출발일자
					String chgRtnDay = map.getChgRtnDay();  // 귀국일 설정 값 EX) 출발일자 + 설정 일
					String sch_seq =  String.valueOf(map.getSeq());			// 운임수집 룰 SEQ
					
					String rtn_dt = ""; //귀국일 조건
					
					if(StringUtils.isNotBlank(schEndDt)){
						schEndDt = StringUtils.replaceAll(schEndDt, "-", "");
					}
					
					if( "Y".equals(alwaysYn) ){ // 수집기간이 상시 일경우
						schFlag = true;
					}else{
						if( schEndDt.compareTo(currentDay) >= 0 ){ //수집종료일이 현재날짜와 같거나 클경우 조회 대상
							schFlag = true;
						}
					}
					
					if("Y".equals(useYn) && schFlag){ // 운임수집 룰 사용 여부 가 Y 일 경우만 운임 수집 
						
						dataInfo = new HashMap();
						dataInfo.put("sch_seq","0");
						
						String search_num = fareDao.selectFare_nvl_sch_num(dataInfo);
						
						if( "Y".equals(depfixYn) ){ // 출발일 변동일 경우	
							dep_dt = CommonUtil.getDayOfWeek(chgMonth, chgWeek, chgDay);
						}
						
						String tmp_dt = StringUtils.replaceAll(dep_dt, "-", "");
						if( tmp_dt.compareTo(currentDay) <= 0  ){
							continue;
						}
						
						rtn_dt = CommonUtil.CALC_date( StringUtils.replaceAll(dep_dt, "-", ""),  Integer.parseInt(chgRtnDay));
						
						System.out.println("dep_apo ===="+ dep_apo);
						System.out.println("rtn_apo ===="+ rtn_apo);
						System.out.println("dep_dt ===="+ dep_dt);
						System.out.println("rtn_dt ===="+ rtn_dt);
						System.out.println("search_num ===="+ search_num);
						System.out.println("sch_seq ===="+ sch_seq);
						
						String h_dep_dt = StringUtils.replaceAll(dep_dt, "-", "");
						String h_rtn_dt = StringUtils.replaceAll(rtn_dt, "-", "");
						
						int mode_cnt = fareSchdSvc.searchMode(null, dep_apo, rtn_apo, h_dep_dt, h_rtn_dt , search_num, sch_seq);
						int hnt_cnt = fareSchdSvc.searchHana(null, dep_apo, rtn_apo, dep_dt, rtn_dt, search_num, sch_seq );
						int interpark_cnt = fareSchdSvc.searchInPark(null, dep_apo, rtn_apo, dep_dt, rtn_dt, search_num, sch_seq);
						int onlinetour_cnt = fareSchdSvc.searchOnLine(null, dep_apo, rtn_apo, dep_dt, rtn_dt , search_num, sch_seq);
						
						System.out.println("mode_cnt ===="+mode_cnt);
						System.out.println("hnt_cnt ===="+hnt_cnt);
						System.out.println("interpark_cnt ===="+interpark_cnt);
						System.out.println("onlinetour_cnt ===="+onlinetour_cnt);
					}
					
				}
				
				commonLogger.info("Scheduler.searchFareBatch Success ["+ CommonUtil.getYYYYMMDDHHMMss()+"]");
			}
		} catch (Exception e) {
			commonLogger.error("Scheduler.searchFareBatch :"+ ExceptionUtils.getMessage(e));	
		}
	}
}
