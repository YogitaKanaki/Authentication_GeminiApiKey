package com.example.securityandapi.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        //this is called build a pattern
        return http
                .csrf(customizer->customizer.disable())
                .authorizeHttpRequests(request->request.anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session->session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)).build();


//        http.csrf(customizer->customizer.disable()); //to disable the csrf token
//        http.authorizeHttpRequests(request->request.anyRequest().authenticated());  //so nobody can access the page without the authentication
//        http.formLogin(Customizer.withDefaults());
//        http.httpBasic(Customizer.withDefaults()); //for postman
//        http.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); //it creates new seesionId everytime we refresh but to use the formlogin ui we have comment the formbasic
//        return http.build();
    }

    //this is to change the authentication provider
    @Bean
    //this is interface so we need class obj
    public AuthenticationProvider authenticationProvider(){
        //this authentication provider is used for database
        DaoAuthenticationProvider provider=new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder(10)); //to verify the password
        return provider;
    }

//    @Bean
//    //This is inbuild interface to verify the user
//    public UserDetailsService userDetailsService(){
//
//        //this is also interface so we are using the User(class) to create the objects
//        //we can create different users with roles like user,admin,trainer,etc anything you want
//        UserDetails user1= User
//                .withDefaultPasswordEncoder()
//                .username("yogita")
//                .password("user123")
//                .roles("USER")
//                .build();
//
//        UserDetails user2= User
//                .withDefaultPasswordEncoder()
//                .username("souji")
//                .password("user123")
//                .roles("ADMIN")
//                .build();
//
//
//        return new InMemoryUserDetailsManager(user1,user2); //since UserDetailsService is a interfac
//        // we cannot create its object so we are using this since this class implements the UserDetailsService
//    }
}
