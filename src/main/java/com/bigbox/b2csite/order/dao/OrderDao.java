package com.bigbox.b2csite.order.dao;

import java.util.List;

import com.bigbox.b2csite.common.DataAccessException;
import com.bigbox.b2csite.order.model.entity.OrderEntity;

public interface OrderDao {

	int insertReturningInt(OrderEntity order) throws DataAccessException;
	
	List<OrderEntity> findOrdersByCustomer(long customerId) throws DataAccessException;

	OrderEntity findById(long orderId) throws DataAccessException;

	OrderEntity insert(OrderEntity order) throws DataAccessException;

	OrderEntity update(OrderEntity order) throws DataAccessException;

	void remove(OrderEntity order) throws DataAccessException;

	// Other finder operations
	List<OrderEntity> findByCustomerId(long customerId) throws DataAccessException;

	List<OrderEntity> findByOrderSource(String orderSourceCode) throws DataAccessException;
}