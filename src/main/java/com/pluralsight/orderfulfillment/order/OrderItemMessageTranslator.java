package com.pluralsight.orderfulfillment.order;

import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pluralsight.orderfulfillment.constant.OrderConstant;

@Component
public class OrderItemMessageTranslator
{
	private static Logger log = LoggerFactory.getLogger(OrderItemMessageTranslator.class);
	
	@Inject
	private OrderService orderSerivce;
	
	/**
	 * @param orderIds
	 * @return
	 */
	public String transformToOrderItemMessage(Map<String, Object> orderIds)
	{
		String output = null;
		
		try
		{
			if (orderIds == null)
			{
				throw new Exception("OrderId cannot not be null.");
			}
			
			if(!orderIds.containsKey(OrderConstant.ORDER_ID))
			{
				throw new Exception("Cannot find a valid key name [id]");
			}
			if(orderIds.get("id") == null || !Long.class.isInstance(orderIds.get(OrderConstant.ORDER_ID)))
			{
				throw new Exception("OrderId is in wrong format");
			}
			
			output = orderSerivce.processCreateOrderMessage((Long)orderIds.get(OrderConstant.ORDER_ID));
		}
		catch (Exception e)
		{
			log.error("Order processing failed in transformToOrderItemMessage()", e);
		}
		
		return output;
	}
}
