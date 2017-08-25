package com.ix.shorten.url.api;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

import com.ix.shorten.url.model.Account;

@ComponentScan({"com.ix.shorten.url.controller"})
@Configuration
@Import(RepositoryRestMvcConfiguration.class)
public class BEWebConfiguration extends RepositoryRestMvcConfiguration {
  @Override
  protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
    super.configureRepositoryRestConfiguration(config);
    config.exposeIdsFor(Account.class);
  }
}
