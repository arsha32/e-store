package com.example.ecommerce.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.ecommerce.model.AppRole;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.model.Role;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.RoleRepository;
import com.example.ecommerce.security.jwt.AuthEntryPointJwt;
import com.example.ecommerce.security.jwt.AuthTokenFilter;
import com.example.ecommerce.security.service.UserDetailsServiceImpl;

import java.util.Set;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity
public class WebSecurityConfig {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);

//        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                         auth.requestMatchers("/api/auth/**").permitAll()
                               .requestMatchers("/h2test/**").permitAll()
                                // .requestMatchers("/api/admin/**").permitAll()
                                .requestMatchers("/api/seller/**").hasAnyRole("ADMIN","SELLER")
                                .requestMatchers("/api/cart/**").permitAll()
                                .requestMatchers("/api/carts/**").permitAll()
                                .requestMatchers("/api/public/**").permitAll()
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/api/test/**").permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .requestMatchers("/images/**").permitAll()
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.headers(headers -> headers.frameOptions(
                frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring().requestMatchers("/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui/index.html",
                "/webjars/**"));
    }


    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Retrieve or create roles
            Role userRole = roleRepository.findByRoleName(AppRole.Role_User)
                    .orElseGet(() -> {
                        Role newUserRole = new Role(AppRole.Role_User);
                        return roleRepository.save(newUserRole);
                    });

            Role sellerRole = roleRepository.findByRoleName(AppRole.Role_Seller)
                    .orElseGet(() -> {
                        Role newSellerRole = new Role(AppRole.Role_Seller);
                        return roleRepository.save(newSellerRole);
                    });

            Role adminRole = roleRepository.findByRoleName(AppRole.Role_Admin)
                    .orElseGet(() -> {
                        Role newAdminRole = new Role(AppRole.Role_Admin);
                        return roleRepository.save(newAdminRole);
                    });

            Set<Role> userRoles = Set.of(userRole);
            Set<Role> sellerRoles = Set.of(sellerRole);
            Set<Role> adminRoles = Set.of(userRole, sellerRole, adminRole);


            // Create users if not already present
            if (!customerRepository.existsByCustomerName("user1")) {
                Customer user1 = new Customer("user1", "user1@example.com",   passwordEncoder.encode("password1"));
                customerRepository.save(user1);
            }

            if (!customerRepository.existsByCustomerName("seller1")) {
                Customer seller1 = new Customer("seller1", "seller1@example.com", passwordEncoder.encode("password2"));
                customerRepository.save(seller1);
            }

            if (!customerRepository.existsByCustomerName("admin")) {
                Customer admin = new Customer("admin", "admin@example.com", passwordEncoder.encode("adminPass"));
                customerRepository.save(admin);
            }

            // Update roles for existing users
            customerRepository.findByCustomerName("user1").ifPresent(user -> {
                user.setRoles(userRoles);
                customerRepository.save(user);
            });

            customerRepository.findByCustomerName("seller1").ifPresent(seller -> {
                seller.setRoles(sellerRoles);
                customerRepository.save(seller);
            });

            customerRepository.findByCustomerName("admin").ifPresent(admin -> {
                admin.setRoles(adminRoles);
                customerRepository.save(admin);
            });
        };
    }

}