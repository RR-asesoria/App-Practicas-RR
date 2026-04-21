package org.gestoriarr.appgestoriarr.config;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.gestoriarr.appgestoriarr.model.Usuario;
import org.gestoriarr.appgestoriarr.repository.UsuarioRepo;
import org.jspecify.annotations.NonNull;
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
@AllArgsConstructor
public class FirebaseAuthenticationFilter extends OncePerRequestFilter{

	private final UsuarioRepo repository;
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
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
				request.setAttribute("uid", uid);
				if(SecurityContextHolder.getContext().getAuthentication() == null) {

					Optional<Usuario> usuario = repository.findById(uid);

					if (usuario.isEmpty()){
						throw new IllegalArgumentException("User not found");
					}

					List<GrantedAuthority> authiAuthorities = 
							List.of(new SimpleGrantedAuthority("ROLE_"+usuario.get().getRole()));
					
					
					UsernamePasswordAuthenticationToken authentication = 
							new UsernamePasswordAuthenticationToken(uid, null, authiAuthorities);
					
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					
					SecurityContextHolder.getContext().setAuthentication(authentication);
					
				}
				
		} catch (Exception e) {

			if (e instanceof FirebaseAuthException){
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().write("Invalid token");
				return;
			} else if (e instanceof IllegalArgumentException) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				response.getWriter().write(e.getMessage());
			} else {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().write("Invalid token");
			}
		}
			
			filterChain.doFilter(request, response);
			
		}
		
		
		
		
	}

	
	


