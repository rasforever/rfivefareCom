<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no">
<title>HNT FARE</title>
<!-- css -->
<link rel="stylesheet" type="text/css" href="/css/normalize.css">
<link rel="stylesheet" type="text/css" href="/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="/css/bootstrap-datepicker3.min.css">
<link rel="stylesheet" type="text/css" href="/css/select2.min.css">
<link rel="stylesheet" type="text/css" href="/css/minimal/minimal.css">
<link rel="stylesheet" type="text/css" href="/css/jPicker-1.1.6.min.css">
<link rel="stylesheet" type="text/css" href="/css/jPicker.css">
<link rel="stylesheet" type="text/css" href="/css/common.css">

<!-- js -->
<script src="/js/jquery-1.8.3.min.js"></script>
<!-- <script src="js/bootstrap.min.js"></script> -->
<!--[if lt IE 9]>
<script src="js/html5.js"></script>
<script src="js/respond.min.js"></script>
<![endif]-->
</head>

<body><a href="#content" class="skip sr-only sr-only-focusable" onclick="jQuery('#content a:first').focus();return false;">Skip to Content</a>

<!-- wrap -->
<div id="login">
	<ul>
		<li class="i1" style="display:none"></li>
		<li class="i2" style="display:none"></li>
		<li class="i3" style="display:none"></li>
		<li class="i4" style="display:none"></li>
	</ul>
	<h1><img class="img-responsive" src="/img/logo.png" alt="logo"></h1>
	<form id="loginFrm" name="loginFrm">
		<input type="text" class="itx" placeholder="username" id="username" name="username" maxlength="15" onkeydown="javascript:if(event.keyCode==13){loginAction();}">
		<input type="password" class="itx" placeholder="password" id="password" name="password" maxlength="15" onkeydown="javascript:if(event.keyCode==13){loginAction();}">
		<div class="checkbox text-right">
			<!-- <label>
				<input type="checkbox" id="checksaveid1" name="checksaveid" > Save your ID
			</label> -->
		</div>
		<button type="button" class="icon-login-btn" onclick="loginAction();">Login Now</button>
	</form>
	<form id="loginFrmAction" name="loginFrmAction"></form>
</div>

<script>
jQuery(function($){
	"use strict";
	var loginBg = $('#login');
	loginBg.find('li').eq(Math.floor(Math.random()*4)).show();
// resize
	function pxHeight(){
		loginBg.find('li').css('height',$(window).height());
	}
	pxHeight();
	$(window).resize(pxHeight);
});

function loginAction(){
	$('#username').val($('#username').val().toUpperCase());
	var newurl	= window.location.protocol;
	var newhost = window.location.host+""+window.location.pathname;
	/* if(newurl != 'https:'){
		newhost = "http://"+newhost;
	}else{
		newhost = "https://"+newhost;
	} */
	if($("#username").val() == ''){
		$("#username").focus();
		alert("Please enter your ID.");
		return false;
	}
	if($("#password").val() == ''){
		$("#password").focus();
		alert("Please enter your password.");
		return false;
	}
	var checkFlag = 'N';
	var username = $("#username").val();
	var password = $("#password").val();
	//var url = newhost + "/main/login.hnt";
	var url = "/main/login.hnt";
	$.ajax({
		type:"post"
		,url:url
		, async: false
		, dataType : "json"
		,data: {
		"userId": username,
		"password": password
		}
		,success:function(args){
			if( args.ErrorCode == "0" ){
				var url = "/search/fareInfoSearchList.hnt";
				document.loginFrmAction.action = url;
				document.loginFrmAction.submit();
			}else{
				alert("ID/PassWord 를 확인해 주세요.");
				return;	
			}
			
		}
		,error:function(e){
			alert(e.responseText);
		}
	});
}

$(function(){
	$('input[name=checksaveid]').on('ifChecked', function(event){
		 var expdate = new Date();
		 expdate.setTime(expdate.getTime() + 1000 * 3600 * 24 * 30); // 30일
		 $('#username').val($('#username').val().toUpperCase());
		 var username = $('#username').val();
		 setCookie("saveid", username, expdate);
	});
	$('input[name=checksaveid]').on('ifUnchecked', function(event){
		var expdate = new Date();
		expdate.setTime(expdate.getTime() - 1);
		$('#username').val($('#username').val().toUpperCase());
		var username = $('#username').val();
		setCookie("saveid", username, expdate);
	});
	
});

function getid(form) {
  form.username.value = getCookie("saveid");
  if(form.username.value == ''){
	  $('#checksaveid1').iCheck('uncheck');
  }else{
	  $('#checksaveid1').iCheck('Check');
  }
}
</script>

<!-- js -->
<!--[if lt IE 9]>
<script src="js/jquery.placeholder.js"></script>
<script src="js/ie9.js"></script>
<![endif]-->
<script src="/js/select2.full.min.js"></script>
<script src="/js/bootstrap-datepicker.min.js"></script>
<script src="/js/icheck.min.js"></script>
<script src="/js/jpicker-1.1.6.min.js"></script>
<script src="/js/common.js"></script>
</body>
</html>