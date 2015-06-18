package openperipheral.tests;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.Map;

import openperipheral.api.converter.IConverter;
import openperipheral.api.struct.ScriptStruct;
import openperipheral.api.struct.ScriptStruct.Output;
import openperipheral.api.struct.StructField;
import openperipheral.converter.StructCache;
import openperipheral.converter.StructCache.IStructHandler;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Maps;

@RunWith(MockitoJUnitRunner.class)
public class StructTest {

	@Mock
	private IConverter converter;

	private void setupOutboundConverter(Object input, Object output) {
		when(converter.fromJava(input)).thenReturn(output);
	}

	private <T> void setupInboundConverter(Object input, Class<? extends T> expected, T output) {
		when(converter.toJava(input, (Type)expected)).thenReturn(output);
	}

	private void verifyInboundConversion(Object input, Class<?> cls) {
		verify(converter).toJava(input, (Type)cls);
	}

	private void verifyOutboundConversion(Object input) {
		verify(converter).fromJava(input);
	}

	private void assertInboundConversionFail(IStructHandler structConverter, Map<Object, Object> input, int indexOffset) {
		try {
			structConverter.toJava(converter, input, indexOffset);
			Assert.fail("Exception not thrown");
		} catch (RuntimeException e) {}
	}

	private void assertOutboundConversionFail(IStructHandler structConverter, Map<Object, Object> input, int indexOffset) {
		try {
			structConverter.toJava(converter, input, indexOffset);
			Assert.fail("Exception not thrown");
		} catch (IllegalArgumentException e) {}
	}

	public static final String SKIP_VALUE = "skip!";

	@ScriptStruct
	public static class SimpleStruct {

		@StructField
		public String a;

		@StructField
		public int b;

		public String skip = SKIP_VALUE;
	}

	@Test
	public void testNamedOutboundConversion() {
		final IStructHandler c = getConverter(SimpleStruct.class);

		SimpleStruct struct = new SimpleStruct();
		struct.a = "aaaa";
		struct.b = 2;

		final String resultA = "ca";
		setupOutboundConverter(struct.a, resultA);

		final String resultB = "cb";
		setupOutboundConverter(struct.b, resultB);

		Map<?, ?> fromJava = c.fromJava(converter, struct, 0);

		Map<Object, Object> result = Maps.newHashMap();
		result.put("a", resultA);
		result.put("b", resultB);

		Assert.assertEquals(result, fromJava);

		verifyOutboundConversion(struct.a);
		verifyOutboundConversion(struct.b);
	}

	protected IStructHandler getConverter(Class<?> cls) {
		StructCache cache = new StructCache();
		IStructHandler c = cache.getHandler(cls);
		return c;
	}

	@Test
	public void testNamedInboundConversion() {
		final IStructHandler c = getConverter(SimpleStruct.class);

		final String inputA = "ca";
		final String inputB = "zzzz";

		Map<Object, Object> input = Maps.newHashMap();
		input.put("a", inputA);
		input.put("b", inputB);

		final String resultA = "ca";
		final int resultB = 2;

		setupInboundConverter(inputA, String.class, resultA);
		setupInboundConverter(inputB, int.class, resultB);

		Object o = c.toJava(converter, input, 1);

		verifyInboundConversion(inputA, String.class);
		verifyInboundConversion(inputB, int.class);

		Assert.assertTrue(o instanceof SimpleStruct);

		SimpleStruct converted = (SimpleStruct)o;

		Assert.assertEquals(resultA, converted.a);
		Assert.assertEquals(resultB, converted.b);
		Assert.assertEquals(SKIP_VALUE, converted.skip);
	}

	@Test
	public void testNamedInboundConversionExtraFields() {
		final IStructHandler c = getConverter(SimpleStruct.class);

		final String inputA = "ca";
		final String inputB = "zzzz";
		final String inputExtra = "!!!!";

		Map<Object, Object> input = Maps.newHashMap();
		input.put("a", inputA);
		input.put("b", inputB);
		input.put("extra", inputExtra);

		final String resultA = "ca";
		final int resultB = 2;

		setupInboundConverter(inputA, String.class, resultA);
		setupInboundConverter(inputB, int.class, resultB);

		assertInboundConversionFail(c, input, 3);
	}

	@Test
	public void testNamedInboundConversionInvalidKey() {
		final IStructHandler c = getConverter(SimpleStruct.class);

		final String inputA = "ca";
		final String inputB = "zzzz";

		Map<Object, Object> input = Maps.newHashMap();
		input.put('a', inputA);
		input.put("b", inputB);

		assertInboundConversionFail(c, input, 4);
	}

