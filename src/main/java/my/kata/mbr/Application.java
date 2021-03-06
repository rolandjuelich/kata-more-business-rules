package my.kata.mbr;

import my.kata.mbr.order.command.ProcessOrder;
import my.kata.mbr.order.event.OrderProcessed;
import my.kata.mbr.order.event.OrderProcessingDelayed;
import my.kata.mbr.payment.PaymentService;
import my.kata.mbr.stock.StockService;

public class Application {

	private final EventBus<DomainEvent> events;
	private final StockService stock;
	private final PaymentService payment;

	public Application(final EventBus<DomainEvent> events, final StockService stock,
			final PaymentService payment) {
		this.events = events;
		this.stock = stock;
		this.payment = payment;
	}

	public void process(final ProcessOrder order) {

		if (!payment.receivedFor(order.id()) && !order.payedByCreditCard()) {
			final String reason = "no payment received yet";
			events.publish(new OrderProcessingDelayed(order.id(), reason));
			return;
		}

		if (!stock.goodsAvailableFor(order.id())) {
			final String reason = "order will be processed as soon as goods are available";
			events.publish(new OrderProcessingDelayed(order.id(), reason));
			return;
		}

		events.publish(new OrderProcessed(order.id()));

	}

}
