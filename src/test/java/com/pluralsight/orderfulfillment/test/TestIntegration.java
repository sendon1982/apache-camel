package com.pluralsight.orderfulfillment.test;

import org.springframework.context.annotation.Configuration;

@Configuration
public class TestIntegration
{
/*	@Bean
	public DataSource dataSource()
	{
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
		dataSource.setUrl("jdbc:derby:memory:orders;create=true");
		
		return dataSource;
	}
	
	@Bean
	public JdbcTemplate jdbcTemplate()
	{
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
		return jdbcTemplate;
	}
	
	@Bean(initMethod = "create", destroyMethod = "destroy")
	public DerbyDatabaseBean derbyDatabaseBean()
	{
		DerbyDatabaseBean derby = new DerbyDatabaseBean();
		derby.setJdbcTemplate(jdbcTemplate());
		return derby;
	}
*/
}
