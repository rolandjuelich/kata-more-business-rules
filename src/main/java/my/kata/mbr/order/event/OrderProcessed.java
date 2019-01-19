package my.kata.mbr.order.event;

import my.kata.mbr.DomainEvent;
import my.kata.mbr.order.OrderId;

public class OrderProcessed implements DomainEvent {

	private final OrderId orderId;

	public OrderProcessed(final OrderId orderId) {
		this.orderId = orderId;
	}

	public OrderId orderId() {
		return orderId;
	}

}
