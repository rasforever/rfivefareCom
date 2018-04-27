/**
 * File name : FareSchdSvc.java
 * Package : com.hanatour.fareCompare.service
 * Description : 
 *
 * <pre>
 * <변경이력>
 * 수정일			수정자				수정내용
 * ------------------------------------------------------
 * 2017. 11. 15.		hana	최초 작성
 * </pre>
 */
package com.hanatour.fareCompare.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanatour.fareCompare.dao.FareDao;
import com.hanatour.fareCompare.dao.FareInfoDao;
import com.hanatour.fareCompare.model.CommonFareVo;
import com.hanatour.fareCompare.model.FareInfoVo;
import com.hanatour.fareCompare.model.SystemDefinition;
import com.hanatour.fareCompare.util.CommonUtil;
import com.hanatour.fareCompare.contants.ApiConstants;

/**
 * Class name : FareSchdSvc
 * Description :
 * @author hana
 * @author 2017. 11. 15.
 * @version 1.0
 */
@Service
public class FareSchdSvc {
	private static Logger commonLogger = LogManager.getLogger("COMMONLOGGER");
	
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	SystemDefinition systemDefinition;
	
	@Autowired
	FareDao fareDao;
	
	@Autowired
	FareInfoDao fareInfodao;
	
	@Autowired
	PlatformTransactionManager transactionManager; 
	
	@Autowired
	ReloadableResourceBundleMessageSource messageSource;
	
	private void log(String key, String url, String body, boolean isLog){
		if( isLog && commonLogger.isDebugEnabled() ){
			commonLogger.debug(new StringBuffer("[KEY:").append(key).append("] [").append(url).append("] [").append(body).append("]").toString());
		}
	}
	
