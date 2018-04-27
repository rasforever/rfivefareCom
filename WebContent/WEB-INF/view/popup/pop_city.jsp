<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<%@ include file="/WEB-INF/view/include/import.jsp" %>
<%@ include file="/WEB-INF/view/include/head.jsp" %>
<%@ include file="/WEB-INF/view/include/commonScript.jsp" %>
<script type="text/javascript">
	/*도시검색 팝업 script*/
	var citySearch = {
		tag_id : "",
		init : function(obj){
			citySearch.tag_id = obj;
			$(document).on('click', '#btnCitySearch', function(){
				var citnm = $("#airCtname");
				if($.trim(citnm.val()) == ""){
					alert("도시명을 입력해주세요.");
					citnm.focus();
					return;
				}
				
				citySearch.searchAjaxCall(citnm.val());
			});
			
			citySearch.initMajorCityMake();
		},
		initMajorCityMake : function(){
			var htmlString = "";
			for(var i = 0; i < airMajorCities.length; i++ ){
				htmlString += "<tr>";
				htmlString += "<th>"+ airMajorCities[i].country_area +"</th>";
				htmlString += "<td class='tdl'>"; 
				for(var j = 0; j < airMajorCities[i].area_info.length; j++){
					
					var apcd = airMajorCities[i].area_info[j].lcCode;
					var apnm = airMajorCities[i].area_info[j].lcName;
					htmlString +="<a href=\"javascript:citySearch.selectCity('"+ apcd +"');\" class=\"a_city\">"+ apnm +"</a>"; 
					
					if(j+1 ==  airMajorCities[i].area_info.length){
						htmlString += "</td></tr>";	
					}
				}
			}
			$("#cityList").html(htmlString);	
		},
		selectCity : function(city_code){
			$(opener.document).find('#'+citySearch.tag_id).val(city_code);
			self.close();
		},
		searchAjaxCall : function(citynm){
			var ApCtSearchRQ = {};
			ApCtSearchRQ.ApCtName = citynm;
			jsonString = "{\"ApCtSearchRQ\" : "+ JSON.stringify(ApCtSearchRQ) + "}";
			
			$.ajax({
				url : "/farecompare/getAirCityListJson/ApCtSearchRQ/ws.hnt",
				data : jsonString,
				dataType : "json",
				type : "POST",
				contentType : "application/json; charset=utf-8",
				success : function(data) {
					citySearch.cityListMake(data.d);
				},
				error : function(jqXHR, textStatus, errorThrown) {
					// 실패시
					//alert("Error :" + jqXHR.resonseText);
					showAlert("alert", "시스템이 불안정 합니다. 다시 시도 해 주세요.", function(){
						hideAlert();
					});
				}
			});
		},
		cityListMake : function(data){
			console.log(data);
			var d = JSON.parse(data);
			if(parseInt(d.ApCtSearchRS.ErrorCode) < 0){
				alert(d.ApCtSearchRS.ErrorDesc);
				return;
			}
			var cityList = d.ApCtSearchRS.ApCtList;
			
			if( cityList == null || cityList.length < 1 ){
				$("#cityListNodata").show();
				$("#cityListTb").hide();
			}else{
				$("#cityListNodata").hide();
			}
			var rowHtml = "";
			if(cityList instanceof Array){
				for( var i = 0; i < cityList.length; i++ ){
					var apnm = cityList[i].ApCtLclName;
					var apcd = cityList[i].ApCtLclCode;
					var ap_citynm = cityList[i].CountryLclName;
					var ap_stCode = cityList[i].ApStateCode;
					var ap_stNm = cityList[i].ApStateName;
					
					rowHtml += "<tr onclick=\"javascript:citySearch.selectCity('"+ apcd +"');\">";
					rowHtml += "<td>"+ apcd +"</td>";
					rowHtml += "<td>"+apnm+"</td>";
					rowHtml += "<td><span>"+ap_citynm+"</td>";
					if(ap_stCode == "" || ap_stCode == undefined){
						rowHtml += "<td>&nbsp;</td>";		
					}else{
						rowHtml += "<td>"+ap_stNm+"</td>";	
					}
					rowHtml += "</tr>";
				}		
			}else{
				var apnm = cityList.ApCtLclName;
				var apcd = cityList.ApCtLclCode;
				var ap_citynm = cityList.CountryLclName;
				var ap_stCode = cityList.ApStateCode;
				var ap_stNm = cityList.ApStateName;
				
				rowHtml += "<tr onclick=\"javascript:citySearch.selectCity('"+ apcd +"');\">";
				rowHtml += "<td>"+ apcd +"</td>";
				rowHtml += "<td>"+apnm+"</td>";
				rowHtml += "<td><span>"+ap_citynm+"</td>";
				if(ap_stCode == "" || ap_stCode == undefined){
					rowHtml += "<td>&nbsp;</td>";		
				}else{
					rowHtml += "<td>"+ap_stNm+"</td>";	
				}
				rowHtml += "</tr>";
			}	
			$("#cityListTr").html(rowHtml);
			$("#cityListTb").show();
		}
		
	}
	
	$(document).ready(function(){
		var tag_id = '${param.tag_id}';
		citySearch.init(tag_id);
	});
	
	
