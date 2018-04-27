/*============================================== 
작성일 : 2015.04.10
작성자 : 한승태
페이지 이름 : 사용자 정의 code
페이지 설명 : 사용자 정의 code
===============================================*/


//항공권 국내도시/ 다구간 귀국
var airlineDomesticCity = [{'ApCtLclCode':'SEL','ApCtLclName':'서울(인천/김포)'},{'ApCtLclCode':'GMP','ApCtLclName':'김포'},{'ApCtLclCode':'ICN','ApCtLclName':'인천'}
,{'ApCtLclCode':'KWJ','ApCtLclName':'광주'},{'ApCtLclCode':'TAE','ApCtLclName':'대구'},{'ApCtLclCode':'MPK','ApCtLclName':'목포'}
,{'ApCtLclCode':'PUS','ApCtLclName':'부산'},{'ApCtLclCode':'RSU','ApCtLclName':'여수'},{'ApCtLclCode':'USN','ApCtLclName':'울산'}
,{'ApCtLclCode':'CJU','ApCtLclName':'제주'},{'ApCtLclCode':'HIN','ApCtLclName':'진주'},{'ApCtLclCode':'CJJ','ApCtLclName':'청주'}
,{'ApCtLclCode':'KPO','ApCtLclName':'포항'},{'ApCtLclCode':'MWX','ApCtLclName':'무안'}
];






