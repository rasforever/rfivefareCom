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



import com.hanatour.fareCompare.model.FareInfoVo;
import com.hanatour.fareCompare.model.MemberVo;

@Repository("fareInfo")
public class FareInfoDao{
	
	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;
	
	private static Logger logger = LogManager.getLogger("COMMONLOGGER");
	
	public String getDual() throws Exception{
        return sqlSessionTemplate.selectOne("fareInfo.getDual");
    }
	public List<HashMap<String,Object>> getFareSearchInfo(Map dataInfo) throws Exception{
		return sqlSessionTemplate.selectList("fareInfo.getFareSearchInfo", dataInfo);
	}
	public HashMap<String,Object> getFareUpdateInfo(Map dataInfo) throws Exception{
		return sqlSessionTemplate.selectOne("fareInfo.getFareUpdateInfo", dataInfo);
	}
	
	public void fareInfoInsert(Map dataInfo) {
		sqlSessionTemplate.insert("fareInfo.fareInfoInsert", dataInfo);
	}
	public void fareInfoUpdate(Map dataInfo) {
		sqlSessionTemplate.update("fareInfo.fareInfoUpdate", dataInfo);
	}
	public void fareInfoDelete(Map dataInfo) {
		sqlSessionTemplate.update("fareInfo.fareInfoDelete", dataInfo);
	}
	
	public List<FareInfoVo> getSelectSearchInfo(Map dataInfo) throws Exception{
		return sqlSessionTemplate.selectList("fareInfo.getSelectSearchInfo", dataInfo);
	}
	
	public String getSearchInfoCnt() throws Exception{
		return sqlSessionTemplate.selectOne("fareInfo.getSearchInfoCnt");
	};
	
	
}


