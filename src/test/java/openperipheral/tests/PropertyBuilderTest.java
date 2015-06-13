package openperipheral.tests;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import openperipheral.adapter.*;
import openperipheral.adapter.IMethodDescription.IArgumentDescription;
import openperipheral.adapter.property.PropertyListBuilder;
import openperipheral.adapter.types.TypeHelper;
import openperipheral.api.Constants;
import openperipheral.api.adapter.IIndexedPropertyCallback;
import openperipheral.api.adapter.IPropertyCallback;
import openperipheral.api.adapter.IndexedCallbackProperty.GetFromFieldType;
import openperipheral.api.adapter.method.ArgType;
import openperipheral.api.converter.IConverter;
import openperipheral.api.helpers.Index;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

public class PropertyBuilderTest {

	private static final boolean IS_DELEGATING = true;

	private static final boolean READ_ONLY = true;

	private static final boolean READ_WRITE = false;

	private static final boolean NOT_NULLABLE = false;

	private static final boolean NULLABLE = true;

	private static final boolean NOT_EXPANDABLE = false;

	private static final String SOURCE = "sourcezzz";

	private static class Value {}

	private static class ConvertedValue {}

	private static class Key {}

	private static class ConvertedKey {}

	public static class FieldSource {
		public int intField;

		public String stringField;

		public Map<String, Integer> mapField;

		public List<String> listField;

		public boolean[] arrayField;
	}