	@Test
	public void testNamedInboundConversionMissingFields() {
		final IStructHandler c = getConverter(SimpleStruct.class);

		final String inputA = "ca";

		Map<Object, Object> input = Maps.newHashMap();
		input.put("a", inputA);

		final String resultA = "ca";

		setupInboundConverter(inputA, String.class, resultA);

		assertInboundConversionFail(c, input, 5);
	}

	@ScriptStruct
	public static class SimpleStructOptional {

		@StructField(isOptional = true)
		public String a = SKIP_VALUE;

		@StructField
		public int b;
	}

	@Test
	public void testNamedInboundConversionOptionalFields() {
		final IStructHandler c = getConverter(SimpleStructOptional.class);

		final String inputB = "ca";

		Map<Object, Object> input = Maps.newHashMap();
		input.put("b", inputB);

		final int resultB = 12;

		setupInboundConverter(inputB, int.class, resultB);

		Object o = c.toJava(converter, input, 6);

		verifyInboundConversion(inputB, int.class);

		Assert.assertTrue(o instanceof SimpleStructOptional);

		SimpleStructOptional converted = (SimpleStructOptional)o;

		Assert.assertEquals(resultB, converted.b);
		Assert.assertEquals(SKIP_VALUE, converted.a);

	}

	@ScriptStruct(defaultOutput = Output.TABLE)
	public static class SimpleTableDefaultOrdering {

		@StructField
		public String a;

		@StructField
		public int b;

		public String skip;
	}

	@Test
	public void testDefaultOrderedOutboundConversionOneIndexed() {
		final IStructHandler c = getConverter(SimpleTableDefaultOrdering.class);

		SimpleTableDefaultOrdering struct = new SimpleTableDefaultOrdering();
		struct.a = "aaaa";
		struct.b = 2;
		struct.skip = "zzzz";

		final int resultA = 5;
		setupOutboundConverter(struct.a, resultA);

		final String resultB = "cb";
		setupOutboundConverter(struct.b, resultB);

		Map<?, ?> fromJava = c.fromJava(converter, struct, 1);

		Map<Object, Object> result = Maps.newHashMap();
		result.put(1, resultA);
		result.put(2, resultB);

		Assert.assertEquals(result, fromJava);

		verifyOutboundConversion(struct.a);
		verifyOutboundConversion(struct.b);
	}

	@Test
	public void testDefaultOrderedOutboundConversionZeroIndexed() {
		final IStructHandler c = getConverter(SimpleTableDefaultOrdering.class);

		SimpleTableDefaultOrdering struct = new SimpleTableDefaultOrdering();
		struct.a = "aaaa";
		struct.b = 2;
		struct.skip = "zzzz";

		final int resultA = 5;
		setupOutboundConverter(struct.a, resultA);

		final String resultB = "cb";
		setupOutboundConverter(struct.b, resultB);

		Map<?, ?> fromJava = c.fromJava(converter, struct, 0);

		Map<Object, Object> result = Maps.newHashMap();
		result.put(0, resultA);
		result.put(1, resultB);

		Assert.assertEquals(result, fromJava);

		verifyOutboundConversion(struct.a);
		verifyOutboundConversion(struct.b);
	}

	@Test
	public void testDefaultOrderedInboundConversionZeroIndexed() {
		final IStructHandler c = getConverter(SimpleTableDefaultOrdering.class);

		final String inputA = "ca";
		final String inputB = "zzzz";

		Map<Object, Object> input = Maps.newHashMap();
		input.put(0, inputA);
		input.put(1, inputB);

		final String resultA = "ca";
		final int resultB = 2;

		setupInboundConverter(inputA, String.class, resultA);
		setupInboundConverter(inputB, int.class, resultB);

		Object o = c.toJava(converter, input, 0);

		verifyInboundConversion(inputA, String.class);
		verifyInboundConversion(inputB, int.class);

		Assert.assertTrue(o instanceof SimpleTableDefaultOrdering);

		SimpleTableDefaultOrdering converted = (SimpleTableDefaultOrdering)o;

		Assert.assertEquals(resultA, converted.a);
		Assert.assertEquals(resultB, converted.b);
	}

	@Test
	public void testDefaultOrderedInboundConversionOneIndexed() {
		final IStructHandler c = getConverter(SimpleTableDefaultOrdering.class);

		final String inputA = "ca";
		final String inputB = "zzzz";

		Map<Object, Object> input = Maps.newHashMap();
		input.put(1, inputA);
		input.put(2, inputB);

		final String resultA = "ca";
		final int resultB = 2;

		setupInboundConverter(inputA, String.class, resultA);
		setupInboundConverter(inputB, int.class, resultB);

		Object o = c.toJava(converter, input, 1);

		verifyInboundConversion(inputA, String.class);
		verifyInboundConversion(inputB, int.class);

		Assert.assertTrue(o instanceof SimpleTableDefaultOrdering);

		SimpleTableDefaultOrdering converted = (SimpleTableDefaultOrdering)o;

		Assert.assertEquals(resultA, converted.a);
		Assert.assertEquals(resultB, converted.b);
	}

