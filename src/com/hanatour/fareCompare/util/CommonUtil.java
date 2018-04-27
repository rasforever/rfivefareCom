package com.hanatour.fareCompare.util;
     
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;


public class CommonUtil { 
    /**
     * The empty String <code>""</code>.
     * @since 2.0
     */
    public static final String EMPTY = "";
    
    /**
     * 시스템 날짜를 구한다.(초까지)
     * @return String (ex: 2005.04.11 18:08:11)
     */
    public static String getSysDate() {
        
        Calendar today = Calendar.getInstance();
        int year = today.get(Calendar.YEAR);
        int mon = today.get(Calendar.MONTH) + 1;
        int day = today.get(Calendar.DAY_OF_MONTH);
        
        String str = "";
        
        str += year + ".";
        
        if (mon < 10)
            str += "0";
        str += mon + ".";
        
        if (day < 10)
            str += "0";
        str += day;
        
        str += " " + getNowTime();
        
        return str;
    }
    
    /**
     * 현재 월일시분초 반환(ex:0411180500)
     * @return String
     */
    public static String getMMDDhhmmss() {
        
        Calendar today = Calendar.getInstance();
        int mon = today.get(Calendar.MONTH) + 1;
        int day = today.get(Calendar.DAY_OF_MONTH);
        int hour = today.get(Calendar.HOUR_OF_DAY);
        int min = today.get(Calendar.MINUTE);
        int sec = today.get(Calendar.SECOND);
        
        String str = "";
        
        if (mon < 10)
            str += "0";
        str += mon;
        
        if (day < 10)
            str += "0";
        str += day;
        
        if (hour < 10)
            str += "0";
        str += hour;
        
        if (min < 10)
            str += "0";
        str += min;
        
        if (sec < 10)
            str += "0";
        str += sec;
        
        return str;
    }
    
    /**
     * 현재 년월일시분 반환
     * @return String (ex: 200504111808)
     */
    public static String getYYYYMMDDHHMM() {
        
        Calendar today = Calendar.getInstance();
        int year = today.get(Calendar.YEAR);
        int mon = today.get(Calendar.MONTH) + 1;
        int day = today.get(Calendar.DAY_OF_MONTH);
        int hour = today.get(Calendar.HOUR_OF_DAY);
        int min = today.get(Calendar.MINUTE);
        
        String str = "";
        
        str += year;
        
        if (mon < 10)
            str += "0";
        str += mon;
        
        if (day < 10)
            str += "0";
        str += day;
        
        if (hour < 10)
            str += "0";
        str += hour;
        
        if (min < 10)
            str += "0";
        str += min;
        
        return str;
    }
    
    /**
     * 현재 년월일 반환
     * @return String (ex: 20050411)
     */
    public static String getYYYYMMDD() {
        
        Calendar today = Calendar.getInstance();
        int year = today.get(Calendar.YEAR);
        int mon = today.get(Calendar.MONTH) + 1;
        int day = today.get(Calendar.DAY_OF_MONTH);
        
        String str = "";
        
        str += year;
        
        if (mon < 10)
            str += "0";
        str += mon;
        
        if (day < 10)
            str += "0";
        str += day;
        
        return str;
    }
    
    
    /**
     * 현재 년월일 반환 ('-' 포함)
     * @return String (ex: 2005-04-11)
     */
    public static String getYYYYMMDD_dash() {
        
        Calendar today = Calendar.getInstance();
        int year = today.get(Calendar.YEAR);
        int mon = today.get(Calendar.MONTH) + 1;
        int day = today.get(Calendar.DAY_OF_MONTH);
        
        String str = "";
        
        str += year;
        str += "-";
        if (mon < 10)
            str += "0";
        str += mon;
        str += "-";
        
        if (day < 10)
            str += "0";
        str += day;
        
        return str;
    }
    
    
    
