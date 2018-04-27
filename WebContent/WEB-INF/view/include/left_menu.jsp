<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div id="side">
	<!-- account -->
	<section class="account">
		<h1 class="sr-only">account</h1>
		<div>
			<i class="icon-user"></i> User ID <i class="split"></i> <strong class="user">${member.mem_id}</strong>
		</div>
	</section>
	<!-- gnb -->
	<nav id="gnb">
		<h1 class="sr-only">Global Navigation Bar</h1>
		<ul>
			<li class="gnb2">
				<h2>운임조회</h2>
				<ul>
					<li><a class="i3" href="/search/fareInfoSearchList.hnt">운임수집</a></li>
					<%
						if("Y".equals(member.getIsLogin())){
							int intAuthCd = Integer.parseInt(member.getAuth_cd());	
							if( intAuthCd > 0 ){
					%>
					<li> <a class="i4" href="/search/fare_list.hnt">실시간 운임 검색</a></li>
					<%			
							}
						}
						 
					%>
					
				</ul>
			</li>
		</ul>
	</nav>
</div>