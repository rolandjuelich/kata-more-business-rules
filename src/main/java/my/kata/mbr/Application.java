package my.kata.mbr;

import my.kata.mbr.order.OrderId;
import my.kata.mbr.order.command.ProcessOrder;
import my.kata.mbr.order.event.OrderProcessed;
import my.kata.mbr.order.event.OrderProcessingDelayed;
import my.kata.mbr.stock.StockService;

public class Application {

	private final EventBus<DomainEvent> domainEvents;
	private final StockService stock;

	public Application(final EventBus<DomainEvent> domainEvents, final StockService stock) {
		this.domainEvents = domainEvents;
		this.stock = stock;
	}

	public void process(final ProcessOrder command) {

		final OrderId orderId = command.orderId();

		if (stock.goodsNotAvailable(orderId)) {
			final String reason = "order will be processed as soon as goods are available";
			domainEvents.publish(new OrderProcessingDelayed(orderId, reason));
			return;
		}

		domainEvents.publish(new OrderProcessed(orderId));

	}

}
