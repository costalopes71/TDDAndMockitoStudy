package com.bigbox.b2csite.order.service.impl;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.bigbox.b2csite.common.DataAccessException;
import com.bigbox.b2csite.common.ServiceException;
import com.bigbox.b2csite.order.dao.OrderDao;
import com.bigbox.b2csite.order.model.domain.OrderSummary;
import com.bigbox.b2csite.order.model.entity.OrderEntity;
import com.bigbox.b2csite.order.model.transformer.OrderEntityToOrderSummaryTransformer;


public class OrderServiceImplTest {

	private final static long CUSTOMER_ID = 1;
	
	private OrderServiceImpl target = null;
	
	protected @Mock OrderDao mockOrderDao;
	protected @Mock OrderEntityToOrderSummaryTransformer mockTransformer;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		this.target = new OrderServiceImpl();
		this.target.setOrderDao(mockOrderDao);
		this.target.setTransformer(mockTransformer);
	}
	
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
	
	@Test
	public void test_openNewOrder_successfullyRetriesDataInsert() throws Exception {
		
		// Setup
		Mockito.when(mockOrderDao.insert(Mockito.any(OrderEntity.class)))
		.thenThrow(new DataAccessException("First Ex")).thenReturn(1);
		
		// Execution
		this.target.openNewOrder(CUSTOMER_ID);
		
		// Verification
		Mockito.verify(mockOrderDao, Mockito.times(2)).insert(Mockito.any(OrderEntity.class));
		
	}
	
	@Test(expected=ServiceException.class)
	public void test_openNewOrder_failedDataInsert() throws Exception {
		
		// Setup
		Mockito.when(mockOrderDao.insert(Mockito.any(OrderEntity.class)))
		.thenThrow(new DataAccessException("First Ex"))
		.thenThrow(new DataAccessException("Second Ex"));
		
		try {
			// Execution
			this.target.openNewOrder(CUSTOMER_ID);
		}
		finally {
			// Verification
			Mockito.verify(mockOrderDao, Mockito.times(2))
			.insert(Mockito.any(OrderEntity.class));
		}
	}
	
	@Test
	public void test_openNewOrder_success() throws Exception {
		
		// Setup
		Mockito.when(mockOrderDao.insert(Mockito.any(OrderEntity.class)))
		.thenReturn(1);
		
		// Execution
		String orderNumber = this.target.openNewOrder(CUSTOMER_ID);
		
		// Verification
		ArgumentCaptor<OrderEntity> orderEntityCaptor =
				ArgumentCaptor.forClass(OrderEntity.class);
		Mockito.verify(mockOrderDao).insert(orderEntityCaptor.capture());
		
		OrderEntity capturedOrderEntity = orderEntityCaptor.getValue();
		
		Assert.assertNotNull(capturedOrderEntity);
		Assert.assertEquals(CUSTOMER_ID, capturedOrderEntity.getCustomerId());
		Assert.assertEquals(orderNumber, capturedOrderEntity.getOrderNumber());
	}
}
