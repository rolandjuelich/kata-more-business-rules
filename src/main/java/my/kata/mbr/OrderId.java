package my.kata.mbr;

public final class OrderId {

	private int value;

	private OrderId(int value) {
		this.value = value;
	}

	public static OrderId of(int value) {
		return new OrderId(value);
	}

	public int value() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + value;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderId other = (OrderId) obj;
		if (value != other.value)
			return false;
		return true;
	}
	
	
}
