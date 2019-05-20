package com.bigbox.b2csite.order.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.bigbox.b2csite.order.model.domain.OrderSummary;
import com.bigbox.b2csite.order.model.entity.OrderEntity;
import com.bigbox.b2csite.order.model.transformer.OrderEntityToOrderSummaryTransformer;
import com.bigbox.b2site.order.dao.OrderDao;
import com.bigbox.b2site.order.service.impl.OrderServiceImpl;

public class OrderServiceImplTest {

	private static final long CUSTOMER_ID = 1L;

	@Test
	public void testGetOrderSummarySuccess() throws Exception {
		
		// Setup
		OrderServiceImpl target = new OrderServiceImpl();
		
		OrderDao mockOrderDao = Mockito.mock(OrderDao.class);
		target.setOrderDao(mockOrderDao);
		
		OrderEntityToOrderSummaryTransformer mockTransformer = 
				Mockito.mock(OrderEntityToOrderSummaryTransformer.class);
		target.setTransformer(mockTransformer);
		
		OrderEntity orderEntityFixture = new OrderEntity();
		LinkedList<OrderEntity> orderEntityListFixture = new LinkedList<>();
		orderEntityListFixture.add(orderEntityFixture);
		
		Mockito.when(mockOrderDao.findOrdersByCustomer(CUSTOMER_ID))
			.thenReturn(orderEntityListFixture);
		
		OrderSummary orderSummaryFixture = new OrderSummary();
		Mockito.when(mockTransformer.transform(orderEntityFixture))
			.thenReturn(orderSummaryFixture);
		
		// Execution
		List<OrderSummary> result = target.getOrderSummary(CUSTOMER_ID);
		
		// Verification
		
		Mockito.verify(mockOrderDao).findOrdersByCustomer(CUSTOMER_ID);
		Mockito.verify(mockTransformer).transform(orderEntityFixture);
		
		assertNotNull(result);
		assertEquals(1, result.size());
		assertSame(orderSummaryFixture, result.get(0));
		
	}
	
	
}
