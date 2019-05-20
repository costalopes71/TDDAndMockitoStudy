package com.bigbox.b2site.order.service;

import java.util.List;

import com.bigbox.b2csite.order.model.domain.OrderSummary;
import com.bigbox.b2site.common.ServiceException;

public interface OrderService {

	List<OrderSummary> getOrderSummary(long customerId) throws ServiceException;
}
