package org.gestoriarr.appgestoriarr.config;

import org.gestoriarr.appgestoriarr.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuditoriaInterceptor implements HandlerInterceptor{
	
	
	@Autowired
	private LogService logService;

	@Value("${app.logging.enabled}")
	private boolean loggingEnabled;
	
	@Override
	public boolean preHandle(HttpServletRequest request,
							HttpServletResponse response, 
							Object handler) {
		if (!loggingEnabled) return true;
		Authentication auth = SecurityContextHolder
				.getContext().getAuthentication();
		
		
		if(auth!=null&&auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
			
			String uid = auth.getName();
			
			logService.registrarAccion(
					uid,
					request.getMethod(), 
					request.getRequestURI()
			);
			
		}
		
		
		return true;
		
	}
	
	

}







/*
 * 
 * System.out.println("El usuario está autenticado?" + auth.isAuthenticated());
		
		System.out.println("Muestrame el nombre: "+auth.getName());
		
		System.out.println("Muestrame las credenciales"+auth.getCredentials().toString());
		
		System.out.println("¿Es nulo?"+auth.getDetails().toString());
 * 
 * */
 