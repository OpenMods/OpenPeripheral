package openperipheral.api.helpers;

public class Index extends Number implements Comparable<Index> {
	private static final long serialVersionUID = 1L;

	public int offset;

	public final int value;

	public Index(int value, int offset) {
		this.value = value - offset;
		this.offset = offset;
	}

	public Index(int value) {
		this.value = value;
	}

	public Integer box() {
		return Integer.valueOf(value);
	}

	public int unbox() {
		return value;
	}

	@Override
	public int intValue() {
		return value + offset;
	}

	@Override
	public long longValue() {
		return intValue();
	}

	@Override
	public float floatValue() {
		return intValue();
	}

	@Override
	public double doubleValue() {
		return intValue();
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Index) && (value == ((Index)obj).value);
	}

	@Override
	public int compareTo(Index other) {
		return Integer.compare(this.value, other.value);
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(value);
	}

	@Override
	public String toString() {
		return "(" + value + "+" + offset + ")";
	}

	public void checkElementIndex(int size, int index) {
		if (size < 0) throw new IllegalArgumentException("Negative size: " + size);

		if (index < 0) throw new IndexOutOfBoundsException(String.format("Index %d must be larger than %d", index + offset, offset));
		if (index >= size) throw new IndexOutOfBoundsException(String.format("Index %d less than %d", index + offset, size + offset));
	}

}
