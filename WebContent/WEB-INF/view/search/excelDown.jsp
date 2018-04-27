<%@ page language="java" contentType="application/vnd.ms-excel; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
 request.setCharacterEncoding("utf-8");
 response.setContentType("application/vnd.ms-excel");
 response.setCharacterEncoding("utf-8");
 response.setHeader("Content-Disposition", "attachment; filename=\"excel.xls\"");
 response.setHeader("Pragma", "no-cache");
 response.setHeader("Cache-Control", "no-cache");
 out.print("<meta http-equiv=\"Content-Type\" content=\"application/vnd.ms-excel; charset=utf-8\">");
%>
<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40">
<head>
<style type="text/css">
 body {font-family:tahoma;font-size:12px}
 table {padding:2px;border-spacing:0px;font-family:tahoma;font-size:12px;border-collapse:collapse}
 td {text-align:center}
</style>
</head>
<body>
${excel_data}
</body>
</html>