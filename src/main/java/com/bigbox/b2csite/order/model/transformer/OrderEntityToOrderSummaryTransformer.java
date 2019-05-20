package com.bigbox.b2csite.order.model.transformer;

import java.math.BigDecimal;

import com.bigbox.b2csite.order.model.domain.OrderSummary;
import com.bigbox.b2csite.order.model.entity.OrderEntity;
import com.bigbox.b2csite.order.model.entity.OrderItemEntity;

public class OrderEntityToOrderSummaryTransformer {

	public OrderSummary transform(OrderEntity orderEntity) {
		
		if (orderEntity == null) {
			throw new IllegalArgumentException("orderEntity should not be null");
		}
		
		OrderSummary orderSumamryResult = new OrderSummary(); 
		
		orderSumamryResult.setOrderNumber(orderEntity.getOrderNumber());
		BigDecimal totalAmount = new BigDecimal("0.00");
		
		int itemCount = 0;
		for (OrderItemEntity item : orderEntity.getOrderItemList()) {
			
			itemCount += item.getQuantity();
			
			BigDecimal quantityBD = new BigDecimal(item.getQuantity());
			BigDecimal totalItem = item.getSellingPrice().multiply(quantityBD);
			totalAmount = totalAmount.add(totalItem);
			
		}
		
		orderSumamryResult.setTotalAmount(totalAmount);
		orderSumamryResult.setItemCount(itemCount);
		
		return orderSumamryResult;
	}

}
