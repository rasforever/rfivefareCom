// Scroll Move
function scrollMove(t,h){
	"use strict";
	if(h==undefined) h=0;
	var o = jQuery('body');
	if(navigator.userAgent.toLowerCase().match(/trident/i)){
		o = jQuery('html');
	}
	o.animate({
		scrollTop:jQuery(t).offset().top-h
	},500);
}

jQuery(function($){
	"use strict";
	var gnb=$("#gnb");
	var gnbBtn=$("#mTg a");

	gnbBtn.on("click",function  () {
		$(this).toggleClass('gnb_on');
		$('#gnb').slideToggle('250');
		return false;
	});
	// gnb
	gnb.find('>ul>li>a').click(function(){
		scrollMove($(this).attr('href'));
		$('#gnb').slideUp(250);
		return false;
	});
	
	var agent = navigator.userAgent.toLowerCase();
 
	if ( (navigator.appName == 'Netscape' && navigator.userAgent.search('Trident') != -1) || (agent.indexOf("msie") != -1) ) {
		//select : https://select2.github.io/
		 $('select').select2({
			minimumResultsForSearch: Infinity					
		});
		
		$('.select-H').select2({
			disabled: true
		});	
		
		$('.select-W').select2({
			width: '100%'
		});	
	}else if (agent.indexOf("chrome") != -1) {
		$('.cromleft').css("margin","auto");
		var mmm = checkMobileDevice();
		if(!mmm){
			$('.btn-ch').css("line-height","100%");
		}
	
	}

	

// checkbox
	$('input').iCheck({
		checkboxClass: 'icheckbox_minimal',
		radioClass: 'iradio_minimal'
	});

// datepicker
	$('.input-daterange').find("input").datepicker({
		format: "yyyy-mm-dd",	
		inline: true,
		autoclose: true,
		language : "kr"
	});


// colorpicker : http://www.digitalmagicpro.com/jPicker/
	$('.colorpicker').jPicker({
		window:{
			expandable: true,
			position:
			{
			  x: 'center', // acceptable values "left", "center", "right", "screenCenter", or relative px value
			  y: 'center', // acceptable values "top", "bottom", "center", or relative px value
			},
			effects:
			{
			  type: 'fade', // effect used to show/hide an expandable picker. Acceptable values "slide", "show", "fade"
			  speed:
			  {
				show: 'fast', // duration of "show" effect. Acceptable values are "fast", "slow", or time in ms
				hide: 'fast' // duration of "hide" effect. Acceptable value are "fast", "slow", or time in ms
			  }
    }
		},
		images:{
			clientPath: 'images/'
		}
	});

    $('label').unbind("click").css("cursor","default");


	 $("body").delegate(".input-daterange>input", "keydown", function (e) {
		
		//$(this).datepicker('hide');

		console.log(e.keyCode);
		if(event.shiftKey)
		{
			return false;
		}

        if ((e.keyCode >= 48 && e.keyCode <= 57 )|| (e.keyCode >= 96 && e.keyCode <= 105 )|| e.keyCode == 8 || e.keyCode == 46 || e.keyCode == 9 || (e.keyCode >= 37 && e.keyCode <= 40 )|| e.keyCode == 17 || (event.ctrlKey && e.keyCode == 67) || (event.ctrlKey && e.keyCode == 86) || (event.ctrlKey && e.keyCode == 88) ||( e.keyCode >= 33 && e.keyCode <= 36)) {
            
        }
        else
		{	//���ڰ� �ƴϸ� ������ ����.

			if(e.keyCode != 190 && e.keyCode != 110 )
			{
				return false;
			}
		}
    })

	 $("body").delegate(".input-daterange>input", "keyup", function (e) {
		
		if($(this).val().length == 8)
		{
			var vDate = $(this).val().substring(0,4) +"."+ $(this).val().substring(4,6) +"."+ $(this).val().substring(6,8)
			
			if(isValidDate(vDate))
			{
				$(this).val(vDate);
				$(this).datepicker('update')
			}
		}
    })

	$("body").delegate(".input-daterange>input", "click", function (e) {
		
		$(this).datepicker('show');

    })
	
});


$(window).resize(function(){
	var agent = navigator.userAgent.toLowerCase();
 
	if ( (navigator.appName == 'Netscape' && navigator.userAgent.search('Trident') != -1) || (agent.indexOf("msie") != -1) ) {
		var wsize = $('body').width();
		if(wsize < 769){
			 $('select').select2({
			 	width:'100%'				
			 });
		}
	}
	
});