</script>
</head>

<body class="pa10">

<!-- 검색 -->
<div class="bx">
	<form class="row form-inline">
		<div class="col-sm-10">
			<div class="form-group mr20 mr0-xs">
				<label for="">도시명</label>
				<input type="text" class="form-control w50 text-center" id="airCtname">
			</div>
		</div>
		<div class="col-sm-2">
			<button type="button" class="btn btn-block bg-purple" id="btnCitySearch">SEARCH</button>
		</div>
	</form>
</div>

<table class="table" id="cityListTb" style="display:none;">
	<caption class="pa0"><span class="sr-only">도시검색</span></caption>
		<thead>
		<tr>
			<th>도시코드</th>
			<th>도시</th>
			<th>국가</th>
			<th>주</th>
		</tr>
	</thead>
	<tbody id="cityListTr">
		<!-- 
		<tr>
			<td>BKK</td>
			<td>방콕</td>
			<td>태국</td>
			<td>애틀란타 주</td>
		</tr>
		 -->
	</tbody>
</table>

<table class="table" id="cityListNodata" style="display:none;">
	<caption class="pa0"><span class="sr-only">도시검색</span></caption>
		<thead>
		<tr>
			<th>도시코드</th>
			<th>도시</th>
			<th>국가</th>
			<th>주</th>
		</tr>
	</thead>
	<tbody id="cityListTr">
		<tr>
			<td>BKK</td>
			<td>방콕</td>
			<td>태국</td>
			<td>애틀란타 주</td>
		</tr>
	</tbody>
