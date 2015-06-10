package com.pluralsight.orderfulfillment.order;

import static org.junit.Assert.fail;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.pluralsight.orderfulfillment.config.IntergrationConfig;
import com.pluralsight.orderfulfillment.test.TestIntegration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestIntegration.class, IntergrationConfig.class})
public class NewWebsiteOrderRouteTest
{
	@Inject
	private JdbcTemplate jdbcTemplate;
	
	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void test()
	{
		fail("Not yet implemented");
	}

}
