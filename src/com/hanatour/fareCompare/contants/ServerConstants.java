/**
 * File name : ServerConstants.java
 * Package : com.hanatour.fareCompare.contants
 * Description : 
 *
 * <pre>
 * <변경이력>
 * 수정일			수정자				수정내용
 * ------------------------------------------------------
 * 2017. 10. 12.		hana	최초 작성
 * </pre>
 */
package com.hanatour.fareCompare.contants;

/**
 * Class name : ServerConstants
 * Description :
 * @author sthan
 * @author 2017. 10. 12.
 * @version 1.0
 */
public class ServerConstants {
	/*
	 * 구동 모드 - 운영 
	 */
	public static final int SERVER_MODE_PRODUCTION = 1;
	
	/*
	 * 구동모드 - 베타
	 */
	public static final int SERVER_MODE_BETA = 2;
	
	/*
	 * 구동모드 - 개발
	 */
	public static final int SERVER_MODE_DEVELOPMENT = 3;
	
	/*
	 * 구동모드 - 로컬
	 */
	public static final int SERVER_MODE_LOCAL = 9;
	
	
	/*
	 * 개발서버 IP 대역 
	 */
	public static final String[] testHostPrefixList = {"localhost"};
	
	/*
	 * 로컬 IP 대역 
	 */
	public static final String[] localHostPrefixList = {"localhost", "210.92.252"};
}
