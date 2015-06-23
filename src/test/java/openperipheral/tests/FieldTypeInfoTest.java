package openperipheral.tests;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import openperipheral.adapter.property.FieldTypeInfoBuilder;
import openperipheral.adapter.property.FieldTypeInfoBuilder.Result;
import openperipheral.adapter.types.SingleType;
import openperipheral.api.adapter.IScriptType;
import openperipheral.api.helpers.Index;
import openperipheral.api.struct.ScriptStruct;
import openperipheral.api.struct.StructField;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Throwables;

public class FieldTypeInfoTest {

	private static final String TYPE_A_DESCRIPTION = "TEST_TYPE_A";
	private static final String TYPE_B_DESCRIPTION = "TEST_TYPE_B";

	private static final IScriptType TYPE_A = new SingleType(TYPE_A_DESCRIPTION);
	private static final IScriptType TYPE_B = new SingleType(TYPE_B_DESCRIPTION);

	public static class Types {
		public Map<String, Integer> mapField;

		public List<Boolean> listField;
	}

	private static Type getType(String fieldName) {
		try {
			Field f = Types.class.getField(fieldName);
			return f.getGenericType();
		} catch (Throwable t) {
			throw Throwables.propagate(t);
		}
	}

	private static final Type MAP_TYPE = getType("mapField");

	private static final Type LIST_TYPE = getType("listField");

	@ScriptStruct
	public static class UniformStruct {
		@StructField
		public int a;

		@StructField
		public int b;

		@StructField
		public int c;
	}

	@ScriptStruct
	public static class VariaStruct {
		@StructField
		public int a;

		@StructField
		public String b;

		@StructField
		public boolean c;
	}

	private static void matchKeyType(Result result, Class<?> keyType) {
		Assert.assertEquals(keyType, result.keyType);
	}

	private static void matchDocTypes(Result result, String keyType, String valueType) {
		Assert.assertEquals(keyType, result.keyDocType.describe());
		Assert.assertEquals(valueType, result.valueDocType.describe());
	}

	public static void matchConstantValue(Result result, Class<?> valueType) {
		Assert.assertEquals(valueType, result.valueType.getType("blarg"));
		Assert.assertEquals(valueType, result.valueType.getType(""));
		Assert.assertEquals(valueType, result.valueType.getType(4));
	}

	public static void matchFieldValue(Result result, String name, Class<?> type) {
		Assert.assertEquals(type, result.valueType.getType(name));
	}

	@Test
	public void testAllDeducedMap() {
		FieldTypeInfoBuilder builder = new FieldTypeInfoBuilder(MAP_TYPE);
		final Result result = builder.build();
		matchDocTypes(result, "string", "number");
		matchKeyType(result, String.class);
		matchConstantValue(result, Integer.class);
	}

	@Test
	public void testAllDeducedList() {
		FieldTypeInfoBuilder builder = new FieldTypeInfoBuilder(LIST_TYPE);
		final Result result = builder.build();
		matchDocTypes(result, "number", "boolean");
		matchKeyType(result, Index.class);
		matchConstantValue(result, Boolean.class);
	}

	@Test
	public void testAllDeducedArrayObject() {
		FieldTypeInfoBuilder builder = new FieldTypeInfoBuilder(String[].class);
		final Result result = builder.build();
		matchDocTypes(result, "number", "string");
		matchKeyType(result, Index.class);
		matchConstantValue(result, String.class);
	}

	@Test
	public void testAllDeducedArrayPrimitive() {
		FieldTypeInfoBuilder builder = new FieldTypeInfoBuilder(boolean[].class);
		final Result result = builder.build();
		matchDocTypes(result, "number", "boolean");
		matchKeyType(result, Index.class);
		matchConstantValue(result, boolean.class);
	}

	@Test
	public void testAllDeducedUniformStruct() {
		FieldTypeInfoBuilder builder = new FieldTypeInfoBuilder(UniformStruct.class);
		final Result result = builder.build();
		matchDocTypes(result, "string", "number");
		matchUniformStruct(result);
	}

	private static void matchUniformStruct(final Result result) {
		matchKeyType(result, String.class);
		matchFieldValue(result, "a", int.class);
		matchFieldValue(result, "b", int.class);
		matchFieldValue(result, "c", int.class);
	}

	@Test
	public void testAllDeducedVariaStruct() {
		FieldTypeInfoBuilder builder = new FieldTypeInfoBuilder(VariaStruct.class);
		final Result result = builder.build();
		matchDocTypes(result, "string", "*");
		matchVariedStruct(result);
	}

	private static void matchVariedStruct(final Result result) {
		matchKeyType(result, String.class);
		matchFieldValue(result, "a", int.class);
		matchFieldValue(result, "b", String.class);
		matchFieldValue(result, "c", boolean.class);
	}

