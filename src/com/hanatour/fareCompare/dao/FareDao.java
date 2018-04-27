/**
 * File name : FareDao.java
 * Package : com.hanatour.fareCompare.dao
 * Description : 운임 관련 DAO
 *
 * <pre>
 * <변경이력>
 * 수정일			수정자				수정내용
 * ------------------------------------------------------
 * 2017. 10. 18.		sthan	최초 작성
 * </pre>
 */
/**
 * Class name : MemberDao
 * Description :
 * @author sthan
 * @author 2017. 10. 18.
 * @version 1.0
 */
package com.hanatour.fareCompare.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.dao.DataAccessException;

import com.hanatour.fareCompare.model.CommonFareVo;

@Repository("FareDao")
public class FareDao{
	
	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;
	
	private static Logger logger = LogManager.getLogger("COMMONLOGGER");
	
	/*
	 * 운임 정보 저장
	 */
	public int insertFare(CommonFareVo fare) throws Exception{
		return sqlSessionTemplate.insert("Fare.insertFare", fare);
	};
	
	/*
	 * 운임그룹 번호 NEXT Value 조회
	 */
	public String selectFare_nvl_sch_num(Map dataInfo) throws Exception{
		return sqlSessionTemplate.selectOne("Fare.selectFare_nvl_sch_num", dataInfo);
	};
	
	/*
	 * 운임그룹 번호 MAX 조회
	 */
	public String selectFare_max_sch_num(Map dataInfo) throws Exception{
		return sqlSessionTemplate.selectOne("Fare.selectFare_max_sch_num", dataInfo);
	};
	
	
	
	/*
	 * 운임 정보 조회
	 */
	public List<HashMap<String,Object>> selectFare(HashMap<String,Object> schOption) throws Exception{
		return sqlSessionTemplate.selectList("Fare.selectFare", schOption);
	};
	
	/*
	 * 운임수집 룰 별 운임 정보 조회
	 */
	public List<HashMap<String,Object>> selectCollectFare(HashMap<String,Object> schOption) throws Exception{
		return sqlSessionTemplate.selectList("Fare.selectCollectFare2", schOption);
	};
	
	/*
	 * 운임수집 룰 별 항공사 정보 조회
	 */
	public List<HashMap<String,Object>> selectAirList(HashMap<String,Object> schOption) throws Exception{
		return sqlSessionTemplate.selectList("Fare.selectAirList", schOption);
	};
	
	/*
	 * 운임수집 룰 별 카드프모션 정보 조회
	 */
	public List<HashMap<String,Object>> selectPromotionList(HashMap<String,Object> schOption) throws Exception{
		return sqlSessionTemplate.selectList("Fare.selectPromotionList", schOption);
	};

}


