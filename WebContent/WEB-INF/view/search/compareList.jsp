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
	var FareList = {
		init : function(){
			var errorCode = '${ErrorCode}';
			var errordesc = '${ErrorDesc}';
			
			if(parseInt(errorCode) < 0  ){
				alert(errordesc);
				history.back();
			}
			
			$('#btnFareSearch').click(function(){FareList.btnFareSearch()});
			$('#btnExcelDown').click(function(){FareList.excelDownLoad()});
			$('#cardRuleViewBtn').click(function(){$('#cardRulelist').toggle()});
			$("#promoArr").val(); //운임조건 hidden field 초기화
			
			var farelist =  ${fareList};
			var airlist =  ${airList};
			var promolist =  ${promotionList};
			
			if( farelist.length > 0){
				FareList.makeHtml(farelist);	
			}
			if( airlist.length > 0){
				FareList.makeAirList(airlist);
			}
			if( promolist.length > 0){
				FareList.makePromoList(promolist);
			}
			
			
			//console.log(farelist);
			//FareList.makeHtml(farelist);	
			//FareList.makePromoList(promolist);
			//FareList.makeAirList(airlist);
		},
		btnFareSearch : function(){
			var send_array = '';
			var send_data = '';
		    $('input[name=promo_nm]:checked').each(function(){
		    	send_data = $(this).val();
		    	if(send_array.length > 0){
		    		send_array += ',' + send_data;
		    	}else{
		    		send_array += send_data ;
		    	}
		    })
			$("#promoArr").val(send_array);
		    
			var url = "/search/compareList.hnt?sch_seq=${param.sch_seq}";
			document.search_frm.action = url;
			document.search_frm.submit();
			
		},
		makeHtml : function(data){
			FLoading.show();
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
			var capHtml = obj2.DEP_APO+"/"+ obj2.RTN_APO + "왕복" +  dap_dt +"~"+ rtn_dt + ", 최종 업데이트 "+ reg_dt +" , 성인1인 기준 / 대기 제외";
			
			$("#sch_update_desc").html(capHtml);
			$("#fareList").html(sHtml);
			
			
			
			FareList.makeRowspan();
		},
		makeRowspan : function(){
			$(".gubun").each(function () {
				var air_cd = $(this).data("air");
				
				//var rows = $(".gubun[data-cd]:contains('" + air_cd + "')");
			    var rows = $('.gubun[data-air="'+ air_cd +'"]');
				if (rows.length > 1) {
			    	rows.eq(0).attr("rowspan", rows.length);
			        rows.not(":eq(0)").remove(); 
			    } 
			});
			
			FLoading.hide();
		},
		makeAirList : function(data){
			var airhtml = "";
			for(var i=0; i<data.length; i++){
				airhtml += "<option value='"+data[i].AIR_CD+"'>"+data[i].AIR_CD+"</option>";   
		    }
		    $("#air_cd").append(airhtml);
		    $("#air_cd").val("${air_cd}");
		    
		},
		makePromoList : function(data){
			var promohtml = "";
			promohtml += '<ul>';
			var promoList = ${promoList};
			
			for(var i=0; i<data.length; i++){
				
				var strChkFlag = "";
				
				if($.inArray(data[i].PROMOTION_NM, promoList) > -1 ){
					strChkFlag = "checked";		
				}
				
				promohtml += '<li>';
				promohtml += '<input type="checkbox" value="'+data[i].PROMOTION_NM+'" name="promo_nm" class="promo_chk" '+strChkFlag+'>';
				promohtml += '		<label for="promo_list_00">'+data[i].PROMOTION_NM+'</label>';
				promohtml += '</li>';
				
				//promohtml += "<option value='"+data[i].PROMOTION_NM+"'>"+data[i].PROMOTION_NM+"</option>";   
		    }
			promohtml += '<ul>';
		    $("#cardRulelist").append(promohtml);
		    
		    //$("#cardRulelist").val("${promo_nm}");
		},
		excelDownLoad : function(){
			var send_array = '';
			var send_data = '';
		    $('input[name=promo_nm]:checked').each(function(){
		    	send_data = $(this).val();
		    	if(send_array.length > 0){
		    		send_array += ',' + send_data;
		    	}else{
		    		send_array += send_data ;
		    	}
		    })
			$("#promoArr").val(send_array);
			
			var url = "/search/excelDown.hnt?sch_seq=${param.sch_seq}";
			document.search_frm.action = url;
			document.search_frm.submit();
		}
		
	}
	
	$(document).ready(function(){
		FareList.init();
	});
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
				<form class="row form-inline" id="search_frm" name="search_frm" method="post">
				<input type="hidden" name="promoArr" id="promoArr" />
					<div class="col-sm-10">
						<div class="form-group mr20 mr0-xs">
							<label for="">▶ 결과내 재 검색</label>
						</div>
						
						<div class="form-group mr20 mr0-xs">
							<label for="">항공사</label>
							<select class="form-control input-sm" id="air_cd" name="air_cd">
								<option value="">전체</option>
							</select>
							<!-- <input type="text" class="form-control w100" id="depApo" value="ICN"> -->
						</div>
						<!-- 
						<div class="form-group mr20 mr0-xs">
							<label for="">직항/경유</label>
							<select class="form-control input-sm" id="selViaCd">
								<option value="0">직항</option>
								<option value="1">경유</option>
							</select>
							<input type="text" class="form-control w100" id="depApo" value="ICN">
						</div>
						 -->
						<!--  
						<div class="form-group mr20 ml10-xs">
							<label for="">운임조건</label>
							<select class="form-control input-sm" id="promo_nm" name="promo_nm">
								<option value="">전체</option>
							</select>
						</div>
						 -->
						<div class="form-group mr20 ml10-xs">
							<div class="promo_option_sel">
								<strong class="title" >운임조건</strong>
								<div class="sel_area">
									<div class="txt" id="cardRuleViewBtn"><span>전체</span></div>
									<div class="lists" id="cardRulelist" style="display:none;">
										<!-- <ul>
											<li>
												<input type="checkbox" value="00" id="promo_list_00" name="promo_chk" class="promo_chk" checked="">
												<label for="promo_list_00">평일</label>
											</li>
											<li class="on"> 체크박스 체크시 li 에 on 클래스 삽입
												<input type="checkbox" value="01" id="promo_list_01" name="promo_list" class="promo_chk" checked="checked">
												<label for="promo_list_01">국민아멕스카드</label>
											</li>
											<li>
												<input type="checkbox" value="02" id="promo_list_02" name="promo_list" class="promo_chk" checked="">
												<label for="promo_list_02">삼성카드이벤트</label>
											</li>
										</ul> -->
									</div>
								</div>
							</div>
						</div> 
					</div>
					<div class="form-group col-sm-2">
						<button type="button" class="btn btn-block bg-gblue" id="btnExcelDown">ExcelDown</button>
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
			<div class="table-responsive pr pt10 pb50" id="tb_fareList">
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