package com.ix.shorten.url.api;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories("com.ix.shorten.url.repository")
@ComponentScan({ "com.ix.shorten.url.dao", "com.ix.shorten.url.service" })
@Profile("dev")
@Import({ SecurityConfig.class })
@EnableTransactionManagement
public class BEAppConfiguration {

  @Bean
  public DataSource dataSource() {
    final BasicDataSource dataSource = new BasicDataSource();
    dataSource.setDriverClassName("org.h2.Driver");
    dataSource.setUrl("jdbc:h2:./data/db;AUTO_SERVER=TRUE;MODE=PostgreSQL;DATABASE_TO_UPPER=false");
    dataSource.setUsername("sa");
    dataSource.setPassword("");

    return dataSource;
  }

  @Value("classpath:sql/account_schema.sql")
  private Resource accountSchemaScript;

  @Value("classpath:sql/account_insert.sql")
  private Resource accountInsertScript;

  @Value("classpath:sql/redirect_schema.sql")
  private Resource redirectSchemaScript;

  @Value("classpath:sql/redirect_insert.sql")
  private Resource redirectInsertScript;

  @Bean
  public DataSourceInitializer dataSourceInitializer(final DataSource dataSource) {
    final DataSourceInitializer initializer = new DataSourceInitializer();
    initializer.setDataSource(dataSource);
    initializer.setDatabasePopulator(databasePopulator());
    return initializer;
  }

  private DatabasePopulator databasePopulator() {
    final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
    populator.addScript(accountSchemaScript);
    populator.addScript(accountInsertScript);
    populator.addScript(redirectSchemaScript);
    populator.addScript(redirectInsertScript);
    return populator;
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    final LocalContainerEntityManagerFactoryBean entityManagerFactory =
        new LocalContainerEntityManagerFactoryBean();
    entityManagerFactory.setDataSource(dataSource());
    entityManagerFactory.setPackagesToScan(new String[] { "com.ix.shorten.url.model" });
    final HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
    hibernateJpaVendorAdapter.setDatabase(Database.H2);
    hibernateJpaVendorAdapter.setGenerateDdl(true);
    hibernateJpaVendorAdapter.setShowSql(true);

    entityManagerFactory.setJpaVendorAdapter(hibernateJpaVendorAdapter);
    entityManagerFactory.setJpaProperties(hibernateProperties());
    entityManagerFactory.setPersistenceUnitName("puShortenUrl");
    return entityManagerFactory;
  }

  @Bean
  public PlatformTransactionManager transactionManager() {
    return new JpaTransactionManager();
  }

  Properties hibernateProperties() {
    return new Properties() {
      private static final long serialVersionUID = 1L;
      {
        setProperty("hibernate.hbm2ddl.auto", "create");
        setProperty("hibernate.show_sql", "true");
        setProperty("hibernate.format_sql", "true");
        setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        setProperty("hibernate.cache.region.jbc2.cachefactory", "java:CacheManager");
        setProperty("hibernate.cache.use_second_level_cache", "false");
        setProperty("hibernate.cache.use_query_cache", "false");
        setProperty("hibernate.cache.use_minimal_puts", "true");
        setProperty("hibernate.hbm2ddl.import_files_sql_extractor",
            "org.hibernate.tool.hbm2ddl.MultipleLinesSqlCommandExtractor");
      }
    };
  }
}
