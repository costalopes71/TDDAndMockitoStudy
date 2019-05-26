package com.bigbox.b2csite.order.dao.impl;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.math.BigDecimal;

import javax.persistence.EntityTransaction;

import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bigbox.b2csite.order.model.entity.OrderEntity;
import com.bigbox.b2csite.order.model.entity.OrderItemEntity;

public class OrderDaoJpaImplTest_Demo3 extends BaseDBUnitTestForJPADao {
	
	private final static class DataFiles {
		@SuppressWarnings("unused")
		private final static String XML_DATA_SET = 
				"com/bigbox/b2csite/order/dao/impl/OrderDaoJpaImplTest_XMLDataSet.xml";
		private final static String FLAT_XML_DATA_SET = 
				"com/bigbox/b2csite/order/dao/impl/OrderDaoJpaImplTest_FlatXMLDataSet.xml";
		@SuppressWarnings("unused")
		private final static String XLS_DATA_SET = 
				"com/bigbox/b2csite/order/dao/impl/OrderDaoJpaImplTest_XlsDataSet.xls";
	}
	
	private OrderDaoJpaImpl target = null;
	
	IDataSet dataSet = null;
	
	@Before
	public void setup() throws Exception {
		
		target = new OrderDaoJpaImpl();
		target.setEntityManager(entityManager);
		
		// Add data set initialization
		InputStream is =
			ClassLoader.getSystemResourceAsStream(DataFiles.FLAT_XML_DATA_SET);
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		dataSet = builder.build(is);
		
		DatabaseOperation.INSERT.execute(CONN, dataSet);
	}
	
	@After
	public void teardown() throws Exception {
		DatabaseOperation.DELETE.execute(CONN, dataSet);
	}
	
	@Test
	public void testInsert() throws Exception {
		
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		OrderEntity orderEntityFixture = entityManager.find(OrderEntity.class, Long.valueOf(1));
		OrderItemEntity itemFixture = new OrderItemEntity();
		itemFixture.setOwningOrder(orderEntityFixture);
		itemFixture.setQuantity(2);
		itemFixture.setSellingPrice(new BigDecimal("10.00"));
		itemFixture.setSku("Item1SKU");
		
		entityManager.persist(itemFixture);
		transaction.commit();
		
		QueryDataSet queryDataSet = new QueryDataSet(CONN);
		String queryString = "SELECT * FROM OrderItemEntity WHERE owningOrder_Id = 1";
		queryDataSet.addTable("OrderItemEntity", queryString);
		
		DatabaseOperation.REFRESH.execute(CONN, queryDataSet);
		
		ITable orderItemTable = queryDataSet.getTable("OrderItemEntity");
		assertEquals(1, orderItemTable.getRowCount());
		assertOrderItemTable(itemFixture, orderItemTable, 0);
		
		DatabaseOperation.DELETE.execute(CONN, queryDataSet);
		
	}
	
	private void assertOrderItemTable(OrderItemEntity orderItemEntity, ITable orderItemTable, int row) throws DataSetException {
		
		Assert.assertEquals(String.valueOf(orderItemEntity.getId()), orderItemTable.getValue(row, "id").toString());
		Assert.assertEquals(String.valueOf(orderItemEntity.getOwningOrder().getId()), orderItemTable.getValue(row, "owningOrder_id").toString());
		Assert.assertEquals(Integer.valueOf(orderItemEntity.getQuantity()), orderItemTable.getValue(row, "quantity"));
		Assert.assertEquals(orderItemEntity.getSellingPrice(), (BigDecimal) orderItemTable.getValue(row, "sellingPrice"));
		Assert.assertEquals(orderItemEntity.getSku(), orderItemTable.getValue(row, "sku"));
	}
}
