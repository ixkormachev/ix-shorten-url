package com.ix.shorten.url.api;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

  @Override
  protected WebApplicationContext createRootApplicationContext() {
    final WebApplicationContext context = super.createRootApplicationContext();
    ((ConfigurableEnvironment) context.getEnvironment())
        .setActiveProfiles("dev");
    return context;
  }

  @Override
  protected Class<?>[] getRootConfigClasses() {
    return new Class<?>[] { BEAppConfiguration.class };
  }

  @Override
  protected Class<?>[] getServletConfigClasses() {
    return new Class[] { BEWebConfiguration.class };
  }

  @Override
  protected String[] getServletMappings() {
    return new String[] { "/*" };
  }
}
