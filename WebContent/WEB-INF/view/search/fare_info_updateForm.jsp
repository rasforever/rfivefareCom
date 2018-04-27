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
	dispChange();
});
function dispChange() {
	var depFixYn = $("#depFixYn").val();
	if(depFixYn == 'Y'){
		$('#depFixYnChk1').iCheck('check');
		$("#view1").css("display","inline-block");
		$("#view2").css("display","none");
		
	}else if(depFixYn == 'N'){
		$('#depFixYnChk2').iCheck('check');
		$("#view1").css("display","none");
		$("#view2").css("display","inline-block");
	}
	var alwaysYn = $("#alwaysYn").val();
	if(alwaysYn == 'Y'){
		$('#alwaysYnChe1').iCheck('check');
		$("#view32").css("display","none");
	}else if(alwaysYn == 'N'){
		$('#alwaysYnChe2').iCheck('check');
		$("#view3").css("display","inline-block");
	}

}
	
function updateAction() {
	$("#depApo").val($("#depApo").val().toUpperCase());	
	$("#arrApo").val($("#arrApo").val().toUpperCase());	
	$("#carr").val($("#carr").val().toUpperCase());	
	var depApo = $("#depApo").val();
	var arrApo = $("#arrApo").val();
	var carr = $("#carr").val();
	if(depApo.length != 3){
		alert("출발지 코드는 3자리 코드 이여야 합니다.");
		$("#depApo").focus();
		return false;
	}
	if(arrApo.length != 3){
		alert("도착지 코드는 3자리 코드 이여야 합니다.");
		$("#arrApo").focus();
		return false;
	}
	if(carr.length != 2){
		alert("항공코드는 2자리 코드 이여야 합니다.");
		$("#carr").focus();
		return false;
	}
	
	var url = "/search/fareInfoUpdateAction.hnt";
	document.insertForm.action = url;
	document.insertForm.submit();
}
$(function() {
	$('input[name=depFixYnChk]').on('ifChecked', function(event){
		var flag = $("input[name=depFixYnChk]:checked").val();
		if(flag == 'Y'){
			$("#view1").css("display","inline-block");
			$("#view2").css("display","none");
			$("#depFixYn").val('Y');
			$("#depDt").val('');
		}else if(flag == 'N'){
			$("#view2").css("display","inline-block");
			$("#view1").css("display","none");
			$("#depFixYn").val('N');
			$("#chgMonth").val('');
		    $("#chgWeek").val('');
		    $("#chgMonth").val('');
		}
	});
	$('input[name=alwaysYnChe]').on('ifChecked', function(event){
		var flag = $("input[name=alwaysYnChe]:checked").val();
		if(flag == 'Y'){
			$("#view3").css("display","none");
			$("#alwaysYn").val('Y');
			$("#schEndDt").val('');
		}else if(flag == 'N'){
			$("#view3").css("display","inline-block");
			$("#alwaysYn").val('Y');
		}
	});
});

</script>
</head>

<body><a href="#content" class="skip sr-only sr-only-focusable" onclick="jQuery('#content a:first').focus();return false;">Skip to Content</a>

