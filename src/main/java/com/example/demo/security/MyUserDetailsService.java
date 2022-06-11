package com.example.demo.security;


import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.example.demo.model.Profesional;
import com.example.demo.repository.ProfesionalRepo;

@Component
public class MyUserDetailsService implements UserDetailsService {

	@Autowired ProfesionalRepo profRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Profesional p = profRepo.findByEmail(email).orElse(null);
        if(p==null)
            throw new UsernameNotFoundException("Could not findUser with email = " + email);
      
        return new org.springframework.security.core.userdetails.User(
                email,
                p.getContrasenia(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
