package my.kata.mbr;

public class Application {

	private final EventBus eventBus;

	public Application(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public void process(final CreditCardOrder command) {
		eventBus.publish(new OrderProcessed(command.orderId()));
	}

}
