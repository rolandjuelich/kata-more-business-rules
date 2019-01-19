package my.kata.mbr.order.event;

import my.kata.mbr.DomainEvent;
import my.kata.mbr.order.OrderId;

public class OrderProcessingDelayed implements DomainEvent{

	private final OrderId orderId;
	private final String reason;

	public OrderProcessingDelayed(final OrderId orderId, final String reason) {
		this.orderId = orderId;
		this.reason = reason;
	}

	public OrderId orderId() {
		return orderId;
	}

	public String reason() {
		return reason;
	}

}
