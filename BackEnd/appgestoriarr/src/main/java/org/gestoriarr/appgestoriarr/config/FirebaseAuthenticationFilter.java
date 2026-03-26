package org.gestoriarr.appgestoriarr.config;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import org.gestoriarr.appgestoriarr.model.Usuario;

import org.gestoriarr.appgestoriarr.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter{

	@Autowired
	private UsuarioService service;
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		//TODO BLOQUE PARA VER TODAS LAS PETICIONES ENTRANTES
		Enumeration<String> headers = request.getHeaderNames();
		

		while (headers.hasMoreElements()) {
		    String h = headers.nextElement();
		    System.out.println("HEADER: " + h + " = " + request.getHeader(h));
		}
		
		
		String header = request.getHeader("Authorization");
		
		
		//TODO VEMOS SI EL TOKEN RECIBIDO ES NULO
		System.out.println("Hola desde doFilterInternal. El header es nulo?"+ (header == null));
		
		
		
		if(header == null || !header.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		
			
		String token = header.substring(7);
		
		
			
		try {
				
				FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
				
				
				String uid = decodedToken.getUid();
				
				Usuario usuario = service.encontrarPorId(uid);
				
				List<GrantedAuthority> authiAuthorities = 
						List.of(new SimpleGrantedAuthority("ROLE_"+usuario.getRole()));
				
				UsernamePasswordAuthenticationToken authentication = 
						new UsernamePasswordAuthenticationToken(uid, null, authiAuthorities);
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
				
				
		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			filterChain.doFilter(request, response);
			
		};
		
		
		
		
	}


/*
 * 
 * VERSIÓN FUNCIONAL 1 DEL FILTRO
 * 
 * @Autowired
	private UsuarioService usuarioService;
 * 
 * 
 * @Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		//TODO BLOQUE PARA VER TODAS LAS PETICIONES ENTRANTES
		Enumeration<String> headers = request.getHeaderNames();
		

		while (headers.hasMoreElements()) {
		    String h = headers.nextElement();
		    System.out.println("HEADER: " + h + " = " + request.getHeader(h));
		}
		
		
		String header = request.getHeader("Authorization");
		
		
		//TODO VEMOS SI EL TOKEN RECIBIDO ES NULO
		System.out.println("Hola desde doFilterInternal. El header es nulo?"+ (header == null));
		
		
		
		if(header == null || !header.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		
			
		String token = header.substring(7);
		
		
			
		try {
				
				FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
				
				String uid = decodedToken.getUid();
				
				
				Usuario usuario = usuarioService.obtenerUsuario(uid);
				
				List<GrantedAuthority> authiAuthorities = 
						List.of(new SimpleGrantedAuthority("ROLE_"+usuario.getRole()));
				
				UsernamePasswordAuthenticationToken authentication = 
						new UsernamePasswordAuthenticationToken(uid, null, authiAuthorities);
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
				
				
		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			filterChain.doFilter(request, response);
			
		};
 * 
 * 
 * 
 * 
 * 
 * */


/*
 * 
 * SEGUNDA VERSIÓN FUNCIONAL CON CLAIMS
 * 
 * 
 * @Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		//TODO BLOQUE PARA VER TODAS LAS PETICIONES ENTRANTES
		Enumeration<String> headers = request.getHeaderNames();
		

		while (headers.hasMoreElements()) {
		    String h = headers.nextElement();
		    System.out.println("HEADER: " + h + " = " + request.getHeader(h));
		}
		
		
		String header = request.getHeader("Authorization");
		
		
		//TODO VEMOS SI EL TOKEN RECIBIDO ES NULO
		System.out.println("Hola desde doFilterInternal. El header es nulo?"+ (header == null));
		
		
		
		if(header == null || !header.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		
			
		String token = header.substring(7);
		
		
			
		try {
				
				FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
				
				String uid = decodedToken.getUid();
				
				
				
				String role = (String) decodedToken.getClaims().get("role");
				
				if(role == null) {
					throw new RuntimeException("El usuario no tiene un rol asignado");
				}
				
				List<GrantedAuthority> authiAuthorities = 
						List.of(new SimpleGrantedAuthority("ROLE_"+role));
				
				UsernamePasswordAuthenticationToken authentication = 
						new UsernamePasswordAuthenticationToken(uid, null, authiAuthorities);
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
				
				
		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			filterChain.doFilter(request, response);
			
		};
 * 
 * 
 * */
	
	