	private static Field getTargetField(String name) {
		try {
			return FieldSource.class.getField(name);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	public final Field intField = getTargetField("intField");

	public final Field stringField = getTargetField("stringField");

	public final Field mapField = getTargetField("mapField");

	public final Field listField = getTargetField("listField");

	public final Field arrayField = getTargetField("arrayField");

	private static List<IMethodExecutor> buildPropertyList(PropertyListBuilder builder) {
		List<IMethodExecutor> output = Lists.newArrayList();
		builder.addMethods(output);
		return output;
	}

	private static List<IMethodExecutor> buildPropertyListAndCheckSize(PropertyListBuilder builder, final int expectedSize) {
		List<IMethodExecutor> output = buildPropertyList(builder);
		Assert.assertEquals(expectedSize, output.size());
		return output;
	}

	private static IMethodExecutor findExecutor(String target, Iterable<IMethodExecutor> executors) {
		for (IMethodExecutor e : executors)
			if (e.description().getNames().contains(target)) return e;

		Assert.fail("Can't find method named " + target);
		return null;
	}

	private static IMethodExecutor findAndVerifyExecutor(String target, Iterable<IMethodExecutor> executors) {
		final IMethodExecutor executor = findExecutor(target, executors);
		checkMethodEnv(executor);
		return executor;
	}

	private static void checkMethodEnv(IMethodExecutor executor) {
		Assert.assertTrue(executor.requiredEnv().containsKey(Constants.ARG_CONVERTER));
		Assert.assertTrue(executor.requiredEnv().get(Constants.ARG_CONVERTER).equals(IConverter.class));
	}

	private static void checkDescription(IMethodExecutor executor, String returnType, ArgType... args) {
		final IMethodDescription description = executor.description();
		Assert.assertEquals(returnType, description.returnTypes().describe());

		final List<IArgumentDescription> arguments = description.arguments();
		Assert.assertEquals(args.length, arguments.size());
		for (int i = 0; i < args.length; i++)
			Assert.assertEquals(TypeHelper.single(args[i]).describe(), arguments.get(i).type().describe());
	}

	private static void checkDescriptionRaw(IMethodExecutor executor, String returnType, String... args) {
		final IMethodDescription description = executor.description();
		Assert.assertEquals(returnType, description.returnTypes().describe());

		final List<IArgumentDescription> arguments = description.arguments();
		Assert.assertEquals(args.length, arguments.size());
		for (int i = 0; i < args.length; i++)
			Assert.assertEquals(args[i], arguments.get(i).type().describe());
	}

	private static void checkParamOptionality(IMethodExecutor executor, int index, boolean isOptional) {
		final List<IArgumentDescription> arguments = executor.description().arguments();
		Assert.assertEquals(isOptional, arguments.get(index).optional());
	}

	private static void verifySingleGetterExecution(IMethodExecutor executor, Field targetField) {
		final IPropertyCallback target = mock(IPropertyCallback.class);
		final IConverter converter = mock(IConverter.class);
		final IMethodCall call = executor.startCall(target);
		call.setEnv(Constants.ARG_CONVERTER, converter);

		final Value markerValue = new Value();
		when(target.getField(any(Field.class))).thenReturn(markerValue);

		final ConvertedValue markerConvertedValue = new ConvertedValue();
		when(converter.fromJava(any())).thenReturn(markerConvertedValue);

		try {
			final Object[] result = call.call();
			Assert.assertArrayEquals(new Object[] { markerConvertedValue }, result);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}

		verify(target).getField(targetField);
		verify(converter).fromJava(markerValue);
	}

	private static void verifySingleSetterExecution(IMethodExecutor executor, Field targetField) {
		final IPropertyCallback target = mock(IPropertyCallback.class);
		final IConverter converter = Mockito.mock(IConverter.class);
		final IMethodCall call = executor.startCall(target);
		call.setEnv(Constants.ARG_CONVERTER, converter);

		final ConvertedValue markerConvertedValue = new ConvertedValue();
		when(converter.toJava(any(), any(Type.class))).thenReturn(markerConvertedValue);

		final Value markerValue = new Value();
		try {
			final Object[] result = call.call(markerValue);
			Assert.assertArrayEquals(new Object[0], result);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}

		verify(converter).toJava(markerValue, targetField.getGenericType());
		verify(target).setField(targetField, markerConvertedValue);
	}

	private static void verifySingleNullableSetterExecution(IMethodExecutor executor, Field targetField) {
		final IPropertyCallback target = mock(IPropertyCallback.class);
		final IConverter converter = Mockito.mock(IConverter.class);
		final IMethodCall call = executor.startCall(target);
		call.setEnv(Constants.ARG_CONVERTER, converter);

		try {
			final Object[] result = call.call((Object)null);
			Assert.assertArrayEquals(new Object[0], result);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}

		verifyNoMoreInteractions(converter);
		verify(target).setField(targetField, null);
	}

	@Test
	public void testSingleGetterOnly() {
		PropertyListBuilder builder = new PropertyListBuilder(intField, SOURCE);
		builder.addSingle("test", "", "", IS_DELEGATING, READ_ONLY, NOT_NULLABLE, ArgType.AUTO);

		List<IMethodExecutor> output = buildPropertyListAndCheckSize(builder, 1);

		{
			IMethodExecutor executor = findAndVerifyExecutor("getTest", output);
			checkDescription(executor, "number");
			verifySingleGetterExecution(executor, intField);
		}
	}

	@Test
	public void testSingleGetterSetter() {
		PropertyListBuilder builder = new PropertyListBuilder(intField, SOURCE);
		builder.addSingle("test", "", "", IS_DELEGATING, READ_WRITE, NOT_NULLABLE, ArgType.STRING);

		List<IMethodExecutor> output = buildPropertyListAndCheckSize(builder, 2);

		{
			IMethodExecutor executor = findAndVerifyExecutor("getTest", output);
			checkDescription(executor, "string");
			verifySingleGetterExecution(executor, intField);
		}

		{
			IMethodExecutor executor = findAndVerifyExecutor("setTest", output);
			checkDescription(executor, "()", ArgType.STRING);
			verifySingleSetterExecution(executor, intField);
		}
	}

	@Test
	public void testSingleNullableGetterSetter() {
		PropertyListBuilder builder = new PropertyListBuilder(stringField, SOURCE);
		builder.addSingle("test", "", "", IS_DELEGATING, READ_WRITE, NULLABLE, ArgType.STRING);

		List<IMethodExecutor> output = buildPropertyListAndCheckSize(builder, 2);

		{
			IMethodExecutor executor = findAndVerifyExecutor("getTest", output);
			checkDescription(executor, "string");
			verifySingleGetterExecution(executor, stringField);
		}

		{
			IMethodExecutor executor = findAndVerifyExecutor("setTest", output);
			checkDescription(executor, "()", ArgType.STRING);
			verifySingleNullableSetterExecution(executor, stringField);
		}
	}

	private static void verifyIndexedGetterExecution(IMethodExecutor executor, Field targetField, Type keyType) {
		final IIndexedPropertyCallback target = mock(IIndexedPropertyCallback.class);
		final IConverter converter = mock(IConverter.class);
		final IMethodCall call = executor.startCall(target);
		call.setEnv(Constants.ARG_CONVERTER, converter);

		final ConvertedKey convertedIndexValue = new ConvertedKey();
		when(converter.toJava(any(), any(Type.class))).thenReturn(convertedIndexValue);

		final Value markerValue = new Value();
		when(target.getField(any(Field.class), any())).thenReturn(markerValue);

		final ConvertedValue markerConvertedValue = new ConvertedValue();
		when(converter.fromJava(any())).thenReturn(markerConvertedValue);

		final Value indexValue = new Value();

		try {
			final Object[] result = call.call(indexValue);
			Assert.assertArrayEquals(new Object[] { markerConvertedValue }, result);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}

		verify(converter).toJava(indexValue, keyType);
		verify(target).getField(targetField, convertedIndexValue);
		verify(converter).fromJava(markerValue);
	}

	private static void verifyIndexedSetterExecution(IMethodExecutor executor, Field targetField, Type keyType, Type valueType) {
		final IIndexedPropertyCallback target = mock(IIndexedPropertyCallback.class);
		final IConverter converter = Mockito.mock(IConverter.class);
		final IMethodCall call = executor.startCall(target);
		call.setEnv(Constants.ARG_CONVERTER, converter);

		final ConvertedKey markerConvertedKey = new ConvertedKey();
		when(converter.toJava(any(), eq(keyType))).thenReturn(markerConvertedKey);

		final ConvertedValue markerConvertedValue = new ConvertedValue();
		when(converter.toJava(any(), eq(valueType))).thenReturn(markerConvertedValue);

		final Value markerValue = new Value();
		final Key markerKey = new Key();

		try {
			final Object[] result = call.call(markerValue, markerKey);
			Assert.assertArrayEquals(new Object[0], result);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}

		verify(converter).toJava(markerValue, valueType);
		verify(converter).toJava(markerKey, keyType);
		verify(target).setField(targetField, markerConvertedKey, markerConvertedValue);
	}

	private static void verifyIndexedNullableSetterExecution(IMethodExecutor executor, Field targetField, Type keyType) {
		final IIndexedPropertyCallback target = mock(IIndexedPropertyCallback.class);
		final IConverter converter = Mockito.mock(IConverter.class);
		final IMethodCall call = executor.startCall(target);
		call.setEnv(Constants.ARG_CONVERTER, converter);

		final ConvertedKey markerConvertedKey = new ConvertedKey();
		when(converter.toJava(any(), eq(keyType))).thenReturn(markerConvertedKey);

		final Key markerKey = new Key();

		try {
			final Object[] result = call.call(null, markerKey);
			Assert.assertArrayEquals(new Object[0], result);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}

		verify(converter).toJava(markerKey, keyType);
		verify(target).setField(targetField, markerConvertedKey, null);
	}

	@Test
	public void testIndexedGetterOnly() {
		PropertyListBuilder builder = new PropertyListBuilder(mapField, SOURCE);
		builder.addIndexed("Hello", "", "", IS_DELEGATING, READ_ONLY, NOT_NULLABLE, NOT_EXPANDABLE, GetFromFieldType.class, ArgType.AUTO, GetFromFieldType.class, ArgType.AUTO);

		List<IMethodExecutor> output = buildPropertyListAndCheckSize(builder, 1);

		{
			IMethodExecutor executor = findAndVerifyExecutor("getHello", output);
			checkDescription(executor, "number", ArgType.STRING);
			checkParamOptionality(executor, 0, false);
			verifyIndexedGetterExecution(executor, mapField, String.class);
		}
	}

	@Test
	public void testIndexedGetterOnlyWithCustomTypes() {
		PropertyListBuilder builder = new PropertyListBuilder(intField, SOURCE);
		builder.addIndexed("Hello", "", "", IS_DELEGATING, READ_ONLY, NOT_NULLABLE, NOT_EXPANDABLE, float.class, ArgType.AUTO, String.class, ArgType.AUTO);

		List<IMethodExecutor> output = buildPropertyListAndCheckSize(builder, 1);

		{
			IMethodExecutor executor = findAndVerifyExecutor("getHello", output);
			checkDescription(executor, "string", ArgType.NUMBER);
			checkParamOptionality(executor, 0, false);
			verifyIndexedGetterExecution(executor, intField, float.class);
		}
	}

	@Test
	public void testIndexedGetterOnlyWithCustomDocTypes() {
		PropertyListBuilder builder = new PropertyListBuilder(mapField, SOURCE);
		builder.addIndexed("Hello", "", "", IS_DELEGATING, READ_ONLY, NOT_NULLABLE, NOT_EXPANDABLE, GetFromFieldType.class, ArgType.BOOLEAN, GetFromFieldType.class, ArgType.TABLE);

		List<IMethodExecutor> output = buildPropertyListAndCheckSize(builder, 1);

		{
			IMethodExecutor executor = findAndVerifyExecutor("getHello", output);
			checkDescription(executor, "table", ArgType.BOOLEAN);
			checkParamOptionality(executor, 0, false);
			verifyIndexedGetterExecution(executor, mapField, String.class);
		}
	}

	@Test
	public void testIndexedGetterSetter() {
		PropertyListBuilder builder = new PropertyListBuilder(listField, SOURCE);
		builder.addIndexed("Hello", "", "", IS_DELEGATING, READ_WRITE, NOT_NULLABLE, NOT_EXPANDABLE, GetFromFieldType.class, ArgType.AUTO, GetFromFieldType.class, ArgType.AUTO);

		List<IMethodExecutor> output = buildPropertyListAndCheckSize(builder, 2);

		{
			IMethodExecutor executor = findAndVerifyExecutor("getHello", output);
			checkDescription(executor, "string", ArgType.NUMBER);
			checkParamOptionality(executor, 0, false);
			verifyIndexedGetterExecution(executor, listField, Index.class);
		}

		{
			IMethodExecutor executor = findAndVerifyExecutor("setHello", output);
			checkDescription(executor, "()", ArgType.STRING, ArgType.NUMBER);
			checkParamOptionality(executor, 1, false);
			verifyIndexedSetterExecution(executor, listField, Index.class, String.class);
		}
	}

	@Test
	public void testIndexedNullableGetterSetter() {
		PropertyListBuilder builder = new PropertyListBuilder(listField, SOURCE);
		builder.addIndexed("Hello", "", "", IS_DELEGATING, READ_WRITE, NULLABLE, NOT_EXPANDABLE, GetFromFieldType.class, ArgType.AUTO, GetFromFieldType.class, ArgType.AUTO);

		List<IMethodExecutor> output = buildPropertyListAndCheckSize(builder, 2);

		{
			IMethodExecutor executor = findAndVerifyExecutor("getHello", output);
			checkDescription(executor, "string", ArgType.NUMBER);
			checkParamOptionality(executor, 0, false);
			verifyIndexedGetterExecution(executor, listField, Index.class);
		}

		{
			IMethodExecutor executor = findAndVerifyExecutor("setHello", output);
			checkDescription(executor, "()", ArgType.STRING, ArgType.NUMBER);
			checkParamOptionality(executor, 1, false);
			verifyIndexedNullableSetterExecution(executor, listField, Index.class);
		}
	}

	@Test
	public void testMergedGetterOnly() {
		PropertyListBuilder builder = new PropertyListBuilder(mapField, SOURCE);
		builder.addSingle("Hello", "", "", IS_DELEGATING, READ_ONLY, NOT_NULLABLE, ArgType.AUTO);
		builder.addIndexed("Hello", "", "", IS_DELEGATING, READ_ONLY, NOT_NULLABLE, NOT_EXPANDABLE, GetFromFieldType.class, ArgType.AUTO, GetFromFieldType.class, ArgType.AUTO);

		List<IMethodExecutor> output = buildPropertyListAndCheckSize(builder, 1);

		{
			IMethodExecutor executor = findAndVerifyExecutor("getHello", output);
			checkDescription(executor, "{string->number}|number", ArgType.STRING);
			checkParamOptionality(executor, 0, true);
			verifySingleGetterExecution(executor, mapField);
			verifyIndexedGetterExecution(executor, mapField, String.class);
		}
	}

	@Test
	public void testMergedGetterSingleSetter() {
		PropertyListBuilder builder = new PropertyListBuilder(mapField, SOURCE);
		builder.addSingle("Hello", "", "", IS_DELEGATING, READ_WRITE, NOT_NULLABLE, ArgType.AUTO);
		builder.addIndexed("Hello", "", "", IS_DELEGATING, READ_ONLY, NOT_NULLABLE, NOT_EXPANDABLE, GetFromFieldType.class, ArgType.AUTO, GetFromFieldType.class, ArgType.AUTO);

		List<IMethodExecutor> output = buildPropertyListAndCheckSize(builder, 2);

		{
			IMethodExecutor executor = findAndVerifyExecutor("getHello", output);
			checkDescription(executor, "{string->number}|number", ArgType.STRING);
			checkParamOptionality(executor, 0, true);
			verifySingleGetterExecution(executor, mapField);
			verifyIndexedGetterExecution(executor, mapField, String.class);
		}

		{
			IMethodExecutor executor = findAndVerifyExecutor("setHello", output);
			checkDescriptionRaw(executor, "()", "{string->number}");
			verifySingleSetterExecution(executor, mapField);
		}
	}

	@Test
	public void testMergedGetterIndexedSetter() {
		PropertyListBuilder builder = new PropertyListBuilder(mapField, SOURCE);
		builder.addSingle("Hello", "", "", IS_DELEGATING, READ_ONLY, NOT_NULLABLE, ArgType.AUTO);
		builder.addIndexed("Hello", "", "", IS_DELEGATING, READ_WRITE, NOT_NULLABLE, NOT_EXPANDABLE, GetFromFieldType.class, ArgType.AUTO, GetFromFieldType.class, ArgType.AUTO);

		List<IMethodExecutor> output = buildPropertyListAndCheckSize(builder, 2);

		{
			IMethodExecutor executor = findAndVerifyExecutor("getHello", output);
			checkDescription(executor, "{string->number}|number", ArgType.STRING);
			checkParamOptionality(executor, 0, true);
			verifySingleGetterExecution(executor, mapField);
			verifyIndexedGetterExecution(executor, mapField, String.class);
		}

		{
			IMethodExecutor executor = findAndVerifyExecutor("setHello", output);
			checkDescription(executor, "()", ArgType.NUMBER, ArgType.STRING);
			checkParamOptionality(executor, 1, false);
			verifyIndexedSetterExecution(executor, mapField, String.class, Integer.class);
		}
	}

	@Test
	public void testMergedGetterSetter() {
		PropertyListBuilder builder = new PropertyListBuilder(mapField, SOURCE);
		builder.addSingle("Hello", "", "", IS_DELEGATING, READ_WRITE, NOT_NULLABLE, ArgType.AUTO);
		builder.addIndexed("Hello", "", "", IS_DELEGATING, READ_WRITE, NOT_NULLABLE, NOT_EXPANDABLE, GetFromFieldType.class, ArgType.AUTO, GetFromFieldType.class, ArgType.AUTO);

		List<IMethodExecutor> output = buildPropertyListAndCheckSize(builder, 2);

		{
			IMethodExecutor executor = findAndVerifyExecutor("getHello", output);
			checkDescription(executor, "{string->number}|number", ArgType.STRING);
			checkParamOptionality(executor, 0, true);
			verifySingleGetterExecution(executor, mapField);
			verifyIndexedGetterExecution(executor, mapField, String.class);
		}

		{
			IMethodExecutor executor = findAndVerifyExecutor("setHello", output);
			checkDescriptionRaw(executor, "()", "{string->number}|number", "string");
			checkParamOptionality(executor, 1, true);
			verifySingleSetterExecution(executor, mapField);
			verifyIndexedSetterExecution(executor, mapField, String.class, Integer.class);
		}
	}

	@Test
	public void testMergedGetterSetterSameTypes() {
		PropertyListBuilder builder = new PropertyListBuilder(mapField, SOURCE);
		builder.addSingle("Hello", "", "", IS_DELEGATING, READ_WRITE, NOT_NULLABLE, ArgType.BOOLEAN);
		builder.addIndexed("Hello", "", "", IS_DELEGATING, READ_WRITE, NOT_NULLABLE, NOT_EXPANDABLE, GetFromFieldType.class, ArgType.OBJECT, GetFromFieldType.class, ArgType.BOOLEAN);

		List<IMethodExecutor> output = buildPropertyListAndCheckSize(builder, 2);

		{
			IMethodExecutor executor = findAndVerifyExecutor("getHello", output);
			checkDescription(executor, "boolean", ArgType.OBJECT);
			checkParamOptionality(executor, 0, true);
			verifySingleGetterExecution(executor, mapField);
			verifyIndexedGetterExecution(executor, mapField, String.class);
		}

		{
			IMethodExecutor executor = findAndVerifyExecutor("setHello", output);
			checkDescription(executor, "()", ArgType.BOOLEAN, ArgType.OBJECT);
			checkParamOptionality(executor, 1, true);
			verifySingleSetterExecution(executor, mapField);
			verifyIndexedSetterExecution(executor, mapField, String.class, Integer.class);
		}
	}

	@Test
	public void testSplitGetterOnly() {
		PropertyListBuilder builder = new PropertyListBuilder(listField, SOURCE);
		builder.addSingle("Hello", "", "", IS_DELEGATING, READ_ONLY, NOT_NULLABLE, ArgType.AUTO);
		builder.addIndexed("Hi", "", "", IS_DELEGATING, READ_ONLY, NOT_NULLABLE, NOT_EXPANDABLE, GetFromFieldType.class, ArgType.AUTO, GetFromFieldType.class, ArgType.AUTO);

		List<IMethodExecutor> output = buildPropertyListAndCheckSize(builder, 2);

		{
			IMethodExecutor executor = findAndVerifyExecutor("getHello", output);
			checkDescription(executor, "[string]");
			verifySingleGetterExecution(executor, listField);
		}

		{
			IMethodExecutor executor = findAndVerifyExecutor("getHi", output);
			checkDescription(executor, "string", ArgType.NUMBER);
			checkParamOptionality(executor, 0, false);
			verifyIndexedGetterExecution(executor, listField, Index.class);
		}
	}

	@Test
	public void testSplitGetterIndexedSetter() {
		PropertyListBuilder builder = new PropertyListBuilder(listField, SOURCE);
		builder.addSingle("Hello", "", "", IS_DELEGATING, READ_WRITE, NOT_NULLABLE, ArgType.AUTO);
		builder.addIndexed("Hi", "", "", IS_DELEGATING, READ_ONLY, NOT_NULLABLE, NOT_EXPANDABLE, GetFromFieldType.class, ArgType.AUTO, GetFromFieldType.class, ArgType.AUTO);

		List<IMethodExecutor> output = buildPropertyListAndCheckSize(builder, 3);

		{
			IMethodExecutor executor = findAndVerifyExecutor("getHello", output);
			checkDescription(executor, "[string]");
			verifySingleGetterExecution(executor, listField);
		}

		{
			IMethodExecutor executor = findAndVerifyExecutor("setHello", output);
			checkDescriptionRaw(executor, "()", "[string]");
			verifySingleSetterExecution(executor, listField);
		}

		{
			IMethodExecutor executor = findAndVerifyExecutor("getHi", output);
			checkDescription(executor, "string", ArgType.NUMBER);
			checkParamOptionality(executor, 0, false);
			verifyIndexedGetterExecution(executor, listField, Index.class);
		}
	}

	@Test
	public void testSingleGetterSplitSetter() {
		PropertyListBuilder builder = new PropertyListBuilder(arrayField, SOURCE);
		builder.addSingle("Hello", "", "", IS_DELEGATING, READ_ONLY, NOT_NULLABLE, ArgType.AUTO);
		builder.addIndexed("Hi", "", "", IS_DELEGATING, READ_WRITE, NOT_NULLABLE, NOT_EXPANDABLE, GetFromFieldType.class, ArgType.AUTO, GetFromFieldType.class, ArgType.AUTO);

		List<IMethodExecutor> output = buildPropertyListAndCheckSize(builder, 3);

		{
			IMethodExecutor executor = findAndVerifyExecutor("getHello", output);
			checkDescription(executor, "[boolean]");
			verifySingleGetterExecution(executor, arrayField);
		}

		{
			IMethodExecutor executor = findAndVerifyExecutor("getHi", output);
			checkDescription(executor, "boolean", ArgType.NUMBER);
			checkParamOptionality(executor, 0, false);
			verifyIndexedGetterExecution(executor, arrayField, Index.class);
		}

		{
			IMethodExecutor executor = findAndVerifyExecutor("setHi", output);
			checkDescription(executor, "()", ArgType.BOOLEAN, ArgType.NUMBER);
			checkParamOptionality(executor, 1, false);
			verifyIndexedSetterExecution(executor, arrayField, Index.class, boolean.class);
		}
	}

	@Test
	public void testSplitGetterSetter() {
		PropertyListBuilder builder = new PropertyListBuilder(arrayField, SOURCE);
		builder.addSingle("Hello", "", "", IS_DELEGATING, READ_WRITE, NOT_NULLABLE, ArgType.AUTO);
		builder.addIndexed("Hi", "", "", IS_DELEGATING, READ_WRITE, NOT_NULLABLE, NOT_EXPANDABLE, GetFromFieldType.class, ArgType.AUTO, GetFromFieldType.class, ArgType.AUTO);

		List<IMethodExecutor> output = buildPropertyListAndCheckSize(builder, 4);

		{
			IMethodExecutor executor = findAndVerifyExecutor("getHello", output);
			checkDescription(executor, "[boolean]");
			verifySingleGetterExecution(executor, arrayField);
		}

		{
			IMethodExecutor executor = findAndVerifyExecutor("setHello", output);
			checkDescriptionRaw(executor, "()", "[boolean]");
			verifySingleSetterExecution(executor, arrayField);
		}

		{
			IMethodExecutor executor = findAndVerifyExecutor("getHi", output);
			checkDescription(executor, "boolean", ArgType.NUMBER);
			checkParamOptionality(executor, 0, false);
			verifyIndexedGetterExecution(executor, arrayField, Index.class);
		}

		{
			IMethodExecutor executor = findAndVerifyExecutor("setHi", output);
			checkDescription(executor, "()", ArgType.BOOLEAN, ArgType.NUMBER);
			checkParamOptionality(executor, 1, false);
			verifyIndexedSetterExecution(executor, arrayField, Index.class, boolean.class);
		}
	}
}
