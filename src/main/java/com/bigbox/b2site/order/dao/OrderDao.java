package com.bigbox.b2site.order.dao;

import java.util.List;

import com.bigbox.b2csite.order.model.entity.OrderEntity;
import com.bigbox.b2site.common.DataAccessException;

public interface OrderDao {

	OrderEntity findById(long id) throws DataAccessException;
	int insert(OrderEntity order) throws DataAccessException;
	
	List<OrderEntity> findOrdersByCustomer(long customerId) throws DataAccessException;
}