    /**
     * Calendar 객체를 받아 현재 날짜를 YYYYMMDD 형식으로 반환한다.
     * @return String (ex: 20050411)
     */
    public static String getYYYYMMDD(Calendar _c) {
        
        int year = _c.get(Calendar.YEAR);
        int mon = _c.get(Calendar.MONTH) + 1;
        int day = _c.get(Calendar.DAY_OF_MONTH);
        
        String str = "";
        
        str += year;
        
        if (mon < 10)
            str += "0";
        str += mon;
        
        if (day < 10)
            str += "0";
        str += day;
        
        return str;
    }
    
    
    /**
     * Calendar 객체를 받아 현재 날짜를 YYYY-MM-DD HH:mm:ss 형식으로 반환한다.
     * @return String (ex: 2005-04-11 23:51:29)
     */
    public static String getYYYYMMDDHHMMss() {
        
        Calendar today = Calendar.getInstance();
        int year = today.get(Calendar.YEAR);
        int mon = today.get(Calendar.MONTH) + 1;
        int day = today.get(Calendar.DAY_OF_MONTH);
        int hour = today.get(Calendar.HOUR_OF_DAY);
        int min = today.get(Calendar.MINUTE);
        int sec = today.get(Calendar.SECOND);
        
        String str = "";
        
        str += year;
        str += "-";
        
        if (mon < 10)
            str += "0";
        str += mon;
        str += "-";
        
        if (day < 10)
            str += "0";
        str += day;
        str += " ";
        
        if (hour < 10)
            str += "0";
        str += hour;
        str += ":";
        
        if (min < 10)
            str += "0";
        str += min;
        str += ":";
        
        if (sec < 10)
            str += "0";
        str += sec;
        
        return str;
    }

 
    /**
     * 현재 시각 반환
     * @return String(ex : 18:05:00)
     */
    public static String getNowTime() {
        
        Calendar today = Calendar.getInstance();
        int hour = today.get(Calendar.HOUR_OF_DAY);
        int min = today.get(Calendar.MINUTE);
        int sec = today.get(Calendar.SECOND);
        
        String str = "";
        
        if (hour < 10)
            str += "0";
        str += hour;
        
        str += ":";
        
        if (min < 10)
            str += "0";
        str += min;
        
        str += ":";
        
        if (sec < 10)
            str += "0";
        str += sec;
        
        return str;
    }
    
    /**
     * 현재시간 반환(시분초밀리세컨드)
     * @return String (ex : 180811164 )
     */
    public static String getHHmmssmilli() {
        
        Calendar today = Calendar.getInstance();
        int hour = today.get(Calendar.HOUR_OF_DAY);
        int min = today.get(Calendar.MINUTE);
        int sec = today.get(Calendar.SECOND);
        int millisec = today.get(Calendar.MILLISECOND);
        
        String str = "";
        
        if (hour < 10)
            str += "0";
        str += hour;
        
        if (min < 10)
            str += "0";
        str += min;
        
        if (sec < 10)
            str += "0";
        str += sec;
        
        if (millisec < 10)
            str += "00";
        if (millisec < 100 && millisec >= 10)
            str += "0";
        
        str += millisec;
        
        return str;
    }
    /**
     * 스트링 자르기
     * 지정한 정수의 개수 만큼 빈칸(" ")을 스트링을 구한다.
     * 절단된 String의 바이트 수가 자를 바이트 개수를 넘지 않도록 한다.
     *
     * @param str 원본 String
     * @param length 자를 바이트 개수
     * @return type 절단된 String
     */
     public static String cut_String(String str, int length ,String type) {
         byte[] bytes = str.getBytes();
         int len = bytes.length;
         int counter = 0;

         if (length >= len) {
             StringBuffer sb = new StringBuffer();
             sb.append(str);
             for(int i=0;i<length-len;i++)
             {
                 sb.append(' ');
             }
             return sb.toString();
         }

         for (int i = length - 1; i >= 0; i--)
         {
             if (((int)bytes[i] & 0x80) != 0)
             counter++;
         }

         String f_str = null;

         if(type.equals("+")) {
             f_str = new String(bytes, 0, length + (counter % 2));
         } else if(type.equals("-")) {
             f_str = new String(bytes, 0, length - (counter % 2));
         } else {
             f_str = new String(bytes, 0, length - (counter % 2));
         }

         return f_str;
     }
     
