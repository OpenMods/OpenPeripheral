package openperipheral.tests;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import openperipheral.api.converter.IConverter;
import openperipheral.api.struct.ScriptStruct;
import openperipheral.api.struct.ScriptStruct.Output;
import openperipheral.api.struct.StructField;
import openperipheral.converter.*;
import openperipheral.converter.StructHandlerProvider.IFieldHandler;
import openperipheral.converter.StructHandlerProvider.IStructHandler;
import openperipheral.converter.StructHandlerProvider.InvalidStructureException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
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
		} catch (RuntimeException e) {}
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
		verifyFieldOrder(c, "a", "b");

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

	private static void verifyFieldOrder(IStructHandler c, String... fields) {
		final List<String> names = Lists.newArrayList();
		for (IFieldHandler handler : c.fields())
			names.add(handler.name());

		Assert.assertEquals(Arrays.asList(fields), names);
	}

	protected IStructHandler getConverter(Class<?> cls) {
		StructHandlerProvider cache = new StructHandlerProvider();
		IStructHandler c = cache.getHandler(cls);
		return c;
	}

	@Test
	public void testNamedInboundConversion() {
		final IStructHandler c = getConverter(SimpleStruct.class);
		verifyFieldOrder(c, "a", "b");

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
		verifyFieldOrder(c, "a", "b");

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
		verifyFieldOrder(c, "a", "b");

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
		verifyFieldOrder(c, "a", "b");

		final String inputA = "ca";

		Map<Object, Object> input = Maps.newHashMap();
		input.put("a", inputA);

		final String resultA = "ca";

		setupInboundConverter(inputA, String.class, resultA);

		assertInboundConversionFail(c, input, 5);
	}

	@ScriptStruct
	public static class SimpleStructOptional {

		@StructField(optional = true)
		public String a = SKIP_VALUE;

		@StructField
		public int b;
	}

	@Test
	public void testNamedInboundConversionOptionalFields() {
		final IStructHandler c = getConverter(SimpleStructOptional.class);
		verifyFieldOrder(c, "a", "b");

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
		verifyFieldOrder(c, "a", "b");

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
		verifyFieldOrder(c, "a", "b");

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
		verifyFieldOrder(c, "a", "b");

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
		verifyFieldOrder(c, "a", "b");

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
		verifyFieldOrder(c, "a", "b");

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

		@StructField(index = 1)
		public String a;

		@StructField(index = 0)
		public int b;

		@StructField(index = 2)
		public Float c;
	}

	@Test
	public void testCustomOrderedOutboundConversionZeroIndexed() {
		final IStructHandler c = getConverter(SimpleTableForcedOrdering.class);
		verifyFieldOrder(c, "b", "a", "c");

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
		result.put(2, resultA);
		result.put(3, resultC);

		Assert.assertEquals(result, fromJava);

		verifyOutboundConversion(struct.a);
		verifyOutboundConversion(struct.b);
		verifyOutboundConversion(struct.c);
	}

	@Test
	public void testCustomOrderedOutboundConversionOneIndexed() {
		final IStructHandler c = getConverter(SimpleTableForcedOrdering.class);
		verifyFieldOrder(c, "b", "a", "c");

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
		result.put(1, resultA);
		result.put(2, resultC);

		Assert.assertEquals(result, fromJava);

		verifyOutboundConversion(struct.a);
		verifyOutboundConversion(struct.b);
		verifyOutboundConversion(struct.c);
	}

	@Test
	public void testCustomOrderedInboundConversion() {
		final IStructHandler c = getConverter(SimpleTableForcedOrdering.class);
		verifyFieldOrder(c, "b", "a", "c");

		final String inputA = "ca";
		final String inputB = "zzzz";
		final int inputC = 999;

		Map<Object, Object> input = Maps.newHashMap();
		input.put(1, inputB);
		input.put(2, inputA);
		input.put(3, inputC);

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

	public static class GenericBase1<A, B> {
		@StructField
		public A fieldA;

		@StructField
		public B fieldB;
	}

	public static class GenericBase2<A, B, C, D> extends GenericBase1<C, D> {
		@StructField
		public A fieldC;

		@StructField
		public B fieldD;
	}

	@ScriptStruct
	public static class GenericDerrived extends GenericBase2<Integer, String, Boolean, Float> {}

	@Test
	public void testGenericBaseOutboundConversion() {
		final IStructHandler c = getConverter(GenericDerrived.class);
		verifyFieldOrder(c, "fieldA", "fieldB", "fieldC", "fieldD");

		GenericDerrived struct = new GenericDerrived();
		struct.fieldA = false;
		struct.fieldB = 3.5f;
		struct.fieldC = 34;
		struct.fieldD = "Hello";

		final int resultA = 5;
		setupOutboundConverter(struct.fieldA, resultA);

		final String resultB = "cb";
		setupOutboundConverter(struct.fieldB, resultB);

		final float resultC = 0.2f;
		setupOutboundConverter(struct.fieldC, resultC);

		final boolean resultD = false;
		setupOutboundConverter(struct.fieldD, resultD);

		Map<?, ?> fromJava = c.fromJava(converter, struct, 0);

		Map<Object, Object> result = Maps.newHashMap();
		result.put("fieldA", resultA);
		result.put("fieldB", resultB);
		result.put("fieldC", resultC);
		result.put("fieldD", resultD);

		Assert.assertEquals(result, fromJava);

		verifyOutboundConversion(struct.fieldA);
		verifyOutboundConversion(struct.fieldB);
		verifyOutboundConversion(struct.fieldC);
		verifyOutboundConversion(struct.fieldD);
	}

	@Test
	public void testGenericBaseInboundConversion() {
		final IStructHandler c = getConverter(GenericDerrived.class);
		verifyFieldOrder(c, "fieldA", "fieldB", "fieldC", "fieldD");

		final Boolean inputA = false;
		final Float inputB = 999.3f;
		final Integer inputC = 3214;
		final String inputD = "zzzz";

		Map<Object, Object> input = Maps.newHashMap();
		input.put("fieldA", inputA);
		input.put("fieldB", inputB);
		input.put("fieldC", inputC);
		input.put("fieldD", inputD);

		final Boolean resultA = true;
		final Float resultB = 9432.4f;
		final Integer resultC = 5425;
		final String resultD = "sfs";

		setupInboundConverter(inputA, Boolean.class, resultA);
		setupInboundConverter(inputB, Float.class, resultB);
		setupInboundConverter(inputC, Integer.class, resultC);
		setupInboundConverter(inputD, String.class, resultD);

		Object o = c.toJava(converter, input, 1);

		verifyInboundConversion(inputA, Boolean.class);
		verifyInboundConversion(inputB, Float.class);
		verifyInboundConversion(inputC, Integer.class);
		verifyInboundConversion(inputD, String.class);

		Assert.assertTrue(o instanceof GenericDerrived);

		GenericDerrived converted = (GenericDerrived)o;

		Assert.assertEquals(resultA, converted.fieldA);
		Assert.assertEquals(resultB, converted.fieldB);
		Assert.assertEquals(resultC, converted.fieldC);
		Assert.assertEquals(resultD, converted.fieldD);
	}

	@ScriptStruct
	public static class DuplicateManualIndex {

		@StructField(index = 1)
		public String a;

		@StructField(index = 1)
		public int b;

		@StructField(index = 0)
		public Float c;
	}

	@Test(expected = InvalidStructureException.class)
	public void testDuplicateManualIndex() {
		getConverter(DuplicateManualIndex.class);
	}

	@ScriptStruct
	public static class DuplicateAutomaticIndex {

		@StructField(index = 1)
		public String a;

		@StructField()
		public int b;

		@StructField(index = 0)
		public Float c;
	}

	@Test(expected = InvalidStructureException.class)
	public void testDuplicateAutomaticIndex() {
		getConverter(DuplicateAutomaticIndex.class);
	}

	@ScriptStruct
	public static class NegativeIndex {
		@StructField(index = -1)
		public String a;

		@StructField()
		public int b;
	}

	@Test(expected = InvalidStructureException.class)
	public void testNegativeIndex() {
		getConverter(NegativeIndex.class);
	}

	@ScriptStruct
	public static class NonContinuousIndex {
		@StructField
		public String a;

		@StructField(index = 2)
		public int b;
	}

	@Test(expected = InvalidStructureException.class)
	public void testNonContinuousIndex() {
		getConverter(NonContinuousIndex.class);
	}

}
