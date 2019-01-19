package my.kata.mbr.order;

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import my.kata.mbr.Application;
import my.kata.mbr.DomainEvent;
import my.kata.mbr.EventBus;
import my.kata.mbr.order.command.ProcessOrder;
import my.kata.mbr.order.event.OrderProcessed;
import my.kata.mbr.order.event.OrderProcessingDelayed;
import my.kata.mbr.payment.PaymentService;
import my.kata.mbr.stock.StockService;

public class OrderProcessingAcceptanceTest {

	@SuppressWarnings("unchecked")
	private final EventBus<DomainEvent> eventBus = mock(EventBus.class);
	private final StockService stock = mock(StockService.class);
	private final PaymentService payment = mock(PaymentService.class);
	private final Application application = new Application(eventBus, stock, payment);

	@Test
	public void shouldWaitForPaymentOfNonCreditCardOrders() {
		// given
		final ProcessOrder order = someNonCreditCardOrder();
		given(payment.receivedFor(order.id())).willReturn(false);

		// when
		application.process(order);

		// then
		final OrderProcessingDelayed event = catchEvent(OrderProcessingDelayed.class);
		assertThat(event.reason()).isEqualTo("no payment received yet");
	}

	@Test
	public void shouldProcessOrdersIfPaymentArrived() {
		// given
		final ProcessOrder order = someNonCreditCardOrder();
		given(payment.receivedFor(order.id())).willReturn(true);
		given(stock.goodsAvailableFor(order.id())).willReturn(true);

		// when
		application.process(order);

		// then
		final OrderProcessed event = catchEvent(OrderProcessed.class);
		assertThat(event.orderId()).isEqualTo(order.id());
	}

	@Test
	public void shouldProcessOrderImmediatlyIfPayedByCreditCard() {
		// given
		final ProcessOrder order = someCreditCardOrder();
		given(stock.goodsAvailableFor(order.id())).willReturn(true);

		// when
		application.process(order);

		// then
		final OrderProcessed event = catchEvent(OrderProcessed.class);
		assertThat(event.orderId()).isEqualTo(order.id());
	}

	@Test
	public void shouldDelayProcessingCreditCardOrdersWhileGoodsAreNotInStock() {
		// given
		final ProcessOrder order = someCreditCardOrder();
		given(stock.goodsAvailableFor(order.id())).willReturn(false);

		// when
		application.process(order);

		// then
		final OrderProcessingDelayed event = catchEvent(OrderProcessingDelayed.class);
		assertThat(event.orderId()).isEqualTo(order.id());
		assertThat(event.reason()).isEqualTo("order will be processed as soon as goods are available");
	}

	private static ProcessOrder someNonCreditCardOrder() {
		boolean payedByCreditCard = false;
		return new ProcessOrder(OrderId.of(nextInt()), payedByCreditCard);
	}

	private static ProcessOrder someCreditCardOrder() {
		boolean payedByCreditCard = true;
		return new ProcessOrder(OrderId.of(nextInt()), payedByCreditCard);
	}

	private <T extends DomainEvent> T catchEvent(final Class<T> clazz) {
		final ArgumentCaptor<T> published = ArgumentCaptor.forClass(clazz);
		verify(eventBus).publish(published.capture());
		return published.getValue();
	}
}