function checkMobileDevice() {
        var mobileKeyWords = new Array('Android', 'iPhone', 'iPod', 'BlackBerry', 'Windows CE', 'SAMSUNG', 'LG', 'MOT', 'SonyEricsson');
        for (var info in mobileKeyWords) {
            if (navigator.userAgent.match(mobileKeyWords[info]) != null) {
                return true;
            }
        }
        return false;
}


function isValidDate(str){
	if(str=="" || str==null){return false;}				
	var m = str.match(/(\d{4}).(\d{2}).(\d{2})/);
	
	if( m === null || typeof m !== 'object'){return false;}				
	
	if (typeof m !== 'object' && m !== null && m.size!==3){return false;}
				
	var ret = true; //RETURN VALUE						
	var thisYear = new Date().getFullYear(); //YEAR NOW
	var minYear = 1999; //MIN YEAR
	
	if( (m[1].length < 4) || m[1] < minYear || m[1] > thisYear){ret = false;}
	  // MONTH CHECK          
    if( (m[2].length < 2) || m[2] < 1 || m[2] > 12) {ret = false;}
    // DAY CHECK
    if( (m[3].length < 2) || m[3] < 1 || m[3] > 31) {ret = false;}

	return ret;			
}

/*
 * @author sthan 
 * 
 */
FLoading = {
	show : function(){//로딩시작
		if( $("#pageLoading").length == 0 ){
			$('<div id="pageLoading" class="pageLoading"></div>').appendTo('body');
		}else{
			$("#pageLoading").show();
		}
	},
	hide : function(){//로딩종료
		$("#pageLoading").hide();
	}
}

//콤마표현
function numberWithCommas(x){
	var cStr ="";
	if( x == "0"){
		cStr = "-";
	}else if(x == "0.0"){
		cStr = x;
	}else{
		x=String(x);
		x=x.replace(/,/g,'');
		cStr = x.toString().replace(/\B(?=(\d{3})+(?!\d))/g,","); 
	}
	return cStr;
}
//날짜표현 
function dateStrFormat(formatType,dateStr){
	if( dateStr == null || dateStr == undefined || dateStr == ''){
		dateStr = dateObjToDateStr();
	}
	/*숫자 제외한 문자 치환 -20150420 hst*/
	dateStr = dateStr.replace(/[^0-9]/g,'');
	
	var yyyy = dateStr.substring(0, 4);
	var yy = dateStr.substring(2, 4);
	var month = dateStr.substring(4, 6);
	var date = dateStr.substring(6, 8);
	var returnVal = formatType.replace(/yyyy/,yyyy).replace(/yy/,yy).replace(/mm/,month).replace(/dd/,date);
	return returnVal;
}

//set cookie
function setCookie(name, value, expireDays){
	var todayDate = new Date();
	todayDate.setDate(todayDate.getDate() + expireDays);
	document.cookie = name + "=" + escape(value) + ";path=/;expires=" + todayDate.toGMTString() +";";
}

// get cookie
function getCookieValue(cookieName){
	var cookieValue = null;
	if(document.cookie.length > 0){
		var offset = document.cookie.indexOf(cookieName +"=");
		if(offset != -1) {
			offset += cookieName.length;
			end = document.cookie.indexOf(";", offset);
			if (end == -1)	end = document.cookie.length;
			cookieValue = unescape(document.cookie.substring(offset+1, end));
		}
	}
		
	if(cookieValue == null)
		return "";
	
	return cookieValue;
}

//cookie key to value  getCookieNameToKey("cookiename", "memberid") ) 
function getCookieKeyToValue(cookieName, cookieKey){
	try{
		var cookieValue = getCookieValue(cookieName);
		var arCookie = cookieValue.split("&");
		var returnString = "";
		for(var i=0; i < arCookie.length; i++){
			var str = arCookie[i];			
			if(str.indexOf(""+cookieKey) > -1 ){
				returnString = str.substring( str.indexOf("=") +1, str.length);
				returnString = returnString.replace("\"", "");							
				break;
			}
		}
		return returnString;
	}catch(e){
		return null;		
	}
}

// remove cookie
function removeCookie(name){
	var expireDate = new Date();
	expireDate.setDate(expireDate.getDate()-1);
	
	document.cookie = name+"=;expires=" + expireDate.toGMTString() + ";path=/";
	
	location.href="/";
}
