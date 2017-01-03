package openperipheral.api.helpers;

import com.google.common.primitives.Ints;
import openperipheral.api.architecture.IArchitecture;

/**
 * Simple class for easy handling of not-zero indexed collections.
 * If used, OpenPeripheral will convert any index to zero-base value with script language specific offset.
 * For example, in Lua value 3 will become {@code Index(value = 2, offset = 1}.
 *
 * To create index with proper offset for given architecture use {@link IArchitecture#createIndex(int)}.
 *
 * Idea: Index(2,1) and Index(1,0) represent same index, but different numeric values
 */
public class Index extends Number implements Comparable<Index> {
	private static final long serialVersionUID = 2L;

	/**
	 * Zero-based value of index
	 */
	public final int value;

	public final int offset;

	private Index(int value, int offset) {
		this.value = value;
		this.offset = offset;
	}

	public static Index fromJava(int zeroBasedValue, int offset) {
		return new Index(zeroBasedValue, offset);
	}

	public static Index toJava(int nonZeroBasedValue, int offset) {
		return new Index(nonZeroBasedValue - offset, offset);
	}

	@Override
	public int intValue() {
		return value + offset;
	}

	@Override
	public long longValue() {
		return (long)value + offset;
	}

	@Override
	public float floatValue() {
		return (float)value + offset;
	}

	@Override
	public double doubleValue() {
		return (double)value + offset;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Index) && (value == ((Index)obj).value);
	}

	@Override
	public int compareTo(Index other) {
		return Ints.compare(this.value, other.value);
	}

	@Override
	public int hashCode() {
		return Ints.hashCode(value);
	}

	@Override
	public String toString() {
		return Integer.toString(value + offset);
	}

	public void checkElementIndex(String name, int size) {
		if (size < 0) throw new IllegalArgumentException("Negative size: " + size);

		if (value < 0) throw new IndexOutOfBoundsException(String.format("%s (%d) must be at least %d", name, value + offset, offset));
		if (value >= size) throw new IndexOutOfBoundsException(String.format("%s (%d) must be less than %d", name, value + offset, size + offset));
	}

	public void checkElementIndex(int size) {
		checkElementIndex("index", size);
	}

}
