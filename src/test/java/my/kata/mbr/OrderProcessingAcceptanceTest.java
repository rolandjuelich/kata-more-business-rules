package my.kata.mbr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.apache.commons.lang3.RandomUtils;
import org.assertj.core.api.Assertions;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class OrderProcessingAcceptanceTest {

	private final EventBus eventBus = mock(EventBus.class);
	private final Application application = new Application(eventBus);

	@Test
	public void shouldProcessCreditCardOrdersImmediatly() {
		// given
		OrderId someOrderId = OrderId.of(RandomUtils.nextInt());

		// when
		application.process(new CreditCardOrder(someOrderId));

		// then
		final ArgumentCaptor<OrderProcessed> published = ArgumentCaptor.forClass(OrderProcessed.class);
		verify(eventBus).publish(published.capture());
		assertThat(published.getValue().orderId()).isInstanceOf(OrderId.class).isEqualTo(someOrderId);
	}

	@Test
	@Ignore
	public void shouldDelayProcessingCreditCardOrdersWhileGoodsAreNotInStock() {
		Assertions.fail("not yet");
	}
}
