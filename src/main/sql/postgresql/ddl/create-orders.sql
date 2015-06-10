/*
 * PostgreSQL DML for creating the orders database objects for Pluralsight's 
 * course Apache Camel Introduction to Integration. This should be run 
 * for initial setup or when the database needs to be re-loaded. 
 */

drop table if exists orders.orderItem;
drop table if exists orders.order;
drop table if exists orders.catalogItem;
drop table if exists orders.customer;


/*
 * Table: Customer
 * Description: Contains the customer information
 */
create table orders.customer (
	id bigint not null auto_increment, 
	firstName text not null, 
	lastName text not null, 
	email text not null, 
	primary key (id)
) ;

/*
 * Table: CatalogItem
 * Description: Contains the catalog item
 */
create table orders.catalogItem (
	id bigint not null auto_increment, 
	itemNumber text not null,
	itemName text not null, 
	itemType text not null, 
	primary key (id)
) ;


/*
 * Table: Order
 * Description: Contains base order details
 */
create table orders.order (
	id bigint not null auto_increment, 
	customer_id bigint not null,
	orderNumber text not null, 
	timeOrderPlaced timestamp not null,
	lastUpdate timestamp not null, 
	status text not null, 
	primary key (id)
) ;

alter table orders.order add constraint orders_fk_1 foreign key (customer_id) references orders.customer (id);

create table orders.orderItem (
	id bigint not null auto_increment, 
	order_id bigint not null, 
	catalogItem_id bigint not null, 
	status text not null, 
	price decimal(20,5), 
	lastUpdate timestamp not null, 
	quantity integer not null, 
	primary key (id)
) ;

alter table orders.orderItem add constraint orderItem_fk_1 foreign key (order_id) references orders.order (id);
alter table orders.orderItem add constraint orderItem_fk_2 foreign key (catalogItem_id) references orders.catalogItem (id);

/*
 * PostgreSQL DML for creating the orders database objects for Pluralsight's 
 * course Apache Camel Introduction to Integration. This should be run 
 * for initial setup or when the database needs to be re-loaded. 
 */

drop table if exists orders.orderItem;
drop table if exists orders.order;
drop table if exists orders.catalogItem;
drop table if exists orders.customer;


/*
 * Table: Customer
 * Description: Contains the customer information
 */
create table orders.customer (
	id bigint not null auto_increment, 
	firstName text not null, 
	lastName text not null, 
	email text not null, 
	primary key (id)
) ;

/*
 * Table: CatalogItem
 * Description: Contains the catalog item
 */
create table orders.catalogItem (
	id bigint not null auto_increment, 
	itemNumber text not null,
	itemName text not null, 
	itemType text not null, 
	primary key (id)
) ;


/*
 * Table: Order
 * Description: Contains base order details
 */
create table orders.order (
	id bigint not null auto_increment, 
	customer_id bigint not null,
	orderNumber text not null, 
	timeOrderPlaced timestamp not null,
	lastUpdate timestamp not null, 
	status text not null, 
	primary key (id)
) ;

alter table orders.order add constraint orders_fk_1 foreign key (customer_id) references orders.customer (id);

create table orders.orderItem (
	id bigint not null auto_increment, 
	order_id bigint not null, 
	catalogItem_id bigint not null, 
	status text not null, 
	price decimal(20,5), 
	lastUpdate timestamp not null, 
	quantity integer not null, 
	primary key (id)
) ;

alter table orders.orderItem add constraint orderItem_fk_1 foreign key (order_id) references orders.order (id);
alter table orders.orderItem add constraint orderItem_fk_2 foreign key (catalogItem_id) references orders.catalogItem (id);

