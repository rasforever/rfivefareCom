<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/include/import.jsp" %>
<%@ include file="/WEB-INF/view/include/head.jsp" %>
<script type="text/javascript">
	$(document).ready(function(){
		console.log("view on");
		$("#result").load("http://hgrsapisvc.hanatour.com/air/airWs.asmx/getAirCityListXml?requestXml=%3CApCtSearchRQ%3E%3CSiteCode%3EC00001S0001%3C/SiteCode%3E%3CLicenseKey%3E4025238517405993955840252385174025233659374763470038517%3C/LicenseKey%3E%3CInpChn%3EMD2%3C/InpChn%3E%3CAgtCode%3E1000%3C/AgtCode%3E%3CLangCode%3EKOR%3C/LangCode%3E%3CSalesSite%3EHANATOUR%3C/SalesSite%3E%3CSalSiteCode%3EC00002S0001%3C/SalSiteCode%3E%3CApCtName%3E%EB%9F%B0%EB%8D%98%3C/ApCtName%3E%3CRqAgtCode%3E1000%3C/RqAgtCode%3E%3CRqId%3ECJOTOUR%3C/RqId%3E%3CRqLclName%3ECJOTOUR%3C/RqLclName%3E%3C/ApCtSearchRQ%3E");
	});
</script>
<div id="result">
</div>
