package com.sadetech.user_info.service;

import com.sadetech.user_info.model.CustomUserDetails;
import com.sadetech.user_info.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class OurUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public CustomUserDetails loadUserByUsername(String emailOrPhone) throws UsernameNotFoundException {
        return userRepository.findByEmailOrPhoneNumber(emailOrPhone)
                .map(user -> new CustomUserDetails(
                        user.getUsername(),
                        user.getPassword(),
                        (Collection<GrantedAuthority>) user.getAuthorities()
                ))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + emailOrPhone));
    }

}