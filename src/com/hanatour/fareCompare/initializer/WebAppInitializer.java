/**
 * File name : WebAppInitializer.java
 * Package : com.hanatour.fareCompare.initializer
 * Description : Spring Dispatcher config 파일
 *
 * <pre>
 * <변경이력>
 * 수정일			수정자				수정내용
 * ------------------------------------------------------
 * 2017. 10. 17.		sthan	최초 작성
 * </pre>
 */
package com.hanatour.fareCompare.initializer;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.apache.commons.lang3.CharEncoding;
import org.apache.logging.log4j.web.Log4jServletContextListener;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.hanatour.fareCompare.config.PersistenceConfig;
import com.hanatour.fareCompare.config.RootConfig;
import com.hanatour.fareCompare.config.WebMvcConfig;

/**
 * Class name : WebAppInitializer
 * Description :  Spring Dispatcher Servlet 초기화 설정
 * @author sthan
 * @date 2017. 10. 17.
 * @version 1.0
 */
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	/**
	 * ContextLoaderListener 가 생성한 애플리케이션 컨텍스트를 설정
	 */
	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { RootConfig.class, PersistenceConfig.class };
	}

	/**
	 * DispatcherServlet이 WebConfig에 정의된 Bean 로딩
	 */
	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] { WebMvcConfig.class  };
	}

	/**
	 * DispatcherServlet이 매핑되기 위한 하나 혹은 여러 개의 패스를 지정
	 */
	@Override
	protected String[] getServletMappings() {
		return new String[]{"/"};  // 기본 서블릿인 "/"에만 매핑하여 모든 요청을 처리함
	}
	
	/**
	 *  Dispatcher Servlet Filter 등록
	 */
	@Override
	protected Filter[] getServletFilters() {
		return new Filter[] {
				getCharacterEncodingFilter()
				};
	}
	
	/**
	 * Character Encoding filter 처리 - UTF-8로 처리
	 * @return CharacterEncodingFilter
	 */
	protected CharacterEncodingFilter getCharacterEncodingFilter() {
		//  UTF-8 Character encoding filter 추가
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding(CharEncoding.UTF_8);
		characterEncodingFilter.setForceEncoding(Boolean.TRUE);
		return characterEncodingFilter;
	}
	
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		servletContext.addListener(new Log4jServletContextListener());
		servletContext.setInitParameter("log4jConfiguration", "/WEB-INF/log4j2.xml");

		super.onStartup(servletContext);
	}
	
	
	/**
	 * Custom 설정
	 */
	@Override
	protected void customizeRegistration(ServletRegistration.Dynamic registration) {
		// 404 에러에 대해서org.springframework.web.servlet.NoHandlerFoundException throw 하도록 설정
		registration.setInitParameter("throwExceptionIfNoHandlerFound", "true");
	}

}
