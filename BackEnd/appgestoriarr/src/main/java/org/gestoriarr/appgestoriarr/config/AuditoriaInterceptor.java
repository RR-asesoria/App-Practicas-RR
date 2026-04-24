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
	public void afterCompletion(HttpServletRequest request,
							HttpServletResponse response, 
							Object handler, Exception ex) {

		if (!loggingEnabled) return;

		if (ex == null)return;

		Authentication auth = SecurityContextHolder
				.getContext().getAuthentication();

		String uid = "anonymous";
		
		if(auth!=null&&auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
			uid = auth.getName();
		}

		logService.registrarError(
				uid,
				request.getMethod(),
				request.getRequestURI(),
				ex.getMessage()
		);
		
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
 