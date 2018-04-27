/**
 * File name : ApiController.java
 * Package : com.hanatour.fareCompare.controller
 * Description : API 통신 관련 컨트롤러
 *
 * <pre>
 * <변경이력>
 * 수정일			수정자				수정내용
 * ------------------------------------------------------
 * 2017. 10. 20.		sthan	최초 작성
 * </pre>
 */
package com.hanatour.fareCompare.controller;


import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanatour.fareCompare.contants.ApiConstants;
import com.hanatour.fareCompare.model.CommonFareVo;
import com.hanatour.fareCompare.model.SystemDefinition;

/**
 * Class name : ApiController
 * Description :A
 * @author hana
 * @author 2017. 10. 20.
 * @version 1.0
 */

@Controller
public class ApiController {
	private static Logger commonLogger = LogManager.getLogger("COMMONLOGGER");

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	SystemDefinition systemDefinition;
	
	@Autowired
	ReloadableResourceBundleMessageSource messageSource;
	
	
	
	private void log(String key, String url, String body){
		if(commonLogger.isDebugEnabled()){
			commonLogger.debug(new StringBuffer("[KEY:").append(key).append("] [").append(url).append("] [").append(body).append("]").toString());
		}
	}
	
	
	/**
	 * javascript ajax용 HGSR 웹서비스 호출 범용 메소드
	 * @throws IOException 
	 * @throws JsonProcessingException
	 * @author sthan 
	 */
	@RequestMapping(value={"/farecompare/{method}/{rqname}/ws.hnt"}, produces="application/json; charset=utf-8")
	@ResponseBody
	public String hntCommonWs(HttpServletRequest request, HttpServletResponse response, @PathVariable(value="method") String method, @PathVariable(value="rqname") String rqname, @RequestBody String requestBody) throws JsonProcessingException, IOException {
		//API 호출
		String key = null;
		if(request != null){
			key = request.getRequestedSessionId();
		}
		String requestUrl =  ApiConstants.HNT_API_URL+"/"+method;
		String rqBody = getRequestBody(requestBody, method, rqname);
		log(key, requestUrl, rqBody);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		ResponseEntity<String> responseEntity = restTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity(rqBody, headers),String.class);
		String responseBody = responseEntity.getBody();
		log(key, requestUrl, responseBody);
		return responseBody;
	}
	
	private String getRequestBody(String body, String method, String rqname){
		try{
			if(StringUtils.isNoneBlank(body) && body.indexOf(rqname) > 0){
	            int startIdx =body.indexOf(rqname); 
	            startIdx =body.indexOf("{", startIdx); 
	            String prefixString = body.substring(0,startIdx + 1);
	            String postfixString = body.substring(startIdx + 1);
	            String baseString = "\"SiteCode\": \"" + ApiConstants.SITE_CODE + "\","+
						             "\"LicenseKey\" : \"" + ApiConstants.LICENSE_KEY + "\","+
						             "\"InpChn\" : \"MDAIR\","+
						             "\"LangCode\" : \"" + ApiConstants.LANGUAGE_CODE +  "\","+
						             "\"AgtCode\" : \"" + ApiConstants.AGENT_CODE +  "\","+
						             "\"SalesSite\" :\"" + ApiConstants.SALES_SITE + "\","+
						             "\"RqAgtCode\" : \"1000\","+
						             "\"RqId\" : \"HANATOUR\","+
						             "\"RqLclName\" : \"하나투어\",";
	            body =  prefixString + baseString +postfixString;
		        if(body.indexOf(",}") > 0  ){
		        	body = StringUtils.replace(body, ",}", "}");
		        }
		        if(body.indexOf(", }") > 0  ){
		        	body = StringUtils.replace(body, ", }", "}");
		        }
			}
		}catch(Exception ex){
			commonLogger.error("FareSchdSvc.searchOnLine Exception :"+ ExceptionUtils.getMessage(ex));
			body = null;
		}
		return body;
	}
	
	
	@RequestMapping(value="/farecompare/searchHnt/ws.hnt")
	@ResponseBody
	public String searchHnt(HttpServletRequest request,  @RequestBody String requestBody, Model model) throws Exception{
		
		String key = "";
		
		if(request != null){
			key = request.getRequestedSessionId();
		}
		
		String dep_apo = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("dep_apo"), ""));
		String rtn_apo = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("rtn_apo"), ""));
		String dep_dt = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("dep_dt"), ""));
		String rtn_dt = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("rtn_dt"), ""));
		
		String h_dep_dt = StringUtils.replaceAll(dep_dt, "-", "");
		String h_rtn_dt = StringUtils.replaceAll(rtn_dt, "-", "");
		
		System.out.println("dep_apo ===="+request.getParameter("dep_apo") );
		System.out.println("rtn_apo ===="+request.getParameter("rtn_apo") );
		System.out.println("dep_dt ===="+request.getParameter("dep_dt") );
		System.out.println("rtn_dt ===="+request.getParameter("rtn_dt") );
		
		//String requestUrl = ApiConstants.HNT_FARE_URL_PRODUCTION;
		String requestUrl = ApiConstants.HNT_API_URL + "/air/airWs.asmx/getFareSchdIdXml";
		
		StringBuffer reqXml = new StringBuffer();
		//reqXml.append("<?xml version='1.0' encoding='utf-8'?>");
		//reqXml.append("<soap12:Envelope xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:xsd='http://www.w3.org/2001/XMLSchema' xmlns:soap12='http://www.w3.org/2003/05/soap-envelope'>"); 
		//reqXml.append("<soap12:Body>");
		//reqXml.append("<getResponse><reqXml><![CDATA[<?xml version='1.0' encoding='UTF-8'?>");
		
		reqXml.append("<FareSchdRQ>");
		reqXml.append("<SiteCode>C00001S000</SiteCode>");
		reqXml.append("<LicenseKey>4025238517405993955840252385174025233659374763470038517</LicenseKey>");
		reqXml.append("<InpChn>FLTCOM</InpChn>");
		reqXml.append("<AgtCode>1000</AgtCode>");
		reqXml.append("<LangCode>KOR</LangCode>");
		reqXml.append("<SalesSite>HANATOUR</SalesSite>");
		reqXml.append("<SalSiteCode>C00002S001</SalSiteCode>");
		reqXml.append("<GscCode>HANATOUR</GscCode>");
		reqXml.append("<RqAgtCode>1000</RqAgtCode>");
		reqXml.append("<RqId>DOTCOM</RqId>");
		reqXml.append("<RqLclName>DOTCOM</RqLclName>");
		reqXml.append("<Trip>RT</Trip>");
		reqXml.append("<DepDate0>"+h_dep_dt+"</DepDate0>");
		reqXml.append("<DepDate1>"+h_rtn_dt+"</DepDate1>");
		reqXml.append("<DepDate2></DepDate2>");
		reqXml.append("<Retdate>"+h_rtn_dt+"</Retdate>");
		reqXml.append("<Dep0>"+dep_apo+"</Dep0>");
		reqXml.append("<Arr0>"+rtn_apo+"</Arr0>");
		reqXml.append("<Dep1>"+rtn_apo+"</Dep1>");
		reqXml.append("<Arr1>"+dep_apo+"</Arr1>");
		reqXml.append("<Dep2></Dep2>");
		reqXml.append("<Arr2></Arr2>");
		reqXml.append("<Val></Val>");
		reqXml.append("<Comp>Y</Comp>");
		reqXml.append("<NewComp>Y</NewComp>");
		reqXml.append("<ShortNode>Y</ShortNode>");
		reqXml.append("<Car></Car>");
		reqXml.append("<Adt>1</Adt>");
		reqXml.append("<Chd>0</Chd>");
		reqXml.append("<Inf>0</Inf>");
		reqXml.append("<CRuleType>A</CRuleType>");
		reqXml.append("<FareType>R</FareType>");
		reqXml.append("</FareSchdRQ>");
		//reqXml.append("]]></reqXml></getResponse></soap12:Body></soap12:Envelope>"); 
		
		log(key, requestUrl, reqXml.toString());
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("requestXml", reqXml.toString());
		
		String responseBody = restTemplate.postForObject(requestUrl, params, String.class);
		
		
		//위에서 응답받은 XML 을 Json 으로 변환한다.
		JSONObject xmlJSONObj = null;
		try {
			xmlJSONObj = XML.toJSONObject(responseBody);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String jsonPrettyPrintString = xmlJSONObj.toString();
	    //System.out.println(jsonPrettyPrintString);
		
	    JSONArray jPriceList = new JSONArray(); //하나투어 운임가격 리스트
		JSONArray jSkdList = new JSONArray(); 	// 스케줄 리스트
		JSONArray jSkdMapList = new JSONArray(); // 운임 스케줄 맵핑 리스트
		
		// 모두투어 운임 Json 데이터를 파싱하여 중 필요한 데이터 만을 추출 한다.
		jPriceList = xmlJSONObj.getJSONObject("FareSchdRS").getJSONArray("FareList");
		jSkdList = xmlJSONObj.getJSONObject("FareSchdRS").getJSONArray("SkdList");
		jSkdMapList = xmlJSONObj.getJSONObject("FareSchdRS").getJSONObject("Fsamap").getJSONArray("Fa");
		
		
		List<CommonFareVo> fareList = new ArrayList<CommonFareVo>();
		List<String> compFareList = new ArrayList<String>();
		List<String> compPriceList = new ArrayList<String>();
	    
		
		for(int i=0; i < jPriceList.length(); i++ ){
			System.out.println("i==="+i);
			String fare = jPriceList.getJSONObject(i).get("Sf").toString();			//기본운임
			String tax = jPriceList.getJSONObject(i).get("AdtTaxBill").toString();	//tax
			String oil = jPriceList.getJSONObject(i).get("AdtFuel").toString();		//유류세
			String mkAirNm = jPriceList.getJSONObject(i).get("Crd").toString();		//판매항공사 명
			mkAirNm = StringUtils.replace(mkAirNm, "항공", "");
			
			String mkAirCd = jPriceList.getJSONObject(i).get("Cr").toString();		//판매항공사 코드
			String exp_Dt = jPriceList.getJSONObject(i).get("Vdt").toString();		//유효기간
			String dep_FltNo = "";
			String rtn_FltNo = "";
			String via_Cnt = jPriceList.getJSONObject(i).get("Cnxid").toString();	//경유 횟수
			
			String tmpSkdSeq =  jPriceList.getJSONObject(i).get("Seq").toString();	//스케줄 맵핑 키
			
			String tmpDep = ""; 
			String tmpRtn = ""; 
			// FareList 의 Seq 값과 Fsamap > fl 값의 첫번재 값이 같은 것을 찾는다, fl [11-1-3-0] 값의 첫번째는 운임시퀀스, 2번째 값은 1여정 시퀀스, 3번째 값은 2여정 시퀀스, 4번째 값은 3여정 시퀀스   
			for(int j=0; j < jSkdMapList.length(); j++ ){
				JSONObject fmap = jSkdMapList.getJSONObject(j);
				String[] planArr = fmap.getString("Fl").split("-");
				
				if(tmpSkdSeq.equals(planArr[0])){
					tmpDep = planArr[1];
					tmpRtn = planArr[2];
					break;
				}
			}
			
			//위 Fsmap 에서 찾은 1,2 번째 여정 시퀀스 값 을 기준으로 SkdList list 에서 동일한 Seq 값을 가진 Row를 찾는다.
			for(int j=0; j < jSkdList.length(); j++ ){
				JSONObject skd = jSkdList.getJSONObject(j);
				String skdSet = skd.get("Skdset").toString();
				String skdSeq = skd.get("Seq").toString();
				
				if( "1".equals(skdSet) && tmpDep.equals(skdSeq)){
					
					if( "org.json.JSONObject".equals(skd.get("SegList").getClass().getCanonicalName()) ){
						dep_FltNo = skd.getJSONObject("SegList").get("Mflt").toString();
					}else{
						dep_FltNo = skd.getJSONArray("SegList").getJSONObject(0).get("Mflt").toString();
					}
					
				}
				
				if( "2".equals(skdSet) && tmpRtn.equals(skdSeq)){
					if( "org.json.JSONObject".equals(skd.get("SegList").getClass().getCanonicalName()) ){
						rtn_FltNo = skd.getJSONObject("SegList").get("Mflt").toString();
					}else{
						rtn_FltNo = skd.getJSONArray("SegList").getJSONObject(0).get("Mflt").toString();
					}
				}
				
				if( StringUtils.isNoneBlank(dep_FltNo) && StringUtils.isNoneBlank(rtn_FltNo) ){
					break;
				}
			}
			
			
			//프로모션 카드 운임 중 최저가를 구한다.
			JSONArray cardRecList = jPriceList.getJSONObject(i).optJSONArray("CardRecList");
			
			if( cardRecList == null){
				JSONObject tmpCardInfo = new JSONObject();
				tmpCardInfo.put("CpcCode", jPriceList.getJSONObject(i).get("Idt").toString());
				tmpCardInfo.put("CpcIdtLclDesc", jPriceList.getJSONObject(i).get("Idtd").toString());
				tmpCardInfo.put("CardFareAdt", fare);
				tmpCardInfo.put("TFeeAdt", jPriceList.getJSONObject(i).get("Tfee").toString());
				
				cardRecList = new JSONArray();
				cardRecList.put(tmpCardInfo);
			}
			
			for(int x=0; x < cardRecList.length(); x++ ){
				JSONObject cardInfo = cardRecList.getJSONObject(x);
				CommonFareVo fareVo = new CommonFareVo();
				
				String disFare = cardInfo.get("CardFareAdt").toString(); //할인운임
				String tasf = cardInfo.get("TFeeAdt").toString(); // tasf 발권수수료
				
				//int sumFare = Integer.parseInt(disFare) + Integer.parseInt(tax) + Integer.parseInt(oil);
				
				int sumFare = (int) (Float.parseFloat(disFare) + Float.parseFloat(tax) + Float.parseFloat(oil));
				
				String price = Integer.toString(sumFare); 
				String promoNm = cardInfo.get("CpcIdtLclDesc").toString();
				
				fareVo.setFare(fare);		//기본운임
				fareVo.setTax(tax);			//tax
				fareVo.setDisFare(disFare);	//할인금액
				fareVo.setPrice(price);		//노출금액
				fareVo.setTasf(tasf);		//tasf 발권수수료
				fareVo.setOil(oil);			//유류세
				fareVo.setMkAirNm(mkAirNm);	//판매항공사 명
				fareVo.setMkAirCd(mkAirCd);		//판매항공사 코드
				fareVo.setExp_Dt(exp_Dt);		//유효기간
				fareVo.setDep_FltNo(dep_FltNo);
				fareVo.setRtn_FltNo(rtn_FltNo);
				fareVo.setViaCnt(via_Cnt);
				fareVo.setPromo_Nm(promoNm);
				
				String tmpFare =  fareVo.getMkAirCd() + "|" + fareVo.getPromo_Nm();
				String tmpPrice = fareVo.getPrice();
				
				System.out.println(fareVo.toString());
				int dupIdx = compFareList.indexOf(tmpFare);
				int c_Price = Integer.parseInt(fareVo.getPrice());
				
				if(dupIdx > -1){
					if( c_Price < Integer.parseInt(compPriceList.get(dupIdx))){
						compFareList.set(dupIdx, tmpFare);
						compPriceList.set(dupIdx, tmpPrice);
						fareList.set(dupIdx, fareVo);
					}
				}else{
					compFareList.add(tmpFare);
					compPriceList.add(tmpPrice);
					fareList.add(fareVo);
				}
				
			}
		}
		
		System.out.println("fareList ========="+fareList.toString()); 
		
		return responseBody;
	}
	

	@RequestMapping(value={"/farecompare/searchMode/ws.hnt"}, produces="application/text; charset=utf-8")
	@ResponseBody
	public String searchMode(HttpServletRequest request,  @RequestBody String requestBody, Model model) throws Exception{
		
		String[] arrUrl;
		String requestUrl = "";
		String key = "";
		
		if(request != null){
			key = request.getRequestedSessionId();
		}
		
		System.out.println("request ==="+ requestBody);
		arrUrl = requestBody.split("\\?");
		
		requestUrl = arrUrl[0];
	
		log(key, requestUrl, arrUrl[1]);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		ResponseEntity<String> responseEntity = restTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity(arrUrl[1], headers),String.class);
		String responseBody = responseEntity.getBody();
		
		System.out.println("responseBody ==="+ responseBody);
		
		
		//위에서 응답받은 XML 을 Json 으로 변환한다.
		JSONObject xmlJSONObj = null;
		try {
			xmlJSONObj = XML.toJSONObject(responseBody);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		JSONArray jPriceList = new JSONArray(); //모두투어 운임가격 리스트
		JSONArray jDepSeg = new JSONArray(); 	// 출발 스케줄
		JSONArray jRtnSeg = new JSONArray(); 	// 귀국 스케줄
		JSONArray jPromotion = new JSONArray(); // 프로모션(카드할인)리스트 
		
		String jsonPrettyPrintString = xmlJSONObj.toString();
	    System.out.println(jsonPrettyPrintString);
	        
		// 모두투어 운임 Json 데이터를 파싱하여 중 필요한 데이터 만을 추출 한다.
		jPriceList = xmlJSONObj.getJSONObject("ResponseDetails").getJSONObject("priceInfo").getJSONArray("priceIndex");
		jDepSeg = xmlJSONObj.getJSONObject("ResponseDetails").getJSONObject("flightInfo").getJSONArray("flightIndex").getJSONObject(0).getJSONArray("segGroup");
		jRtnSeg = xmlJSONObj.getJSONObject("ResponseDetails").getJSONObject("flightInfo").getJSONArray("flightIndex").getJSONObject(1).getJSONArray("segGroup");
		jPromotion = xmlJSONObj.getJSONObject("ResponseDetails").getJSONObject("promotions").getJSONArray("promotion");
		
		List<CommonFareVo> fareList = new ArrayList<CommonFareVo>();
		List<String> compFareList = new ArrayList<String>();
		List<String> compPriceList = new ArrayList<String>();
		
		for(int i=0; i < jPriceList.length(); i++ ){
			
			String fare = jPriceList.getJSONObject(i).getJSONObject("summary").get("fare").toString();		//기본운임
			String tax = jPriceList.getJSONObject(i).getJSONObject("summary").get("tax").toString();			//tax
			String disFare = jPriceList.getJSONObject(i).getJSONObject("summary").get("disFare").toString();	//할인금액
			String price = jPriceList.getJSONObject(i).getJSONObject("summary").get("price").toString();		//노출금액
			String tasf = jPriceList.getJSONObject(i).getJSONObject("summary").get("tasf").toString();		//tasf 발권수수료
			String oil = jPriceList.getJSONObject(i).getJSONObject("summary").get("fsc").toString();			//유류세
			String mkAirNm = jPriceList.getJSONObject(i).getJSONObject("summary").get("pvcK").toString();	//판매항공사 명
			mkAirNm = StringUtils.replace(mkAirNm, "항공", "");
			
			String mkAirCd = jPriceList.getJSONObject(i).getJSONObject("summary").get("pvc").toString();		//판매항공사 코드
			String exp_Dt = jPriceList.getJSONObject(i).getJSONObject("summary").get("mas").toString();		//유효기간
			String dep_FltNo = "";
			String rtn_FltNo = "";
			String via_Cnt = "";
			
			String tmpDep =  "";	//출발스케줄 연동 key 
			String tmpRtn = "";		//귀국스케줄 연동 key
			
			if( "org.json.JSONObject".equals(jPriceList.getJSONObject(i).getJSONObject("segGroup").get("seg").getClass().getCanonicalName()) ){
				tmpDep = jPriceList.getJSONObject(i).getJSONObject("segGroup").getJSONObject("seg").getJSONArray("ref").getJSONObject(0).get("content").toString();
			}else{
				tmpDep = jPriceList.getJSONObject(i).getJSONObject("segGroup").getJSONArray("seg").getJSONObject(0).getJSONArray("ref").getJSONObject(0).get("content").toString();
			}
			
			if( "org.json.JSONObject".equals(jPriceList.getJSONObject(i).getJSONObject("segGroup").get("seg").getClass().getCanonicalName()) ){
				tmpRtn = jPriceList.getJSONObject(i).getJSONObject("segGroup").getJSONObject("seg").getJSONArray("ref").getJSONObject(1).get("content").toString();
			}else{
				tmpRtn = jPriceList.getJSONObject(i).getJSONObject("segGroup").getJSONArray("seg").getJSONObject(1).getJSONArray("ref").getJSONObject(1).get("content").toString();
			}
			

			// 출발스케줄 key 값으로 스케줄 리스트 정보에서 맵핑되는 값을 찾아 저장 한다 
			for(int j=0; j < jDepSeg.length(); j++ ){
				String tmpRef = jDepSeg.getJSONObject(j).get("ref").toString();
				
				if( tmpDep.equals(tmpRef)){
					if( "org.json.JSONObject".equals(jDepSeg.getJSONObject(j).get("seg").getClass().getCanonicalName()) ){
						dep_FltNo = jDepSeg.getJSONObject(j).getJSONObject("seg").get("fln").toString();
					}else{
						dep_FltNo = jDepSeg.getJSONObject(j).getJSONArray("seg").getJSONObject(0).get("fln").toString();
					}
					
					via_Cnt = jDepSeg.getJSONObject(j).get("via").toString();
					
					break;
				}
				
			}
			
			// 귀국스케줄 key 값으로 스케줄 리스트 정보에서 맵핑되는 값을 찾아 저장 한다
			for(int j=0; j < jRtnSeg.length(); j++ ){
				String tmpRef = jRtnSeg.getJSONObject(j).get("ref").toString();
				
				if( tmpDep.equals(tmpRef)){
					if( "org.json.JSONObject".equals(jRtnSeg.getJSONObject(j).get("seg").getClass().getCanonicalName()) ){
						rtn_FltNo = jRtnSeg.getJSONObject(j).getJSONObject("seg").get("fln").toString();
					}else{
						rtn_FltNo = jRtnSeg.getJSONObject(j).getJSONArray("seg").getJSONObject(0).get("fln").toString();
					}
					
					break;
				}
			}
			
			//프로모션 정보를 담는다. 
			JSONObject tmpPromo = jPriceList.getJSONObject(i).optJSONObject("promotionInfo");
			JSONArray arrPromo = new JSONArray(); // 프로모션이 여러개일 경우 프로모션 list를 담는다.
			
			if(tmpPromo != null){
				
				if( "org.json.JSONArray".equals(tmpPromo.getJSONObject("item").getJSONObject("promotions").get("promotion").getClass().getCanonicalName()) ){
					arrPromo = tmpPromo.getJSONObject("item").getJSONObject("promotions").getJSONArray("promotion");
				}else{
					JSONObject objPromo = new JSONObject();
					
					objPromo = tmpPromo.getJSONObject("item").getJSONObject("promotions").getJSONObject("promotion");
					
					arrPromo.put(objPromo);
				}
			}else{
				JSONObject objPromo = new JSONObject();
				objPromo.put("incentiveCode", "ADT");
				arrPromo.put(objPromo);
			}
			
			if(arrPromo != null){
				//프로모션 갯수 만큼 운임 데이터 생성
				for(int k=0; k < arrPromo.length(); k++ ){
					String promoNm = null;
					String tmpCd_1 = arrPromo.getJSONObject(k).getString("incentiveCode");
					
					if("ADT".equals(tmpCd_1)){
						promoNm = "성인";
					}else{
						for(int x=0; x < jPromotion.length(); x++ ){
							String tmpCd2 = jPromotion.getJSONObject(x).getString("Code");
							
							if ( tmpCd_1.equals(tmpCd2) ){
								promoNm = jPromotion.getJSONObject(x).getString("content");
								break;
							}
						}
					}
					CommonFareVo fareVo = new CommonFareVo();
					
					fareVo.setFare(fare);		//기본운임
					fareVo.setTax(tax);			//tax
					fareVo.setDisFare(disFare);	//할인금액
					fareVo.setPrice(price);		//노출금액
					fareVo.setTasf(tasf);		//tasf 발권수수료
					fareVo.setOil(oil);			//유류세
					fareVo.setMkAirNm(mkAirNm);	//판매항공사 명
					fareVo.setMkAirCd(mkAirCd);		//판매항공사 코드
					fareVo.setExp_Dt(exp_Dt);		//유효기간
					fareVo.setDep_FltNo(dep_FltNo);
					fareVo.setRtn_FltNo(rtn_FltNo);
					fareVo.setViaCnt(via_Cnt);
					fareVo.setPromo_Nm(promoNm);
					
					String tmpFare =  fareVo.getMkAirCd() + "|" + fareVo.getPromo_Nm();
					String tmpPrice = fareVo.getPrice();
					
					int dupIdx = compFareList.indexOf(tmpFare);
					int c_Price = Integer.parseInt(fareVo.getPrice());
					
					if(dupIdx > -1){
						if( c_Price < Integer.parseInt(compPriceList.get(dupIdx))){
							compFareList.set(dupIdx, tmpFare);
							compPriceList.set(dupIdx, tmpPrice);
							fareList.set(dupIdx, fareVo);
						}
					}else{
						compFareList.add(tmpFare);
						compPriceList.add(tmpPrice);
						fareList.add(fareVo);
					}
				}
			}
		}
		
		System.out.println("fareList ========="+fareList.toString()); 
		
		//List<CommonFareVo> parsList =  FarePase(fareList);
		
		log(key, requestUrl, responseBody);
		
		return responseBody;
	}
	
	
	
	
	
	@RequestMapping(value={"/farecompare/searchInterPark/ws.hnt"}, produces="application/text; charset=utf-8")
	@ResponseBody
	public String searchInterPark(HttpServletRequest request,  Model model) throws Exception{
		
		String dep_apo = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("dep_apo"), ""));
		String rtn_apo = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("rtn_apo"), ""));
		String dep_dt = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("dep_dt"), ""));
		String rtn_dt = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("rtn_dt"), ""));
		
		String h_dep_dt = StringUtils.replaceAll(dep_dt, "-", "");
		String h_rtn_dt = StringUtils.replaceAll(rtn_dt, "-", "");
		
		
		String requestUrl = "";
		String key = "";
		
		if(request != null){
			key = request.getRequestedSessionId();
		}
		
		String site_cd = "interpark";
		
		requestUrl = ApiConstants.INTERPARK_FARE_URL_PRODUCTION;
		//String requestBody = "Soto=N&ptype=I&SeatAvail=Y&dep0="+ dep_apo +"&comp=Y&arr0="+ rtn_apo +"&depdate0="+ h_dep_dt +"&dep1="+ rtn_apo +"&faretype&arr1="+ dep_apo +"&enc=u&SeatType=A&FLEX=Y&PageNo=1&Change=NEW&SplitNo=1500&retdate="+ h_rtn_dt +"&adt=1&chd=0&BEST=Y&MoreKey&inf=0&trip=RT&AirLine&StayLength&JSON=Y";
		
		String requestBody = "SeatType=A&faretype=&dep1="+ rtn_apo +"&ScheduleGroup=Y&arr1=SEL&adt=1&retdate="+ h_rtn_dt +"&chd=0&inf=0&Change=NEW&SeatAvail=Y&comp=Y&dep0="+ dep_apo +"&FLEX=Y&ptype=I&arr0="+ rtn_apo +"&enc=u&MoreKey=&PageNo=1&depdate0="+ h_dep_dt +"&SplitNo=129573&CsplitNo=1&AirLine=&Soto=N&trip=RT&ClassJoin=Y&StayLength=&BEST=N&JSON=Y";
		
		log(key, requestUrl, requestBody);
		
		requestUrl = requestUrl+"?"+requestBody;
		
		URI uri = new URI(requestUrl);
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		//ResponseEntity<String> responseEntity = restTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity(requestBody, headers),String.class);
		
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
		String responseBody = responseEntity.getBody();
		
		//log(key, requestUrl, responseBody);
		
		responseBody = StringUtils.substring(responseBody, 1, responseBody.length() -1);
		
		//System.out.println("responseBody ========" + responseBody);
		
		JSONObject jObj = new JSONObject(responseBody);
		JSONArray jPriceList = new JSONArray(); //인터파크운임가격 리스트
		JSONArray jDepSeg = new JSONArray(); 	// 출발 스케줄리스트
		JSONArray jRtnSeg = new JSONArray(); 	// 귀국 스케줄리스트
		
		
		// 운임 Json 데이터를 파싱하여  필요한 데이터 만을 추출 한다.
		jPriceList = jObj.getJSONObject("Responses").getJSONObject("GoodsList").getJSONArray("Goods");
		jDepSeg = jObj.getJSONObject("Responses").getJSONObject("AirAvail").getJSONObject("StartAvail").getJSONArray("Goods");
		jRtnSeg = jObj.getJSONObject("Responses").getJSONObject("AirAvail").getJSONObject("ReturnAvail").getJSONArray("Goods");
		
		
		List<CommonFareVo> fareList = new ArrayList<CommonFareVo>();
		List<String> compFareList = new ArrayList<String>();
		List<String> compPriceList = new ArrayList<String>();
	    
		try{
			for(int i=0; i < jPriceList.length(); i++ ){
				System.out.println("i==="+i);
				String startCheck = jPriceList.getJSONObject(i).get("StartCheck").toString(); //출발 편 좌석 여부
				String returnCheck = jPriceList.getJSONObject(i).get("ReturnCheck").toString(); //귀국 편 좌석 여부
				String fareFix = jPriceList.getJSONObject(i).get("FareFix").toString();    //요금 확정 여부
				
				if( Integer.parseInt(startCheck) > 0 &&  Integer.parseInt(returnCheck) > 0 && "확정".equals(fareFix)){
					CommonFareVo fareVo = new CommonFareVo();
					
					String fare = jPriceList.getJSONObject(i).get("NormalFare").toString();			//기본운임
					String disFare = jPriceList.getJSONObject(i).get("SaleFare").toString();  	//할인운임
					String tax = jPriceList.getJSONObject(i).get("Tax").toString();				//tax
					String oil = jPriceList.getJSONObject(i).get("Qcharge").toString();			//유류세
					String price = jPriceList.getJSONObject(i).get("TotalSaleFare").toString();	//노출운임
					String tasf = jPriceList.getJSONObject(i).getJSONObject("SotoFare").getJSONObject("Tasf").get("Adt").toString(); //tasf
					String mkAirNm = jPriceList.getJSONObject(i).get("AirLineKor").toString();	//판매항공사 명
					mkAirNm = StringUtils.replace(mkAirNm, "항공", "");
					
					String mkAirCd = jPriceList.getJSONObject(i).get("AirLine").toString();		//판매항공사 코드
					String exp_Dt = "";		//유효기간
					String dep_FltNo = "";	//출발항공편명					
					String rtn_FltNo = "";	//귀국항공편명
					String via_Cnt = "";	//경유 횟수
					String promoNm = jPriceList.getJSONObject(i).get("FareTypeDesc").toString();	//프로모션 이름 
					
					if(!"성인".equals(promoNm)){
						promoNm = StringUtils.replace(promoNm, "성인", "");
						promoNm = StringUtils.replace(promoNm, "(", "");
						promoNm = StringUtils.replace(promoNm, ")", "");
						promoNm = StringUtils.replace(promoNm, "결제조건", "");
					}
					
					
					String tmpExp = jPriceList.getJSONObject(i).get("ValidateMax").toString();
					tmpExp = StringUtils.replace(tmpExp, "일", "D");
					tmpExp = StringUtils.replace(tmpExp, "개월", "M");
					tmpExp = StringUtils.replace(tmpExp, "년", "Y");
					exp_Dt = tmpExp;
					
					
					String depSeg = jPriceList.getJSONObject(i).get("SGC").toString();
					String rtnSeg = jPriceList.getJSONObject(i).get("RGC").toString();
					
					
					for(int s=0; s < jDepSeg.length(); s++){
						String tmpSgc = jDepSeg.getJSONObject(s).getString("SGC");
						String tmpRgc = jDepSeg.getJSONObject(s).getString("RGC");
						
						if( depSeg.equals(tmpSgc) && rtnSeg.equals(tmpRgc) ){
							JSONObject startSeg =  jDepSeg.getJSONObject(s).getJSONObject("StartAvail");
							
							if( "org.json.JSONArray".equals(startSeg.get("AirItn").getClass().getCanonicalName()) ){
								if( "org.json.JSONArray".equals(startSeg.getJSONArray("AirItn").getJSONObject(0).get("seg_detail_t").getClass().getCanonicalName()) ){
									dep_FltNo = startSeg.getJSONArray("AirItn").getJSONObject(0).getJSONArray("seg_detail_t").getJSONObject(0).get("main_flt").toString();
								}else{
									dep_FltNo = startSeg.getJSONArray("AirItn").getJSONObject(0).getJSONObject("seg_detail_t").get("main_flt").toString();
								}
								via_Cnt = startSeg.getJSONArray("AirItn").getJSONObject(0).get("Total_Seg").toString();
							}else{
								if( "org.json.JSONArray".equals(startSeg.getJSONObject("AirItn").get("seg_detail_t").getClass().getCanonicalName()) ){
									dep_FltNo = startSeg.getJSONObject("AirItn").getJSONArray("seg_detail_t").getJSONObject(0).get("main_flt").toString();
								}else{
									dep_FltNo = startSeg.getJSONObject("AirItn").getJSONObject("seg_detail_t").getString("main_flt").toString();
								}
								via_Cnt = startSeg.getJSONObject("AirItn").get("Total_Seg").toString();
							}
							
							break;
						}
						
					}
					
					for(int s=0; s < jRtnSeg.length(); s++){
						String tmpSgc = jRtnSeg.getJSONObject(s).getString("SGC");
						String tmpRgc = jRtnSeg.getJSONObject(s).getString("RGC");
						
						if( depSeg.equals(tmpSgc) && rtnSeg.equals(tmpRgc) ){
							JSONObject endSeg =  jRtnSeg.getJSONObject(s).getJSONObject("ReturnAvail");
							
							if( "org.json.JSONArray".equals(endSeg.get("AirItn").getClass().getCanonicalName()) ){
								if( "org.json.JSONArray".equals(endSeg.getJSONArray("AirItn").getJSONObject(0).get("seg_detail_t").getClass().getCanonicalName()) ){
									rtn_FltNo = endSeg.getJSONArray("AirItn").getJSONObject(0).getJSONArray("seg_detail_t").getJSONObject(0).get("main_flt").toString();
								}else{
									rtn_FltNo = endSeg.getJSONArray("AirItn").getJSONObject(0).getJSONObject("seg_detail_t").get("main_flt").toString();
								}
							}else{
								if( "org.json.JSONArray".equals(endSeg.getJSONObject("AirItn").get("seg_detail_t").getClass().getCanonicalName()) ){
									rtn_FltNo = endSeg.getJSONObject("AirItn").getJSONArray("seg_detail_t").getJSONObject(0).get("main_flt").toString();
								}else{
									rtn_FltNo = endSeg.getJSONObject("AirItn").getJSONObject("seg_detail_t").getString("main_flt").toString();
								}
							}
							break;
						}
						
					}
					
					/*
					JSONObject startSeg =  jPriceList.getJSONObject(i).getJSONObject("AirAvail").getJSONObject("StartAvail");
					JSONObject returnSeg =  jPriceList.getJSONObject(i).getJSONObject("AirAvail").getJSONObject("ReturnAvail");
					if( "org.json.JSONArray".equals(startSeg.get("AirItn").getClass().getCanonicalName()) ){
						if( "org.json.JSONArray".equals(startSeg.getJSONArray("AirItn").getJSONObject(0).get("seg_detail_t").getClass().getCanonicalName()) ){
							dep_FltNo = startSeg.getJSONArray("AirItn").getJSONObject(0).getJSONArray("seg_detail_t").getJSONObject(0).get("main_flt").toString();
						}else{
							dep_FltNo = startSeg.getJSONArray("AirItn").getJSONObject(0).getJSONObject("seg_detail_t").get("main_flt").toString();
						}
						via_Cnt = startSeg.getJSONArray("AirItn").getJSONObject(0).get("Total_Seg").toString();
					}else{
						if( "org.json.JSONArray".equals(startSeg.getJSONObject("AirItn").get("seg_detail_t").getClass().getCanonicalName()) ){
							dep_FltNo = startSeg.getJSONObject("AirItn").getJSONArray("seg_detail_t").getJSONObject(0).get("main_flt").toString();
						}else{
							dep_FltNo = startSeg.getJSONObject("AirItn").getJSONObject("seg_detail_t").getString("main_flt").toString();
						}
						via_Cnt = startSeg.getJSONObject("AirItn").get("Total_Seg").toString();
					}
					if( "org.json.JSONArray".equals(returnSeg.get("AirItn").getClass().getCanonicalName()) ){
						if( "org.json.JSONArray".equals(returnSeg.getJSONArray("AirItn").getJSONObject(0).get("seg_detail_t").getClass().getCanonicalName()) ){
							rtn_FltNo = returnSeg.getJSONArray("AirItn").getJSONObject(0).getJSONArray("seg_detail_t").getJSONObject(0).getString("main_flt").toString();
						}else{
							rtn_FltNo = returnSeg.getJSONArray("AirItn").getJSONObject(0).getJSONObject("seg_detail_t").getString("main_flt").toString();
						}
					}else{
						if( "org.json.JSONArray".equals(returnSeg.getJSONObject("AirItn").get("seg_detail_t").getClass().getCanonicalName()) ){
							rtn_FltNo = returnSeg.getJSONObject("AirItn").getJSONArray("seg_detail_t").getJSONObject(0).getString("main_flt").toString();
						}else{
							rtn_FltNo = returnSeg.getJSONObject("AirItn").getJSONObject("seg_detail_t").getString("main_flt").toString();
						}
					}
					*/
					
					fareVo.setSiteCd(site_cd); // 사이트명
					fareVo.setDep_Apo(dep_apo); // 출발공항
					fareVo.setRtn_Apo(rtn_apo); // 도착공항
					fareVo.setDep_Dt(h_dep_dt); 	// 출발일
					fareVo.setRtn_Dt(h_rtn_dt);   // 귀국일
					fareVo.setFare(fare);		//기본운임
					fareVo.setTax(tax);			//tax
					fareVo.setDisFare(disFare);	//할인금액
					fareVo.setPrice(price);		//노출금액
					fareVo.setTasf(tasf);		//tasf 발권수수료
					fareVo.setOil(oil);			//유류세
					fareVo.setMkAirNm(mkAirNm);	//판매항공사 명
					fareVo.setMkAirCd(mkAirCd);		//판매항공사 코드
					fareVo.setExp_Dt(exp_Dt);		//유효기간
					fareVo.setDep_FltNo(dep_FltNo);
					fareVo.setRtn_FltNo(rtn_FltNo);
					fareVo.setViaCnt(via_Cnt);
					fareVo.setPromo_Nm(promoNm);
					
					
					String tmpFare =  fareVo.getMkAirCd() + "|" + fareVo.getPromo_Nm();
					String tmpPrice = fareVo.getPrice();
					int dupIdx = compFareList.indexOf(tmpFare);
					int c_Price = Integer.parseInt(fareVo.getPrice());
					
					if(dupIdx > -1){
						if( c_Price < Integer.parseInt(compPriceList.get(dupIdx))){
							compFareList.set(dupIdx, tmpFare);
							compPriceList.set(dupIdx, tmpPrice);
							fareList.set(dupIdx, fareVo);
						}
					}else{
						compFareList.add(tmpFare);
						compPriceList.add(tmpPrice);
						fareList.add(fareVo);
					}
				}
				
				
			}
		}catch(Exception e){
			commonLogger.error("ApiController.searchInterPark Exception :"+ ExceptionUtils.getMessage(e));
		}
		
		
		System.out.println("fareList ========="+fareList.toString()); 
		
		return responseBody;
	}
	
	
	@RequestMapping(value={"/farecompare/searchOnlineTour/ws.hnt"}, produces="application/text; charset=utf-8")
	@ResponseBody
	public String searchOnlineTour(HttpServletRequest request,  Model model) throws Exception{
		
		String dep_apo = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("dep_apo"), ""));
		String rtn_apo = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("rtn_apo"), ""));
		String dep_dt = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("dep_dt"), ""));
		String rtn_dt = StringUtils.trim(StringUtils.defaultIfEmpty(request.getParameter("rtn_dt"), ""));
		
		String h_dep_dt = StringUtils.replaceAll(dep_dt, "-", "");
		String h_rtn_dt = StringUtils.replaceAll(rtn_dt, "-", "");
		
		
		String requestUrl = "";
		String key = "";
		
		if(request != null){
			key = request.getRequestedSessionId();
		}
		
		String site_cd = "onlinetour";
		
		requestUrl = ApiConstants.ONLINETOUR_FARE_URL_PRODUCTION;
		String requestBody = "soto=N&trip=RT&startDt="+h_dep_dt+"&endDt="+h_rtn_dt+"&sDate1=&sDate2=&sDate3=&sCity1="+dep_apo+"&eCity1="+rtn_apo+"&eCity1NtDesc=&sCity2="+ rtn_apo +"&eCity2="+ dep_apo +"&sCity3=&eCity3=&adt=1&chd=0&inf=0&filterAirLine=&stayLength=&stayChk=&seatType=A&best=Y&sgc=&rgc=&partnerCode=&eventNum=&classJoinNum=&partnerNum=&fareType=Y&eventInd=&schedule=Y&splitNo=1000&epricingYn=N&blockGoodsYn=Y&summary=N&SGMap=Y";
		log(key, requestUrl, requestBody);
		
		
		requestUrl = requestUrl+"?"+requestBody;
		
		URI uri = new URI(requestUrl);
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		//ResponseEntity<String> responseEntity = restTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity(requestBody, headers),String.class);
		
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
		String responseBody = responseEntity.getBody();
		
		//log(key, requestUrl, responseBody);
		
		JSONObject jObj = new JSONObject(responseBody);
		JSONArray jPriceList = new JSONArray(); //운임가격 리스트
		JSONArray jSKDList = new JSONArray(); //스케줄 리스트
		
		// 운임 Json 데이터를 파싱하여  필요한 데이터 만을 추출 한다.
		jPriceList = jObj.getJSONObject("Responses").getJSONObject("GoodsList").getJSONArray("Goods");
		jSKDList = jObj.getJSONObject("Responses").getJSONObject("AirAvailList").getJSONArray("MappingList");
		
		List<CommonFareVo> fareList = new ArrayList<CommonFareVo>();
		List<String> compFareList = new ArrayList<String>();
		List<String> compPriceList = new ArrayList<String>();
	    
		String tasf = "0";
		
		for(int i=0; i < jPriceList.length(); i++ ){
			System.out.println("i==="+i);
			String startCheck = jPriceList.getJSONObject(i).get("StartCheck").toString(); //출발 편 좌석 여부
			String returnCheck = jPriceList.getJSONObject(i).get("ReturnCheck").toString(); //귀국 편 좌석 여부
			String fareFix = jPriceList.getJSONObject(i).get("FareFix").toString();    //요금 확정 여부
			
			if( Integer.parseInt(startCheck) > 0 &&  Integer.parseInt(returnCheck) > 0 && "확정".equals(fareFix)){
				
				String exp_Dt = "";		//유효기간
				String dep_FltNo = "";	//출발항공편명					
				String rtn_FltNo = "";	//귀국항공편명
				String via_Cnt = jPriceList.getJSONObject(i).get("VIA").toString();	//경유 횟수
				String tmpExp = jPriceList.getJSONObject(i).get("ValidateMax").toString();
				tmpExp = StringUtils.replace(tmpExp, "일", "D");
				tmpExp = StringUtils.replace(tmpExp, "개월", "M");
				tmpExp = StringUtils.replace(tmpExp, "년", "Y");
				tmpExp = StringUtils.replace(tmpExp, "365D", "1Y");
				exp_Dt = tmpExp;
				
				
				String startSeg =  jPriceList.getJSONObject(i).get("SGC").toString();
				String returnSeg = jPriceList.getJSONObject(i).get("RGC").toString();
				
				for(int k=0; k < jSKDList.length(); k++ ){
					System.out.println("k==="+k);
						boolean skdFlag = false;
						if( "org.json.JSONArray".equals(jSKDList.getJSONObject(k).get("Mapping").getClass().getCanonicalName()) ){	
							for( int j=0; j < jSKDList.getJSONObject(k).getJSONArray("Mapping").length(); j++ ){
								String sgc = jSKDList.getJSONObject(k).getJSONArray("Mapping").getJSONObject(j).get("SGC").toString();
								String rgc = jSKDList.getJSONObject(k).getJSONArray("Mapping").getJSONObject(j).get("RGC").toString();
								if( startSeg.equals(sgc) && returnSeg.equals(rgc)){
									skdFlag = true;
									break;
								}
							}
						}else{
							String sgc = jSKDList.getJSONObject(k).getJSONObject("Mapping").get("SGC").toString();
							String rgc = jSKDList.getJSONObject(k).getJSONObject("Mapping").get("RGC").toString();
							if( startSeg.equals(sgc) && returnSeg.equals(rgc)){
								skdFlag = true;
							}
						}
						if( skdFlag ){
							String stItnNm = jSKDList.getJSONObject(k).getJSONObject("AirAvail").getJSONObject("AvailList1").names().get(0).toString();
							String rtItnNm = jSKDList.getJSONObject(k).getJSONObject("AirAvail").getJSONObject("AvailList2").names().get(0).toString();;
							
							JSONObject availList1 =  jSKDList.getJSONObject(k).getJSONObject("AirAvail").getJSONObject("AvailList1").getJSONObject(stItnNm);
							JSONObject availList2 =  jSKDList.getJSONObject(k).getJSONObject("AirAvail").getJSONObject("AvailList2").getJSONObject(rtItnNm);
//							
							if( "org.json.JSONArray".equals(availList1.get("SegDetail").getClass().getCanonicalName()) ){
								dep_FltNo = availList1.getJSONArray("SegDetail").getJSONObject(0).get("FltNum").toString();
							}else{
								dep_FltNo = availList1.getJSONObject("SegDetail").get("FltNum").toString();
							}
							
							if( "org.json.JSONArray".equals(availList2.get("SegDetail").getClass().getCanonicalName()) ){
								dep_FltNo = availList2.getJSONArray("SegDetail").getJSONObject(0).get("FltNum").toString();
							}else{
								dep_FltNo = availList2.getJSONObject("SegDetail").get("FltNum").toString();
							}
//							
//							if( "org.json.JSONArray".equals(availList1.get("AirItnList").getClass().getCanonicalName()) ){
//
//								if( "org.json.JSONArray".equals(availList1.getJSONArray("AirItnList").getJSONObject(0).get("SegDetail").getClass().getCanonicalName()) ){
//									dep_FltNo = availList1.getJSONArray("AirItnList").getJSONObject(0).getJSONArray("SegDetail").getJSONObject(0).get("FltNum").toString();
//								}else{
//									dep_FltNo = availList1.getJSONArray("AirItnList").getJSONObject(0).getJSONObject("SegDetail").get("FltNum").toString();
//								}
//							}else{
//								if( "org.json.JSONArray".equals(availList1.getJSONObject("AirItnList").get("SegDetail").getClass().getCanonicalName()) ){
//									//dep_FltNo = availList1.getJSONArray("AirItnList").getJSONObject(0).getJSONArray("SegDetail").getJSONObject(0).get("FltNum").toString();
//									dep_FltNo = availList1.getJSONObject("AirItnList").getJSONArray("SegDetail").getJSONObject(0).get("FltNum").toString();
//								}else{
//									dep_FltNo = availList1.getJSONObject("AirItnList").getJSONObject("SegDetail").get("FltNum").toString();
//								}
//							}
//							
//							if( "org.json.JSONArray".equals(availList2.get("AirItnList").getClass().getCanonicalName()) ){
//								
//								if( "org.json.JSONArray".equals(availList2.getJSONArray("AirItnList").getJSONObject(0).get("SegDetail").getClass().getCanonicalName()) ){
//									rtn_FltNo = availList2.getJSONArray("AirItnList").getJSONObject(0).getJSONArray("SegDetail").getJSONObject(0).get("FltNum").toString();
//								}else{
//									rtn_FltNo = availList2.getJSONArray("AirItnList").getJSONObject(0).getJSONObject("SegDetail").get("FltNum").toString();
//								}
//								
//							}else{
//								if( "org.json.JSONArray".equals(availList2.getJSONObject("AirItnList").get("SegDetail").getClass().getCanonicalName()) ){
//									//dep_FltNo = availList1.getJSONArray("AirItnList").getJSONObject(0).getJSONArray("SegDetail").getJSONObject(0).get("FltNum").toString();
//									rtn_FltNo = availList2.getJSONObject("AirItnList").getJSONArray("SegDetail").getJSONObject(0).get("FltNum").toString();
//								}else{
//									rtn_FltNo = availList2.getJSONObject("AirItnList").getJSONObject("SegDetail").get("FltNum").toString();
//								}
//							}
//							
							break;
						}

				}
				
				//프로모션 정보를 담는다. 
				JSONObject tmpPromo = jPriceList.getJSONObject(i).getJSONObject("EventFareList");
				JSONArray arrPromo = new JSONArray(); // 프로모션이 여러개일 경우 프로모션 list를 담는다.
				
				if( "org.json.JSONArray".equals(tmpPromo.get("EventFare").getClass().getCanonicalName()) ){
					arrPromo = tmpPromo.getJSONArray("EventFare");
				}else{
					JSONObject objPromo = new JSONObject();
					
					if( null == tmpPromo.optJSONObject("EventFare") ){
						objPromo.put("FareTypeDesc", jPriceList.getJSONObject(i).get("FareTypeDesc").toString());
						objPromo.put("SaleFare", jPriceList.getJSONObject(i).get("SaleFare").toString());
						
						String sfare = jPriceList.getJSONObject(i).get("SaleFare").toString();
						String stax = jPriceList.getJSONObject(i).get("Tax").toString();				//tax
						String soil = jPriceList.getJSONObject(i).get("Qcharge").toString();			//유류세
						
						int sumFare = (int) (Float.parseFloat(sfare) + Float.parseFloat(stax) + Float.parseFloat(soil));
						String price = Integer.toString(sumFare); 
						objPromo.put("TotalSaleFare", price);
						
						arrPromo.put(objPromo);
					}else{
						objPromo = tmpPromo.getJSONObject("EventFare");
						arrPromo.put(objPromo);
					}
				}
				
				
				if(arrPromo != null){
					//프로모션 갯수 만큼 운임 데이터 생성
					for(int k=0; k < arrPromo.length(); k++ ){
						CommonFareVo fareVo = new CommonFareVo();
						
						String promoNm = arrPromo.getJSONObject(k).get("FareTypeDesc").toString();	//프로모션 이름 
						if(!"성인".equals(promoNm)){
							promoNm = StringUtils.replace(promoNm, "성인", "");
							promoNm = StringUtils.replace(promoNm, "/", "");
							promoNm = StringUtils.replace(promoNm, "결제", "");
							promoNm = StringUtils.replaceAll(promoNm, "\\p{Z}", "");
						}
						
						String fare = jPriceList.getJSONObject(i).get("NormalFare").toString();		//기본운임
						String disFare = arrPromo.getJSONObject(k).get("SaleFare").toString();  	//할인운임
						String tax = jPriceList.getJSONObject(i).get("Tax").toString();				//tax
						String oil = jPriceList.getJSONObject(i).get("Qcharge").toString();			//유류세
						String price = arrPromo.getJSONObject(k).get("TotalSaleFare").toString();   //노출운임
						
						String mkAirNm = jPriceList.getJSONObject(i).get("AirLineKor").toString();	//판매항공사 명
						String mkAirCd = jPriceList.getJSONObject(i).get("AirLine").toString();		//판매항공사 코드
						mkAirNm = StringUtils.replace(mkAirNm, "항공", "");
						
						fareVo.setSiteCd(site_cd); // 사이트명
						fareVo.setDep_Apo(dep_apo); // 출발공항
						fareVo.setRtn_Apo(rtn_apo); // 도착공항
						fareVo.setDep_Dt(h_dep_dt); 	// 출발일
						fareVo.setRtn_Dt(h_rtn_dt);   // 귀국일
						fareVo.setFare(fare);		//기본운임
						fareVo.setTax(tax);			//tax
						fareVo.setDisFare(disFare);	//할인금액
						fareVo.setPrice(price);		//노출금액
						fareVo.setTasf(tasf);		//tasf 발권수수료
						fareVo.setOil(oil);			//유류세
						fareVo.setMkAirNm(mkAirNm);	//판매항공사 명
						fareVo.setMkAirCd(mkAirCd);		//판매항공사 코드
						fareVo.setExp_Dt(exp_Dt);		//유효기간
						fareVo.setDep_FltNo(dep_FltNo);
						fareVo.setRtn_FltNo(rtn_FltNo);
						fareVo.setViaCnt(via_Cnt);
						fareVo.setPromo_Nm(promoNm);
						
						
						String tmpFare =  fareVo.getMkAirCd() + "|" + fareVo.getPromo_Nm();
						String tmpPrice = fareVo.getPrice();
						
						int dupIdx = compFareList.indexOf(tmpFare);
						int c_Price = Integer.parseInt(fareVo.getPrice());
						
						if(dupIdx > -1){
							if( c_Price < Integer.parseInt(compPriceList.get(dupIdx))){
								compFareList.set(dupIdx, tmpFare);
								compPriceList.set(dupIdx, tmpPrice);
								fareList.set(dupIdx, fareVo);
							}
						}else{
							compFareList.add(tmpFare);
							compPriceList.add(tmpPrice);
							fareList.add(fareVo);
						}

					}
				}
			}
			
			
		}
		
		System.out.println("fareList ========="+fareList.toString()); 
		
		return responseBody;
	}
//	
//	public List<CommonFareVo> FarePase( List<CommonFareVo> fareList ){ 
//		
//		Comparator<CommonFareVo> fareComparator; 
//		Comparator<CommonFareVo> fareComparator2;
//		
//		//fareComparator = (fc1, fc2) -> ( ( NumberUtils.toInt(fc1.getPrice(), 0) ) - ( NumberUtils.toInt(fc2.getPrice(), 0) )  );
//		fareComparator2 = (fc1, fc2) -> fc2.getMkAirNm().compareTo(fc1.getMkAirNm());
//		
//		//List<CommonFareVo> resultList = fareList.stream().sorted(fareComparator2).sorted(fareComparator).collect(Collectors.toList());
//		List<CommonFareVo> resultList = fareList.stream().sorted(fareComparator2).collect(Collectors.toList());
//			
//		return resultList;
//	}
//	
}
