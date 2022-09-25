package org.univaq.collectors.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
 * Spring Security di default blocca tutte le richieste
 */

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    // La lista di tutti gli endpoint che non necessitano di autenticazione
    final String[] publicRoutes = {"/auth/**"};

    @Autowired
    private CustomUserDetailsService customUserDetailsService;


    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // Usa il metodo loadUserByUsername di CustomUserDetailsService per recuperare l'utente dal database
        // E usa la cifratura BCrypt per verificare la password
        auth.userDetailsService(customUserDetailsService).passwordEncoder(getPasswordEncoder());
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.cors().and().csrf().disable()
                // Permette l'accesso agli endpoint pubblici 
                .authorizeRequests().antMatchers(publicRoutes).permitAll()
                // Tutti gli altri endpoint richiedono l'autenticazione
                .anyRequest().authenticated()
                .and()
                // Permette di non creare sessioni
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Aggiunge il filtro per la gestione del token jwt
        http.addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
    }


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        // Crea un oggetto per la cifratura BCrypt
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        // Crea un oggetto per la cifratura BCrypt
        return new BCryptPasswordEncoder();
    }
    
}