<!-- wrap -->
<div id="wrap">
	<!-- side -->
	<div id="side">
		<!-- account -->
		<section class="account">
			<h1 class="sr-only">account</h1>
			<div>
				<i class="icon-user"></i> User ID <i class="split"></i> <strong class="user">H2501</strong>
			</div>
		</section>
		<!-- gnb -->
		<nav id="gnb">
			<h1 class="sr-only">Global Navigation Bar</h1>
			<%@ include file="/WEB-INF/view/include/left_menu.jsp" %>
		</nav>
	</div>
	<!-- //side -->

	<!-- container -->
	<div id="container">
		<!-- header -->
		<header id="hd">
			<a id="sideTg" class="icon-mn" href="#side" onClick="$('body').toggleClass('gnb_fold');return false;">sidebar open/close</a>
			<h1><img class="hidden-xs" src="/img/logo2.png" alt=""><img class="visible-xs" src="/img/logo_m.png" alt=""><span class="sr-only">HNT FARE</span></h1>
			<a class="icon-logout" href="#sid">logout</a>
		</header>
		<!-- //header -->
		<!-- breadcrumb -->
		<ul class="breadcrumb">
			<li><strong>운임수집</strong></li>
			<li class="active">운임수집 룰 정보 등록</li>
		</ul>
		<!-- //breadcrumb -->
		<!-- content -->
		<div id="content">
		<form id="insertForm" name="insertForm">
			<input type="hidden" id="upSeq" name="upSeq" value="${fareUpdateList.seq}" >
			<!-- 입력 -->
			<div class="tb-style3">
					<!-- Depart -->
					<div class="row">
						<!-- 인클루드 -->
						<div class="col-sm-6 form-group">
							<!-- 추가단위 -->
							<div class="clude-item clearfix">
								<div class="" id="diDiv">
									<div class="input-group">
                                        <label for="" class="fs10">출발지</label>
										<input type="text" class="form-control" name="depApo" id="depApo" maxlength="3" value="${fareUpdateList.depApo}"/>
										<span class="input-group-addon pt20"> - </span>
                                        <label for="" class="fs10">목적지</label>
										<input type="text" class="form-control" name="arrApo" id="arrApo" maxlength="3" value="${fareUpdateList.rtnApo}"/>
									</div>
								</div>
							</div>
							<!-- Add Arrival 체크시 -->
						</div>
						<!-- 익스클루드 -->
						<div class="col-sm-6 form-group">
							<!-- 추가단위 -->
							<div class="clude-item clearfix">
								<h4 class="clude-hd"><span style="background:#daedf3">항공사/왕복여부 </span></h4>
								<div class="input-bx" id="deDiv">
									<div class="input-group">
                                        <label for="" class="fs10">항공사</label>
										<input type="text" class="input-sm form-control w100" name="carr" id="carr" maxlength="2" value="${fareUpdateList.airCd}"/>
										<span class="input-group-addon pt20">-</span>
                                        <label for="" class="fs10">왕복/편도</label>
										<select class="form-control w100" id="tripType" name="tripType" >
											<option value="RT" <c:if test="${'RT' eq fareUpdateList.tripType}">selected="selected"</c:if>>왕복</option>
											<option value="OW" <c:if test="${'OW' eq fareUpdateList.tripType}">selected="selected"</c:if>>편도</option>
										</select>
									</div>
								</div>
							</div>
							*항공사2코드입력 ex)YY전체, KE:대한항공
						</div>
					</div>
			</div>
			<!-- 입력 -->
			<div class="tb-style3">
				<div class="row form-horizontal">
					<div class="col-sm-6">
						<h4 class="clude-hd2"><span style="background:#ffed99">출발일</span></h4>
						<div class="tb-style3">
							<div class="row form-group mb5">
								<label class="col-sm-4 radio"><input type="radio" id="depFixYnChk1" name="depFixYnChk" value="Y">변동</label>
								<label class="col-sm-4 radio"><input type="radio" id="depFixYnChk2" name="depFixYnChk" value="N">고정</label>
								<input type="hidden" id="depFixYn" name="depFixYn" value="${fareUpdateList.depFixYn}">
							</div>
							<div class="row form-group mb4" id="view1" style="display: block;">
								<label class="col-sm-5 "><font style="color: red;">Month+ : </font><input type="text" style="width: 30px" id="chgMonth" name="chgMonth" maxlength="2" value="${fareUpdateList.chgMonth}"></label>
								<label class="col-sm-5 "><font style="color: red;">Week : </font><input type="text" style="width: 30px" id="chgWeek" name="chgWeek" maxlength="1" value="${fareUpdateList.chgWeek}"></label>
								<label class="col-sm-5 "><font style="color: red;">Day : </font><input type="text" style="width: 30px" id="chgDay" name="chgDay" maxlength="2" value="${fareUpdateList.chgDay}"></label>
							</div>
							<div class="row form-group mb4 input-daterange" id="view2" style="display: none;">
								<label class="col-sm-5 "><font style="color: red;">고정날짜 : </font><input type="text" class="input-sm form-control" name="depDt" id="depDt" value="${fareUpdateList.depDt}" style="width: 50px"/></label>
							</div>
						</div>
					</div>
					<div class="col-sm-6 pt10-xs">
						<h4 class="clude-hd2"><span style="background:#ffed99">귀국일</span></h4>
						<div class="tb-style3">
							<div class="row form-group mb4" id="view1" style="display: block;">
								<label class="col-sm-5 "><font style="color: red;">귀국일+ : </font><input type="text" style="width: 30px" id="chgRtnDay" name="chgRtnDay" value="${fareUpdateList.chgRtnDay}" maxlength="2"></label>
							</div>
							<div class="row form-group mb4" id="view1" style="display: block;">
								<label class="col-sm-4 radio"><input type="radio" id="alwaysYnChe1" name="alwaysYnChe" value="Y">상시수집</label>
								<label class="col-sm-4 radio"><input type="radio" id="alwaysYnChe2" name="alwaysYnChe" value="N">종료일설정</label>
								<input type="hidden" id="alwaysYn" name="alwaysYn" value="${fareUpdateList.alwaysYn}">
								<div class="row form-group mb4 input-daterange" id="view3" style="display: none;">
									<input type="text" class="input-sm form-control" name="schEndDt" id="schEndDt" style="width: 50px" value="${fareUpdateList.schEndDt}"/>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="text-center pa10">
				<button type="button" class="btn bg-mint w90" onclick="updateAction();">SAVE</button>
				<button type="button" class="btn bg-grey w90" onclick="javascript:history.back();">LIST</button>
			</div>
		</form>
		</div>
		<!-- //content -->
	</div>
	<!-- //container -->
</div>

<%@ include file="/WEB-INF/view/include/commonScript.jsp" %>
</body>
</html>