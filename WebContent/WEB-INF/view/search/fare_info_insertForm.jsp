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
function insertAction() {
	$("#depApo").val($("#depApo").val().toUpperCase());	
	$("#arrApo").val($("#arrApo").val().toUpperCase());	
	
	var depfixyn = $("#depFixYn").val();
	var depApo = $("#depApo").val();
	var arrApo = $("#arrApo").val();
	var chgRtnDay = $("#chgRtnDay").val();
	var alwaysYn = $("#alwaysYn").val();
	
	var pattEn = /[a-zA-Z]/;  //영문만 허용
	var regNumber = /^[0-9]*$/; //숫자만 허용
		
	if(depApo.length != 3 && !pattEn.test( depApo ) ){
		alert("출발지 코드는 영문 3자리 코드 이여야 합니다.");
		$("#depApo").focus();
		return false;
	}
	if(arrApo.length != 3 && !pattEn.test( arrApo )){
		alert("도착지 코드는 영문 3자리 코드 이여야 합니다.");
		$("#arrApo").focus();
		return false;
	}
	
	if(depfixyn == 'Y'){
		var chgMonth = $("#chgMonth").val();
		var chgWeek = $("#chgWeek option:selected").val();
		var chgDay = $("#chgDay option:selected").val();
		
		if( $.trim(chgMonth) == "" ){
			alert("Month(월)을 입력해 주세요.");
			$("#chgMonth").focus();
			return false;
		}
		if( !regNumber.test(chgMonth) || parseInt(chgMonth) > 12){
			alert("Month(월)은 최대2 자리 숫자만 입력해 주세요.");
			$("#chgMonth").focus();
			return false;
		}
		
		if( $.trim(chgWeek) == "" ){
			alert("Week(주)를  입력해 주세요.");
			$("#chgWeek").focus();
			return false;
		}
		
		if( $.trim(chgDay) == "" ){
			alert("Day(요일)을 입력해 주세요.");
			$("#chgDay").focus();
			return false;
		}
	}else{
		var depDt = $("#depDt").val();  
		if( $.trim(depDt) == "" ){
			alert("출발일자를 입력해 주세요.");
			$("#depDt").focus();
			return false;
		}
	}
	
	if( $.trim(chgRtnDay) == "" ){
		alert("귀국일 정보를 설정 해 주세요.");
		$("#chgRtnDay").focus();
		return false;
	}
	
	
	if( $.trim(alwaysYn) == "N" ){
		var schEndDt = $("#schEndDt").val();
		if(  $.trim(schEndDt) == "" ){
			alert("운임 수집 종료일을 설정 해 주세요.");
			$("#schEndDt").focus();
			return false;	
		}	
	}
	
	$.ajax({
        url : '/search/searchInfoCnt.hnt',
        data : '',
        dataType : 'text',
        type : 'POST',
        success : function(data) {
            console.log(data);
            var result = $.trim(data);
            if(parseInt(result) < 20  ){
            	var url = "/search/fareInfoInsertAction.hnt";
            	document.insertForm.action = url;
            	document.insertForm.submit();
            }else{
            	alert("운임 수집 룰 정보는 최대 20개 까지 만 가능합니다.");
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
			$("#alwaysYn").val('N');
		}
	});
	
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
			<li><strong>운임수집</strong></li>
			<li class="active">운임수집 룰 정보 등록</li>
		</ul>
		<!-- //breadcrumb -->
		<!-- content -->
		<div id="content">
		<form id="insertForm" name="insertForm">
			<!-- 입력 -->
			<div class="bx">
					<!-- Depart -->
					<div class="row">
						<div class="col-sm-3 form-group">
							<label for="">출발지</label>
							<input type="text" class="form-control onlyEng" name="depApo" id="depApo"  style="ime-mode:inactive; text-transform:uppercase;" maxlength="3"/>
						</div>
						<div class="col-sm-3 form-group">
							<label for="">도착지</label>
							<input type="text" class="form-control onlyEng" name="arrApo" id="arrApo" style="ime-mode:inactive; text-transform:uppercase;" maxlength="3"/>
						</div>
						<!-- 익스클루드 -->
						<div class="col-sm-3 form-group" style="display:none;">
							<label for="">항공사</label>
							<input type="text" class="input-sm form-control" name="carr" id="carr" maxlength="2"/>
						</div>
						<div class="col-sm-3 form-group" style="display:none;">
							<label for="">왕복/편도</label>
							<select class="form-control" id="tripType" name="tripType" >
								<option value="RT" >왕복</option>
								<option value="OW" >편도</option>
							</select>
						</div>
						<!-- 
						<div class="col-sm-6 form-group" style="display:block;">
							<div class="clude-item clearfix">
								<h4 class="clude-hd"><span style="background:#daedf3">항공사/왕복여부 </span></h4>
								<div class="input-bx" id="deDiv">
									<div class="input-group">
                                        <label for="" class="fs10">항공사</label>
										<input type="text" class="input-sm form-control w100" name="carr" id="carr" maxlength="2"/>
										<span class="input-group-addon pt20">-</span>
                                        <label for="" class="fs10">왕복/편도</label>
										<select class="form-control w100" id="tripType" name="tripType" >
											<option value="RT" >왕복</option>
											<option value="OW" >편도</option>
										</select>
									</div>
								</div>
							</div>
							*항공사2코드입력 ex)YY전체, KE:대한항공
						</div>
						 -->
					</div>
			</div>
			<!-- 입력 -->
			<div class="tb-style3">
				<div class="row form-horizontal">
					<div class="col-sm-6">
						<h4 class="clude-hd2"><span style="background:#ffed99">출발일</span></h4>
						<div class="tb-style3">
							<div class="row form-group mb5">
								<label class="col-sm-4 radio"><input type="radio" id="depFixYnChk1" name="depFixYnChk" value="Y" checked="checked">변동</label>
								<label class="col-sm-4 radio"><input type="radio" id="depFixYnChk2" name="depFixYnChk" value="N">고정</label>
								<input type="hidden" id="depFixYn" name="depFixYn" value="Y">
							</div>
							<div class="row form-group mb4 pl15" id="view1" style="display: block;">
								<label><font style="color: red;">Month+ : </font><input type="number" class="form-control w100" id="chgMonth" name="chgMonth" min="1" max="12"></label>
								<label><font style="color: red;">Week : </font>
									<select class="form-control w100" id="chgWeek" name="chgWeek" >
										<option value="1" >1주</option>
										<option value="2" >2주</option>
										<option value="3" >3주</option>
										<option value="4" >4주</option>
									</select>
									<!-- <input type="text" style="width: 30px" id="chgWeek" name="chgWeek" maxlength="1"> -->
								</label>
								<label><font style="color: red;">Day : </font>
									<select class="form-control w100" id="chgDay" name="chgDay">
										<option value="1" >일</option>
										<option value="2" >월</option>
										<option value="3" >화</option>
										<option value="4" >수</option>
										<option value="5" >목</option>
										<option value="6" >금</option>
										<option value="7" >토</option>
									</select>
									<!-- <input type="text" style="width: 30px" id="chgDay" name="chgDay" maxlength="2"> -->
								</label>
							</div>
							<div class="row form-group mb4 input-daterange pl15" id="view2" style="display: none;">
								<label><font style="color: red;">출발일자 : </font><input type="text" class="input-sm form-control w100" name="depDt" id="depDt"/></label>
							</div>
						</div>
					</div>
					<div class="col-sm-6">
						<h4 class="clude-hd2"><span style="background:#ffed99">귀국일</span></h4>
						<div class="tb-style3">
							<div class="row form-group mb5" id="view1" style="display: block;">
								<label class="pl15"><font style="color: red;">귀국일+ : </font><input type="text" class="w100" id="chgRtnDay" name="chgRtnDay" value="" maxlength="1"></label>
							</div>
							<div class="row form-group mb5" id="view1" style="display: block;">
								<label class="col-sm-3 radio"><input type="radio" id="alwaysYnChe1" name="alwaysYnChe" value="Y" checked="checked">상시수집</label>
								<label class="col-sm-3 radio"><input type="radio" id="alwaysYnChe2" name="alwaysYnChe" value="N">종료일설정</label>
								<input type="hidden" id="alwaysYn" name="alwaysYn" value="Y">
								<div class="form-group mb5 input-daterange" id="view3" style="display: none;">
									<input type="text" class="input-sm form-control w100" name="schEndDt" id="schEndDt" />
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="text-center pa10">
				<button type="button" class="btn bg-mint w90" onclick="insertAction();">SAVE</button>
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