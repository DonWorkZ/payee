package com.fdmgroup.pilotbank2.authentication.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static com.fdmgroup.pilotbank2.authentication.security.SecurityConstants.*;

@Configuration
@ComponentScan
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
//TODO - make sure to test this class at the integration test level (no unit testing due to the nature of security configuration)

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/", "/v3/api-docs/**", "/swagger-ui/**", "/webjars/**", "/h2/**",
                    SIGN_UP_URL, DEVICE_AUTHORIZATION_URL,FORGOT_PW_URL, SECURITY_ANSWER_URL, TWO_STEP_VERIFICATION_INITIATION_URL,
                    REQUEST_SECURITY_CODE_URL, VERIFY_SECURITY_CODE_URL, UPDATE_PW_URL, LOGIN_URL)
                .permitAll()                                                                //The URLS here are allowed to anybody
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()                  //Allows Options pre-flight for CORS
            .antMatchers().hasAnyRole()                                                    //These are locked down by role
            .antMatchers().hasAnyAuthority()                                               //These are locked down by authority
            .anyRequest().authenticated()                                                  //Everything else is locked down
            .and()
            .addFilter(new JWTAuthenticationFilter(authenticationManager(), userDetailsService))
            .addFilter(new JWTAuthorizationFilter(authenticationManager(), userDetailsService))
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.headers().frameOptions().disable();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("http://localhost:4200/", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
}
