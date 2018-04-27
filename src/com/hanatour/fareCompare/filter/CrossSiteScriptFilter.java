/**
 * File name : CrossSiteScriptFilter.java
 * Package : com.hanatour.fareCompare.filter
 * Description : XSS 처리
 *
 * <pre>
 * <변경이력>
 * 수정일			수정자				수정내용
 * ------------------------------------------------------
 * 2017. 10. 10.		han seungTae	최초작성
 * </pre>
 */
package com.hanatour.fareCompare.filter;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Class name : CrossSiteScriptFilter
 * Description : XSS 처리
 * @author han seungtae
 * @date 2017. 10. 12.
 * @version	 1.0
 */
public class CrossSiteScriptFilter implements Filter{

	@Override
	public void init(FilterConfig arg0) throws ServletException {} 
	
	@Override
	public void destroy() {}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)throws IOException, ServletException {
		chain.doFilter(new RequestWrapper((HttpServletRequest) request), response);
	}
	
	public final class RequestWrapper extends HttpServletRequestWrapper{
		public RequestWrapper(HttpServletRequest request) {
			super(request);
		}
		
		public String[] getParameterValues(String name){
			String [] values = super.getParameterValues(name);
			if(values == null){
				return null;
			}
			String [] encodedValues = new String[values.length];
			for(int i = 0 ; i < values.length ; i++){
				encodedValues[i] = cleanXSS(values[i]);
			}
			return encodedValues;
		}
		
		public String getParameter(String name){
			return cleanXSS(super.getParameter(name));
		}
		
		public String getHeader(String name){
			return cleanXSS(super.getHeader(name));
		}
		
		private String cleanXSS(String value){
			if(value != null){
				Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
				value = scriptPattern.matcher(value).replaceAll("");

				scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
				value = scriptPattern.matcher(value).replaceAll("");

				scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
				value = scriptPattern.matcher(value).replaceAll("");

				scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
				value = scriptPattern.matcher(value).replaceAll("");

				scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
				value = scriptPattern.matcher(value).replaceAll("");

				scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
				value = scriptPattern.matcher(value).replaceAll("");

				scriptPattern = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
				value = scriptPattern.matcher(value).replaceAll("");

				scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
				value = scriptPattern.matcher(value).replaceAll("");

				scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
				value = scriptPattern.matcher(value).replaceAll("");

				scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
				value = scriptPattern.matcher(value).replaceAll("");
				
				value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
			}
			return value;
		}
		
	}
}
