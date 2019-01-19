package my.kata.mbr.order.command;

import my.kata.mbr.order.OrderId;

public class ProcessOrder {

	private final OrderId orderId;

	public ProcessOrder(final OrderId orderId) {
		this.orderId = orderId;
	}

	public OrderId orderId() {
		return orderId;
	}

}
