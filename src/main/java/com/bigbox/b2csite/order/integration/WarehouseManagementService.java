package com.bigbox.b2csite.order.integration;

import com.bigbox.b2csite.order.model.message.OrderMessage;

public class WarehouseManagementService {

	public static boolean sendOrder(OrderMessage orderMessage) throws WMSUnavailableException {
		throw new WMSUnavailableException("WMS is currently down for unknown reason");
	}
}