	@Test
	public void testOverrideDocTypesSimple() {
		FieldTypeInfoBuilder builder = new FieldTypeInfoBuilder(MAP_TYPE);
		builder.overrideKeyDocType(TYPE_A);
		builder.overrideValueDocType(TYPE_B);
		final Result result = builder.build();
		matchDocTypes(result, TYPE_A_DESCRIPTION, TYPE_B_DESCRIPTION);
		matchKeyType(result, String.class);
		matchConstantValue(result, Integer.class);
	}

	@Test
	public void testOverrideDocTypesStruct() {
		FieldTypeInfoBuilder builder = new FieldTypeInfoBuilder(VariaStruct.class);
		builder.overrideKeyDocType(TYPE_A);
		builder.overrideValueDocType(TYPE_B);
		final Result result = builder.build();
		matchDocTypes(result, TYPE_A_DESCRIPTION, TYPE_B_DESCRIPTION);
		matchVariedStruct(result);
	}

	@Test
	public void testOverrideKeyTypeSimple() {
		FieldTypeInfoBuilder builder = new FieldTypeInfoBuilder(MAP_TYPE);
		builder.overrideKeyType(Boolean.class);
		final Result result = builder.build();
		matchDocTypes(result, "boolean", "number");
		matchKeyType(result, Boolean.class);
		matchConstantValue(result, Integer.class);
	}

	@Test
	public void testOverrideKeyTypeStruct() {
		FieldTypeInfoBuilder builder = new FieldTypeInfoBuilder(VariaStruct.class);
		builder.overrideKeyType(Boolean.class);
		final Result result = builder.build();
		matchDocTypes(result, "boolean", "*");
		matchKeyType(result, Boolean.class);
		matchFieldValue(result, "a", int.class);
		matchFieldValue(result, "b", String.class);
		matchFieldValue(result, "c", boolean.class);
	}

	@Test
	public void testOverrideValueTypeSimple() {
		FieldTypeInfoBuilder builder = new FieldTypeInfoBuilder(LIST_TYPE);
		builder.overrideValueType(Boolean.class);
		final Result result = builder.build();
		matchDocTypes(result, "number", "boolean");
		matchKeyType(result, Index.class);
		matchConstantValue(result, Boolean.class);
	}

	@Test
	public void testOverrideValueTypeStruct() {
		FieldTypeInfoBuilder builder = new FieldTypeInfoBuilder(VariaStruct.class);
		builder.overrideValueType(boolean.class);
		final Result result = builder.build();
		matchDocTypes(result, "string", "boolean");
		matchKeyType(result, String.class);
		matchFieldValue(result, "a", boolean.class);
		matchFieldValue(result, "b", boolean.class);
		matchFieldValue(result, "c", boolean.class);
	}

	@Test
	public void testOverrideKeyValueTypeSimple() {
		FieldTypeInfoBuilder builder = new FieldTypeInfoBuilder(int[].class);
		builder.overrideKeyType(UniformStruct.class);
		builder.overrideValueType(boolean.class);
		final Result result = builder.build();
		matchDocTypes(result, "table", "boolean");
		matchKeyType(result, UniformStruct.class);
		matchConstantValue(result, boolean.class);
	}

	@Test
	public void testOverrideKeyValueTypeStruct() {
		FieldTypeInfoBuilder builder = new FieldTypeInfoBuilder(VariaStruct.class);
		builder.overrideKeyType(UniformStruct.class);
		builder.overrideValueType(Boolean.class);
		final Result result = builder.build();
		matchDocTypes(result, "table", "boolean");
		matchKeyType(result, UniformStruct.class);
		matchFieldValue(result, "a", Boolean.class);
		matchFieldValue(result, "b", Boolean.class);
		matchFieldValue(result, "c", Boolean.class);
	}

	@Test
	public void testOverrideAllTypeSimple() {
		FieldTypeInfoBuilder builder = new FieldTypeInfoBuilder(LIST_TYPE);
		builder.overrideKeyType(String.class);
		builder.overrideValueType(double.class);
		builder.overrideKeyDocType(TYPE_B);
		builder.overrideValueDocType(TYPE_A);
		final Result result = builder.build();
		matchDocTypes(result, TYPE_B_DESCRIPTION, TYPE_A_DESCRIPTION);
		matchKeyType(result, String.class);
		matchConstantValue(result, double.class);
	}

	@Test
	public void testOverrideAllTypeStruct() {
		FieldTypeInfoBuilder builder = new FieldTypeInfoBuilder(VariaStruct.class);
		builder.overrideKeyType(int.class);
		builder.overrideValueType(Float.class);
		builder.overrideKeyDocType(TYPE_B);
		builder.overrideValueDocType(TYPE_A);
		final Result result = builder.build();
		matchDocTypes(result, TYPE_B_DESCRIPTION, TYPE_A_DESCRIPTION);
		matchKeyType(result, int.class);
		matchFieldValue(result, "a", Float.class);
		matchFieldValue(result, "b", Float.class);
		matchFieldValue(result, "c", Float.class);
	}
}
