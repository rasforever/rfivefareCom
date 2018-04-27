<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
<!-- <script src="${resourceUrlProvider.getForLookupPath('/js/bootstrap.min.js')}"></script> -->
<!--[if lt IE 9]>
<script src="${resourceUrlProvider.getForLookupPath('/js/html5.js')}"></script>
<script src="${resourceUrlProvider.getForLookupPath('/js/respond.min.js')}"></script>
<![endif]-->
<script type="text/javascript">
	var isLogin = "${member.isLogin}";
	
	if( isLogin != 'Y'){
		alert("로그인 후 이용해 주세요.");
		location.href = "/";
	}
</script>