	public int searchMode(String key, String dep_apo, String rtn_apo, String dep_dt, String rtn_dt, String search_num, String search_seq) throws Exception{
		int insCnt = -1;
		try{
			String site_cd = "modetour";
			
			String h_dep_dt = StringUtils.replaceAll(dep_dt, "-", "");
			String h_rtn_dt = StringUtils.replaceAll(rtn_dt, "-", "");
			
			String requestUrl = ApiConstants.MODE_FARE_URL_PRODUCTION;
			String requestBody = "SNM=2&OPN=N&SAT=KOR&DLC="+ dep_apo +"&ALC="+ rtn_apo +"&ROT=RT&SAC=&CCD=Y&PTC=ADT&DTD="+dep_dt+"&ARD="+rtn_dt+"&NRR=200&FLD=&NOP=1&ADC=1&CHC=0&IFC=0";
			log(key, requestUrl, requestBody, true);
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			ResponseEntity<String> responseEntity = restTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity(requestBody, headers),String.class);
			String responseBody = responseEntity.getBody();
			
			log(key, requestUrl, responseBody, false);
			
			//위에서 응답받은 XML 을 Json 으로 변환한다.
			JSONObject xmlJSONObj = null;
			xmlJSONObj = XML.toJSONObject(responseBody);
			
			JSONArray jPriceList = new JSONArray(); //모두투어 운임가격 리스트
			JSONArray jDepSeg = new JSONArray(); 	// 출발 스케줄
			JSONArray jRtnSeg = new JSONArray(); 	// 귀국 스케줄
			JSONArray jPromotion = new JSONArray(); // 프로모션(카드할인)리스트 
			
			String jsonPrettyPrintString = xmlJSONObj.toString();
			
			//System.out.println("jsonPrettyPrintString ======"+jsonPrettyPrintString);
			
			// 모두투어 운임 Json 데이터를 파싱하여 중 필요한 데이터 만을 추출 한다.
			jPriceList = xmlJSONObj.getJSONObject("ResponseDetails").getJSONObject("priceInfo").getJSONArray("priceIndex");
			jDepSeg = xmlJSONObj.getJSONObject("ResponseDetails").getJSONObject("flightInfo").getJSONArray("flightIndex").getJSONObject(0).getJSONArray("segGroup");
			jRtnSeg = xmlJSONObj.getJSONObject("ResponseDetails").getJSONObject("flightInfo").getJSONArray("flightIndex").getJSONObject(1).getJSONArray("segGroup");
			
			if( StringUtils.isNoneBlank(xmlJSONObj.getJSONObject("ResponseDetails").get("promotions").toString())  ){
				jPromotion = xmlJSONObj.getJSONObject("ResponseDetails").getJSONObject("promotions").getJSONArray("promotion");
			}
			
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
				String mkAirCd = jPriceList.getJSONObject(i).getJSONObject("summary").get("pvc").toString();		//판매항공사 코드
				String exp_Dt = jPriceList.getJSONObject(i).getJSONObject("summary").get("mas").toString();		//유효기간
				String dep_FltNo = "";
				String rtn_FltNo = "";
				String via_Cnt = "";
				
				String tmpExp = exp_Dt;
				tmpExp = StringUtils.replace(tmpExp, "일", "D");
				tmpExp = StringUtils.replace(tmpExp, "개월", "M");
				tmpExp = StringUtils.replace(tmpExp, "년", "Y");
				tmpExp = StringUtils.replace(tmpExp, "365D", "1Y");
				exp_Dt = tmpExp;
				
				String tmpDep =  "";	//출발스케줄 연동 key 
				String tmpRtn = "";		//귀국스케줄 연동 key
				
				mkAirNm = StringUtils.replace(mkAirNm, "항공", "");
				
				if( "org.json.JSONObject".equals(jPriceList.getJSONObject(i).getJSONObject("segGroup").get("seg").getClass().getCanonicalName()) ){
					
					
					tmpDep = jPriceList.getJSONObject(i).getJSONObject("segGroup").getJSONObject("seg").getJSONArray("ref").getJSONObject(0).get("content").toString();
					tmpRtn = jPriceList.getJSONObject(i).getJSONObject("segGroup").getJSONObject("seg").getJSONArray("ref").getJSONObject(1).get("content").toString();
				}else{
					tmpDep = jPriceList.getJSONObject(i).getJSONObject("segGroup").getJSONArray("seg").getJSONObject(0).getJSONArray("ref").getJSONObject(0).get("content").toString();
					tmpRtn = jPriceList.getJSONObject(i).getJSONObject("segGroup").getJSONArray("seg").getJSONObject(0).getJSONArray("ref").getJSONObject(1).get("content").toString();
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
					
					if( tmpRtn.equals(tmpRef)){
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
						fareVo.setSch_Num(search_num);
						fareVo.setSch_Seq(search_seq);
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
			
			insCnt = fareInsert(fareList);
			
			//List<CommonFareVo> parsList =  FarePase(fareList);
			
		}catch(Exception e){
			insCnt = -1;
			commonLogger.error("FareSchdSvc.searchMode Exception :"+ ExceptionUtils.getMessage(e));
			commonLogger.error((new StringBuffer()).append("FareSchdSvc.searchMode Exception :[")
					.append("dep_apo :").append(dep_apo)
					.append(",rtn_apo:").append(rtn_apo)
					.append(",dep_dt:").append(dep_dt)
					.append(",rtn_dt:").append(rtn_dt)
					.append(",search_num:").append(search_num)
					.append(",search_seq:").append(search_seq)
					.append("]"));
		}
		
		return insCnt;
	}
	
	
	/*	하나투어 운임 조회 & parse
	 * 
	 */
	public int searchHana(String key, String dep_apo, String rtn_apo, String dep_dt, String rtn_dt , String search_num , String search_seq) throws Exception{
		int insCnt = -1;
		
		try{
			
			String site_cd = "hanatour";
			String h_dep_dt = StringUtils.replaceAll(dep_dt, "-", "");
			String h_rtn_dt = StringUtils.replaceAll(rtn_dt, "-", "");
			
			//String requestUrl = ApiConstants.HNT_FARE_URL_PRODUCTION;
			String requestUrl = ApiConstants.HNT_FARE_URL_PRODUCTION + "/air/airWs.asmx/getFareSchdIdXml";
			
			StringBuffer reqXml = new StringBuffer();
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
			
			log(key, requestUrl, reqXml.toString(), true);
			
			MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
			params.add("requestXml", reqXml.toString());
			
			String responseBody = restTemplate.postForObject(requestUrl, params, String.class);
			
			log(key, requestUrl, responseBody, false);
			
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
				
				String fare = jPriceList.getJSONObject(i).get("Sf").toString();			//기본운임
				String tax = jPriceList.getJSONObject(i).get("AdtTaxBill").toString();	//tax
				String oil = jPriceList.getJSONObject(i).get("AdtFuel").toString();		//유류세
				
				String mkAirCd = jPriceList.getJSONObject(i).get("Cr").toString();		//판매항공사 코드
				String mkAirNm = jPriceList.getJSONObject(i).get("Crd").toString();		//판매항공사 명
				
				mkAirNm = StringUtils.replace(mkAirNm, "항공", "");
				
				String exp_Dt = jPriceList.getJSONObject(i).get("Vdt").toString();		//유효기간
				String dep_FltNo = "";
				String rtn_FltNo = "";
				String via_Cnt = jPriceList.getJSONObject(i).get("Cnxid").toString();	//경유 횟수
				
				String tmpSkdSeq =  jPriceList.getJSONObject(i).get("Seq").toString();	//스케줄 맵핑 키
				
				String tmpExp = exp_Dt;
				tmpExp = StringUtils.replace(tmpExp, "일", "D");
				tmpExp = StringUtils.replace(tmpExp, "개월", "M");
				tmpExp = StringUtils.replace(tmpExp, "년", "Y");
				tmpExp = StringUtils.replace(tmpExp, "365D", "1Y");
				exp_Dt = tmpExp;
				
				String tmpDep = ""; 
				String tmpRtn = ""; 
				// FareList 의 Seq 값과 Fsamap > fl 값의 첫번재 값이 같은 것을 찾는다, fl [11-1-3-0] 값의 첫번째는 운임시퀀스, 2번째 값은 1여정 시퀀스, 3번째 값은 2여정 시퀀스, 4번째 값은 3여정 시퀀스   
				for(int j=0; j < jSkdMapList.length(); j++ )
				{
					JSONObject fmap = jSkdMapList.getJSONObject(j);
					String[] planArr = fmap.getString("Fl").split("-");
					
					if(tmpSkdSeq.equals(planArr[0])){
						tmpDep = planArr[1];
						tmpRtn = planArr[2];
						break;
					}
				}
				
				//위 Fsmap 에서 찾은 1,2 번째 여정 시퀀스 값 을 기준으로 SkdList list 에서 동일한 Seq 값을 가진 Row를 찾는다.
				for(int j=0; j < jSkdList.length(); j++ )
				{
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
				}
				
				//프로모션 카드 운임 중 최저가를 구한다.
				JSONArray cardRecList = jPriceList.getJSONObject(i).optJSONArray("CardRecList");
				
				if( cardRecList == null ){
					JSONObject tmpCardInfo = new JSONObject();
					tmpCardInfo.put("CpcCode", jPriceList.getJSONObject(i).get("Idt").toString());
					tmpCardInfo.put("CpcIdtLclDesc", jPriceList.getJSONObject(i).get("Idtd").toString());
					tmpCardInfo.put("CardFareAdt", jPriceList.getJSONObject(i).get("Dsf").toString());
					tmpCardInfo.put("TFeeAdt", jPriceList.getJSONObject(i).get("Tfee").toString());
					
					cardRecList = new JSONArray();
					cardRecList.put(tmpCardInfo);
				}
				
				for(int x=0; x < cardRecList.length(); x++ ){
					JSONObject cardInfo = cardRecList.getJSONObject(x);
					CommonFareVo fareVo = new CommonFareVo();
					
					String disFare = cardInfo.get("CardFareAdt").toString(); //할인운임
					String tasf = cardInfo.get("TFeeAdt").toString(); // tasf 발권수수료
					
					int sumFare = (int) (Float.parseFloat(disFare) + Float.parseFloat(tax) + Float.parseFloat(oil));
					
					String price = Integer.toString(sumFare); 
					String promoNm = cardInfo.get("CpcIdtLclDesc").toString();
					
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
					fareVo.setSch_Num(search_num);
					fareVo.setSch_Seq(search_seq);
					
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
			
			System.out.println("fareList ========="+fareList.toString()); 

			insCnt = fareInsert(fareList);
			
		}catch(Exception e){
			insCnt = -1;
			commonLogger.error("FareSchdSvc.searchHana Exception :"+ ExceptionUtils.getMessage(e));
			commonLogger.error((new StringBuffer()).append("FareSchdSvc.searchHana Exception :[")
					.append("dep_apo :").append(dep_apo)
					.append(",rtn_apo:").append(rtn_apo)
					.append(",dep_dt:").append(dep_dt)
					.append(",rtn_dt:").append(rtn_dt)
					.append(",search_num:").append(search_num)
					.append(",search_seq:").append(search_seq)
					.append("]"));
		}
		
		return insCnt;
	}
	
	
	
	
	/*	하나투어 운임 조회 & parse
	 * 
	 */
	public int searchHanaRT(String key, String dep_apo, String rtn_apo, String dep_dt, String rtn_dt , String search_num , String search_seq) throws Exception{
		int insCnt = -1;
		
		try{
			
			String site_cd = "hanatour";
			String h_dep_dt = StringUtils.replaceAll(dep_dt, "-", "");
			String h_rtn_dt = StringUtils.replaceAll(rtn_dt, "-", "");
			
			//String requestUrl = ApiConstants.HNT_FARE_URL_PRODUCTION;
			String requestUrl = ApiConstants.HNT_FARE_URL_PRODUCTION + "/air/airWs.asmx/getFareSchdIdXml";
			
			StringBuffer reqXml = new StringBuffer();
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
			reqXml.append("<FareType>V</FareType>");
			reqXml.append("</FareSchdRQ>");
			
			log(key, requestUrl, reqXml.toString(), true);
			
			MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
			params.add("requestXml", reqXml.toString());
			
			String responseBody = restTemplate.postForObject(requestUrl, params, String.class);
			
			log(key, requestUrl, responseBody, false);
			
			//위에서 응답받은 XML 을 Json 으로 변환한다.
			JSONObject xmlJSONObj = null;
			try {
				xmlJSONObj = XML.toJSONObject(responseBody);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String jsonPrettyPrintString = xmlJSONObj.toString();
		    System.out.println(jsonPrettyPrintString);
			
		    JSONArray jPriceList = new JSONArray(); //하나투어 운임가격 리스트
			JSONArray jSkdList = new JSONArray(); 	// 스케줄 리스트
			JSONArray jSkdMapList = new JSONArray(); // 운임 스케줄 맵핑 리스트
			
			
			
			if( "0".equals(xmlJSONObj.getJSONObject("FareSchdRS").get("ErrorCode").toString()) ){
				if(  "org.json.JSONObject".equals(xmlJSONObj.getJSONObject("FareSchdRS").get("FareList").getClass().getCanonicalName())){
					JSONObject obj = new JSONObject(xmlJSONObj.getJSONObject("FareSchdRS").get("FareList").toString());
					jPriceList.put(obj); 
				}else{
					jPriceList = xmlJSONObj.getJSONObject("FareSchdRS").getJSONArray("FareList");
				}
				
				if(  "org.json.JSONObject".equals(xmlJSONObj.getJSONObject("FareSchdRS").get("SkdList").getClass().getCanonicalName())){
					JSONObject obj = new JSONObject(xmlJSONObj.getJSONObject("FareSchdRS").get("SkdList").toString());
					jSkdList.put(obj); 
				}else{
					jSkdList = xmlJSONObj.getJSONObject("FareSchdRS").getJSONArray("SkdList");
				}
				
				
				if(  "org.json.JSONObject".equals(xmlJSONObj.getJSONObject("FareSchdRS").getJSONObject("Fsamap").get("Fa").getClass().getCanonicalName())){
					JSONObject obj = new JSONObject(xmlJSONObj.getJSONObject("FareSchdRS").getJSONObject("Fsamap").get("Fa").toString());
					jSkdMapList.put(obj); 
				}else{
					jSkdMapList = xmlJSONObj.getJSONObject("FareSchdRS").getJSONObject("Fsamap").getJSONArray("Fa");
				}
				
				
				//jPriceList = xmlJSONObj.getJSONObject("FareSchdRS").getJSONArray("FareList");
				//jSkdList = xmlJSONObj.getJSONObject("FareSchdRS").getJSONArray("SkdList");
				//jSkdMapList = xmlJSONObj.getJSONObject("FareSchdRS").getJSONObject("Fsamap").getJSONArray("Fa");
				
				
				List<CommonFareVo> fareList = new ArrayList<CommonFareVo>();
				List<String> compFareList = new ArrayList<String>();
				List<String> compPriceList = new ArrayList<String>();
			    
				
				for(int i=0; i < jPriceList.length(); i++ ){
					
					String seatCheck = jPriceList.getJSONObject(i).get("St").toString();
					
					if( "OK".equals(seatCheck) ){
						String fare = jPriceList.getJSONObject(i).get("Sf").toString();			//기본운임
						String tax = jPriceList.getJSONObject(i).get("AdtTaxBill").toString();	//tax
						String oil = jPriceList.getJSONObject(i).get("AdtFuel").toString();		//유류세
						
						String mkAirCd = jPriceList.getJSONObject(i).get("Cr").toString();		//판매항공사 코드
						String mkAirNm = jPriceList.getJSONObject(i).get("Crd").toString();		//판매항공사 명
						
						mkAirNm = StringUtils.replace(mkAirNm, "항공", "");
						
						String exp_Dt = jPriceList.getJSONObject(i).get("Vdt").toString();		//유효기간
						String dep_FltNo = "";
						String rtn_FltNo = "";
						String via_Cnt = jPriceList.getJSONObject(i).get("Cnxid").toString();	//경유 횟수
						
						String tmpSkdSeq =  jPriceList.getJSONObject(i).get("Seq").toString();	//스케줄 맵핑 키
						
						String tmpExp = exp_Dt;
						tmpExp = StringUtils.replace(tmpExp, "일", "D");
						tmpExp = StringUtils.replace(tmpExp, "개월", "M");
						tmpExp = StringUtils.replace(tmpExp, "년", "Y");
						tmpExp = StringUtils.replace(tmpExp, "365D", "1Y");
						exp_Dt = tmpExp;
						
						String tmpDep = ""; 
						String tmpRtn = ""; 
						// FareList 의 Seq 값과 Fsamap > fl 값의 첫번재 값이 같은 것을 찾는다, fl [11-1-3-0] 값의 첫번째는 운임시퀀스, 2번째 값은 1여정 시퀀스, 3번째 값은 2여정 시퀀스, 4번째 값은 3여정 시퀀스   
						for(int j=0; j < jSkdMapList.length(); j++ )
						{
							JSONObject fmap = jSkdMapList.getJSONObject(j);
							String[] planArr = fmap.getString("Fl").split("-");
							
							if(tmpSkdSeq.equals(planArr[0])){
								tmpDep = planArr[1];
								tmpRtn = planArr[2];
								break;
							}
						}
						
						//위 Fsmap 에서 찾은 1,2 번째 여정 시퀀스 값 을 기준으로 SkdList list 에서 동일한 Seq 값을 가진 Row를 찾는다.
						for(int j=0; j < jSkdList.length(); j++ )
						{
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
						}
						
						//프로모션 카드 운임 중 최저가를 구한다.
						JSONArray cardRecList = jPriceList.getJSONObject(i).optJSONArray("CardRecList");
						
						if( cardRecList == null ){
							JSONObject tmpCardInfo = new JSONObject();
							tmpCardInfo.put("CpcCode", jPriceList.getJSONObject(i).get("Idt").toString());
							tmpCardInfo.put("CpcIdtLclDesc", jPriceList.getJSONObject(i).get("Idtd").toString());
							tmpCardInfo.put("CardFareAdt", jPriceList.getJSONObject(i).get("Dsf").toString());
							tmpCardInfo.put("TFeeAdt", jPriceList.getJSONObject(i).get("Tfee").toString());
							
							cardRecList = new JSONArray();
							cardRecList.put(tmpCardInfo);
						}
						
						for(int x=0; x < cardRecList.length(); x++ ){
							JSONObject cardInfo = cardRecList.getJSONObject(x);
							CommonFareVo fareVo = new CommonFareVo();
							
							String disFare = cardInfo.get("CardFareAdt").toString(); //할인운임
							String tasf = cardInfo.get("TFeeAdt").toString(); // tasf 발권수수료
							
							int sumFare = (int) (Float.parseFloat(disFare) + Float.parseFloat(tax) + Float.parseFloat(oil));
							
							String price = Integer.toString(sumFare); 
							String promoNm = "하나투어 RT/"+cardInfo.get("CpcIdtLclDesc").toString();
							
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
							fareVo.setSch_Num(search_num);
							fareVo.setSch_Seq(search_seq);
							
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

				insCnt = fareInsert(fareList);
				
			}
			
		}catch(Exception e){
			insCnt = -1;
			commonLogger.error("FareSchdSvc.searchHanaRT Exception :"+ ExceptionUtils.getMessage(e));
			commonLogger.error((new StringBuffer()).append("FareSchdSvc.searchHanaRT Exception :[")
					.append("dep_apo :").append(dep_apo)
					.append(",rtn_apo:").append(rtn_apo)
					.append(",dep_dt:").append(dep_dt)
					.append(",rtn_dt:").append(rtn_dt)
					.append(",search_num:").append(search_num)
					.append(",search_seq:").append(search_seq)
					.append("]"));
		}
		
		return insCnt;
	}
	
	
	/*	인터파크 운임조회 & parse
	 * 
	 */
	public int searchInPark(String key, String dep_apo, String rtn_apo, String dep_dt, String rtn_dt , String search_num , String search_seq) throws Exception{
		int insCnt = -1;
		
		try{	
			String h_dep_dt = StringUtils.replaceAll(dep_dt, "-", "");
			String h_rtn_dt = StringUtils.replaceAll(rtn_dt, "-", "");
			String requestUrl = "";
			String site_cd = "interpark";
			
			requestUrl = ApiConstants.INTERPARK_FARE_URL_PRODUCTION;
			//String requestBody = "Soto=N&ptype=I&SeatAvail=Y&dep0="+ dep_apo +"&comp=Y&arr0="+ rtn_apo +"&depdate0="+ h_dep_dt +"&dep1="+ rtn_apo +"&faretype&arr1="+ dep_apo +"&enc=u&SeatType=A&FLEX=Y&PageNo=1&Change=NEW&SplitNo=2000&retdate="+ h_rtn_dt +"&adt=1&chd=0&BEST=Y&MoreKey&inf=0&trip=RT&AirLine&StayLength&JSON=Y";
			String requestBody = "SeatType=A&faretype=&dep1="+ rtn_apo +"&ScheduleGroup=Y&arr1="+dep_apo+"&adt=1&retdate="+ h_rtn_dt +"&chd=0&inf=0&Change=NEW&SeatAvail=Y&comp=Y&dep0="+ dep_apo +"&FLEX=Y&ptype=I&arr0="+ rtn_apo +"&enc=u&MoreKey=&PageNo=1&depdate0="+ h_dep_dt +"&SplitNo=129573&CsplitNo=100&AirLine=&Soto=N&trip=RT&ClassJoin=Y&StayLength=&BEST=N&JSON=Y";
			log(key, requestUrl, requestBody, true);
			
			requestUrl = requestUrl+"?"+requestBody;
			/*
			URI uri = new URI(requestUrl);
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			//ResponseEntity<String> responseEntity = restTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity(requestBody, headers),String.class);
			
			ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
			String responseBody = responseEntity.getBody();
			*/
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			ResponseEntity<String> responseEntity = restTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity("", headers),String.class);
			String responseBody = responseEntity.getBody();
			
			
			System.out.println("responseBody ====" + responseBody);
			
			log(key, requestUrl, responseBody, false);
			
			responseBody = StringUtils.substring(responseBody, 1, responseBody.length() -1);
			
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
		    
			
			for(int i=0; i < jPriceList.length(); i++ ){
				
				String startCheck = jPriceList.getJSONObject(i).get("StartCheck").toString(); //출발 편 좌석 여부
				String returnCheck = jPriceList.getJSONObject(i).get("ReturnCheck").toString(); //귀국 편 좌석 여부
				String fareFix = jPriceList.getJSONObject(i).get("FareFix").toString();    //요금 확정 여부
				
				if( Integer.parseInt(startCheck) > 0 &&  Integer.parseInt(returnCheck) > 0){
					CommonFareVo fareVo = new CommonFareVo();
					
					String fare = jPriceList.getJSONObject(i).get("NormalFare").toString();			//기본운임
					String disFare = jPriceList.getJSONObject(i).get("SaleFare").toString();  	//할인운임
					String tax = jPriceList.getJSONObject(i).get("Tax").toString();				//tax
					String oil = jPriceList.getJSONObject(i).get("Qcharge").toString();			//유류세
					String price = jPriceList.getJSONObject(i).get("TotalSaleFare").toString();	//노출운임
					String tasf = jPriceList.getJSONObject(i).getJSONObject("SotoFare").getJSONObject("Tasf").get("Adt").toString(); //tasf
					String mkAirNm = jPriceList.getJSONObject(i).get("AirLineKor").toString();	//판매항공사 명
					String mkAirCd = jPriceList.getJSONObject(i).get("AirLine").toString();		//판매항공사 코드
					String exp_Dt = "";		//유효기간
					String dep_FltNo = "";	//출발항공편명					
					String rtn_FltNo = "";	//귀국항공편명
					String via_Cnt = "";	//경유 횟수
					String promoNm = jPriceList.getJSONObject(i).get("FareTypeDesc").toString();	//프로모션 이름 
					
					mkAirNm = StringUtils.replace(mkAirNm, "항공", "");
					
					
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
					tmpExp = StringUtils.replace(tmpExp, "365D", "1Y");
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
							
							int tmp_viaCnt = (Integer.parseInt(via_Cnt) -1);
							
							via_Cnt = String.valueOf(tmp_viaCnt);
							
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
					fareVo.setSch_Num(search_num);
					fareVo.setSch_Seq(search_seq);
					
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
			
			System.out.println("fareList ========="+fareList.toString()); 

			insCnt = fareInsert(fareList);
			
		}catch(Exception e){
			insCnt = -1;
			commonLogger.error("FareSchdSvc.searchInPark Exception :"+ ExceptionUtils.getMessage(e));
			commonLogger.error((new StringBuffer()).append("FareSchdSvc.searchInPark Exception :[")
					.append("dep_apo :").append(dep_apo)
					.append(",rtn_apo:").append(rtn_apo)
					.append(",dep_dt:").append(dep_dt)
					.append(",rtn_dt:").append(rtn_dt)
					.append(",search_num:").append(search_num)
					.append(",search_seq:").append(search_seq)
					.append("]"));
			//throw e;
		}
		
		return insCnt;
	}
	
	
	
	/*	인터파크 운임조회 & parse
	 * 
	 */
	public int searchInParkRT(String key, String dep_apo, String rtn_apo, String dep_dt, String rtn_dt , String search_num , String search_seq) throws Exception{
		int insCnt = -1;
		
		try{	
			String h_dep_dt = StringUtils.replaceAll(dep_dt, "-", "");
			String h_rtn_dt = StringUtils.replaceAll(rtn_dt, "-", "");
			String requestUrl = "";
			String site_cd = "interpark";
			
			requestUrl = ApiConstants.INTERPARK_FARERT_URL_PRODUCTION;
			String requestBody = "DepDate="+ h_dep_dt +"&RetDate="+ h_rtn_dt +"&Adt=1&Chd=0&Inf=0&Dep0="+dep_apo+"&Arr0="+rtn_apo+"&Dep1="+rtn_apo+"&Arr1="+dep_apo+"&Trip=RT&Comp=Y&PageNo=1&SplitNo=1000";
			log(key, requestUrl, requestBody, true);
			
			requestUrl = requestUrl + "?"+requestBody;
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_XML);
			headers.set("openapikey", "8C5273CC68C648EEB1CCA795C74A548B");
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			ResponseEntity<String> responseEntity = restTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity("", headers),String.class);
			String responseBody = responseEntity.getBody();
			
			log(key, requestUrl, responseBody, true);
			
			//위에서 응답받은 String Json 으로 변환한다.
			JSONObject jObj = new JSONObject(responseBody);
			JSONArray jPriceList = new JSONArray(); //인터파크운임가격 리스트
			JSONArray jDepSeg = new JSONArray(); 	// 출발 스케줄리스트
			JSONArray jRtnSeg = new JSONArray(); 	// 귀국 스케줄리스트
			
			
			// 운임 Json 데이터를 파싱하여  필요한 데이터 만을 추출 한다.
			jPriceList = jObj.getJSONArray("GoodsList");
			
			List<CommonFareVo> fareList = new ArrayList<CommonFareVo>();
			List<String> compFareList = new ArrayList<String>();
			List<String> compPriceList = new ArrayList<String>();
		    
			for(int i=0; i < jPriceList.length(); i++ ){
				CommonFareVo fareVo = new CommonFareVo();
				
				String fare = jPriceList.getJSONObject(i).get("NormalFare").toString();		//기본운임
				String disFare = jPriceList.getJSONObject(i).get("SaleFare").toString();  	//할인운임
				String tax = jPriceList.getJSONObject(i).get("AdtAirportTax").toString();	//tax
				String oil = jPriceList.getJSONObject(i).get("AdtTax").toString();			//유류세
				
				int sumPrice = (int) (Float.parseFloat(disFare) + Float.parseFloat(tax) + Float.parseFloat(oil));
				
				String price = String.valueOf(sumPrice);	//노출운임
				String tasf = "0"; //tasf
				String mkAirNm = jPriceList.getJSONObject(i).get("AirLineKor").toString();	//판매항공사 명
				String mkAirCd = jPriceList.getJSONObject(i).get("AirLine").toString();		//판매항공사 코드
				String exp_Dt = jPriceList.getJSONObject(i).get("Day").toString()+"D";			//유효기간
				
				String dep_FltNo = jPriceList.getJSONObject(i).getJSONObject("AirAvail").getJSONObject("StartAvail").getJSONArray("AirItn").getJSONObject(0).get("main_flt").toString();	//출발항공편명					
				String rtn_FltNo = jPriceList.getJSONObject(i).getJSONObject("AirAvail").getJSONObject("ReturnAvail").getJSONArray("AirItn").getJSONObject(0).get("main_flt").toString();	//귀국항공편명
				String via_Cnt = jPriceList.getJSONObject(i).getJSONObject("AirAvail").getJSONObject("StartAvail").get("Total_Seg").toString();	//경유 횟수
				int tmp_viaCnt = (Integer.parseInt(via_Cnt) -1);
				
				via_Cnt = String.valueOf(tmp_viaCnt);
				
				String promoNm = "인터파크 긴급땡처리";	//프로모션 이름 
				
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
				fareVo.setSch_Num(search_num);
				fareVo.setSch_Seq(search_seq);
				
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
			
			System.out.println("fareList ========="+fareList.toString()); 

			insCnt = fareInsert(fareList);
			
		}catch(Exception e){
			insCnt = -1;
			commonLogger.error("FareSchdSvc.searchInParkRT Exception :"+ ExceptionUtils.getMessage(e));
			commonLogger.error((new StringBuffer()).append("FareSchdSvc.searchInPark Exception :[")
					.append("dep_apo :").append(dep_apo)
					.append(",rtn_apo:").append(rtn_apo)
					.append(",dep_dt:").append(dep_dt)
					.append(",rtn_dt:").append(rtn_dt)
					.append(",search_num:").append(search_num)
					.append(",search_seq:").append(search_seq)
					.append("]"));
			//throw e;
		}
		
		return insCnt;
	}
	
	
	
	/*	온라인 투어 운임조회 & parse
	 * 
	 */
	public int searchOnLine(String key, String dep_apo, String rtn_apo, String dep_dt, String rtn_dt , String search_num , String search_seq) throws Exception{
		int insCnt = -1;
		
		try{
			String h_dep_dt = StringUtils.replaceAll(dep_dt, "-", "");
			String h_rtn_dt = StringUtils.replaceAll(rtn_dt, "-", "");
			String requestUrl = "";
			String site_cd = "onlinetour";
			
			requestUrl = ApiConstants.ONLINETOUR_FARE_URL_PRODUCTION;
			String requestBody = "soto=N&trip=RT&startDt="+h_dep_dt+"&endDt="+h_rtn_dt+"&sDate1=&sDate2=&sDate3=&sCity1="+dep_apo+"&eCity1="+rtn_apo+"&eCity1NtDesc=&sCity2="+ rtn_apo +"&eCity2="+ dep_apo +"&sCity3=&eCity3=&adt=1&chd=0&inf=0&filterAirLine=&stayLength=&stayChk=&seatType=A&best=Y&sgc=&rgc=&partnerCode=&eventNum=&classJoinNum=&partnerNum=&fareType=Y&eventInd=&schedule=Y&splitNo=1000&epricingYn=N&blockGoodsYn=Y&summary=N&SGMap=Y";
			log(key, requestUrl, requestBody, true);
			
			requestUrl = requestUrl+"?"+requestBody;
			
			URI uri = new URI(requestUrl);
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
			String responseBody = responseEntity.getBody();
			
			log(key, requestUrl, responseBody, false);
			
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
				//System.out.println("i==="+i);
				String startCheck = jPriceList.getJSONObject(i).get("StartCheck").toString(); //출발 편 좌석 여부
				String returnCheck = jPriceList.getJSONObject(i).get("ReturnCheck").toString(); //귀국 편 좌석 여부
				String fareFix = jPriceList.getJSONObject(i).get("FareFix").toString();    //요금 확정 여부
				
				//if( Integer.parseInt(startCheck) > 0 &&  Integer.parseInt(returnCheck) > 0 && "확정".equals(fareFix)){
				if( Integer.parseInt(startCheck) > 0 &&  Integer.parseInt(returnCheck) > 0 ){	
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
						//System.out.println("k==="+k);
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
									rtn_FltNo = availList2.getJSONArray("SegDetail").getJSONObject(0).get("FltNum").toString();
								}else{
									rtn_FltNo = availList2.getJSONObject("SegDetail").get("FltNum").toString();
								}								
								break;
							}

					}
					
					//프로모션 정보를 담는다. 
					JSONObject tmpPromo = jPriceList.getJSONObject(i).getJSONObject("EventFareList");
					JSONArray arrPromo = new JSONArray(); // 프로모션이 여러개일 경우 프로모션 list를 담는다.
					JSONObject objPromo = new JSONObject();
					
					if( "org.json.JSONArray".equals(tmpPromo.get("EventFare").getClass().getCanonicalName()) ){
						
						arrPromo = tmpPromo.getJSONArray("EventFare");
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
							if(mkAirCd.equals("NH")){
								mkAirNm = "전일본공수";
							}else if(mkAirCd.equals("SQ")){
								mkAirNm = "싱가포르";
							}else if(mkAirCd.equals("CX")){
								mkAirNm = "케세이퍼시픽";
							}else if(mkAirCd.equals("KL")){
								mkAirNm = "KLM네덜라드";
							}else if(mkAirCd.equals("LH")){
								mkAirNm = "루프트한자";
							}else if(mkAirCd.equals("LO")){
								mkAirNm = "LOT폴란드";
							}else if(mkAirCd.equals("AY")){
								mkAirNm = "핀에어";
							}
							
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
							fareVo.setSch_Num(search_num);
							fareVo.setSch_Seq(search_seq);
							
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
			insCnt = fareInsert(fareList);
			
		}catch(Exception e){
			insCnt = -1;
			commonLogger.error("FareSchdSvc.searchOnLine Exception :"+ ExceptionUtils.getMessage(e));
			commonLogger.error((new StringBuffer()).append("FareSchdSvc.searchOnLine Exception :[")
					.append("dep_apo :").append(dep_apo)
					.append(",rtn_apo:").append(rtn_apo)
					.append(",dep_dt:").append(dep_dt)
					.append(",rtn_dt:").append(rtn_dt)
					.append(",search_num:").append(search_num)
					.append(",search_seq:").append(search_seq)
					.append("]"));
			//throw e;
		}
		
		return insCnt;
	}
	
	
	
	/*	온라인 투어 운임조회 & parse
	 * 
	 */
	public int searchOnLineRT(String key, String dep_apo, String rtn_apo, String dep_dt, String rtn_dt , String search_num , String search_seq) throws Exception{
		int insCnt = -1;
		
		try{
			String h_dep_dt = StringUtils.replaceAll(dep_dt, "-", "");
			String h_rtn_dt = StringUtils.replaceAll(rtn_dt, "-", "");
			String requestUrl = "";
			String site_cd = "onlinetour";
			
			requestUrl = ApiConstants.ONLINETOUR_FARERT_URL_PRODUCTION;
			String requestBody = "soto=N&trip=RT&startDt="+h_dep_dt+"&endDt="+h_rtn_dt+"&sDate1=&sDate2=&sDate3=&sCity1="+dep_apo+"&eCity1="+rtn_apo+"&eCity1NtDesc=&sCity2="+ rtn_apo +"&eCity2="+ dep_apo +"&sCity3=&eCity3=&adt=1&chd=0&inf=0&filterAirLine=&stayLength=&stayChk=&seatType=A&best=Y&sgc=&rgc=&partnerCode=&eventNum=&classJoinNum=&partnerNum=&fareType=Y&eventInd=&schedule=N&splitNo=1000&epricingYn=N&blockGoodsYn=Y&summary=N&SGMap=Y";
			log(key, requestUrl, requestBody, true);
			
			requestUrl = requestUrl+"?"+requestBody;
			
			URI uri = new URI(requestUrl);
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
			String responseBody = responseEntity.getBody();
			
			log(key, requestUrl, responseBody, true);
			
			JSONObject jObj = new JSONObject(responseBody);
			JSONArray jPriceList = new JSONArray(); //운임가격 리스트
			
			// 운임 Json 데이터를 파싱하여  필요한 데이터 만을 추출 한다.
			jPriceList = jObj.getJSONArray("blockGoods");
			
			List<CommonFareVo> fareList = new ArrayList<CommonFareVo>();
			List<String> compFareList = new ArrayList<String>();
			List<String> compPriceList = new ArrayList<String>();
			String tasf = "0";
			if( jPriceList.length() > 0)
			{
				for(int i=0; i < jPriceList.length(); i++ ){
					CommonFareVo fareVo = new CommonFareVo();
					
					String fare = jPriceList.getJSONObject(i).get("NormalFare").toString();		//기본운임
					String disFare = jPriceList.getJSONObject(i).get("SaleFare").toString();  	//할인운임
					String tax = "0";	//tax
					String oil = jPriceList.getJSONObject(i).get("Qcharge").toString();			//유류세
					
					int sumPrice = (int) (Float.parseFloat(disFare) + Float.parseFloat(tax) + Float.parseFloat(oil));
					
					String price = String.valueOf(sumPrice);	//노출운임
					String mkAirNm = jPriceList.getJSONObject(i).get("List1AirVKR").toString();	//판매항공사 명
					String mkAirCd = jPriceList.getJSONObject(i).get("List1AirV").toString();		//판매항공사 코드
					String exp_Dt = jPriceList.getJSONObject(i).get("ValidateMax").toString()+"D";			//유효기간
					
					String dep_FltNo = jPriceList.getJSONObject(i).get("List1FltNum").toString();	//출발항공편명					
					String rtn_FltNo = jPriceList.getJSONObject(i).get("List2FltNum").toString();	//귀국항공편명
					String via_Cnt = jPriceList.getJSONObject(i).get("VIA").toString();	//경유 횟수
					
					String promoNm = "온라인투어 공동구매땡처리";	//프로모션 이름 
					
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
					fareVo.setSch_Num(search_num);
					fareVo.setSch_Seq(search_seq);
					
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
				
				insCnt = fareInsert(fareList);
			}
			
			System.out.println("fareList ========="+fareList.toString()); 
			
		}catch(Exception e){
			insCnt = -1;
			commonLogger.error("FareSchdSvc.searchOnLineRT Exception :"+ ExceptionUtils.getMessage(e));
			commonLogger.error((new StringBuffer()).append("FareSchdSvc.searchOnLine Exception :[")
					.append("dep_apo :").append(dep_apo)
					.append(",rtn_apo:").append(rtn_apo)
					.append(",dep_dt:").append(dep_dt)
					.append(",rtn_dt:").append(rtn_dt)
					.append(",search_num:").append(search_num)
					.append(",search_seq:").append(search_seq)
					.append("]"));
			//throw e;
		}
		
		return insCnt;
	}
	
	
	/*	운임 수집 데이터를 DB 에 저장
	 * 
	 */
	public int fareInsert(List<CommonFareVo> fareList) throws Exception{
		int resultCnt = -1;
		
		DefaultTransactionDefinition trDef = new DefaultTransactionDefinition();
		trDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus trSts = transactionManager.getTransaction(trDef);
		
		try{
			for(int i =0; i < fareList.size(); i++){
				CommonFareVo fv = fareList.get(i);
				resultCnt = fareDao.insertFare(fv);
			}
			transactionManager.commit(trSts);
			
		}catch(Exception e){
			commonLogger.error("FareSchdSvc.fareInsert DB Insert Error :"+ ExceptionUtils.getMessage(e));
			try{
				if(!trSts.isCompleted()) transactionManager.rollback(trSts);
			}catch(Exception ex){
				commonLogger.error("FareSchdSvc.fareInsert transaction Exception :"+ ExceptionUtils.getMessage(e));
			}
		}
		
		return resultCnt;
	}
	
	public String fareSearchRealTime(String seq){
		String result = "";
		int hnt_cnt = 0;
		int hntRT_cnt = 0;
		int mode_cnt = 0;
		int interpark_cnt = 0;
		int onlinetour_cnt = 0;
		
		List<FareInfoVo> fareSearchList = null;
		try {
			
			commonLogger.info("FareSchdSvc.fareSearchRealTime Start ["+ CommonUtil.getYYYYMMDDHHMMss()+"]");
			
			String currentDay = CommonUtil.getYYYYMMDD();
			Map dataInfo = new HashMap();
			dataInfo.put("seq", StringUtils.trim(seq));
			
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
						dataInfo.put("sch_seq",sch_seq);
						
						String search_num = fareDao.selectFare_nvl_sch_num(dataInfo);
						
						if( "Y".equals(depfixYn) ){ // 출발일 변동일 경우	
							dep_dt = CommonUtil.getDayOfWeek(chgMonth, chgWeek, chgDay);
						}
						
						String tmp_dt = StringUtils.replaceAll(dep_dt, "-", "");
						if( tmp_dt.compareTo(currentDay) <= 0  ){
							continue;
						}
						
						rtn_dt = CommonUtil.CALC_date( StringUtils.replaceAll(dep_dt, "-", ""),  Integer.parseInt(chgRtnDay)-1);
						
						System.out.println("dep_apo ===="+ dep_apo);
						System.out.println("rtn_apo ===="+ rtn_apo);
						System.out.println("dep_dt ===="+ dep_dt);
						System.out.println("rtn_dt ===="+ rtn_dt);
						System.out.println("search_num ===="+ search_num);
						System.out.println("sch_seq ===="+ sch_seq);
						
						String h_dep_dt = StringUtils.replaceAll(dep_dt, "-", "");
						String h_rtn_dt = StringUtils.replaceAll(rtn_dt, "-", "");
						
						
						mode_cnt = searchMode(null, dep_apo, rtn_apo, h_dep_dt, h_rtn_dt , search_num, sch_seq);
						hnt_cnt = searchHana(null, dep_apo, rtn_apo, dep_dt, rtn_dt, search_num, sch_seq );
						hntRT_cnt = searchHanaRT(null, dep_apo, rtn_apo, dep_dt, rtn_dt, search_num, sch_seq );
						interpark_cnt = searchInPark(null, dep_apo, rtn_apo, dep_dt, rtn_dt, search_num, sch_seq);
						onlinetour_cnt = searchOnLine(null, dep_apo, rtn_apo, dep_dt, rtn_dt , search_num, sch_seq);
					}
					
					HashMap resultMap = new HashMap();
					resultMap.put("mode_cnt", mode_cnt);
					resultMap.put("hnt_cnt", hnt_cnt);
					resultMap.put("hntRT_cnt", hntRT_cnt);
					resultMap.put("interpark_cnt", interpark_cnt);
					resultMap.put("onlinetour_cnt", onlinetour_cnt);
					
					
					if( mode_cnt > 0 && hnt_cnt > 0 && interpark_cnt > 0 && onlinetour_cnt > 0 && hntRT_cnt > 0 ){
						resultMap.put("ErrorCode", "0");
					}else{
						resultMap.put("ErrorCode", "-1");
					}
					
					ObjectMapper mapper = new ObjectMapper();	
					result = mapper.writeValueAsString(resultMap);
					
				}
				
				commonLogger.info("FareSchdSvc.fareSearchBatch Success ["+ CommonUtil.getYYYYMMDDHHMMss()+"]");
			}else{
				result = "{\"ErrorCode\":\"-99\"}";
			}
		} catch (Exception e) {
			result = "{\"ErrorCode\":\"-99\"}";
			commonLogger.error("FareSchdSvc.fareSearchBatch Exception :"+ ExceptionUtils.getMessage(e));
			
		}
		
		System.out.println("result====="+result);
		return result;
		
	}
	
	
	public String fareSearchBatch(){
		String result = "";

		List<FareInfoVo> fareSearchList = null;
		try {
			
			commonLogger.info("FareSchdSvc.fareSearchBatch Start ["+ CommonUtil.getYYYYMMDDHHMMss()+"]");
			
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
						dataInfo.put("sch_seq",sch_seq);
						
						String search_num = fareDao.selectFare_nvl_sch_num(dataInfo);
						
						if( "Y".equals(depfixYn) ){ // 출발일 변동일 경우	
							dep_dt = CommonUtil.getDayOfWeek(chgMonth, chgWeek, chgDay);
						}
						
						String tmp_dt = StringUtils.replaceAll(dep_dt, "-", "");
						if( tmp_dt.compareTo(currentDay) <= 0  ){
							continue;
						}
						
						rtn_dt = CommonUtil.CALC_date( StringUtils.replaceAll(dep_dt, "-", ""),  Integer.parseInt(chgRtnDay)-1);
						
						System.out.println("dep_apo ===="+ dep_apo);
						System.out.println("rtn_apo ===="+ rtn_apo);
						System.out.println("dep_dt ===="+ dep_dt);
						System.out.println("rtn_dt ===="+ rtn_dt);
						System.out.println("search_num ===="+ search_num);
						System.out.println("sch_seq ===="+ sch_seq);
						
						String h_dep_dt = StringUtils.replaceAll(dep_dt, "-", "");
						String h_rtn_dt = StringUtils.replaceAll(rtn_dt, "-", "");
						
						int mode_cnt = searchMode(null, dep_apo, rtn_apo, h_dep_dt, h_rtn_dt , search_num, sch_seq);
						int hnt_cnt = searchHana(null, dep_apo, rtn_apo, dep_dt, rtn_dt, search_num, sch_seq );
						int interpark_cnt = searchInPark(null, dep_apo, rtn_apo, dep_dt, rtn_dt, search_num, sch_seq);
						int onlinetour_cnt = searchOnLine(null, dep_apo, rtn_apo, dep_dt, rtn_dt , search_num, sch_seq);
						
						System.out.println("mode_cnt ===="+mode_cnt);
						System.out.println("hnt_cnt ===="+hnt_cnt);
						System.out.println("interpark_cnt ===="+interpark_cnt);
						System.out.println("onlinetour_cnt ===="+onlinetour_cnt);
						
					}
					
				}
				
				commonLogger.info("FareSchdSvc.fareSearchBatch Success ["+ CommonUtil.getYYYYMMDDHHMMss()+"]");
			}
		} catch (Exception e) {
			
			commonLogger.error("FareSchdSvc.fareSearchBatch Exception :"+ ExceptionUtils.getMessage(e));
			
		}
		
		
		return result;
		
	}
}
