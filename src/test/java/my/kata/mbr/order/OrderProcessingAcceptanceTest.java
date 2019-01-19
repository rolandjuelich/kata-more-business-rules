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
import my.kata.mbr.order.OrderId;
import my.kata.mbr.order.command.ProcessOrder;
import my.kata.mbr.order.event.OrderProcessed;
import my.kata.mbr.order.event.OrderProcessingDelayed;
import my.kata.mbr.stock.StockService;

public class OrderProcessingAcceptanceTest {

	@SuppressWarnings("unchecked")
	private final EventBus<DomainEvent> eventBus = mock(EventBus.class);
	private final StockService stock = mock(StockService.class);
	private final Application application = new Application(eventBus, stock);

	@Test
	public void shouldProcessCreditCardOrdersImmediatly() {
		// given
		final OrderId someOrderId = OrderId.of(nextInt());
		final ProcessOrder command = new ProcessOrder(someOrderId);

		// when
		application.process(command);

		// then
		final OrderProcessed event = catchEvent(OrderProcessed.class);
		assertThat(event.orderId()).isEqualTo(someOrderId);
	}


	@Test
	public void shouldDelayProcessingCreditCardOrdersWhileGoodsAreNotInStock() {
		// given
		final OrderId someOrderId = OrderId.of(nextInt());
		final ProcessOrder command = new ProcessOrder(someOrderId);
		given(stock.goodsNotAvailable(someOrderId)).willReturn(true);

		// when
		application.process(command);

		// then
		final OrderProcessingDelayed event = catchEvent(OrderProcessingDelayed.class);
		assertThat(event.orderId()).isEqualTo(someOrderId);
		assertThat(event.reason()).isEqualTo("order will be processed as soon as goods are available");
	}
	
	private <T extends DomainEvent> T catchEvent(final Class<T> clazz) {
		final ArgumentCaptor<T> published = ArgumentCaptor.forClass(clazz);
		verify(eventBus).publish(published.capture());
		return published.getValue();
	}
}
