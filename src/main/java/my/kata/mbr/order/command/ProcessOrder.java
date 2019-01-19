package my.kata.mbr.order.command;

import my.kata.mbr.order.OrderId;

public class ProcessOrder {

	private final OrderId id;
	private boolean payedByCreditCard;

	public ProcessOrder(final OrderId orderId, boolean payedByCreditCard) {
		this.id = orderId;
		this.payedByCreditCard = payedByCreditCard;
	}

	public OrderId id() {
		return id;
	}

	public boolean payedByCreditCard() {
		return payedByCreditCard;
	}

}
