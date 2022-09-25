package org.univaq.collectors.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.univaq.collectors.repositories.CollectorsRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CollectorsRepository collectorsRepository;

    public CustomUserDetailsService(CollectorsRepository collectorsRepository) {
        this.collectorsRepository = collectorsRepository;
    }

    // Si occupa di caricare l'utente dal database
    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return collectorsRepository.findByEmail(email)
                .map(collector -> new CustomUserDetails(collector.getEmail(), collector.getPassword()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    
}
