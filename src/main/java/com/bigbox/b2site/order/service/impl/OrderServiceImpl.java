package com.bigbox.b2site.order.service.impl;

import java.util.LinkedList;
import java.util.List;

import com.bigbox.b2csite.order.model.domain.OrderSummary;
import com.bigbox.b2csite.order.model.entity.OrderEntity;
import com.bigbox.b2csite.order.model.transformer.OrderEntityToOrderSummaryTransformer;
import com.bigbox.b2site.common.DataAccessException;
import com.bigbox.b2site.common.ServiceException;
import com.bigbox.b2site.order.dao.OrderDao;
import com.bigbox.b2site.order.service.OrderService;

public class OrderServiceImpl implements OrderService {

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

}
