/**
 * File name : ExceptionAdvice.java
 * Package : com.hanatour.fareCompare.advice
 * Description : 
 *
 * <pre>
 * <변경이력>
 * 수정일			수정자				수정내용
 * ------------------------------------------------------
 * 2018. 1. 8.		hana	최초 작성
 * </pre>
 */
package com.hanatour.fareCompare.advice;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Class name : ExceptionAdvice
 * Description :
 * @author hana
 * @author 2018. 1. 8.
 * @version 1.0
 */
public class ExceptionAdvice {
	private static Logger commonLogger = LogManager.getLogger("COMMONLOGGER");
	
	@Autowired
	ReloadableResourceBundleMessageSource messageSource;
	
	@ResponseStatus(HttpStatus.NOT_FOUND) //404
	@ExceptionHandler({NoHandlerFoundException.class})
	public String handleNoHandlerFound(HttpServletRequest request, Model model){
		commonLogger.warn("[DISPATCHER "+(Integer)request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)+"] "+(String)request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI));
		model.addAttribute("message", messageSource.getMessage("MESSAGE."+HttpStatus.NOT_FOUND, null, null));
		return "/common/message.hnt";
	}
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //500
	@ExceptionHandler({Exception.class})
	public String defaultExceptionHandle(HttpServletRequest request, Model model, Exception e){
		commonLogger.warn("[Internal Server Exception "+ request.getRequestURL(), e);
		model.addAttribute("message", messageSource.getMessage("MESSAGE."+HttpStatus.INTERNAL_SERVER_ERROR, null, null));
		return "/common/message.hnt";
	}
}
