/**
 * File name : PersistenceConfig.java
 * Package : com.hanatour.fareCompare.config
 * Description : 
 *
 * <pre>
 * <변경이력>
 * 수정일			수정자				수정내용
 * ------------------------------------------------------
 * 2017. 10. 16.		sthan	최초 작성
 * </pre>
 */
package com.hanatour.fareCompare.config;

import java.io.IOException;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
//import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
//import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * Class name : PersistenceConfig
 * Description : MariaDB Config 설정관련
 * @author sthan
 * @author 2017. 10. 16.
 * @version 1.0
 */
@Configuration
@EnableTransactionManagement
public class PersistenceConfig {

	private static Logger logger = LogManager.getLogger("COMMONLOGGER");
	
	 /**
	  * DataSource 설정
	  * </bean>
	  *
	  * @return
	  */
	 @Bean
	 public DataSource dataSource() {
	  DriverManagerDataSource dataSource = new DriverManagerDataSource();
	  //dataSource.setDriverClassName("core.log.jdbc.driver.MysqlDriver");
	  //dataSource.setUrl("jdbc:mySql://localhost:3306/hnt_fare");
	  dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
	  dataSource.setUrl("jdbc:mariadb://localhost:3306/hnt_fare");
	  dataSource.setUsername("root");
	  dataSource.setPassword("gkdrhdxla1!");
	  return dataSource;
	 }
	 
	 /**
	  * TransactionManager설정
	  * @return
	  */
	 @Bean
	 public PlatformTransactionManager transactionManager() {
	  return new DataSourceTransactionManager(dataSource());
	 }
	 
	 /**
	  * SqlSessionFactory 설정
	  * @param dataSource
	  * @param applicationContext
	  * @return
	  * @throws IOException
	  */
	 @Bean
	 public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) throws IOException {

	  SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();

	  factoryBean.setDataSource(dataSource);
	  factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:com/hanatour/fareCompare/dao/*.xml"));
	  
	  return factoryBean;
	 }
	 	
	 /**
	  * SqlSessionTemplate 설정
	  * @param sqlSessionFactory
	  * @return
	  */
	  @Bean
	  public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
	      return new SqlSessionTemplate(sqlSessionFactory);
	  }

}

