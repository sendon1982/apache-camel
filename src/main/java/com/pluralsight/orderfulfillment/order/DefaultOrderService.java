package com.pluralsight.orderfulfillment.order;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.*;

import javax.inject.*;
import javax.transaction.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;

import org.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import com.pluralsight.orderfulfillment.catalog.*;
import com.pluralsight.orderfulfillment.customer.*;
import com.pluralsight.orderfulfillment.generated.FulfillmentCenter;
import com.pluralsight.orderfulfillment.generated.ObjectFactory;
import com.pluralsight.orderfulfillment.generated.OrderItemType;
import com.pluralsight.orderfulfillment.generated.OrderType;
import com.pluralsight.orderfulfillment.order.fulfillment.*;

/**
 * Services related to orders
 * 
 * @author Michael Hoffman, Pluralsight
 * 
 */
@Transactional
@Service
public class DefaultOrderService implements OrderService
{
	private static final Logger log = LoggerFactory.getLogger(DefaultOrderService.class);

	@Inject
	private OrderRepository orderRepository;

	@Inject
	private OrderItemRepository orderItemRepository;

	@Inject
	private FulfillmentProcessor fulfillmentProcessor;

	@Override
	public List<Order> getOrderDetails()
	{
		List<Order> orders = new ArrayList<Order>();

		try
		{
			populateOrderDetails(orders, orderRepository.findAll());
		}
		catch (Exception e)
		{
			log.error("An error occurred while retrieving all orders: " + e.getMessage(), e);
		}

		return orders;
	}

	@Override
	public void processOrderFulfillment()
	{
		try
		{
			fulfillmentProcessor.run();
		}
		catch (Exception e)
		{
			log.error("An error occurred during the execution of order fulfillment processing: " + e.getMessage(), e);
		}
	}

	@Override
	public List<Order> getOrderDetails(OrderStatus orderStatus, int fetchSize)
	{
		List<Order> orders = new ArrayList<Order>();

		try
		{
			populateOrderDetails(orders, orderRepository.findByStatus(orderStatus.getCode(), new PageRequest(0, fetchSize)));
		}
		catch (Exception e)
		{
			log.error("An error occurred while getting orders by order status: " + e.getMessage(), e);
		}

		return orders;
	}

	@Override
	@org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
	public void processOrderStatusUpdate(List<Order> orders, OrderStatus orderStatus) throws Exception
	{
		List<Long> orderIds = new ArrayList<Long>();
		for (Order order : orders)
		{
			orderIds.add(order.getId());
		}
		orderRepository.updateStatus(orderStatus.getCode(), new Date(System.currentTimeMillis()), orderIds);
		orderItemRepository.updateStatus(orderStatus.getCode(), new Date(System.currentTimeMillis()), orderIds);
		for (Order order : orders)
		{
			order.setStatus(orderStatus.getCode());
		}
	}

	@Override
	public List<OrderItem> getOrderItems(long id)
	{
		List<OrderItem> orderItems = new ArrayList<OrderItem>();

		try
		{
			List<OrderItemEntity> orderItemEntities = orderItemRepository.findByOrderId(id);
			populateOrderItems(orderItems, orderItemEntities);
		}
		catch (Exception e)
		{
			log.error("An error occurred while retrieving order items for the order id |" + id + "|: " + e.getMessage(), e);
		}
		return orderItems;
	}

	/**
	 * Populate the list of orders based on order entity details.
	 * 
	 * @param orders
	 * @param orderEntities
	 */
	private void populateOrderDetails(List<Order> orders, Iterable<OrderEntity> orderEntities)
	{
		for (Iterator<OrderEntity> iterator = orderEntities.iterator(); iterator.hasNext();)
		{
			OrderEntity entity = iterator.next();
			CustomerEntity customerEntity = entity.getCustomer();
			Customer customer = new Customer(customerEntity.getId(), customerEntity.getFirstName(),customerEntity.getLastName(), customerEntity.getEmail());
			orders.add(new Order(entity.getId(), customer, entity.getOrderNumber(), entity.getTimeOrderPlaced(), entity.getLastUpdate(), OrderStatus.getOrderStatusByCode(entity.getStatus()).getDescription()));
		}
	}

	/**
	 * Populate OrderItem list for Order
	 * 
	 * @param orderItems
	 * @param orderItemEntities
	 */
	private void populateOrderItems(List<OrderItem> orderItems, Iterable<OrderItemEntity> orderItemEntities)
	{
		for (Iterator<OrderItemEntity> iterator = orderItemEntities.iterator(); iterator.hasNext();)
		{
			OrderItemEntity entity = iterator.next();
			CatalogItemEntity catalogItemEntity = entity.getCatalogItem();
			CatalogItem catalogItem = new CatalogItem(catalogItemEntity.getId(), catalogItemEntity.getItemNumber(),catalogItemEntity.getItemName(), catalogItemEntity.getItemType());
			orderItems.add(new OrderItem(entity.getId(), catalogItem, entity.getStatus(), entity.getPrice(), entity.getLastUpdate(), entity.getQuantity()));
		}
	}

	/* (non-Javadoc)
	 * @see com.pluralsight.orderfulfillment.order.OrderService#processCreateOrderMessage(java.lang.Long)
	 * 
	 * Need to add @Transactional tag since this method is not invoked from the Web Page
	 * which means the "OpenEntityManagerInViewFilter" will not work on this method.
	 */
	@Override
	@org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
	public String processCreateOrderMessage(Long orderId) throws Exception
	{
		OrderEntity orderEntity = orderRepository.findOne(orderId);
		com.pluralsight.orderfulfillment.generated.Order order = buildOrderXmlType(orderEntity);
		
		JAXBContext context = JAXBContext.newInstance(com.pluralsight.orderfulfillment.generated.Order.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		StringWriter sw = new StringWriter();
		marshaller.marshal(order, sw);
		return sw.toString();
	}

	/**
	 * Build Order XML type based on Order Entity
	 * 
	 * @param orderEntity
	 * @return
	 * @throws Exception
	 */
	private com.pluralsight.orderfulfillment.generated.Order buildOrderXmlType(OrderEntity orderEntity) throws Exception
	{
		ObjectFactory objectFactory = new ObjectFactory();
		OrderType orderType = objectFactory.createOrderType();
		
		orderType.setFirstName(orderEntity.getCustomer().getFirstName());
		orderType.setLastName(orderEntity.getCustomer().getLastName());
		orderType.setEmail(orderEntity.getCustomer().getEmail());
		orderType.setFulfillmentCenter(FulfillmentCenter.ABC_FULFILLMENT_CENTER);
		orderType.setOrderNumber(orderEntity.getOrderNumber());
		
		GregorianCalendar cal = new GregorianCalendar();
		orderType.setTimeOrderPlaced(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal));
		
		for (OrderItemEntity orderItemEntity : orderEntity.getOrderItems())
		{
			OrderItemType orderItemType = objectFactory.createOrderItemType();
			orderItemType.setItemNumber(orderItemEntity.getCatalogItem().getItemNumber());	
			orderItemType.setPrice(orderItemEntity.getPrice());
			orderItemType.setQuantity(new BigDecimal(orderItemEntity.getQuantity()));
			orderType.getOrderItems().add(orderItemType);
		}

		com.pluralsight.orderfulfillment.generated.Order orderElement = objectFactory.createOrder();
		orderElement.setOrderType(orderType);
		
		return orderElement;
	}

}
