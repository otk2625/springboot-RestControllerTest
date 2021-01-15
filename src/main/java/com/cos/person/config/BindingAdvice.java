package com.cos.person.config;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cos.person.domain.CommonDto;

import io.sentry.Sentry;

//@Configuation : 설정할때 사용!
//@Component : @Controller가 뜨고나서 뜸 순서가 뒤쪽 , 설정이 아닌 모든 것들에 사용
@Component
@Aspect
public class BindingAdvice {
	
	//로그 남기기
	private static final Logger log = LoggerFactory.getLogger(BindingAdvice.class);

	
	// 어떤함수가 언제 몇번 실행됐는지 횟수같은거 로그 남기기
		@Before("execution(* com.cos.person.web..*Controller.*(..))")
		public void testCheck() {
			//request 값 처리 못하나요?
			HttpServletRequest request = 
					((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
			System.out.println("주소 : "+request.getRequestURI());

			System.out.println("전처리 로그를 남겼습니다.");

		}
	
	//@Before
	//@After
		  
	@Around("execution(* com.cos.person.web..*Controller.*(..))")
	public Object validCheck(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		String type = proceedingJoinPoint.getSignature().getDeclaringTypeName();
		String method = proceedingJoinPoint.getSignature().getName();
		
		System.out.println("type : " + type);
		System.out.println("method : " + method);
		
		Object[] args = proceedingJoinPoint.getArgs();
		
		for(Object arg : args) {
			if(arg instanceof BindingResult) {
				BindingResult bindingResult = (BindingResult)arg;
				
				//서비스 : 정싱적인 화면 -> 사용자요청
				if(bindingResult.hasErrors()) {
					Map<String, String> errorMap = new HashMap<>();
					
					for(FieldError error : bindingResult.getFieldErrors()) {
						errorMap.put(error.getField(), error.getDefaultMessage());
						
						// 로그 레벨 error, warn, info, debug
						log.warn(type+"."+method+"() => 필드 : "+error.getField()+", 메시지 : "+error.getDefaultMessage());
						log.debug(type+"."+method+"() => 필드 : "+error.getField()+", 메시지 : "+error.getDefaultMessage());
						Sentry.captureMessage(type+"."+method+"() => 필드 : "+error.getField()+", 메시지 : "+error.getDefaultMessage());

						//DB연결 -> DB남기기
						//File file = new File();
					}
					
					return new CommonDto<>(HttpStatus.BAD_REQUEST.value(),errorMap);
				}
			}
		}
		return proceedingJoinPoint.proceed(); //함수의 스택을 실행한다.
	}
}
