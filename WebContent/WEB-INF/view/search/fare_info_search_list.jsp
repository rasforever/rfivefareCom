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
	$(".golist").on("click", function(){
		var sch_seq = $(this).parent().data("seq");
		console.log("sch_seq===="+sch_seq);
		location.href="/search/compareList.hnt?sch_seq="+sch_seq;
	});
	
	// 클래스명 sel_table의 테이블의 row에 마우스 오버시 색상변경
	$( "#fareList tr" ).on( "mouseover", function() {
		$( this ).css( "background-color", "#f4f4f4" );
		$( this).children("td").css( "cursor", "pointer" );
	});
	
	$( "#fareList tr" ).on( "mouseleave", function() {
		$( this ).css( "background-color", "" );
	}); 
	
});


function fareinfoSearchAction() {
	$("#depApo").val($("#depApo").val().toUpperCase());	
	$("#arrApo").val($("#arrApo").val().toUpperCase());	
	var arrApo = $("#arrApo").val();
	if(arrApo.length != 3 && arrApo.length != 0){
		alert("노선코드는 3자리 코드 이여야 합니다.");
		$("#arrApo").focus();
	}
	var url = "/search/fareInfoSearchList.hnt";
	document.search_frm.action = url;
	document.search_frm.submit();
}
function fareinfoUpdateAction(seq) {
	$("#upSeq").val(seq);	
	var url = "/search/fareInfoUpdateFormAction.hnt";
	document.search_frm.action = url;
	document.search_frm.submit();
}
function fareinfoInsertFormAction() {
	var url = "/search/fareInfoInsertFormAction.hnt";
	document.search_frm.action = url;
	document.search_frm.submit();
}
function fareinfoDeleteAction() {
	var send_array = '';
	var send_data = '';
    $('input[name=checkSeq]:checked').each(function(){
    	send_data = $(this).val();
    	if(send_array.length > 0){
    		send_array += '/' + send_data;
    	}else{
    		send_array += send_data ;
    	}
    })
	$("#deSeq").val(send_array);	
	var url = "/search/fareInfoDeleteAction.hnt";
	document.search_frm.action = url;
	document.search_frm.submit();
}
$(function() {
	$('input[name=checkAll]').on('ifChecked', function(event){
		$('input[name=checkSeq]').iCheck('check');
	});
	$('input[name=checkAll]').on('ifUnchecked', function(event){
			$('input[name=checkSeq]').iCheck('uncheck');
	});
	
});


