package org.univaq.collectors.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /*
     * Responsabilità:
     * 1. Verificare che il valore del header Authorization non sia null e che inizi per Bearer
     *    Esempio di Authorization header: Bearer <token jwt>
     * 2. Verifica se il token è valido
     * 3. Se il token è valido, aggiungrlo in spring security context
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        // 1.
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            email = jwtUtil.extractEmail(jwt);
        }

        // Cerchiamo l'utente nel database con l'email che abbiamo estratto dal token
        CustomUserDetails userDetails = this.customUserDetailsService.loadUserByUsername(email);

        // 2.
        if (!jwtUtil.isTokenExpired(jwt)) {

            // 3.
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            // SecurityContextHolder viene utilizzato per memorizzare i dettagli dell'utente attualmente autenticato
            // noto anche come Principal
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
    
}
