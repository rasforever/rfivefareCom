/**
 * File name : WebAppListender.java
 * Package : com.hanatour.gmair.config
 * Description : Web Application 관련 파일
 *
 * <pre>
 * <변경이력>
 * 수정일			수정자				수정내용
 * ------------------------------------------------------
 * 2017. 10. 10.		han seungTae	최초작성
 * </pre>
 */
package com.hanatour.fareCompare.config;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class name : WebAppListender
 * Description : Web Application 의 Life Cycle Listener
 * @author sthan
 * @date 2017. 10. 10.
 * @version 1.0
 */
@WebListener
public class WebAppListener implements ServletContextListener{
	
	private static Logger logger = LogManager.getLogger("COMMONLOGGER");

	@Override
	public void contextInitialized(ServletContextEvent arg0) {		
		logger.debug("contextInitialized Call");
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		logger.debug("contextDestroyed Call");
	}
	

}