package openperipheral.tests;

import openperipheral.adapter.types.IntegerRange;
import org.junit.Assert;
import org.junit.Test;

public class MiscTests {

	private static void testRange(String expected, IntegerRange range) {
		Assert.assertEquals(expected, range.describe());
	}

	@Test
	public void testIntegerRange() {
		testRange("(..)", new IntegerRange(null, null));
		testRange("(3..)", IntegerRange.leftBounded(3, true));
		testRange("[3..)", IntegerRange.leftBounded(3, false));
		testRange("(..5)", IntegerRange.rightBounded(5, true));
		testRange("(..5]", IntegerRange.rightBounded(5, false));
		testRange("(3..5)", IntegerRange.open(5, 3));
		testRange("(3..5)", IntegerRange.open(3, 5));
		testRange("[3..5]", IntegerRange.closed(3, 5));
		testRange("(3..5]", IntegerRange.leftOpen(3, 5));
		testRange("[3..5)", IntegerRange.rightOpen(3, 5));
	}

}
