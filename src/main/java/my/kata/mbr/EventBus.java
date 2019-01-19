package my.kata.mbr;

public interface EventBus<T extends DomainEvent> {

	void publish(T event);

}
