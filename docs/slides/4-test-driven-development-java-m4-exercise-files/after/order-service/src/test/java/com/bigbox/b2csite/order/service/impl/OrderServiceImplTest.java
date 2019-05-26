package com.bigbox.b2csite.order.service.impl;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.bigbox.b2csite.order.dao.OrderDao;
import com.bigbox.b2csite.order.model.domain.OrderSummary;
import com.bigbox.b2csite.order.model.entity.OrderEntity;
import com.bigbox.b2csite.order.model.transformer.OrderEntityToOrderSummaryTransformer;


public class OrderServiceImplTest {

	private final static long CUSTOMER_ID = 1;
	
	@Test
	public void test_getOrderSummary_success() throws Exception {
		
		OrderServiceImpl target = new OrderServiceImpl();
		
		// Setup
		OrderDao mockOrderDao = Mockito.mock(OrderDao.class);
		target.setOrderDao(mockOrderDao);
		
		OrderEntityToOrderSummaryTransformer mockTransformer =
				Mockito.mock(OrderEntityToOrderSummaryTransformer.class);
		target.setTransformer(mockTransformer);
		
		OrderEntity orderEntityFixture = new OrderEntity();
		List<OrderEntity> orderEntityFixtureList = new LinkedList<>();
		orderEntityFixtureList.add(orderEntityFixture);
		
		Mockito.when(mockOrderDao.findOrdersByCustomer(CUSTOMER_ID))
		.thenReturn(orderEntityFixtureList);
		
		OrderSummary orderSummaryFixture = new OrderSummary();
		
		Mockito.when(mockTransformer.transform(orderEntityFixture))
		.thenReturn(orderSummaryFixture);
		
		// Execution
		List<OrderSummary> result = target.getOrderSummary(CUSTOMER_ID);
		
		// Verification
		Mockito.verify(mockOrderDao).findOrdersByCustomer(CUSTOMER_ID);
		Mockito.verify(mockTransformer).transform(orderEntityFixture);
		
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertSame(orderSummaryFixture, result.get(0));
		
	}
}
