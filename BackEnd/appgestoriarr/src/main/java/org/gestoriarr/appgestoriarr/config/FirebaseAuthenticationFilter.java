package org.gestoriarr.appgestoriarr.config;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.gestoriarr.appgestoriarr.model.Usuario;
import org.gestoriarr.appgestoriarr.repository.UsuarioRepo;
import org.gestoriarr.appgestoriarr.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter{
	
	@Autowired
	private UsuarioService usuarioService;
	@Autowired
	private UsuarioRepo repository;
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, 
									HttpServletResponse response, 
									FilterChain filterChain)
			throws ServletException, IOException {
		
		String header = request.getHeader("Authorization");
	
		
		if(header == null || !header.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		
			
		String token = header.substring(7);
			
		try {
				
				FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token,false);
				
				String uid = decodedToken.getUid();
				
				if(SecurityContextHolder.getContext().getAuthentication() == null) {

					Optional<Usuario> usuario = repository.findById(uid);

					if (usuario.isEmpty()){
						throw new RuntimeException("El usuario no fue encontrado");
					}

					List<GrantedAuthority> authiAuthorities = 
							List.of(new SimpleGrantedAuthority("ROLE_"+usuario.get().getRole()));
					
					
					UsernamePasswordAuthenticationToken authentication = 
							new UsernamePasswordAuthenticationToken(uid, null, authiAuthorities);
					
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					
					SecurityContextHolder.getContext().setAuthentication(authentication);
					
				}
				
		}catch (FirebaseAuthException a) {
			// TODO Auto-generated catch block
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("Token invalido");
			return;
		} 
		catch (Exception e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().write("Usuario no autorizado");
			return;
			}
			
			filterChain.doFilter(request, response);
			
		};
		
		
		
		
	}

	
	


