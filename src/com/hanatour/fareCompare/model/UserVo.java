/**
 * File name : Member.java
 * Package : com.hanatour.fareCompare.model
 * Description : 사용자 정보 VO
 *
 * <pre>
 * <변경이력>
 * 수정일			수정자				수정내용
 * ------------------------------------------------------
 * 2017. 10. 18.		sthan	최초 작성
 * </pre>
 */
package com.hanatour.fareCompare.model;

/**
 * Class name : User
 * Description : 
 * @author hana
 * @author 2017. 10. 18.
 * @version 1.0
 */
public class UserVo {
	private int mem_seq;		//순번
	private String mem_id;		//아이디	
	private String mem_nm;		//이름
	private String mem_pwd; 		//패스워드
	private String reg_date;	//등록일
	private String use_yn;		//사용여부
	private String auth_cd; 	//권한
	private String isLogin;
	
	/**
	 * @return the mem_seq
	 */
	public int getMem_seq() {
		return mem_seq;
	}
	/**
	 * @param mem_seq the mem_seq to set
	 */
	public void setMem_seq(int mem_seq) {
		this.mem_seq = mem_seq;
	}
	/**
	 * @return the mem_id
	 */
	public String getMem_id() {
		return mem_id;
	}
	/**
	 * @param mem_id the mem_id to set
	 */
	public void setMem_id(String mem_id) {
		this.mem_id = mem_id;
	}
	/**
	 * @return the mem_nm
	 */
	public String getMem_nm() {
		return mem_nm;
	}
	/**
	 * @param mem_nm the mem_nm to set
	 */
	public void setMem_nm(String mem_nm) {
		this.mem_nm = mem_nm;
	}
	/**
	 * @return the mem_pwd
	 */
	public String getMem_pwd() {
		return mem_pwd;
	}
	/**
	 * @param mem_pwd the mem_pwd to set
	 */
	public void setMem_pwd(String mem_pwd) {
		this.mem_pwd = mem_pwd;
	}
	/**
	 * @return the reg_date
	 */
	public String getReg_date() {
		return reg_date;
	}
	/**
	 * @param reg_date the reg_date to set
	 */
	public void setReg_date(String reg_date) {
		this.reg_date = reg_date;
	}
	/**
	 * @return the use_yn
	 */
	public String getUse_yn() {
		return use_yn;
	}
	/**
	 * @param use_yn the use_yn to set
	 */
	public void setUse_yn(String use_yn) {
		this.use_yn = use_yn;
	}
	/**
	 * @return the auth_cd
	 */
	public String getAuth_cd() {
		return auth_cd;
	}
	/**
	 * @param auth_cd the auth_cd to set
	 */
	public void setAuth_cd(String auth_cd) {
		this.auth_cd = auth_cd;
	}
	
	
	
	/**
	 * @return the isLogin
	 */
	public String getIsLogin() {
		return isLogin;
	}
	/**
	 * @param isLogin the isLogin to set
	 */
	public void setIsLogin(String isLogin) {
		this.isLogin = isLogin;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MemberVo [mem_seq=" + mem_seq + ", mem_id=" + mem_id
				+ ", mem_nm=" + mem_nm + ", mem_pwd=" + mem_pwd + ", reg_date="
				+ reg_date + ", use_yn=" + use_yn + ", auth_cd=" + auth_cd
				+ "]";
	}
	
}