function fareSearchRealTime(seq){
	FLoading.show();
	$.ajax({
        url : '/search/searchRealTime.hnt',
        data : {"seq": seq},
        dataType : 'json',
        type : 'POST',
        //contentType : "application/json; charset=utf-8",
        success : function(data) {
            console.log(data);
            var result = data;
            if(result){
            	if(typeof data != 'object'){
                	result = JSON.parse(data);		
                }
            	FLoading.hide();
            	if("0" != result.ErrorCode ){
            		alert("실시간 운임수집 시 에러가 발생했습니다. 관리자에게 문의 해주세요.");
            	}else{
            		alert("실시간 운임수집이 완료 되었습니다.");
            		location.href="/search/fareInfoSearchList.hnt";
            	}
            	
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
</script>
<%@ include file="/WEB-INF/view/include/commonScript.jsp" %>
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
			<li><strong>운임수집</strong></li>
			<li class="active">운임 수집 룰 정보 리스트</li>
		</ul>
		<!-- //breadcrumb -->
		<!-- content -->
		<div id="content">
			<!-- 검색 -->
			<div class="bx">
				<form class="row form-inline" id="search_frm" name="search_frm" method="post">
					<input type="hidden" id="upSeq" name="upSeq" value="" >
					<input type="hidden" id="deSeq" name="deSeq" value="" >
					<div class="col-sm-10">
						<div class="form-group mr20 mr0-xs">
							<label for="">출발지 : </label>
							<select class="form-control input-sm w100" id="depApo" name="depApo" >
								<option value="" <c:if test="${'' eq depApo}">selected="selected"</c:if>>선택</option>
								<option value="SEL" <c:if test="${'SEL' eq depApo}">selected="selected"</c:if>>서울</option>
								<option value="ICN" <c:if test="${'ICN' eq depApo}">selected="selected"</c:if>>인천</option>
								<option value="GMP" <c:if test="${'GMP' eq depApo}">selected="selected"</c:if>>김포</option>
								<option value="PUS" <c:if test="${'PUS' eq depApo}">selected="selected"</c:if>>부산</option>
							</select>
						</div>
						<div class="form-group mr20 mr0-xs">
							<label for="">도착지 : </label>
							<input type="text" class="form-control w100" id="arrApo" value="${arrApo}" max="3" maxlength="3">
							<button class="btn bg-gblue" type="button" onClick="window.open('/popup/pop_city.hnt?tag_id=arrApo','site_code','width=500,height=500,scrolling=1');">SELECT</button>
						</div>
						<%-- 
						<div class="form-group">
							<label for="">노선 : </label>
							<input type="text" class="form-control w100" id="carr" value="${carr}" max="2" maxlength="2">
						</div>
						 --%>
					</div>
					<div class="form-group col-sm-2">
						<button type="button" class="btn btn-block bg-purple" onclick="fareinfoSearchAction();">SEARCH</button>
					</div>
				</form>
			</div>
			
			<!-- 테이블 -->
			<div class="btn-group-wrp clearfix pr10 pb40">
				<div class="pull-right">
					<button type="button" class="btn bg-blue w70" id="btnFareInsert" onclick="fareinfoInsertFormAction()">INSERT</button>
					<button type="button" class="btn bg-orange w70" id="btnFareDelete" onclick="fareinfoDeleteAction()">DELETE</button>
				</div>
			</div>
			
			<!-- 
			<div class="table-responsive pr">
				<div class="pull-right">
					<span class="col-xs-5"><button type="button" class="btn btn-block bg-blue w70" id="btnFareInsert" onclick="fareinfoInsertFormAction()">INSERT</button></span>
					<span class="col-xs-5"><button type="button" class="btn btn-block bg-blue w70" id="btnFareDelete" onclick="fareinfoDeleteAction()">DELETE</button></span>
				</div>
			</div>
		    -->
			

					
			<!-- 테이블 -->
			<!-- <div class="btn-group-wrp clearfix">
				<div class="pull-right">
					<a class="btn bg-blue w70" href="t16_new.html">+ NEW</a>
					<button type="button" class="btn bg-dpurple w70">OPEN</button>
					<button type="button" class="btn bg-grey w70">CLOSE</button>
					<button type="button" class="btn bg-orange w70">DELETE</button>
				</div>
			</div> -->
			<div class="table-responsive pr pt10">
				<table class="table">
				<caption class="font-r">※ 운임수집 배치시간 : 매일 오전 02:00</caption>
					<thead>
						<tr>
							<th rowspan="2" class="th-rp"><input type="checkbox" id="checkAll" name="checkAll" /></th>
							<th rowspan="2" class="th-rp">순번</th>
							<th rowspan="2" class="th-rp">출발지</th>
							<th rowspan="2" class="th-rp">도착지</th>
							<th rowspan="2" class="th-rp">왕복/편도</th>
							<th rowspan="2" class="th-rp">타입</th>
							<th colspan="3" style="width: 210px;">출발일</th>
							<th rowspan="2" class="th-rp">귀국일 (+)</th>
							<th rowspan="2" class="th-rp">수집종료일</th>
							<th rowspan="2" class="th-rp">최종설정일</th>
							<th rowspan="2" class="th-rp">최종수정자</th>
							<th rowspan="2" class="th-rp">업데이트</th>
						</tr>
						<tr>
							<th>MONTH</th>
							<th>WEEK</th>
							<th>DAY</th>
						</tr>
					</thead>
					<tbody id="fareList">
						<c:if test="${empty fareSearchList}" >
						<tr>
							<td colspan="14"> NO DATA</td>
						</tr>
						</c:if>
						<c:if test="${not empty fareSearchList}" >
						<c:forEach var="model" items="${fareSearchList}" varStatus="status" >
							<tr data-seq="${model.seq}">
								<td><input class="checkSelect" type="checkbox" id="checkSeq_${status.count}" name="checkSeq" value="${model.seq}"></td>
								<td class="golist">${status.count}</td>
								<td class="golist">${model.depApo}</td>
								<td class="golist">${model.rtnApo}</td>
								<td class="golist">${model.tripType}</td>
								<td class="golist">${model.depFixYn}</td>
								<c:if test="${'Y' eq model.depFixYn}">
									<td style="width: 40px;" class="golist">${model.chgMonth}</td>
									<td style="width: 40px;" class="golist">${model.chgWeek}</td>
									<c:if test="${'1' eq model.chgDay}">
										<td style="width: 40px;" class="golist">일</td>
									</c:if>
									<c:if test="${'2' eq model.chgDay}">
										<td style="width: 40px;" class="golist">월</td>
									</c:if>
									<c:if test="${'3' eq model.chgDay}">
										<td style="width: 40px;" class="golist">화</td>
									</c:if>
									<c:if test="${'4' eq model.chgDay}">
										<td style="width: 40px;" class="golist">수</td>
									</c:if>
									<c:if test="${'5' eq model.chgDay}">
										<td style="width: 40px;" class="golist">목</td>
									</c:if>
									<c:if test="${'6' eq model.chgDay}">
										<td style="width: 40px;" class="golist">금</td>
									</c:if>
									<c:if test="${'7' eq model.chgDay}">
										<td style="width: 40px;" class="golist">토</td>
									</c:if>
								</c:if>
								<c:if test="${'N' eq model.depFixYn}">
									<td style="width: 120px;" colspan="3" class="golist">${model.depDt}</td>
								</c:if>
								<td class="golist">${model.chgRtnDay}</td>
								<c:if test="${'Y' eq model.alwaysYn}">
									<td class="golist">상시</td>
								</c:if>
								<c:if test="${'N' eq model.alwaysYn}">
									<td class="golist">${model.schEndDt}</td>
								</c:if>
								<td class="golist">${model.modDate}</td>
								<td class="golist">${model.memNM}</td>
								<td style="width: 80px;"><button type="button" class="btn btn-block bg-blue w70" id="btnFareUpdate" onclick="fareSearchRealTime('${model.seq}')">UPDATE</button></td>
							</tr>
						</c:forEach>
						</c:if>
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


</body>
</html>