//항공권 - 주요도시
var airMajorCities = [
	{"country_area":"일본","area_info":[{"lcName":"도쿄(나리타)", "lcCode":"NRT"},{"lcName":"도쿄(하네다)", "lcCode":"HND"},{"lcName":"오사카", "lcCode":"KIX"}
										,{"lcName":"후쿠오카", "lcCode":"FUK"},{"lcName":"오키나와", "lcCode":"OKA"},{"lcName":"나고야", "lcCode":"NGO"}
										,{"lcName":"삿포로", "lcCode":"SPK"},{"lcName":"시즈오카", "lcCode":"FSZ"}]}
	,{'country_area':'중국', 'area_info':[{'lcName':'홍콩', 'lcCode':'HKG'},{'lcName':'상해(푸동)', 'lcCode':'PVG'},{'lcName':'상해(홍교)', 'lcCode':'SHA'}
										,{'lcName':'청도', 'lcCode':'TAO'},{'lcName':'북경', 'lcCode':'PEK'},{'lcName':'마카오', 'lcCode':'MFM'}
										,{'lcName':'연길', 'lcCode':'YNJ'},{'lcName':'광주', 'lcCode':'CAN'},{'lcName':'대련', 'lcCode':'DLC'}
										,{'lcName':'심양', 'lcCode':'SHE'},{'lcName':'천진', 'lcCode':'TSN'},{'lcName':'항주', 'lcCode':'HGH'}]}
	,{'country_area':'유럽', 'area_info':[{'lcName':'로마', 'lcCode':'ROM'},{'lcName':'이스탄불', 'lcCode':'IST'},{'lcName':'런던(히드로)', 'lcCode':'LHR'}
										,{'lcName':'런던(게트윅)', 'lcCode':'LGW'},{'lcName':'파리', 'lcCode':'PAR'},{'lcName':'바르셀로나', 'lcCode':'BCN'}
										,{'lcName':'프랑크푸르트', 'lcCode':'FRA'},{'lcName':'자그레브', 'lcCode':'ZAG'},{'lcName':'마드리드', 'lcCode':'MAD'}
										,{'lcName':'프라하', 'lcCode':'PRG'},{'lcName':'취리히', 'lcCode':'ZRH'},{'lcName':'아테네', 'lcCode':'ATH'}
										,{'lcName':'암스테르담', 'lcCode':'AMS'}]}
	,{'country_area':'대양주', 'area_info':[{'lcName':'괌', 'lcCode':'GUM'},{'lcName':'시드니', 'lcCode':'SYD'},{'lcName':'사이판', 'lcCode':'SPN'}
										,{'lcName':'멜버른', 'lcCode':'MEL'},{'lcName':'오클랜드', 'lcCode':'AKL'},{'lcName':'브리즈번', 'lcCode':'BNE'}
										,{'lcName':'팔라우', 'lcCode':'ROR'},{'lcName':'퍼스', 'lcCode':'PER'}]}
	,{'country_area':'동남아', 'area_info':[{'lcName':'방콕', 'lcCode':'BKK'},{'lcName':'대만(송산)', 'lcCode':'TSA'},{'lcName':'대만(타오위안)', 'lcCode':'TPE'}
										,{'lcName':'대만(타이중)', 'lcCode':'RMQ'},{'lcName':'싱가포르', 'lcCode':'SIN'},{'lcName':'마닐라', 'lcCode':'MNL'}
										,{'lcName':'세부', 'lcCode':'CEB'},{'lcName':'푸켓', 'lcCode':'HKT'},{'lcName':'호치민', 'lcCode':'SGN'}
										,{'lcName':'하노이', 'lcCode':'HAN'},{'lcName':'발리', 'lcCode':'DPS'},{'lcName':'자카르타', 'lcCode':'JKT'}
										,{'lcName':'쿠알라룸푸르', 'lcCode':'KUL'},{'lcName':'코타키나발루', 'lcCode':'BKI'},{'lcName':'프놈펜', 'lcCode':'PNH'}
										,{'lcName':'클락', 'lcCode':'CRK'},{'lcName':'씨엠림', 'lcCode':'REP'},{'lcName':'다낭', 'lcCode':'DAD'}]}
	,{'country_area':'서남아', 'area_info':[{'lcName':'몰디브', 'lcCode':'MLE'},{'lcName':'델리', 'lcCode':'DEL'},{'lcName':'카트만두', 'lcCode':'KTM'}
										,{'lcName':'첸나이', 'lcCode':'MAA'}]}
	,{'country_area':'중동', 'area_info':[{'lcName':'두바이', 'lcCode':'DXB'},{'lcName':'아부다비', 'lcCode':'AUH'},{'lcName':'카이로', 'lcCode':'CAI'}
										,{'lcName':'테헤란', 'lcCode':'THR'}]}
	,{'country_area':'아프리카', 'area_info':[{'lcName':'요하네스버그', 'lcCode':'JNB'},{'lcName':'케이프타운', 'lcCode':'CPT'},{'lcName':'튀니지', 'lcCode':'TUN'}
										,{'lcName':'나이로비', 'lcCode':'NBO'}]}
	,{'country_area':'미주', 'area_info':[{'lcName':'뉴욕(JFK)', 'lcCode':'JFK'},{'lcName':'뉴욕(LGA)', 'lcCode':'LGA'},{'lcName':'뉴욕(EWR)', 'lcCode':'NYC'}
										,{'lcName':'로스앤젤레스', 'lcCode':'LAX'},{'lcName':'호놀룰루(하와이)', 'lcCode':'HNL'},{'lcName':'샌프란시스코', 'lcCode':'SFO'}
										,{'lcName':'시애틀', 'lcCode':'SEA'},{'lcName':'시카고', 'lcCode':'CHI'},{'lcName':'라스베이거스', 'lcCode':'LAS'}
										,{'lcName':'워싱턴', 'lcCode':'WAS'},{'lcName':'애틀란타', 'lcCode':'ATL'},{'lcName':'보스톤', 'lcCode':'BOS'}
										,{'lcName':'댈러스(DFW)', 'lcCode':'DFW'},{'lcName':'디트로이트', 'lcCode':'DET'}]}
	,{'country_area':'캐나다', 'area_info':[{'lcName':'벤쿠버', 'lcCode':'YVR'},{'lcName':'토론토', 'lcCode':'YTO'},{'lcName':'캘거리', 'lcCode':'YYC'}
										,{'lcName':'에드먼튼', 'lcCode':'YEG'}]}
	,{'country_area':'중남미', 'area_info':[{'lcName':'상파울루', 'lcCode':'GRU'},{'lcName':'리마', 'lcCode':'LIM'},{'lcName':'칸쿤', 'lcCode':'CUN'}
										,{'lcName':'멕시코시티', 'lcCode':'MEX'}]}
	];