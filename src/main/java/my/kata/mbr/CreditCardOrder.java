package my.kata.mbr;

public class CreditCardOrder {

	private OrderId orderId;

	public CreditCardOrder(final OrderId order) {
		this.orderId = order;
	}

	public OrderId orderId() {
		return orderId;
	}

}