	@Test
	public void testDefaultOrderedInboundConversionExtraFields() {
		final IStructHandler c = getConverter(SimpleTableDefaultOrdering.class);

		final String inputA = "ca";
		final String inputB = "zzzz";

		Map<Object, Object> input = Maps.newHashMap();
		input.put(1, inputA);
		input.put(2, inputB);
		input.put(3, "!!!!");

		final String resultA = "ca";
		final int resultB = 2;

		setupInboundConverter(inputA, String.class, resultA);
		setupInboundConverter(inputB, int.class, resultB);

		assertOutboundConversionFail(c, input, 1);
	}

	@ScriptStruct(defaultOutput = Output.TABLE)
	public static class SimpleTableForcedOrdering {

		@StructField(index = 2)
		public String a;

		@StructField(index = 0)
		public int b;

		@StructField(index = 4)
		public Float c;
	}

	@Test
	public void testCustomOrderedOutboundConversionZeroIndexed() {
		final IStructHandler c = getConverter(SimpleTableForcedOrdering.class);

		SimpleTableForcedOrdering struct = new SimpleTableForcedOrdering();
		struct.a = "aaaa";
		struct.b = 2;
		struct.c = 4.0f;

		final int resultA = 5;
		setupOutboundConverter(struct.a, resultA);

		final String resultB = "cb";
		setupOutboundConverter(struct.b, resultB);

		final float resultC = 0.2f;
		setupOutboundConverter(struct.c, resultC);

		Map<?, ?> fromJava = c.fromJava(converter, struct, 1);

		// Lua ordering
		Map<Object, Object> result = Maps.newHashMap();
		result.put(1, resultB);
		result.put(3, resultA);
		result.put(5, resultC);

		Assert.assertEquals(result, fromJava);

		verifyOutboundConversion(struct.a);
		verifyOutboundConversion(struct.b);
		verifyOutboundConversion(struct.c);
	}

	@Test
	public void testCustomOrderedOutboundConversionOneIndexed() {
		final IStructHandler c = getConverter(SimpleTableForcedOrdering.class);

		SimpleTableForcedOrdering struct = new SimpleTableForcedOrdering();
		struct.a = "aaaa";
		struct.b = 2;
		struct.c = 4.0f;

		final int resultA = 5;
		setupOutboundConverter(struct.a, resultA);

		final String resultB = "cb";
		setupOutboundConverter(struct.b, resultB);

		final float resultC = 0.2f;
		setupOutboundConverter(struct.c, resultC);

		Map<?, ?> fromJava = c.fromJava(converter, struct, 0);

		// Java ordering
		Map<Object, Object> result = Maps.newHashMap();
		result.put(0, resultB);
		result.put(2, resultA);
		result.put(4, resultC);

		Assert.assertEquals(result, fromJava);

		verifyOutboundConversion(struct.a);
		verifyOutboundConversion(struct.b);
		verifyOutboundConversion(struct.c);
	}

	@Test
	public void testCustomOrderedInboundConversion() {
		final IStructHandler c = getConverter(SimpleTableForcedOrdering.class);

		final String inputA = "ca";
		final String inputB = "zzzz";
		final int inputC = 999;

		Map<Object, Object> input = Maps.newHashMap();
		input.put(1, inputB);
		input.put(3, inputA);
		input.put(5, inputC);

		final String resultA = "ca";
		final int resultB = 2;
		final Float resultC = 2.4f;

		setupInboundConverter(inputA, String.class, resultA);
		setupInboundConverter(inputB, int.class, resultB);
		setupInboundConverter(inputC, Float.class, resultC);

		Object o = c.toJava(converter, input, 1);

		verifyInboundConversion(inputA, String.class);
		verifyInboundConversion(inputB, int.class);
		verifyInboundConversion(inputC, Float.class);

		Assert.assertTrue(o instanceof SimpleTableForcedOrdering);

		SimpleTableForcedOrdering converted = (SimpleTableForcedOrdering)o;

		Assert.assertEquals(resultA, converted.a);
		Assert.assertEquals(resultB, converted.b);
		Assert.assertEquals(resultC, converted.c);
	}

	@ScriptStruct(allowTableInput = false)
	public static class StructOnly {

		@StructField
		public String a;

		@StructField
		public int b;
	}

	@Test
	public void testStructOnly() {
		final IStructHandler c = getConverter(StructOnly.class);

		final String inputA = "ca";
		final String inputB = "zzzz";

		Map<Object, Object> input = Maps.newHashMap();
		input.put(1, inputB);
		input.put(2, inputA);

		assertInboundConversionFail(c, input, 1);

	}

}
