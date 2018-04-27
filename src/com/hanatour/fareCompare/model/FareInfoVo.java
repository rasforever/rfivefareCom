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
 * Class name : Member
 * Description : 
 * @author hana
 * @author 2017. 10. 18.
 * @version 1.0
 */
public class FareInfoVo {
	private int seq;
	private String depApo;
	private String rtnApo;
	private String airCd;
	private String tripType;
	private String depFixYn;
	private String chgMonth;
	private String chgWeek;
	private String chgDay;
	private String depDt;
	private String chgRtnDay;
	private String alwaysYn;
	private String schEndDt;
	private String useYn;
	
	private int memSeq;
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getDepApo() {
		return depApo;
	}
	public void setDepApo(String depApo) {
		this.depApo = depApo;
	}
	public String getRtnApo() {
		return rtnApo;
	}
	public void setRtnApo(String rtnApo) {
		this.rtnApo = rtnApo;
	}
	public String getAirCd() {
		return airCd;
	}
	public void setAirCd(String airCd) {
		this.airCd = airCd;
	}
	public String getTripType() {
		return tripType;
	}
	public void setTripType(String tripType) {
		this.tripType = tripType;
	}
	public String getDepFixYn() {
		return depFixYn;
	}
	public void setDepFixYn(String depFixYn) {
		this.depFixYn = depFixYn;
	}
	public String getChgMonth() {
		return chgMonth;
	}
	public void setChgMonth(String chgMonth) {
		this.chgMonth = chgMonth;
	}
	public String getChgWeek() {
		return chgWeek;
	}
	public void setChgWeek(String chgWeek) {
		this.chgWeek = chgWeek;
	}
	public String getChgDay() {
		return chgDay;
	}
	public void setChgDay(String chgDay) {
		this.chgDay = chgDay;
	}
	public String getDepDt() {
		return depDt;
	}
	public void setDepDt(String depDt) {
		this.depDt = depDt;
	}
	public String getChgRtnDay() {
		return chgRtnDay;
	}
	public void setChgRtnDay(String chgRtnDay) {
		this.chgRtnDay = chgRtnDay;
	}
	public String getAlwaysYn() {
		return alwaysYn;
	}
	public void setAlwaysYn(String alwaysYn) {
		this.alwaysYn = alwaysYn;
	}
	public String getSchEndDt() {
		return schEndDt;
	}
	public void setSchEndDt(String schEndDt) {
		this.schEndDt = schEndDt;
	}
	public int getMemSeq() {
		return memSeq;
	}
	public void setMemSeq(int memSeq) {
		this.memSeq = memSeq;
	}
	/**
	 * @return the useYn
	 */
	public String getUseYn() {
		return useYn;
	}
	/**
	 * @param useYn the useYn to set
	 */
	public void setUseYn(String useYn) {
		this.useYn = useYn;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FareInfoVo [seq=" + seq + ", depApo=" + depApo + ", rtnApo="
				+ rtnApo + ", airCd=" + airCd + ", tripType=" + tripType
				+ ", depFixYn=" + depFixYn + ", chgMonth=" + chgMonth
				+ ", chgWeek=" + chgWeek + ", chgDay=" + chgDay + ", depDt="
				+ depDt + ", chgRtnDay=" + chgRtnDay + ", alwaysYn=" + alwaysYn
				+ ", schEndDt=" + schEndDt + ", useYn=" + useYn + ", memSeq="
				+ memSeq + "]";
	}
	
}
