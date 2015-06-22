package com.pluralsight.orderfulfillment.config;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.apache.camel.component.jms.JmsConfiguration;
import org.apache.camel.component.sql.SqlComponent;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.pluralsight.orderfulfillment.constant.OrderConstant;
import com.pluralsight.orderfulfillment.generated.FulfillmentCenter;
import com.pluralsight.orderfulfillment.order.OrderStatus;

@Configuration
public class IntergrationConfig extends CamelConfiguration
{
	@Inject
	private Environment environment;
	
	@Inject
	private DataSource dataSource;
	
	@Bean
	public ConnectionFactory jmsConnectionFactory()
	{
		return new ActiveMQConnectionFactory(environment.getProperty(OrderConstant.ACTIVEMQ_BROKER_URL));
	}
	
	@Bean(initMethod = "start", destroyMethod = "stop")
	public PooledConnectionFactory pooledConnectionFactory()
	{
		PooledConnectionFactory factory = new PooledConnectionFactory();
		factory.setConnectionFactory(jmsConnectionFactory());
		factory.setMaxConnections(Integer.valueOf(environment.getProperty(OrderConstant.JMS_MAX_CONNECTION)));
		return factory;
	}
	
	@Bean
	public JmsConfiguration jmsConfiguration()
	{
		JmsConfiguration jmsConfiguration = new JmsConfiguration();
		jmsConfiguration.setConnectionFactory(pooledConnectionFactory());
		return jmsConfiguration;
	}
	
	@Bean
	public ActiveMQComponent activeMQComponent()
	{
		ActiveMQComponent activeMQComponent = new ActiveMQComponent();
		activeMQComponent.setConfiguration(jmsConfiguration());
		
		return activeMQComponent;
	}
	
	
	@Bean
	public SqlComponent sql()
	{
		SqlComponent sqlComponent = new SqlComponent();
		sqlComponent.setDataSource(dataSource);
		return sqlComponent;
	}
	
	@Bean
	public RouteBuilder newWebsiteOrderRoute()
	{
		final String sqlTemplate = "sql:%s?consumer.onConsume=%s";
		
		final String queryString = "select id from orders.order where status = '" + OrderStatus.NEW.getCode() + "'";
		final String updateString = "update orders.order set status = '" + OrderStatus.PROCESSING.getCode() +"' where id = :#id";
		
		return new RouteBuilder()
		{
			@Override
			public void configure() throws Exception
			{
				from(String.format(sqlTemplate, queryString, updateString))
					.beanRef("orderItemMessageTranslator", "transformToOrderItemMessage")
					.to("activemq:queue:ORDER_ITEM_PROCESSING");
			}
		};
				
	}
	
	/**
	 * 
	 * @return
	 */
	@Bean
	public RouteBuilder fulfillmentCenterCotentBasedRouter()
	{
		return new RouteBuilder()
		{
			@Override
			public void configure() throws Exception
			{
				Namespaces namespaces = new Namespaces("o", "http://www.pluralsight.com/orderfulfillment/Order");
				
				from("activemq:queue:ORDER_ITEM_PROCESSING").choice().
					when().xpath("/o:Order/o:OrderType/o:FulfillmentCenter = '" + FulfillmentCenter.ABC_FULFILLMENT_CENTER.value() + "'" , namespaces).to("activemq:queue:ABC_FULFILLMENT_REQUEST").					
					when().xpath("/o:Order/o:OrderType/o:FulfillmentCenter = '" + FulfillmentCenter.FULFILLMENT_CENTER_ONE.value() + "'" , namespaces).to("activemq:queue:FC1_FULFILLMENT_REQUEST").
					otherwise().to("activemq:queue:ERROR_FULFILLMENT_REQUEST");
			}
		};
				
	}
	
	@Override
	public List<RouteBuilder> routes()
	{
		List<RouteBuilder> routeList = new ArrayList<RouteBuilder>();
		
//		routeList.add(new RouteBuilder()
//		{
//			@Override
//			public void configure() throws Exception
//			{
//				String fromPath = "file://" + environment.getProperty("order.fulfillment.center.1.outbound.folder");
//				String toPath = "file://" + environment.getProperty("order.fulfillment.center.1.outbound.folder") + "/test";
//				
//				from(fromPath).to(toPath);
//			}
//		});
		
		routeList.add(newWebsiteOrderRoute());
		routeList.add(fulfillmentCenterCotentBasedRouter());
		
		return routeList;
	}
}
