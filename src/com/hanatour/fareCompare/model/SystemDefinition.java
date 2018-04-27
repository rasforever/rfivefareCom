/**
 * File name : SystemDefinition.java
 * Package : com.hanatour.fareCompare.model.advice
 * Description : 
 *
 * <pre>
 * <변경이력>
 * 수정일			수정자				수정내용
 * ------------------------------------------------------
 * 2017. 10. 16.		hana	최초 작성
 * </pre>
 */
package com.hanatour.fareCompare.model;

/**
 * Class name : SystemDefinition
 * Description : 시스템 전역 변수 저장 모델
 * @author sthan
 * @author 2017. 10. 16.
 * @version 1.0
 */
public class SystemDefinition {
	/*
	 * 서버 구동 모드
	 */
	private int serverMode;
	
	/*
	 * 호스트 네임
	 */
	private String hostName;
	
	/*
	 * REST API URL
	 */
	private String restApiUrl;

	/**
	 * @return the serverMode
	 */
	public int getServerMode() {
		return serverMode;
	}

	/**
	 * @param serverMode the serverMode to set
	 */
	public void setServerMode(int serverMode) {
		this.serverMode = serverMode;
	}

	/**
	 * @return the hostName
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * @param hostName the hostName to set
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	/**
	 * @return the restApiUrl
	 */
	public String getRestApiUrl() {
		return restApiUrl;
	}

	/**
	 * @param restApiUrl the restApiUrl to set
	 */
	public void setRestApiUrl(String restApiUrl) {
		this.restApiUrl = restApiUrl;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SystemDefinition [serverMode=" + serverMode + ", hostName="
				+ hostName + ", restApiUrl=" + restApiUrl + "]";
	}
	
	
}
