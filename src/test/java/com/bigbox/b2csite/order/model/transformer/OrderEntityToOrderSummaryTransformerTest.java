package com.bigbox.b2csite.order.model.transformer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.bigbox.b2csite.order.model.domain.OrderSummary;
import com.bigbox.b2csite.order.model.entity.OrderEntity;
import com.bigbox.b2csite.order.model.entity.OrderItemEntity;

public class OrderEntityToOrderSummaryTransformerTest {

	private OrderEntityToOrderSummaryTransformer target = null;
	
	@Before
	public void setup() {
		target = new OrderEntityToOrderSummaryTransformer();
	}
	
	@Test
	public void testTransformSucess() {
		
		String orderNumberFixture = UUID.randomUUID().toString();
		OrderEntity orderEntityFixture = new OrderEntity();
		orderEntityFixture.setOrderNumber(orderNumberFixture);
		orderEntityFixture.setOrderItemList(new LinkedList<OrderItemEntity>());
		
		OrderItemEntity itemFixture1 = new OrderItemEntity();
		itemFixture1.setQuantity(1);
		itemFixture1.setSellingPrice(new BigDecimal("10.00"));
		orderEntityFixture.getOrderItemList().add(itemFixture1);
		
		OrderItemEntity itemFixture2 = new OrderItemEntity();
		itemFixture2.setQuantity(2);
		itemFixture2.setSellingPrice(new BigDecimal("1.50"));
		orderEntityFixture.getOrderItemList().add(itemFixture2);
		
		OrderSummary result = target.transform(orderEntityFixture);
		
		assertNotNull(result);
		assertEquals(orderNumberFixture, result.getOrderNumber());
		assertEquals(3, result.getItemCount());
		assertEquals(new BigDecimal("13.00"), result.getTotalAmount());
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testTransformInputIsNull() {
		
		target.transform(null);
		
	}
	
	@Test
	public void testNoItemsInOrder() {
		
		String orderNumberFixture = UUID.randomUUID().toString();
		
		OrderEntity orderEntityFixture = new OrderEntity();
		orderEntityFixture.setOrderNumber(orderNumberFixture);
		orderEntityFixture.setOrderItemList(new LinkedList<OrderItemEntity>());
		
		OrderSummary result = target.transform(orderEntityFixture);
		
		assertNotNull(result);
		assertEquals(0, result.getItemCount());
		assertEquals(new BigDecimal("0.00"), result.getTotalAmount());
		
	}
	
}
