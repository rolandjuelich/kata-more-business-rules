package my.kata.mbr;

import my.kata.mbr.order.OrderId;
import my.kata.mbr.order.OrderService;
import my.kata.mbr.order.command.ProcessOrder;
import my.kata.mbr.order.event.OrderProcessed;
import my.kata.mbr.order.event.OrderProcessingDelayed;
import my.kata.mbr.stock.StockService;

public class Application {

	private final EventBus<DomainEvent> domainEvents;
	private final StockService stock;
	private final OrderService orders;

	public Application(final EventBus<DomainEvent> domainEvents, final StockService stock, final OrderService orders) {
		this.domainEvents = domainEvents;
		this.stock = stock;
		this.orders = orders;
	}

	public void process(final ProcessOrder order) {

		final OrderId orderId = order.orderId();

		if (!order.payedByCreditCard() && !orders.paymentComplete(orderId)) {
			return;
		}

		if (!stock.goodsAvailable(orderId)) {
			final String reason = "order will be processed as soon as goods are available";
			domainEvents.publish(new OrderProcessingDelayed(orderId, reason));
			return;
		}

		domainEvents.publish(new OrderProcessed(orderId));

	}

}
