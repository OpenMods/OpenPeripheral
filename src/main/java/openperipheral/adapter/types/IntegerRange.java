package openperipheral.adapter.types;

public class IntegerRange implements IRange {

	public static class Bound {
		public final int value;
		public final boolean isOpen;

		public Bound(int value, boolean isOpen) {
			this.value = value;
			this.isOpen = isOpen;
		}
	}

	public final Bound lower;

	public final Bound upper;

	public IntegerRange(Bound lower, Bound upper) {
		final boolean shouldSwap = lower != null && upper != null && lower.value > upper.value;
		this.lower = shouldSwap? upper : lower;
		this.upper = shouldSwap? lower : upper;
	}

	@Override
	public String describe() {
		StringBuilder result = new StringBuilder();
		if (lower != null) {
			result.append(lower.isOpen? "(" : "[").append(lower.value);
		} else {
			result.append("(");
		}

		result.append("..");

		if (upper != null) {
			result.append(upper.value).append(upper.isOpen? ")" : "]");
		} else {
			result.append(")");
		}

		return result.toString();
	}

	public static IntegerRange leftBounded(int value, boolean isOpen) {
		return new IntegerRange(new Bound(value, isOpen), null);
	}

	public static IntegerRange rightBounded(int value, boolean isOpen) {
		return new IntegerRange(null, new Bound(value, isOpen));
	}

	public static IntegerRange closed(int left, int right) {
		return new IntegerRange(new Bound(left, false), new Bound(right, false));
	}

	public static IntegerRange open(int left, int right) {
		return new IntegerRange(new Bound(left, true), new Bound(right, true));
	}

	public static IntegerRange leftOpen(int left, int right) {
		return new IntegerRange(new Bound(left, true), new Bound(right, false));
	}

	public static IntegerRange rightOpen(int left, int right) {
		return new IntegerRange(new Bound(left, false), new Bound(right, true));
	}
}
