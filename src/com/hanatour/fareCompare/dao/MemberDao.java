/**
 * File name : MemberDao.java
 * Package : com.hanatour.fareCompare.dao
 * Description : 사용자 로그인 관련 DAO  
 *
 * <pre>
 * <변경이력>
 * 수정일			수정자				수정내용
 * ------------------------------------------------------
 * 2017. 10. 18.		hana	최초 작성
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
import com.hanatour.fareCompare.model.MemberVo;
import com.hanatour.fareCompare.model.UserVo;

@Repository("MemberDao")
public class MemberDao{
	
	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;
	
	private static Logger logger = LogManager.getLogger("COMMONLOGGER");
	
	public String getDual() throws Exception{
        return sqlSessionTemplate.selectOne("member.getDual");
    }
	public int getLoingDataCount(Map dataInfo) throws Exception{
		return sqlSessionTemplate.selectOne("member.getLoingDataCount", dataInfo);
	}
	
	public MemberVo getLoingData(Map dataInfo) throws Exception{
		return sqlSessionTemplate.selectOne("member.getLoingData", dataInfo);
	}
	
	public int getLoingCheck(Map dataInfo) throws Exception{
		return sqlSessionTemplate.selectOne("member.getLoingCheck", dataInfo);
	}
	
	/*
	 * 사용자 정보 저장
	 */
	public int insertUserInfo(UserVo usr) throws Exception{
		return sqlSessionTemplate.insert("member.insertUserInfo", usr);
	};
}


