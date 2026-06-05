package com.example.ecommerce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.CustomerRepository;

@Component
public class AuthUtil {
    @Autowired
    private CustomerRepository userRepository;
    
    public String loggedInEmail(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Customer user = userRepository.findByCustomerName(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));

        return user.getEmail();
    }

    public Long loggedInUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Customer user = userRepository.findByCustomerName(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));

        return user.getCustomerId();
    }

    public Customer loggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Customer user = userRepository.findByCustomerName(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));
        return user;

    }

}