</table>
<div class="overflow_divH">
	<ul class="breadcrumb">
		<li><strong>주요도시 바로선택</strong></li>
	</ul>
	<table cellpadding="0" cellspacing="0" class="table" width="100%" summary="주요도시">
		<caption class="pa0"><span class="sr-only">주요도시 국가선택</span></caption>
		<colgroup>
		<col width="17%">
		<col width="83%">
		</colgroup>
		<tbody id="cityList">
	        <!-- 	
			<tr>
				<th>동남아</th>
				<td class="tdl"><a href="#." onclick="findCity('BKK','방콕','C');" class="a_city">방콕</a><a href="#." onclick="findCity('DMK','방콕(돈무앙)','A');" class="a_city">방콕(돈무앙)</a><a href="#." onclick="findCity('TPE','대만(타오위안)','C');" class="a_city">대만(타오위안)</a><a href="#." onclick="findCity('MNL','마닐라','C');" class="a_city">마닐라</a><br><a href="#." onclick="findCity('SIN','싱가포르','C');" class="a_city">싱가포르</a><a href="#." onclick="findCity('CEB','세부','C');" class="a_city">세부</a><a href="#." onclick="findCity('SGN','호치민','C');" class="a_city">호치민</a><a href="#." onclick="findCity('DAD','다낭','C');" class="a_city">다낭</a><a href="#." onclick="findCity('HKT','푸껫','C');" class="a_city">푸껫</a><a href="#." onclick="findCity('HAN','하노이','C');" class="a_city">하노이</a><br><a href="#." onclick="findCity('KLO','보라카이(칼리보)','C');" class="a_city">보라카이(칼리보)</a><a href="#." onclick="findCity('DPS','발리(덴파사르)','C');" class="a_city">발리(덴파사르)</a><a href="#." onclick="findCity('TSA','대만(송산)','A');" class="a_city">대만(송산)</a><a href="#." onclick="findCity('BKI','코타키나발루','C');" class="a_city">코타키나발루</a><a href="#." onclick="findCity('CRK','클락(CRK)','A');" class="a_city">클락(CRK)</a><br><a href="#." onclick="findCity('PNH','프놈펜','C');" class="a_city">프놈펜</a><a href="#." onclick="findCity('JKT','자카르타(JKT)','C');" class="a_city">자카르타(JKT)</a><a href="#." onclick="findCity('CGK','자카르타(CGK)','A');" class="a_city">자카르타(CGK)</a><a href="#." onclick="findCity('REP','시엠립(앙코르와트)','C');" class="a_city">시엠립(앙코르와트)</a><br><a href="#." onclick="findCity('VTE','비엔티안','C');" class="a_city">비엔티안</a><a href="#." onclick="findCity('KUL','쿠알라룸푸르','C');" class="a_city">쿠알라룸푸르</a><a href="#." onclick="findCity('CXR','나트랑(깜랑)','A');" class="a_city">나트랑(깜랑)</a></td>
			</tr>
			<tr>
				<th>일본</th>
				<td class="tdl"><a href="#." onclick="findCity('KIX','오사카','A');" class="a_city">오사카</a><a href="#." onclick="findCity('NRT','도쿄(나리타)','A');" class="a_city">도쿄(나리타)</a><a href="#." onclick="findCity('FUK','후쿠오카','C');" class="a_city">후쿠오카</a><a href="#." onclick="findCity('HND','도쿄(하네다)','A');" class="a_city">도쿄(하네다)</a><a href="#." onclick="findCity('OKA','오키나와','C');" class="a_city">오키나와</a><br><a href="#." onclick="findCity('SPK','삿포로(치토세)','C');" class="a_city">삿포로(치토세)</a><a href="#." onclick="findCity('NGO','나고야','C');" class="a_city">나고야</a><a href="#." onclick="findCity('NGS','나가사키','C');" class="a_city">나가사키</a><a href="#." onclick="findCity('HIJ','히로시마','C');" class="a_city">히로시마</a><a href="#." onclick="findCity('KOJ','가고시마','C');" class="a_city">가고시마</a><br><a href="#." onclick="findCity('YGJ','요나고','C');" class="a_city">요나고</a><a href="#." onclick="findCity('KMI','미야자키','C');" class="a_city">미야자키</a><a href="#." onclick="findCity('TOY','도야마','C');" class="a_city">도야마</a><a href="#." onclick="findCity('FSZ','시즈오카','C');" class="a_city">시즈오카</a><a href="#." onclick="findCity('SDJ','센다이','C');" class="a_city">센다이</a></td>
			</tr>
			<tr>
				<th>중국</th>
				<td class="tdl"><a href="#." onclick="findCity('HKG','홍콩','C');" class="a_city">홍콩</a><a href="#." onclick="findCity('PVG','상해(푸동)','A');" class="a_city">상해(푸동)</a><a href="#." onclick="findCity('TAO','청도(칭다오)','C');" class="a_city">청도(칭다오)</a><a href="#." onclick="findCity('PEK','북경(베이징)','A');" class="a_city">북경(베이징)</a><a href="#." onclick="findCity('MFM','마카오','C');" class="a_city">마카오</a><br><a href="#." onclick="findCity('SHA','상해(홍교)','C');" class="a_city">상해(홍교)</a><a href="#." onclick="findCity('YNJ','연길(옌지)','C');" class="a_city">연길(옌지)</a><a href="#." onclick="findCity('CAN','광주(광저우)','C');" class="a_city">광주(광저우)</a><a href="#." onclick="findCity('SHE','심양(선양)','C');" class="a_city">심양(선양)</a><br><a href="#." onclick="findCity('DLC','대련(다롄)','C');" class="a_city">대련(다롄)</a><a href="#." onclick="findCity('TSN','천진(톈진)','C');" class="a_city">천진(톈진)</a><a href="#." onclick="findCity('WEH','위해(웨이하이)','C');" class="a_city">위해(웨이하이)</a><a href="#." onclick="findCity('HGH','항주(항저우)','C');" class="a_city">항주(항저우)</a><br><a href="#." onclick="findCity('HRB','하얼빈','C');" class="a_city">하얼빈</a><a href="#." onclick="findCity('CGQ','장춘(창춘)','C');" class="a_city">장춘(창춘)</a></td>
			</tr>
			<tr>
				<th>유럽</th>
				<td class="tdl"><a href="#." onclick="findCity('ROM','로마','C');" class="a_city">로마</a><a href="#." onclick="findCity('PAR','파리','C');" class="a_city">파리</a><a href="#." onclick="findCity('LON','런던','C');" class="a_city">런던</a><a href="#." onclick="findCity('BCN','바르셀로나','C');" class="a_city">바르셀로나</a><a href="#." onclick="findCity('FRA','프랑크푸르트','C');" class="a_city">프랑크푸르트</a><a href="#." onclick="findCity('PRG','프라하','C');" class="a_city">프라하</a><br><a href="#." onclick="findCity('IST','이스탄불','C');" class="a_city">이스탄불</a><a href="#." onclick="findCity('MAD','마드리드','C');" class="a_city">마드리드</a><a href="#." onclick="findCity('ZRH','취리히','C');" class="a_city">취리히</a><a href="#." onclick="findCity('ZAG','자그레브','C');" class="a_city">자그레브</a><a href="#." onclick="findCity('MIL','밀라노','C');" class="a_city">밀라노</a><a href="#." onclick="findCity('ATH','아테네','C');" class="a_city">아테네</a><br><a href="#." onclick="findCity('BUD','부다페스트','C');" class="a_city">부다페스트</a><a href="#." onclick="findCity('AMS','암스테르담','C');" class="a_city">암스테르담</a><a href="#." onclick="findCity('BER','베를린','C');" class="a_city">베를린</a></td>
			</tr>
			<tr>
				<th>미주</th>
				<td class="tdl"><a href="#." onclick="findCity('HNL','호놀룰루(하와이)','C');" class="a_city">호놀룰루(하와이)</a><a href="#." onclick="findCity('LAX','로스앤젤레스','C');" class="a_city">로스앤젤레스</a><a href="#." onclick="findCity('JFK','뉴욕(JFK)','A');" class="a_city">뉴욕(JFK)</a><a href="#." onclick="findCity('SFO','샌프란시스코','C');" class="a_city">샌프란시스코</a><br><a href="#." onclick="findCity('SEA','시애틀','C');" class="a_city">시애틀</a><a href="#." onclick="findCity('LAS','라스베이거스','C');" class="a_city">라스베이거스</a><a href="#." onclick="findCity('EWR','뉴욕(뉴왁)','A');" class="a_city">뉴욕(뉴왁)</a><a href="#." onclick="findCity('LGA','뉴욕(라과디아)','A');" class="a_city">뉴욕(라과디아)</a><br><a href="#." onclick="findCity('ORD','시카고(오헤어)','A');" class="a_city">시카고(오헤어)</a><a href="#." onclick="findCity('IAD','워싱톤(IAD)','A');" class="a_city">워싱톤(IAD)</a><a href="#." onclick="findCity('ATL','애틀란타','C');" class="a_city">애틀란타</a><a href="#." onclick="findCity('BOS','보스톤','C');" class="a_city">보스톤</a><br><a href="#." onclick="findCity('DFW','댈러스(DFW)','C');" class="a_city">댈러스(DFW)</a><a href="#." onclick="findCity('DCA','워싱톤(DCA)','A');" class="a_city">워싱톤(DCA)</a><a href="#." onclick="findCity('MCO','올랜도','A');" class="a_city">올랜도</a></td>
			</tr>
			<tr>
				<th>대양주</th>
				<td class="tdl"><a href="#." onclick="findCity('GUM','괌','C');" class="a_city">괌</a><a href="#." onclick="findCity('SYD','시드니','C');" class="a_city">시드니</a><a href="#." onclick="findCity('SPN','사이판','C');" class="a_city">사이판</a><a href="#." onclick="findCity('AKL','오클랜드','C');" class="a_city">오클랜드</a><a href="#." onclick="findCity('MEL','멜버른','C');" class="a_city">멜버른</a><a href="#." onclick="findCity('ROR','팔라우','C');" class="a_city">팔라우</a><a href="#." onclick="findCity('BNE','브리즈번','C');" class="a_city">브리즈번</a><br><a href="#." onclick="findCity('PER','퍼스','C');" class="a_city">퍼스</a><a href="#." onclick="findCity('ADL','아들레이드','C');" class="a_city">아들레이드</a><a href="#." onclick="findCity('CHC','크라이스트처치','C');" class="a_city">크라이스트처치</a><a href="#." onclick="findCity('CNS','케언즈','C');" class="a_city">케언즈</a></td>
			</tr>
			<tr>
				<th>캐나다</th>
				<td class="tdl"><a href="#." onclick="findCity('YVR','밴쿠버','C');" class="a_city">밴쿠버</a><a href="#." onclick="findCity('YTO','토론토','C');" class="a_city">토론토</a><a href="#." onclick="findCity('YYC','캘거리','C');" class="a_city">캘거리</a><a href="#." onclick="findCity('YEG','에드먼튼','A');" class="a_city">에드먼튼</a><a href="#." onclick="findCity('YUL','몬트리올','A');" class="a_city">몬트리올</a></td>
			</tr>
			<tr>
				<th>서남아</th>
				<td class="tdl"><a href="#." onclick="findCity('MLE','몰디브(말레)','C');" class="a_city">몰디브(말레)</a><a href="#." onclick="findCity('KTM','카트만두','C');" class="a_city">카트만두</a><a href="#." onclick="findCity('DEL','델리','C');" class="a_city">델리</a><a href="#." onclick="findCity('CMB','콜롬보','C');" class="a_city">콜롬보</a><a href="#." onclick="findCity('DAC','다카','C');" class="a_city">다카</a></td>
			</tr>
			<tr>
				<th>중남미</th>
				<td class="tdl"><a href="#." onclick="findCity('GRU','상파울루','A');" class="a_city">상파울루</a><a href="#." onclick="findCity('CUN','칸쿤','C');" class="a_city">칸쿤</a><a href="#." onclick="findCity('LIM','리마','C');" class="a_city">리마</a><a href="#." onclick="findCity('EZE','부에노스아이레스','A');" class="a_city">부에노스아이레스</a><a href="#." onclick="findCity('MEX','멕시코시티','C');" class="a_city">멕시코시티</a><br><a href="#." onclick="findCity('SCL','산티아고','C');" class="a_city">산티아고</a><a href="#." onclick="findCity('BOG','보고타','C');" class="a_city">보고타</a><a href="#." onclick="findCity('GUA','과테말라시티','C');" class="a_city">과테말라시티</a><a href="#." onclick="findCity('GIG','리우데자네이루(GIG)','A');" class="a_city">리우데자네이루(GIG)</a><br><a href="#." onclick="findCity('SDU','리우데자네이루(SDU)','A');" class="a_city">리우데자네이루(SDU)</a></td>
			</tr>
			<tr>
				<th>중동</th>
				<td class="tdl"><a href="#." onclick="findCity('DXB','두바이','C');" class="a_city">두바이</a><a href="#." onclick="findCity('TLV','텔아비브','C');" class="a_city">텔아비브</a><a href="#." onclick="findCity('AUH','아부다비','C');" class="a_city">아부다비</a><a href="#." onclick="findCity('THR','테헤란','C');" class="a_city">테헤란</a><a href="#." onclick="findCity('DOH','도하','C');" class="a_city">도하</a></td>
			</tr>
			<tr>
				<th>아프리카</th>
				<td class="tdl"><a href="#." onclick="findCity('CPT','케이프타운','C');" class="a_city">케이프타운</a><a href="#." onclick="findCity('JNB','요하네스버그','C');" class="a_city">요하네스버그</a><a href="#." onclick="findCity('NBO','나이로비','C');" class="a_city">나이로비</a><a href="#." onclick="findCity('MLA','몰타','C');" class="a_city">몰타</a><a href="#." onclick="findCity('ACC','아크라','C');" class="a_city">아크라</a></td>
			</tr>
			 -->
		</tbody>
	</table>
</div>
</body>
</html>