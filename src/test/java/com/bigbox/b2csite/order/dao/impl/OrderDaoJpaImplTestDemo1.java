package com.bigbox.b2csite.order.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.operation.DatabaseOperation;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.bigbox.b2csite.order.model.entity.OrderEntity;

public class OrderDaoJpaImplTestDemo1 extends BaseDBUnitTestForJPADao {

	private OrderDaoJpaImpl target = null;
	
	private DefaultDataSet dataSet = null;
	
	@Before
	public void setup() throws Exception {
		
		target = new OrderDaoJpaImpl();
		target.setEntityManager(entityManager);
		
		dataSet = new DefaultDataSet();
		
		DefaultTable orderSourceEntityTable = new DefaultTable("OrderSourceEntity", DBDataDef.ORDER_SOURCE_ENTITY_COLUMNS);
		Object[][] orderSourceRows = createOrderSourceRows();
		for (Object[] row : orderSourceRows) {
			orderSourceEntityTable.addRow(row);
		}
		dataSet.addTable(orderSourceEntityTable);
		
		DefaultTable orderEntityTable = new DefaultTable("OrderEntity", DBDataDef.ORDER_ENTITY_COLUMNS);
		Object[][] orderRows = createOrderRowData();
		for (Object[] row : orderRows) {
			orderEntityTable.addRow(row);
		}
		dataSet.addTable(orderEntityTable);
		
		DatabaseOperation.INSERT.execute(CONN, dataSet);
		
	}
	
	@After
	public void teardown() throws Exception {
		
		DatabaseOperation.DELETE.execute(CONN, dataSet);
		
	}
	
	@Test
	public void testFindByOrderSource() throws Exception {
		
		// Setup
		
		// Execution
		List<OrderEntity> orderList = target.findByOrderSource("wo");
		
		// Verification
		assertNotNull(orderList);
		assertEquals(1, orderList.size());
		assertEquals("ORD1", orderList.get(0).getOrderNumber());
		
	}
	
	private Object[][] createOrderSourceRows() {
		
		Object[][] orderSourceRows = new Object[][] {
				new Object[] {
					1,
					"so",
					"Store Order",
					"cbrown",
					new DateTime().withYear(2012).withMonthOfYear(12).withDayOfMonth(31).toDate()
				},
				new Object[] {
					2,
					"wo",
					"Web Order",
					"lvanpelt",
					new DateTime().withYear(2012).withMonthOfYear(12).withDayOfMonth(31).toDate()
				},
				new Object[] {
					3,
					"un",
					null,
					"lvanpelt",
					new DateTime().withYear(2013).withMonthOfYear(1).withDayOfMonth(1).toDate()
				}
			};
		return orderSourceRows;
	}
	
	private Object[][] createOrderRowData() {
		
		Object[][] orderRows = new Object[][] {
				new Object[] {
					1,
					"Customer 1 Order 1",
					"ORD1",
					1,
					new DateTime().withYear(2013).withMonthOfYear(12).withDayOfMonth(23).toDateMidnight().toDate(),
					250000,
					null,
					1,
					2	// Reference the web order
				},
				new Object[] {
					2,
					"Customer 1 Order 2",
					"ORD2",
					1,
					new DateTime().withYear(2013).withMonthOfYear(12).withDayOfMonth(23).toDateMidnight().toDate(),
					250000,
					new DateTime().withYear(2013).withMonthOfYear(12).withDayOfMonth(26).toDateMidnight().toDate(),
					1,
					1	// References the store order
				}
			};
		return orderRows;
	}
}
