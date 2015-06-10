package com.pluralsight.orderfulfillment.config;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class IntergrationConfig extends CamelConfiguration
{
	@Inject
	private Environment environment;
	
	@Override
	public List<RouteBuilder> routes()
	{
		List<RouteBuilder> routeList = new ArrayList<RouteBuilder>();
		
		routeList.add(new RouteBuilder()
		{
			@Override
			public void configure() throws Exception
			{
				String fromPath = "file://" + environment.getProperty("order.fulfillment.center.1.outbound.folder");
				String toPath = "file://" + environment.getProperty("order.fulfillment.center.1.outbound.folder") + "/test";
				
				from(fromPath).to(toPath);
			}
		});
		
		return routeList;
	}
}
