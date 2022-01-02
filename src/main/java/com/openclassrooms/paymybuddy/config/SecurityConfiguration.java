package com.openclassrooms.paymybuddy.config;

import com.openclassrooms.paymybuddy.service.CredentialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Spring Security configuration.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  CredentialsService credentialsService;

  // This method is for overriding the default AuthenticationManagerBuilder.
  // We can specify how the user details are kept in the application. It may
  // be in a database, LDAP or in memory.
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(credentialsService);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
        .antMatchers("/subscribe").permitAll()
        .anyRequest().fullyAuthenticated().and()
        .httpBasic().and()
        .csrf().disable();

  }

}
