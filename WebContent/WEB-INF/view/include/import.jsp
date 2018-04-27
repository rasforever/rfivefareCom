<%--
- File name : import.jsp
- Package : 태그라이브러리 jsp 자원 import 파일
- Description : Spring Dispatcher config 파일
- <pre>
- <변경이력>
- 수정일			수정자				수정내용
- --------------------------------------------------
- 2017. 10. 17.		sthan	최초 작성
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.hanatour.fareCompare.model.MemberVo" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<jsp:useBean id="now" class="java.util.Date"/>
<%
	MemberVo member = (MemberVo)request.getAttribute("member");
%>