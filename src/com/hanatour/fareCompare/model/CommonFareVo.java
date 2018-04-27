/**
 * File name : CommonFareVo.java
 * Package : com.hanatour.fareCompare.model
 * Description : 
 *
 * <pre>
 * <변경이력>
 * 수정일			수정자				수정내용
 * ------------------------------------------------------
 * 2017. 11. 13.		sthan	최초 작성
 * </pre>
 */
package com.hanatour.fareCompare.model;

/**
 * Class name : CommonFareVo
 * Description :
 * @author sthan
 * @author 2017. 11. 13.
 * @version 1.0
 */
public class CommonFareVo {
	
	private String Master_SiteCd = "";
	private String SiteCd;
	private String Dep_Apo;
	private String Rtn_Apo;
	private String Dep_Dt;
	private String Rtn_Dt;
	private String Fare;
	private String Tax;
	private String DisFare;
	private String Price;
	private String Tasf;
	private String Oil;
	private String Nett = "0";
	private String MkAirNm;
	private String MkAirCd;
	private String Exp_Dt;
	private String Dep_FltNo;
	private String Rtn_FltNo;
	private String ViaCnt;
	private String Promo_Nm;
	private String Fare_nett;
	private String Sch_Num;
	private String Sch_Seq;
	
	/**
	 * @return the master_SiteCd
	 */
	public String getMaster_SiteCd() {
		return Master_SiteCd;
	}
	/**
	 * @param master_SiteCd the master_SiteCd to set
	 */
	public void setMaster_SiteCd(String master_SiteCd) {
		Master_SiteCd = master_SiteCd;
	}
	/**
	 * @return the siteCd
	 */
	public String getSiteCd() {
		return SiteCd;
	}
	/**
	 * @param siteCd the siteCd to set
	 */
	public void setSiteCd(String siteCd) {
		SiteCd = siteCd;
	}
	
	/**
	 * @return the dep_Apo
	 */
	public String getDep_Apo() {
		return Dep_Apo;
	}
	/**
	 * @param dep_Apo the dep_Apo to set
	 */
	public void setDep_Apo(String dep_Apo) {
		Dep_Apo = dep_Apo;
	}
	/**
	 * @return the rtn_Apo
	 */
	public String getRtn_Apo() {
		return Rtn_Apo;
	}
	/**
	 * @param rtn_Apo the rtn_Apo to set
	 */
	public void setRtn_Apo(String rtn_Apo) {
		Rtn_Apo = rtn_Apo;
	}
	
