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
	
	var FMemmber = {
		init : function(){
			
			$('#btnUserReg').click(function(){FMemmber.setUserRegAjax()});
		},
		setUserRegAjax : function(){
			
			if(FMemmber.memberValid()){
			
				var user_id = $('#user_id').val();
				var user_pwd = $('#user_pwd').val();
				var user_nm = $('#user_nm').val();
				var user_auth = $('#user_auth option:selected').val();
				
				var insData = { user_id : user_id , user_pwd : user_pwd , user_nm : user_nm , user_auth : user_auth};
	
				$.ajax({
		            url : '/member/member_insertProc.hnt',
		            data : insData,
		            dataType : 'json',
		            type : 'POST',
		            //contentType : "application/json; charset=utf-8",
		            success : function(data) {
		                console.log(data);
		                if( data.ErrorCode == "0" ){
		                	alert("사용자가 등록 되었습니다.");
		                }else{
		                	alert("사용자 등록에 실패 하였습니다.");
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
		memberValid : function(){
			var user_id = $('#user_id').val();
			var user_pwd = $('#user_pwd').val();
			var user_nm = $('#user_nm').val();
			var user_auth = $('#user_auth option:selected').val();
			
			if( user_id == "" ){
				alert("아이디를 입력해주세요.");
				$('#user_id').focus();
				return false;
			}
			
			if( user_pwd == "" ){
				alert("패스워드를 입력해주세요.");
				$('#user_pwd').focus();
				return false;
			}
			
			if( user_nm == "" ){
				alert("이름을 입력해 주세요.");
				$('#user_nm').focus();
				return false;
			}
			
			if( user_auth == "" ){
				alert("권한을 선택해 주세요.");
				$('#user_auth').focus();
				return false;
			}
			return true;
		}
	}
	
	$(document).ready(function(){
		FMemmber.init();
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
			<li><strong>사용자 정보</strong></li>
			<li class="active">사용자 정보 등록</li>
		</ul>
		<!-- //breadcrumb -->
		<!-- content -->
		<div id="content">
			<!-- 검색 -->
			<div class="bx">
				<form class="row form-inline" id="search_frm" name="search_frm" method="post">
					<div class="col-sm-10">
						<div class="form-group mr20 mr0-xs">
							<label for="">아이디</label>
							<input type="text" class="form-control w100" id="user_id" value="">
						</div>
						<div class="form-group mr20 mr0-xs">
							<label for="">패스워드</label>
							<input type="text" class="form-control w100" id="user_pwd" value="">
						</div>
						<div class="form-group mr20 ml10-xs">
							<label for="">이름</label>
							<input type="text" class="form-control w100" id="user_nm" value="">
						</div>
						<div class="form-group mr20 ml10-xs">
							<label for="">사용자권한</label>
							<select class="form-control input-sm" id="user_auth">
								<option value="0">일반사용자</option>
								<option value="1">관리자</option>
								<option value="2">권한관리자</option>
							</select>
						</div>
					</div>
					<div class="col-sm-2">
						<button type="button" class="btn btn-block bg-purple" id="btnUserReg">Insert</button>
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
			<%-- 
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
					</tbody>
				</table>
			</div>
			 --%>
		</div>
		<!-- //content -->
	</div>
	<!-- //container -->
</div>

<%@ include file="/WEB-INF/view/include/commonScript.jsp" %>
</body>
</html>