     /**
      *  두 스트링의 날짜값의 차이 비교
      * @param begin 시작일
      * @param end 종료일
      * @return 종료일 - 시작일 =  시작일이 종료일보다 크면 - 리턴 
      *  로직은 리턴이 0 보다 크면 태우면 된다. 
      * @throws ParseException
      */
     public static  long diffOfMinutes(String begin, String end){
    	 SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");

    	 Date beginDate = null;
		try {
			beginDate = formatter.parse(begin);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	 Date endDate = null;
		try {
			endDate = formatter.parse(end);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	 return (endDate.getTime() - beginDate.getTime()) / (60 * 1000);

     }
     
     /**
      * 넘어온 날짜에서 원하는 날짜만큼 빼거나 더한다
      * @param calc_value = 인트값. 더하거나 뺄 날짜
      * @return
      */
     public static String CALC_date(String yyyyMMdd , int calc_value){
		 String strDate = "";
		 try{
			 SimpleDateFormat dt = new SimpleDateFormat("yyyyMMdd"); 
			 Date date = dt.parse(yyyyMMdd); 
			 
			 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			 
			 // 날짜 더하기
		     Calendar cal = Calendar.getInstance();
		     cal.setTime(date);
		     cal.add(Calendar.DATE, calc_value);  
		     
		     strDate = formatter.format(cal.getTime());
		 }catch(Exception e){
			 //System.out.println(e);
			 strDate = "";
		 }
		 
		 return strDate;
     }
     
     /**
      * 월까지만 인자로 받고 달을 더하고 뺀다
      * @param pDate 해당 월까지만 인자로 받는다. ex) 200901  
      * @param pAmount 계산할 숫자값. ex) -1  , 6 등 
      * @return 월까지 리턴 ex)200812
      */
     public String calc_month ( String pDate , int pAmount ) {
     	String temp = "";
     	try{
     		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMM");
     		
     		java.util.Date dt = formatter.parse(pDate);
     		
     		java.util.Calendar calTemp = java.util.Calendar.getInstance();
 	    	
 			calTemp.setTime(dt);
 			calTemp.add( java.util.Calendar.MONTH , pAmount);
 	
 			java.util.Date date = calTemp.getTime();
 			
 	    	temp =  formatter.format(date);
 	    	
     	}catch(Exception e){
     		e.printStackTrace();
     	}
     	return temp; 
     }
    
     /**
      *  인자의 날짜값에 따른 요일 리턴.
      * @param param = 날짜 값. ex) 20090811  , 2008-08-11
      * @param pattern = 날짜형태 ex)yyyyMMdd , yyyy-MM-dd
      * @return String 요일명
      */
     public static String getWeekName( String param  , String pattern ){
    	 String [] week = {"","일","월","화","수","목","금","토"};
    	 java.util.Calendar cal = null;
    	 java.util.Date dt = null;
    	 
    	 try{
    		 
    		 java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(pattern );
    			
    		 dt = formatter.parse( param );
    			
    		 cal = java.util.Calendar.getInstance();
    		 
    		 cal.setTime(dt);
    				
    		 return week[cal.get(java.util.Calendar.DAY_OF_WEEK)]; 
    		 
    	 }catch(Exception e){
    		 e.printStackTrace();
    		 System.out.println("에러");
    	 }
    	 
    	 return "";
     }
     
     /**
      * 세자리마다 콤마 표시. 
      * 소수점있는 데이터는 알아서 소수점 제외하고 앞자리 변환하고 소수점 붙여서 리턴
      * 
      * - 표시 있을경우 콤마표시할때 -,132 이렇게 리턴해서 
      * 그부분 수정함. (2010-05-14) Raul
      * @param figure
      * @return
      */
     public static String getComma_Conv( String value ){
    	 return getComma_Conv( value , "0" );
     }
     
     /**
      * 세자리마다 콤마 표시. 
      * 소수점있는 데이터는 알아서 소수점 제외하고 앞자리 변환하고 소수점 붙여서 리턴
      * 
      * - 표시 있을경우 콤마표시할때 -,132 이렇게 리턴해서 
      * 그부분 수정함. (2010-05-14) Raul
      * @param figure
      * @return
      */
     public static String getComma_Conv( String value , String defaultValue ){
    	 String str = "";
    	 String figure = "";
    	 
    	 boolean isMinus = false;	//value 값이 마이너스값인지 여부 체크 변수 추가
    	 
    	 if( value.lastIndexOf(".") == -1 ){	//소수점 이 있으면
    		 figure = value;
    	 }else {
    		 figure = value.substring( 0 , value.lastIndexOf(".") );
    	 }
    	 
    	 if( figure.lastIndexOf("-") != -1 ){	//-(마이너스) 가 있으면
    		 figure = figure.replaceAll("-", "");
    		 isMinus = true;
    	 }
    	 
    	 try{
	    	 int i = 0; 
	    	 int j = 0; 
	    	 int mok = 0; 
	    	 int div = 0; 
	    	 int size = figure.length(); 
	    	 
	         
	    	 if ( figure  == null || figure  == "" ) 
	    		 return defaultValue; 
	
	    	 if ( size <= 3 )
	    		 return value;	//변환하려고 하는데이터 길이가 3보다 작다. 최초 넘어온 값 그대로 리턴해준다.
	    	 
	    		  
	
	    	 div = size / 3; 
	    	 mok = size % 3; 
	    	 str = figure.substring( 0, mok ); 
	
	    	 for (i = 0, j = 1; i < div; i++ ) 
	    	 { 
	    		 if ( i == 0 && mok == 0 ) 
	    			 str = figure.substring( (i * 3) + mok, (i * 3) + mok + 3 );  
	    		 else 
	    			 str = str + "," + figure.substring( (i * 3) + mok, (i * 3) + mok + 3 );  
	    	 }
	    	 
	    	 if( value.lastIndexOf(".") != -1 ){
	    		 str += value.substring( value.lastIndexOf(".") );
	    	 }
	    	 
	    	 if( isMinus ){
	    		 str = "-" + str;
	    	 }
	    	 
    	 }catch(Exception e){
    		 str = value;
    		 System.out.println("콤마 변환 오류 CommonUtil Line 2694");
    	 }
    	 return str;
    	 
     }
     
	public static String getIP(){
		String result = "";
		try{
			String localhost = InetAddress.getLocalHost().toString( );
			
	        StringTokenizer st = new StringTokenizer( localhost , "/" );
	        String host = st.nextToken( );
	        String ip = st.nextToken( );
	        result = ip;
	        
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static String getIP2(){
		String result = "";
		try {
			boolean isLoopBack = true;
			Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces();
			while (en.hasMoreElements()) {
				NetworkInterface ni = en.nextElement();
				if (ni.isLoopback())
					continue;

				Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					InetAddress ia = inetAddresses.nextElement();
					if (ia.getHostAddress() != null
							&& ia.getHostAddress().indexOf(".") != -1) {
						String ip = ia.getHostAddress();
						
						isLoopBack = false;
						break;
					}
				}
				if (!isLoopBack)
					break;
			}
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
        return result;
	}
	
	public static String getLocalServerIp()
	{
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress())
                    {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        }catch (SocketException ex) {
        	
        }
        return null;
	}
	
	/*
	 * SHA-256 (Sechre Hash Standard) 암호화  
	 */
	public static String EncryptSHA256(String str){
		String SHA = ""; 
		try{
			MessageDigest sh = MessageDigest.getInstance("SHA-256"); 
			sh.update(str.getBytes()); 
			byte byteData[] = sh.digest();
			StringBuffer sb = new StringBuffer(); 
			for(int i = 0 ; i < byteData.length ; i++){
				sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
			}
			SHA = sb.toString();
			
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace(); 
			SHA = null; 
		}
		return SHA;
	}

	
	public static String getToken(HttpServletRequest request){
		HttpSession session = request.getSession();
		return (String)session.getAttribute("Csrp_Token");
	}
	
	/**
	 * 해당 년,월 , 주 , 요일은 몇일 인지를 리턴한다.
     * 2017년 12월 3주차 월요일은 몇일인지 리턴
     * (2017-12-28) hst. 
     * @param 월, 주, 요일
     * @return String ex) 2017-12-28
     */
	public static String getDayOfWeek(String month, String week, String dayOfWeek){
		String strDate = "";
		try{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			
	 		Calendar c = Calendar.getInstance(); 		
	 		c.setTime(new Date());
	 		
	 		c.add(Calendar.MONTH, Integer.parseInt(month)); //월을 더한다
	 		int schMonth = c.get(c.MONTH)+1;	// 조회 월 설정
	 		
	 		c.set(Calendar.WEEK_OF_MONTH, 1); 	// 해당 월의 첫주차를 설정 
		    c.set(Calendar.DAY_OF_WEEK, 5);		// 조회 월의 첫째주의 목요일 설정
		    
		    int tmpMonth = c.get(c.MONTH)+1; // 조회월의 첫째주 목요일이 이전 달이면 조회 주는 +1 을 한다.
		    
		    if( schMonth > tmpMonth  ){
		    	c.setTime(new Date());
		 		c.add(Calendar.MONTH, Integer.parseInt(month)); //월을 더한다
			    c.set(Calendar.WEEK_OF_MONTH, Integer.parseInt(week)+1); // 주차를 더한다 
			    c.set(Calendar.DAY_OF_WEEK, Integer.parseInt(dayOfWeek));
		    }else{
		    	c.setTime(new Date());
		 		c.add(Calendar.MONTH, Integer.parseInt(month)); //월을 더한다
			    c.set(Calendar.WEEK_OF_MONTH, Integer.parseInt(week)); // 주차를 더한다 
			    c.set(Calendar.DAY_OF_WEEK, Integer.parseInt(dayOfWeek));
		    }
		    
		    strDate = formatter.format(c.getTime());
		}catch(Exception ex){
			 System.out.println ( "exception ===="+ex);
			strDate = "";
		}
		return strDate;
 	}
	
}//end class




