package my.kata.mbr.order;

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import my.kata.mbr.Application;
import my.kata.mbr.DomainEvent;
import my.kata.mbr.EventBus;
import my.kata.mbr.order.command.ProcessOrder;
import my.kata.mbr.order.event.OrderProcessed;
import my.kata.mbr.order.event.OrderProcessingDelayed;
import my.kata.mbr.stock.StockService;

public class OrderProcessingAcceptanceTest {

	@SuppressWarnings("unchecked")
	private final EventBus<DomainEvent> eventBus = mock(EventBus.class);
	private final StockService stock = mock(StockService.class);
	private final OrderService orders = mock(OrderService.class);
	private final Application application = new Application(eventBus, stock, orders);

	@Test
	public void shouldWaitForPaymentOfNonCreditCardOrders() {
		// given
		final ProcessOrder command = someNonCreditCardOrder();
		given(orders.paymentComplete(command.orderId())).willReturn(false);

		// when
		application.process(command);

		// then
		verify(eventBus, never()).publish(Mockito.any(DomainEvent.class));
	}
	
	@Test
	public void shouldProcessOrdersIfPaymentArrived() {
		// given
		final ProcessOrder command = someNonCreditCardOrder();
		given(orders.paymentComplete(command.orderId())).willReturn(true);
		given(stock.goodsAvailable(command.orderId())).willReturn(true);
		
		// when
		application.process(command);
		
		// then
		final OrderProcessed event = catchEvent(OrderProcessed.class);
		assertThat(event.orderId()).isEqualTo(command.orderId());
	}

	@Test
	public void shouldProcessOrderImmediatlyIfPayedByCreditCard() {
		// given
		final ProcessOrder command = someCreditCardOrder();
		given(stock.goodsAvailable(command.orderId())).willReturn(true);

		// when
		application.process(command);

		// then
		final OrderProcessed event = catchEvent(OrderProcessed.class);
		assertThat(event.orderId()).isEqualTo(command.orderId());
	}

	@Test
	public void shouldDelayProcessingCreditCardOrdersWhileGoodsAreNotInStock() {
		// given
		final ProcessOrder command = someCreditCardOrder();
		given(stock.goodsAvailable(command.orderId())).willReturn(false);

		// when
		application.process(command);

		// then
		final OrderProcessingDelayed event = catchEvent(OrderProcessingDelayed.class);
		assertThat(event.orderId()).isEqualTo(command.orderId());
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
