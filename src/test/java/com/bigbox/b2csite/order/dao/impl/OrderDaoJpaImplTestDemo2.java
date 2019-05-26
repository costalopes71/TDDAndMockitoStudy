package com.bigbox.b2csite.order.dao.impl;

import java.io.InputStream;
import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bigbox.b2csite.order.model.entity.OrderEntity;

public class OrderDaoJpaImplTestDemo2 extends BaseDBUnitTestForJPADao {
	
	private final static class DataFiles {
		@SuppressWarnings("unused")
		private final static String XML_DATA_SET = 
				"com/bigbox/b2csite/order/dao/impl/OrderDaoJpaImplTest_XMLDataSet.xml";
		private final static String FLAT_XML_DATA_SET = 
				"com/bigbox/b2csite/order/dao/impl/OrderDaoJpaImplTest_FlatXMLDataSet.xml";
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
		
		// using excel file for defining the data
		InputStream is = ClassLoader.getSystemResourceAsStream(DataFiles.XLS_DATA_SET);
		dataSet = new XlsDataSet(is);
		
		// OR
		
		// using XML flat file to define de data
		InputStream xmlIs = ClassLoader.getSystemResourceAsStream(DataFiles.FLAT_XML_DATA_SET);
		dataSet = new FlatXmlDataSetBuilder().build(xmlIs);
		
		DatabaseOperation.INSERT.execute(CONN, dataSet);
	}
	
	@After
	public void teardown() throws Exception {
		DatabaseOperation.DELETE.execute(CONN, dataSet);
	}
	
	@Test
	public void testFindByOrderSource() throws Exception {
		
		List<OrderEntity> resultList = target.findByOrderSource("wo");
		
		Assert.assertNotNull(resultList);
		Assert.assertEquals(1, resultList.size());
		Assert.assertEquals("ORD1", resultList.get(0).getOrderNumber());
	}
}
