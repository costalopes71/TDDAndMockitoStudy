package com.bigbox.b2csite.order.service.impl;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.bigbox.b2csite.common.DataAccessException;
import com.bigbox.b2csite.common.ServiceException;
import com.bigbox.b2csite.order.dao.OrderDao;
import com.bigbox.b2csite.order.model.domain.OrderSummary;
import com.bigbox.b2csite.order.model.entity.OrderEntity;
import com.bigbox.b2csite.order.model.transformer.OrderEntityToOrderSummaryTransformer;
import com.bigbox.b2csite.order.service.impl.OrderServiceImpl;

public class OrderServiceImplTest {

	private static final long CUSTOMER_ID = 1L;
	
	private OrderServiceImpl target = new OrderServiceImpl(); 
	
	@Mock
	OrderDao mockOrderDao;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		target.setOrderDao(mockOrderDao);
	}
	
	@Test
	public void testGetOrderSummarySuccess() throws Exception {
		
		// Setup
		
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
	
	@Test
	public void testOpenNewOrderSuccessfullyRetriesDataInsert() throws Exception {
		
		// setup
		
		Mockito.when(mockOrderDao.insert(Mockito.any(OrderEntity.class)))
			.thenThrow(new DataAccessException("first exception"))
			.thenReturn(1);
		
		//execution
		
		target.openNewOrder(CUSTOMER_ID);
		
		// verification
		
		Mockito.verify(mockOrderDao, Mockito.times(2)).insert(Mockito.any(OrderEntity.class));
		
	}
	
	@Test(expected=ServiceException.class)
	public void testOpenNewOrderFailedDataInsert() throws Exception {
		
		// setup
		Mockito.when(mockOrderDao.insert(Mockito.any(OrderEntity.class)))
			.thenThrow(new DataAccessException("first exception"))
			.thenThrow(new DataAccessException("second excepetion"));
		
		// execution
		
		try {
			
			target.openNewOrder(CUSTOMER_ID);
			
		} finally {
			
			Mockito.verify(mockOrderDao, Mockito.times(2)).insert(Mockito.any(OrderEntity.class));
			
		}
		
	}
	
	@Test
	public void testOpenNewOrderSuccess() throws Exception {
		
		// setup
		
		Mockito.when(mockOrderDao.insert(Mockito.any()))
			.thenReturn(1);

		// execution
		
		String orderNumber = target.openNewOrder(CUSTOMER_ID);
		
		// verification
		
		ArgumentCaptor<OrderEntity> orderEntityCaptor = ArgumentCaptor.forClass(OrderEntity.class);
		
		Mockito.verify(mockOrderDao).insert(orderEntityCaptor.capture());
		
		OrderEntity capturedOrderEntity = orderEntityCaptor.getValue();
		
		assertNotNull(capturedOrderEntity);
		assertEquals(CUSTOMER_ID, capturedOrderEntity.getCustomerId());
		assertEquals(orderNumber, capturedOrderEntity.getOrderNumber());
		
	}
	
}