	/**
	 * @return the dep_Dt
	 */
	public String getDep_Dt() {
		return Dep_Dt;
	}
	/**
	 * @param dep_Dt the dep_Dt to set
	 */
	public void setDep_Dt(String dep_Dt) {
		Dep_Dt = dep_Dt;
	}
	/**
	 * @return the rtn_Dt
	 */
	public String getRtn_Dt() {
		return Rtn_Dt;
	}
	/**
	 * @param rtn_Dt the rtn_Dt to set
	 */
	public void setRtn_Dt(String rtn_Dt) {
		Rtn_Dt = rtn_Dt;
	}
	/**
	 * @return the fare
	 */
	public String getFare() {
		return Fare;
	}
	/**
	 * @param fare the fare to set
	 */
	public void setFare(String fare) {
		Fare = fare;
	}
	/**
	 * @return the tax
	 */
	public String getTax() {
		return Tax;
	}
	/**
	 * @param tax the tax to set
	 */
	public void setTax(String tax) {
		Tax = tax;
	}
	/**
	 * @return the disFare
	 */
	public String getDisFare() {
		return DisFare;
	}
	/**
	 * @param disFare the disFare to set
	 */
	public void setDisFare(String disFare) {
		DisFare = disFare;
	}
	/**
	 * @return the price
	 */
	public String getPrice() {
		return Price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(String price) {
		Price = price;
	}
	/**
	 * @return the tasf
	 */
	public String getTasf() {
		return Tasf;
	}
	/**
	 * @param tasf the tasf to set
	 */
	public void setTasf(String tasf) {
		Tasf = tasf;
	}
	/**
	 * @return the oil
	 */
	public String getOil() {
		return Oil;
	}
	/**
	 * @param oil the oil to set
	 */
	public void setOil(String oil) {
		Oil = oil;
	}
	
	/**
	 * @return the nett
	 */
	public String getNett() {
		return Nett;
	}
	/**
	 * @param nett the nett to set
	 */
	public void setNett(String nett) {
		Nett = nett;
	}
	/**
	 * @return the mkAirNm
	 */
	public String getMkAirNm() {
		return MkAirNm;
	}
	/**
	 * @param mkAirNm the mkAirNm to set
	 */
	public void setMkAirNm(String mkAirNm) {
		MkAirNm = mkAirNm;
	}
	/**
	 * @return the mkAirCd
	 */
	public String getMkAirCd() {
		return MkAirCd;
	}
	/**
	 * @param mkAirCd the mkAirCd to set
	 */
	public void setMkAirCd(String mkAirCd) {
		MkAirCd = mkAirCd;
	}
	/**
	 * @return the exp_Dt
	 */
	public String getExp_Dt() {
		return Exp_Dt;
	}
	/**
	 * @param exp_Dt the exp_Dt to set
	 */
	public void setExp_Dt(String exp_Dt) {
		Exp_Dt = exp_Dt;
	}
	/**
	 * @return the dep_FltNo
	 */
	public String getDep_FltNo() {
		return Dep_FltNo;
	}
	/**
	 * @param dep_FltNo the dep_FltNo to set
	 */
	public void setDep_FltNo(String dep_FltNo) {
		Dep_FltNo = dep_FltNo;
	}
	/**
	 * @return the rtn_FltNo
	 */
	public String getRtn_FltNo() {
		return Rtn_FltNo;
	}
	/**
	 * @param rtn_FltNo the rtn_FltNo to set
	 */
	public void setRtn_FltNo(String rtn_FltNo) {
		Rtn_FltNo = rtn_FltNo;
	}
	/**
	 * @return the viaCnt
	 */
	public String getViaCnt() {
		return ViaCnt;
	}
	/**
	 * @param viaCnt the viaCnt to set
	 */
	public void setViaCnt(String viaCnt) {
		ViaCnt = viaCnt;
	}
	/**
	 * @return the promo_Nm
	 */
	public String getPromo_Nm() {
		return Promo_Nm;
	}
	/**
	 * @param promo_Nm the promo_Nm to set
	 */
	public void setPromo_Nm(String promo_Nm) {
		Promo_Nm = promo_Nm;
	}
	
	/**
	 * @return the fare_nett
	 */
	public String getFare_nett() {
		return Fare_nett;
	}
	/**
	 * @param fare_nett the fare_nett to set
	 */
	public void setFare_nett(String fare_nett) {
		Fare_nett = fare_nett;
	}
	
	/**
	 * @return the sch_Num
	 */
	public String getSch_Num() {
		return Sch_Num;
	}
	/**
	 * @param sch_Num the sch_Num to set
	 */
	public void setSch_Num(String sch_Num) {
		Sch_Num = sch_Num;
	}
	
	/**
	 * @return the sch_Seq
	 */
	public String getSch_Seq() {
		return Sch_Seq;
	}
	/**
	 * @param sch_Seq the sch_Seq to set
	 */
	public void setSch_Seq(String sch_Seq) {
		Sch_Seq = sch_Seq;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[Master_SiteCd=" + Master_SiteCd + ", SiteCd="
				+ SiteCd + ", Dep_Apo=" + Dep_Apo + ", Rtn_Apo=" + Rtn_Apo
				+ ", Dep_Dt=" + Dep_Dt + ", Rtn_Dt=" + Rtn_Dt + ", Fare="
				+ Fare + ", Tax=" + Tax + ", DisFare=" + DisFare + ", Price="
				+ Price + ", Tasf=" + Tasf + ", Oil=" + Oil + ", Nett=" + Nett
				+ ", MkAirNm=" + MkAirNm + ", MkAirCd=" + MkAirCd + ", Exp_Dt="
				+ Exp_Dt + ", Dep_FltNo=" + Dep_FltNo + ", Rtn_FltNo="
				+ Rtn_FltNo + ", ViaCnt=" + ViaCnt + ", Promo_Nm=" + Promo_Nm
				+ ", Fare_nett=" + Fare_nett + ", Sch_Num=" + Sch_Num
				+ ", Sch_Seq=" + Sch_Seq + "]";
	}
}
