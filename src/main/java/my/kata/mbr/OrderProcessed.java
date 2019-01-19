package my.kata.mbr;

public class OrderProcessed {

	private final OrderId orderId;

	public OrderProcessed(final OrderId orderId) {
		this.orderId = orderId;
	}

	public OrderId orderId() {
		return orderId;
	}

}
