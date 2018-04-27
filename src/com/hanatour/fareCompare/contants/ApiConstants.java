/**
 * File name : ApiConstants.java
 * Package : com.hanatour.fareCompare.contants
 * Description : API 처리용 상수 정의 클래스
 *
 * <pre>
 * <변경이력>
 * 수정일			수정자				수정내용
 * ------------------------------------------------------
 * 2017. 10. 12.		sthan	최초 작성
 * </pre>
 */
package com.hanatour.fareCompare.contants;

/**
 * Class name : ApiConstants
 * Description :
 * @author sthan
 * @author 2017. 10. 12.
 * @version 1.0
 */
public class ApiConstants {
	/**************************************
	 * API 호출용 상수 코드
	 * ************************************/
	
	
	public static String HNT_API_URL= "";
	// HNT 관리 API address 운영 
	public static final String HNT_API_URL_PRODUCTION = "http://hgrsapiws.hanatour.com/air/airWs.asmx";
	// HNT 관리 API address 개발 
	public static final String HNT_API_URL_DEVELOPMENT = "http://hgrsapiws2.hanatour.com/air/airWs.asmx";
	// HNT 관리 API address 로컬 
	public static final String HNT_API_URL_LOCAL = "http://hgrsapiws2.hanatour.com/air/airWs.asmx";
	
	
	// HNT Fare WebService address 운영 
	public static final String HNT_FARE_URL_PRODUCTION = "http://hgrsapiws.hanatour.com";
		
	// MODETOUR Fare WebService address 운영 
	public static final String MODE_FARE_URL_PRODUCTION = "http://www.modetour.com/LiveBooking/WebService/Air5/FareAvailList.aspx";
	
	// INTERPARK Fare WebService address 운영 
	public static final String INTERPARK_FARE_URL_PRODUCTION = "http://smartair.interpark.com/AirSearch/GetGoodsSmartList.aspx";
	
	// INTERPARK Fare RT WebService address 운영 
	public static final String INTERPARK_FARERT_URL_PRODUCTION = "http://api-tour.interpark.com/GroupAir/GetList";
	
	// ONLINETOUR Fare WebService address 운영 
	public static final String ONLINETOUR_FARE_URL_PRODUCTION = "http://www.onlinetour.co.kr/flight/international/booking/FareSearchJson";
	
	// ONLINETOUR Fare RT WebService address 운영 
	public static final String ONLINETOUR_FARERT_URL_PRODUCTION = "http://www.onlinetour.co.kr/flight/international/booking/FareSearchJsonQvo";
	
	public static final String SITE_CODE = "C00001S000";
	public static final String LICENSE_KEY = "4025238517405993955840252385174025233659374763470038517";
	public static final String INPUT_CHANNEL = "MDAIR";
	public static final String AGENT_CODE = "1000";
	public static final String LANGUAGE_CODE = "KOR";
	public static final String SALES_SITE = "HNTMOBILE";
	public static final String RENEWAL = "Y";
	
}
