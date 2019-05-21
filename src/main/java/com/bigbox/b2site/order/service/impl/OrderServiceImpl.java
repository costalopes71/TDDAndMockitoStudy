package com.bigbox.b2site.order.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.bigbox.b2csite.order.model.domain.OrderSummary;
import com.bigbox.b2csite.order.model.entity.OrderEntity;
import com.bigbox.b2csite.order.model.transformer.OrderEntityToOrderSummaryTransformer;
import com.bigbox.b2site.common.DataAccessException;
import com.bigbox.b2site.common.ServiceException;
import com.bigbox.b2site.order.dao.OrderDao;
import com.bigbox.b2site.order.service.OrderService;

public class OrderServiceImpl implements OrderService {

	private static final int MAX_INSERT_ATTEMP = 2;
	private OrderDao orderDao = null;
	private OrderEntityToOrderSummaryTransformer transformer = null;
	
	public void setOrderDao(final OrderDao orderDao) {
		this.orderDao = orderDao;
	}
	
	public void setTransformer(final OrderEntityToOrderSummaryTransformer transformer) {
		this.transformer = transformer;
	}
	
	@Override
	public List<OrderSummary> getOrderSummary(long customerId) throws ServiceException {
		
		LinkedList<OrderSummary> resultList = new LinkedList<>();
		
		try {
			
			List<OrderEntity> orderEntityList = this.orderDao.findOrdersByCustomer(customerId);
			
			orderEntityList.forEach(order -> resultList.add(transformer.transform(order)));
			
		} catch (DataAccessException e) {

			throw new ServiceException("Data access error occurred.", e);
			
		}
		
		return resultList;
	}

	public String openNewOrder(long customerId) throws ServiceException {
		
		OrderEntity newOrderEntity = new OrderEntity();
		newOrderEntity.setCustomerId(customerId);
		newOrderEntity.setOrderNumber(UUID.randomUUID().toString());
		
		boolean insertSuccessful = false;
		int insertAttempt = 1;
		while (!insertSuccessful && insertAttempt <= MAX_INSERT_ATTEMP) {
			
			try {
				
				int resultValue = orderDao.insert(newOrderEntity);
				if (resultValue == 1) {
					insertSuccessful = true;
				} else {
					++insertAttempt;
				}
				
			} catch (DataAccessException e) {

				++insertAttempt;
			
			}
			
		}
		
		if (!insertSuccessful) {
			throw new ServiceException("Data access error prevented creation of order");
		}
		
		return newOrderEntity.getOrderNumber();
	}
	
}
