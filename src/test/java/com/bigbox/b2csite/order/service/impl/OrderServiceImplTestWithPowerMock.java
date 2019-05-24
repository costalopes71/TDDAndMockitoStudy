package com.bigbox.b2csite.order.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.bigbox.b2csite.order.dao.OrderDao;
import com.bigbox.b2csite.order.integration.WarehouseManagementService;
import com.bigbox.b2csite.order.model.domain.OrderCompletionAudit;
import com.bigbox.b2csite.order.model.entity.OrderEntity;
import com.bigbox.b2csite.order.model.entity.OrderItemEntity;
import com.bigbox.b2csite.order.model.message.OrderMessage;
import com.bigbox.b2csite.order.model.transformer.OrderEntityToOrderSummaryTransformer;

@RunWith(PowerMockRunner.class)
@PrepareForTest(value= {WarehouseManagementService.class, OrderServiceImpl.class})
public class OrderServiceImplTestWithPowerMock {

	private final static long CUSTOMER_ID = 1;
	private final static long ORDER_ID = 2L;
	private final static String ORDER_NUMBER = "1234";
	
	private OrderServiceImpl target = null;

	@Mock
	protected OrderDao mockOrderDao;
	@Mock
	protected OrderEntityToOrderSummaryTransformer mockTransformer;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		this.target = new OrderServiceImpl();
		this.target.setOrderDao(mockOrderDao);
		this.target.setTransformer(mockTransformer);
	}
	
	@Test
	public void testCompleteOrderSuccess() throws Exception {
		
		//
		// setup
		//
		
		OrderItemEntity itemFixture1 = new OrderItemEntity();
		itemFixture1.setSku("SKU1");
		itemFixture1.setQuantity(1);
		
		OrderItemEntity itemFixture2 = new OrderItemEntity();
		itemFixture2.setSku("SKU2");
		itemFixture2.setQuantity(2);
		
		OrderEntity orderEntityFixture = new OrderEntity();
		orderEntityFixture.setOrderNumber(ORDER_NUMBER);
		orderEntityFixture.setOrderItemList(new LinkedList<OrderItemEntity>());
		orderEntityFixture.getOrderItemList().add(itemFixture1);
		orderEntityFixture.getOrderItemList().add(itemFixture2);
		
		Mockito.when(mockOrderDao.findById(ORDER_ID)).thenReturn(orderEntityFixture);
		
		// static mocking
		PowerMockito.mockStatic(WarehouseManagementService.class);
		
		PowerMockito.when(WarehouseManagementService.sendOrder(Matchers.any(OrderMessage.class))).thenReturn(true);
		
		// mocking object intantiations
		OrderCompletionAudit orderAuditFixture = new OrderCompletionAudit();
		PowerMockito.whenNew(OrderCompletionAudit.class).withAnyArguments().thenReturn(orderAuditFixture);
		// ou
		// PowerMockito.whenNew(OrderCompletionAudit.class).withNoArguments().thenReturn(orderAuditFixture);
		
		//
		// execucao
		//
		target.completeOrder(ORDER_ID);
		
		//
		// verificacao
		//
		
		Mockito.verify(mockOrderDao).findById(ORDER_ID);
		
		PowerMockito.verifyStatic();
		ArgumentCaptor<OrderMessage> orderMessageCaptor = ArgumentCaptor.forClass(OrderMessage.class);
		
		WarehouseManagementService.sendOrder(orderMessageCaptor.capture());
		
		OrderMessage capturedOrderMessage = orderMessageCaptor.getValue();
		
		assertNotNull(capturedOrderMessage);
		assertEquals(ORDER_NUMBER, capturedOrderMessage.getOrderNumber());
		assertEquals(ORDER_NUMBER, orderAuditFixture.getOrderNumber());
		assertNotNull(orderAuditFixture.getCompletionDate());
		
	}
	
}
