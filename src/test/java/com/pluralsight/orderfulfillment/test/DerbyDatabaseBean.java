package com.pluralsight.orderfulfillment.test;

import org.springframework.jdbc.core.JdbcTemplate;

public class DerbyDatabaseBean
{
	private JdbcTemplate jdbcTemplate;
	
	public void create() throws Exception
	{
		dropTables();
		
		createTables();
	}		
	
	/**
	 * Create 
	 */
	private void createTables()
	{
		jdbcTemplate.execute("create schema orders");
		
		jdbcTemplate.execute("create table orders.customer (id bigint not null auto_increment, firstName text not null, lastName text not null, email text not null, primary key (id));");
		jdbcTemplate.execute("create table orders.catalogitem (id bigint not null auto_increment, itemNumber text not null,itemName text not null, itemType text not null, primary key (id));");
		jdbcTemplate.execute("create table orders.order (id bigint not null auto_increment, customer_id bigint not null,orderNumber text not null, timeOrderPlaced timestamp not null,lastUpdate timestamp not null, status text not null, primary key (id));");
		jdbcTemplate.execute("create table orders.orderItem (id bigint not null auto_increment, order_id bigint not null, catalogitem_id bigint not null, status text not null, price decimal(20,5), lastUpdate timestamp not null, quantity integer not null, primary key (id));");
	}

	public void destroy() throws Exception
	{
		dropTables();
	}

	/**
	 * Drop Database schema and tables
	 */
	private void dropTables()
	{
		jdbcTemplate.execute("drop table orders.orderItem");
		jdbcTemplate.execute("drop table orders.order");
		jdbcTemplate.execute("drop table orders.catelogitem");
		jdbcTemplate.execute("drop table orders.customer");
		
		jdbcTemplate.execute("drop schema orders");
	}

	public JdbcTemplate getJdbcTemplate()
	{
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate)
	{
		this.jdbcTemplate = jdbcTemplate;
	}
}
