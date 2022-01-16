package com.openclassrooms.paymybuddy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

/**
 * Configure Pageable resolver to return unpaged as fallback when no pageable parameter sent in
 * controller calls.
 */
@Configuration
public class PageableConfiguration {

  @Bean
  public PageableHandlerMethodArgumentResolverCustomizer customize() {
    return p -> p.setFallbackPageable(Pageable.unpaged());
  }

}