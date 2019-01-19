package my.kata.mbr.order.command;

import my.kata.mbr.order.OrderId;

public class ProcessOrder {

	private final OrderId orderId;
	private boolean payedByCreditCard;

	public ProcessOrder(final OrderId orderId, boolean payedByCreditCard) {
		this.orderId = orderId;
		this.payedByCreditCard = payedByCreditCard;
	}

	public OrderId orderId() {
		return orderId;
	}

	public boolean payedByCreditCard() {
		return payedByCreditCard;
	}

}
