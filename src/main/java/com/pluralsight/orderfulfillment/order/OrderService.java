package com.pluralsight.orderfulfillment.order;

import java.util.*;

/**
 * Interface for order functionality
 * 
 * @author Michael Hoffman, Pluralsight
 * 
 */
public interface OrderService
{

	/**
	 * Returns the details of the order without the order items.
	 * 
	 * @return List<Order>
	 */
	public List<Order> getOrderDetails();

	/**
	 * Returns the order details of the order without the order item for the status passed. The result size is limited
	 * based on the fetch size passed.
	 * 
	 * @param OrderStatus
	 *            orderStatus
	 * @param int fetchSize
	 * @return List<Order>
	 */
	public List<Order> getOrderDetails(OrderStatus orderStatus, int fetchSize);

	/**
	 * Executes the business task of order fulfillment
	 */
	public void processOrderFulfillment();

	/**
	 * Updates the order status of one or more orders. Also will update the list of orders passed to match the status
	 * passed.
	 * 
	 * @param orders
	 * @param orderStatus
	 */
	public void processOrderStatusUpdate(List<Order> orders, OrderStatus orderStatus) throws Exception;

	/**
	 * Retrieves the order items for an order.
	 * 
	 * @param id
	 * @return List<OrderItem>
	 */
	public List<OrderItem> getOrderItems(long id);

	/**
	 * Process to create order message based on the orderId
	 * 
	 * @param orderId
	 */
	public String processCreateOrderMessage(Long orderId) throws Exception;

}
