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
				
				
				sHtml += '<tr>';
				sHtml += '	<td class="gubun"> <img src="http://image1.hanatour.com/2010/images/airline/'+ obj.AIR_CD +'.png" class="w30">'+obj.AIR_NM+'</td>';
				sHtml += '	<td>'+obj.PROMOTION_NM+'</td>';
				sHtml += '	<td>'+obj.OIL_FEE+'</td>';
				sHtml += '	<td>'+obj.TAX_FEE+'</td>';
				sHtml += '	<td>'+obj.FARE_NETT+'</td>';
				sHtml += '	<td>'+obj.H_FARE_NOMAL+'</td>';
				sHtml += '	<td><span data-tooltip-text="'+h_tip+'"> '+obj.H_FARE_DIS+'</span></td>';
				sHtml += '	<td><span data-tooltip-text="'+m_tip+'">'+obj.M_FARE_DIS+'</span></td>';
				sHtml += '	<td><span data-tooltip-text="'+i_tip+'">'+obj.I_FARE_DIS+'</span></td>';
				sHtml += '	<td><span data-tooltip-text="'+o_tip+'">'+obj.O_FARE_DIS+'</span></td>';
				sHtml += '	<td>'+h_dis_rate+'</td>';
				sHtml += '	<td>'+m_dis_rate+'</td>';
				sHtml += '	<td>'+i_dis_rate+'</td>';
				sHtml += '	<td>'+o_dis_rate+'</td>';
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
			<P>${message}</P>
		</div>
		<!-- //content -->
	</div>
	<!-- //container -->
</div>

<%@ include file="/WEB-INF/view/include/commonScript.jsp" %>
</body>
</html>