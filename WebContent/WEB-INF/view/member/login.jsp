<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no">
<title>HNT FARE</title>
<!-- css -->
<link rel="stylesheet" type="text/css" href="${resourceUrlProvider.getForLookupPath('/css/normalize.css')}">
<link rel="stylesheet" type="text/css" href="${resourceUrlProvider.getForLookupPath('/css/bootstrap.min.css')}">
<link rel="stylesheet" type="text/css" href="${resourceUrlProvider.getForLookupPath('/css/bootstrap-datepicker3.min.css')}">
<link rel="stylesheet" type="text/css" href="${resourceUrlProvider.getForLookupPath('/css/select2.min.css')}">
<link rel="stylesheet" type="text/css" href="${resourceUrlProvider.getForLookupPath('/css/minimal/minimal.css')}">
<link rel="stylesheet" type="text/css" href="${resourceUrlProvider.getForLookupPath('/css/jPicker-1.1.6.min.css')}">
<link rel="stylesheet" type="text/css" href="${resourceUrlProvider.getForLookupPath('/css/jPicker.css')}">
<link rel="stylesheet" type="text/css" href="${resourceUrlProvider.getForLookupPath('/css/common.css')}">

<!-- js -->
<script src="${resourceUrlProvider.getForLookupPath('/js/jquery-1.8.3.min.js')}"></script>
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
	<form id="loginFrm">
		<input type="text" class="itx" placeholder="username">
		<input type="password" class="itx" placeholder="password">
		<div class="checkbox text-right">
			<label>
				<input type="checkbox"> Save your ID
			</label>
		</div>
		<button type="button" class="icon-login-btn">Login Now</button>
	</form>
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