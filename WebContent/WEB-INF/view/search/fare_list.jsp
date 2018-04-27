<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<%@ include file="/WEB-INF/view/include/import.jsp" %>
<%@ include file="/WEB-INF/view/include/head.jsp" %>
<script type="text/javascript">
	$(document).ready(function(){
		FareSearch.init();
	});
	
	var FareSearch = {
		init : function(){
			$('#btnFareSearch').click(function(){FareSearch.btnFareSearch()});
		},
		btnFareSearch : function(){
			
			if(FareSearch.searchValid()){
				
				FLoading.show();
				var dep_dt = $('#startDt').val();
				var rtn_dt = $('#endDt').val();
				var dep_apo = $('#depApo').val();
				var rtn_apo = $('#rtnApo').val();
				var schData = { dep_apo : dep_apo , rtn_apo : rtn_apo , dep_dt : dep_dt , rtn_dt : rtn_dt};

				$.ajax({
		            url : '/farecompare/searchFare.hnt',
		            //url : '/farecompare/searchMode/ws.hnt',
		            //url : '/farecompare/searchInterPark/ws.hnt',
		            //url : '/farecompare/searchHnt/ws.hnt',
		            //url : '/farecompare/searchOnlineTour/ws.hnt',
		            data : schData,
		            dataType : 'json',
		            type : 'POST',
		            //contentType : "application/json; charset=utf-8",
		            success : function(data) {
		                console.log(data);
		                var result = data;
		                
		                if(result){
		                	if(typeof data != 'object'){
			                	resutl = JSON.parse(data);		
			                }
		                	
		                	FareSearch.makeHtml(result);
		                }else{
		                	alert("조회 결과가 없습니다.");
		                	FLoading.hide();
		                	console.log("searchFare Error");
		                } 
		            },
		            error : function(jqXHR, textStatus, errorThrown) {
		                console.log("***** modetour search log start");
		                console.log(jqXHR.responseText);
		                console.log(textStatus);
		                console.log(errorThrown);
		                console.log("*****modetour log end");
		            }
		        });		
			}
		},
		makeHtml : function(data){
			var sHtml = "";
			
			for(var i=0; i <data.length; i++ ){
				var obj = data[i];
				var b_fare = parseInt(obj.H_FARE_NOMAL);
				var h_dis_rate = 0;
				var m_dis_rate = 0;
				var i_dis_rate = 0;
				var o_dis_rate = 0;
				var h_tip = "";
				var m_tip = "";
				var i_tip = "";
				var o_tip = "";
				
				if( b_fare > 0){
					if( parseInt(obj.H_FARE_DIS) > 0 ){
						h_dis_rate = ( ( (b_fare - parseInt(obj.H_FARE_DIS)) / b_fare * 100 ) * -1  ).toFixed(1);
					}
					if( parseInt(obj.M_FARE_DIS) > 0 ){
						m_dis_rate = ( ( (b_fare - parseInt(obj.M_FARE_DIS)) / b_fare * 100 ) * -1 ).toFixed(1);	 
					}
					if( parseInt(obj.I_FARE_DIS) > 0 ){
						i_dis_rate = ( ( (b_fare - parseInt(obj.I_FARE_DIS)) / b_fare * 100 ) * -1 ).toFixed(1);	 
					}
					if( parseInt(obj.O_FARE_DIS) > 0 ){
						o_dis_rate = ( ( (b_fare - parseInt(obj.O_FARE_DIS)) / b_fare * 100 ) * -1 ).toFixed(1);
					} 		
				}
				
				if( parseInt(obj.H_FARE_DIS) > 0 ){
					h_tip = obj.H_EXP_DT + "/" + obj.H_VIA_CNT +"\n" + obj.H_DEP_FLT_NO + "/" + obj.H_RTN_FLT_NO; 	
				}
				if( parseInt(obj.M_FARE_DIS) > 0 ){
					m_tip = obj.M_EXP_DT + "/" + obj.M_VIA_CNT +"\n" + obj.M_DEP_FLT_NO + "/" + obj.M_RTN_FLT_NO; 
				}
				if( parseInt(obj.I_FARE_DIS) > 0 ){	
					i_tip = obj.I_EXP_DT + "/" + obj.I_VIA_CNT +"\n" + obj.I_DEP_FLT_NO + "/" + obj.I_RTN_FLT_NO; 
				}
				if( parseInt(obj.O_FARE_DIS) > 0 ){
					o_tip = obj.O_EXP_DT + "/" + obj.O_VIA_CNT +"\n" + obj.O_DEP_FLT_NO + "/" + obj.O_RTN_FLT_NO;
				}
				
				var regExp = /[\s\{\}\[\]\/?.,;:|\)*~`!^\-_+<>@\#$%&\\\=\(\'\"]/gi
				var promo_nm = obj.PROMOTION_NM.replace(regExp, '');
				
				sHtml += '<tr>';
				sHtml += '	<td class="gubun" data-air="'+obj.AIR_CD+'"><img src="http://image1.hanatour.com/2010/images/airline/'+ obj.AIR_CD +'.png" class="w30">'+obj.AIR_CD+'</td>';
				sHtml += '	<td>'+promo_nm+'</td>';
				sHtml += '	<td>'+numberWithCommas(obj.OIL_FEE)+'</td>';
				sHtml += '	<td>'+numberWithCommas(obj.TAX_FEE)+'</td>';
				sHtml += '	<td>'+numberWithCommas(obj.FARE_NETT)+'</td>';
				sHtml += '	<td><span data-tooltip-text="'+h_tip+'"> '+numberWithCommas(obj.H_FARE_DIS)+'</span></td>';
				sHtml += '	<td><span data-tooltip-text="'+m_tip+'">'+numberWithCommas(obj.M_FARE_DIS)+'</span></td>';
				sHtml += '	<td><span data-tooltip-text="'+i_tip+'">'+numberWithCommas(obj.I_FARE_DIS)+'</span></td>';
				sHtml += '	<td><span data-tooltip-text="'+o_tip+'">'+numberWithCommas(obj.O_FARE_DIS)+'</span></td>';
				sHtml += '	<td>'+numberWithCommas(h_dis_rate)+'</td>';
				sHtml += '	<td>'+numberWithCommas(m_dis_rate)+'</td>';
				sHtml += '	<td>'+numberWithCommas(i_dis_rate)+'</td>';
				sHtml += '	<td>'+numberWithCommas(o_dis_rate)+'</td>';
				sHtml += '</tr>';			
			}
			
			var obj2 = data[0];
			var dap_dt = dateStrFormat("yyyy/mm/dd", obj2.DEP_DT);
			var rtn_dt = dateStrFormat("yyyy/mm/dd", obj2.RTN_DT);
			var reg_dt = obj2.REG_DATE;
			var capHtml = obj2.DEP_APO+"/"+ obj2.RTN_APO + "왕복" +  dap_dt +"~"+ rtn_dt + ", 최종 없데이트 "+ reg_dt +" , 성인1인 기준 / 대기 제외";
			
			$("#sch_update_desc").html(capHtml);
			$("#fareList").html(sHtml);
			
			FareSearch.makeRowspan();
		},
		makeRowspan : function(){
			$(".gubun").each(function () {
	              var rows = $(".gubun:contains('" + $(this).text() + "')");
	              if (rows.length > 1) {
	                  rows.eq(0).attr("rowspan", rows.length);
	                  rows.not(":eq(0)").remove(); 
	              } 
	          });
			
			FLoading.hide();
		},
		searchValid : function(){
			var dep_dt = $('#startDt').val();
			var rtn_dt = $('#endDt').val();
			var dep_apo = $('#depApo option:selected').val();
			var rtn_apo = $('#rtnApo').val();
			
			
			dep_dt = dep_dt.replace(/\-/g, '');
			rtn_dt = rtn_dt.replace(/\-/g, '');
			
			if( dep_apo == "" ){
				alert("출발 공항을 선택하세요.");
				$('#depApo').focus();
				return false;
			}
			
			if( rtn_apo == "" ){
				alert("도착공항을 선택하세요.");
				$('#rtnApo').focus();
				return false;
			}
			
			if( dep_dt == "" ){
				alert("출발일자를 입력하세요.");
				$('#startDt').focus();
				return false;
			}
			
			if( rtn_dt == "" ){
				alert("귀국일자를 입력하세요.");
				$('#endDt').focus();
				return false;
			}
			
			if(parseInt(dep_dt) > parseInt(rtn_dt) ){
				alert("귀국일자는 출국일자 이후로 날짜를 선택해 주세요.");
				$('#endDt').focus();
				return false;
			}
			return true;
		}
	}

</script>
</head>

<body><a href="#content" class="skip sr-only sr-only-focusable" onclick="jQuery('#content a:first').focus();return false;">Skip to Content</a>

<!-- wrap -->
<div id="wrap">
	<%@ include file="/WEB-INF/view/include/left_menu.jsp" %>

	<!-- container -->
	<div id="container">
		<%@ include file="/WEB-INF/view/include/sign_out.jsp" %>
		<!-- breadcrumb -->
		<ul class="breadcrumb">
			<li><strong>운임조회</strong></li>
			<li class="active">운임비교</li>
		</ul>
		<!-- //breadcrumb -->
		<!-- content -->
		<div id="content">
			<!-- 검색 -->
			<div class="bx">
				<form class="row form-inline" id="search_frm">
					<div class="col-sm-10">
						<div class="form-group mr20 mr0-xs">
							<label for="">Depart</label>
							<select class="form-control input-sm" id="depApo">
								<option value="SEL">서울(인천/김포)</option>
								<option value="ICN">인천</option>
								<option value="GMP">김포</option>
								<option value="PUS">부산</option>
							</select>
							<!-- <input type="text" class="form-control w100" id="depApo" value="ICN"> -->
						</div>
						<div class="form-group mr20 mr0-xs">
							<label for="">Arrival</label>
							<input type="text" class="form-control w100" id="rtnApo" value="FSZ">
							<button class="btn bg-gblue" type="button" onClick="window.open('/popup/pop_city.hnt?tag_id=rtnApo','site_code','width=500,height=500,scrolling=1');">SELECT</button>
						</div>
						<div class="form-group mr20 ml10-xs">
							<span class="input-daterange">
                                <label for="">From</label>
								<input type="text" class="w100 input-sm form-control mr20" name="startDt" id="startDt" />
                                <label for="">To&nbsp;&nbsp;</label>
								<input type="text" class="w100 input-sm form-control" name="endDt" id="endDt" />
							</span>
						</div>
					</div>
					<div class="form-group col-sm-2">
						<button type="button" class="btn btn-block bg-purple" id="btnFareSearch">SEARCH</button>
					</div>
				</form>
			</div>
			<!-- 테이블 -->
			<!-- <div class="btn-group-wrp clearfix">
				<div class="pull-right">
					<a class="btn bg-blue w70" href="t16_new.html">+ NEW</a>
					<button type="button" class="btn bg-dpurple w70">OPEN</button>
					<button type="button" class="btn bg-grey w70">CLOSE</button>
					<button type="button" class="btn bg-orange w70">DELETE</button>
				</div>
			</div> -->
			<div class="table-responsive pr pt10 pb50">
				<table class="table">
					<caption id="sch_update_desc"></caption>
					<thead>
						<tr>
							<th class="th-chk" rowspan="2"><span class="sr-only">check</span></th>
							<th rowspan="2" class="th-rp">유형</th>
							<th colspan="3" >유류/텍스/NET</th>
							<th colspan="4">자체사이트 기본운임</th>
							<th colspan="4">자체사이트 할인율</th>
						</tr>
						<tr>
							<th>유류</th>
							<th>제세공과금</th>
							<th>NET</th>
							
							<th>하나투어</th>
							<th>모두투어</th>
							<th>인터파크</th>
							<th>온라인투어</th>
							<th>하나투어</th>
							<th>모두투어</th>
							<th>인터파크</th>
							<th>온라인투어</th>
						</tr>
					</thead>
					<tbody id="fareList">
						<tr>
							<td colspan="13"> NO DATA</td>
						</tr>
						<!-- 
						<tr>
							<td><img src="http://image1.hanatour.com/2010/images/airline/OZ.png" class=""><br />아시아나 항공 </td>
							<td>성인</td>
							<td>10,000</td>
							<td>57,300</td>
							<td>430,000</td>
							
							<td>422,000</td>
							<td>421,400</td>
							<td>421,000</td>
							<td>402,300</td>
							
							<td>1.9%</td>
							<td>1.1%</td>
							<td>2.7%</td>
							<td>3.1%</td>
						</tr>
						<tr>
							<td><img src="http://image1.hanatour.com/2010/images/airline/OZ.png" class="w30"><br />아시아나 항공</td>
							<td>성인</td>
							<td>10,000</td>
							<td>57,300</td>
							<td>430,000</td>
							
							<td>422,000</td>
							<td>421,400</td>
							<td>421,000</td>
							<td>402,300</td>
							
							<td>1.9%</td>
							<td>1.1%</td>
							<td>2.7%</td>
							<td>3.1%</td>
						</tr>
						<tr>
							<td>대한항공</td>
							<td>성인</td>
							<td>10,000</td>
							<td>57,300</td>
							<td>430,000</td>
							
							<td>422,000</td>
							<td>421,400</td>
							<td>421,000</td>
							<td>402,300</td>
							
							<td>1.9%</td>
							<td>1.1%</td>
							<td>2.7%</td>
							<td>3.1%</td>
						</tr>
						<tr>
							<td>대한항공</td>
							<td>성인</td>
							<td>10,000</td>
							<td>57,300</td>
							<td>430,000</td>
							
							<td>422,000</td>
							<td>421,400</td>
							<td>421,000</td>
							<td>402,300</td>
							
							<td>1.9%</td>
							<td>1.1%</td>
							<td>2.7%</td>
							<td>3.1%</td>
						</tr>
						<tr>
							<td>대한항공</td>
							<td>성인</td>
							<td>10,000</td>
							<td>57,300</td>
							<td>430,000</td>
							
							<td>422,000</td>
							<td>421,400</td>
							<td>421,000</td>
							<td>402,300</td>
							
							<td>1.9%</td>
							<td>1.1%</td>
							<td>2.7%</td>
							<td>3.1%</td>
						</tr>
						 -->
					</tbody>
				</table>
			</div>
			<!-- 페이지네이션 -->
			<!-- 
			<div class="bx pt15 pb15 fs13">
				<div class="row">
					<div class="col-sm-3 pt7 pt0-xs">
						Showing 1 to 10 of entries
					</div>
					<div class="col-sm-9">
						<nav class="pagination_wrp">
							<ul class="pagination">
								<li><a class="direction" href="#" aria-label="First"><span aria-hidden="true">&laquo;</span></a></li>
								<li><a class="direction" href="#">Previous</a></li>
								<li class="active"><a href="#">1</a></li>
								<li><a href="#">2</a></li>
								<li><a href="#">3</a></li>
								<li><a href="#">4</a></li>
								<li><a href="#">5</a></li>
								<li><a href="#">6</a></li>
								<li><a class="direction" href="#">Next</a></li>
								<li><a class="direction" href="#" aria-label="Last"><span aria-hidden="true">&raquo;</span></a></li>
							</ul>
						</nav>
					</div>
				</div>
			</div>
			 -->
			
		</div>
		<!-- //content -->
	</div>
	<!-- //container -->
</div>

<%@ include file="/WEB-INF/view/include/commonScript.jsp" %>
</body>
</html>