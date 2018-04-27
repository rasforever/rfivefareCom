/**
 * File name : WebMvcConfig.java
 * Package : com.hanatour.fareCompare.config
 * Description : Web MVC Config 파일
 *
 * <pre>
 * <변경이력>
 * 수정일			수정자				수정내용
 * ------------------------------------------------------
 * 2017. 10. 10.		han seungTae	최초작성
 * </pre>
 */
package com.hanatour.fareCompare.config;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.hanatour.fareCompare.contants.ApiConstants;
import com.hanatour.fareCompare.contants.ServerConstants;
import com.hanatour.fareCompare.model.SystemDefinition;
/*
import com.hanatour.gmair.contants.ApiConstants;
import com.hanatour.gmair.contants.GlobalConstants;
import com.hanatour.gmair.contants.ServerConstants;

import com.hanatour.gmair.model.advice.SystemDefinition;
*/
/**
 * Class name : WebMvcConfig
 * Description : Spring MVC 관련 설정 처리
 * @author sthan
 * @date 2017. 10. 10.
 * @version 1.0
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages="com.hanatour.fareCompare")
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	private static Logger logger = LogManager.getLogger("COMMONLOGGER");
	
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		// enable()를 호출하여 DispatcherServlet이 정적 리소스들에 대한 요청을  ServletContainer의 Default Servlet에게 전달을 요청함
		configurer.enable();
	}
	
	
	
	/**
	 * 정적 Resource Handler 설정
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry){
		registry.addResourceHandler("/css/**")
				.addResourceLocations("/css/")
				.setCachePeriod((int)TimeUnit.DAYS.toSeconds(365))
				.resourceChain(Boolean.FALSE)
				.addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));

		registry.addResourceHandler("/js/**")
				.addResourceLocations("/js/")
				.setCachePeriod((int)TimeUnit.DAYS.toSeconds(365))
				.resourceChain(Boolean.FALSE)
				.addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));
		
		registry.addResourceHandler("/images/**")
				.addResourceLocations("/images/")
				.setCachePeriod((int)TimeUnit.DAYS.toSeconds(365))
				.resourceChain(Boolean.FALSE)
				.addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));
		
		registry.addResourceHandler("/imㅎ/**")
				.addResourceLocations("/imㅎ/")
				.setCachePeriod((int)TimeUnit.DAYS.toSeconds(365))
				.resourceChain(Boolean.FALSE)
				.addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));
		
	}

	/**
	 * ViewResolver 등록
	 * @return ViewResolver
	 */
	@Bean
	public InternalResourceViewResolver internalResourceViewResolver() { // View Resolver
		InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
		internalResourceViewResolver.setPrefix("/WEB-INF/view/");
		internalResourceViewResolver.setSuffix(".jsp");
		internalResourceViewResolver.setContentType("text/html; charset=utf-8");
		return internalResourceViewResolver;
	}
	
	/**
	 * Message source를 등록
	 * @return MessageSource
	 */
	@Bean
	public MessageSource messageSource(){
		
		// classpath:messags/message
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames("/WEB-INF/properties/message");
		messageSource.setDefaultEncoding("UTF-8");
		// -1 : never reload, 0 always reload
		messageSource.setCacheSeconds(0);//媛쒕컻湲곌컙�숈븞 �꾩떆, �댁쁺紐⑤뱶�먯꽑 20遺꾩쑝濡� �ㅼ젙
				
		return messageSource;	
		
	}	
	
	
	/**
	 *  Interceptor 추가
	 */
	/*
	@Override
	public void addInterceptors(InterceptorRegistry registry){
		registry.addInterceptor(new LoginInterceptor()); // login check interceptor
	}
	*/
	
	
	/**
	 * Locale Resolver
	 * @return
	 */
	/*
	@Bean
	public LocaleResolver localeResolver() {
		// 荑좏궎 湲곗��쇰줈 濡쒖��쇱쓣 �ㅼ젙
		CookieLocaleResolver cookie = new CookieLocaleResolver();
		cookie.setCookieName("LangCodeCK"); // 荑좏궎 ���λ챸
		cookie.setCookieMaxAge( 365*24*60*60 ); // �좎�湲곌컙 - 1 year
		cookie.setCookiePath("/"); // 荑좏궎 寃쎈줈
		// 湲곕낯 Locale�� �곸뼱濡� 媛뺤젣 �ㅼ젙
		cookie.setDefaultLocale(new Locale(GlobalConstants.DEFAULT_LANG_CODE, 
				GlobalConstants.DEFAULT_COUNTRY_CODE));
		
		return cookie;
	}
	*/
	
	/**
	 * RestTemplate 객체 생성 Bean
	 * @return
	 */
	@Bean
	public RestTemplate restTemplate() {

		HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault());
		httpComponentsClientHttpRequestFactory.setReadTimeout(300*1000);
		httpComponentsClientHttpRequestFactory.setConnectTimeout(4*1000);
		
		RestTemplate restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);
		return restTemplate;
	}
	
	
	@Bean
	public SystemDefinition systemDefinition() {
		SystemDefinition systemDefinition = new SystemDefinition();
		try {
			String hostAddressPrefix = StringUtils.substring(Inet4Address.getLocalHost().getHostAddress(), 0, StringUtils.lastIndexOf(Inet4Address.getLocalHost().getHostAddress(), "."));
			systemDefinition.setHostName(Inet4Address.getLocalHost().getHostName());
			if (ArrayUtils.contains(ServerConstants.testHostPrefixList, hostAddressPrefix)) {
				systemDefinition.setServerMode(ServerConstants.SERVER_MODE_DEVELOPMENT);
				//systemDefinition.setRestApiUrl(ApiConstants.REST_API_URL_DEVELOPMENT);
				ApiConstants.HNT_API_URL = ApiConstants.HNT_API_URL_DEVELOPMENT;
			} else if (ArrayUtils.contains(ServerConstants.localHostPrefixList, hostAddressPrefix)) {
				systemDefinition.setServerMode(ServerConstants.SERVER_MODE_LOCAL);
				//systemDefinition.setRestApiUrl(ApiConstants.REST_API_URL_LOCAL);
				ApiConstants.HNT_API_URL = ApiConstants.HNT_API_URL_LOCAL;
			} else {
				systemDefinition.setServerMode(ServerConstants.SERVER_MODE_PRODUCTION);
				//systemDefinition.setRestApiUrl(ApiConstants.REST_API_URL_PRODUCTION);
				ApiConstants.HNT_API_URL = ApiConstants.HNT_API_URL_PRODUCTION;
			}
		} catch (UnknownHostException e) {
			logger.error("[WebMvcConfig.systemDefinition() Exception ] : ", e);		
		}
		logger.info(systemDefinition.toString());
		return systemDefinition;
	}
	
}