package openperipheral.tests;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import openperipheral.adapter.property.IndexedTypeInfo;
import openperipheral.adapter.property.IndexedTypeInfoBuilder;
import openperipheral.adapter.types.SingleType;
import openperipheral.api.adapter.IScriptType;
import openperipheral.api.adapter.method.ArgType;
import openperipheral.api.helpers.Index;
import openperipheral.api.property.*;
import openperipheral.api.struct.ScriptStruct;
import openperipheral.api.struct.StructField;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Throwables;

public class IndexedTypeInfoBuilderTest {

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

	public abstract static class DefaultCustom implements IIndexedCustomProperty<Boolean, Short> {}

	public abstract static class ProviderCustom implements IIndexedTypedCustomProperty<Short, String> {}

	@PropertyKeyDocType(ArgType.TABLE)
	public abstract static class KeyAnnotatedCustom implements IIndexedCustomProperty<Boolean, Float> {}

	@PropertyValueDocType(ArgType.VOID)
	public abstract static class ValueAnnotatedCustom implements IIndexedCustomProperty<Short, Long> {}

	@PropertyKeyDocType(ArgType.OBJECT)
	@PropertyValueDocType(ArgType.TABLE)
	public abstract static class AllAnnotatedCustom implements IIndexedCustomProperty<String, String> {}

	private static void matchKeyType(IndexedTypeInfo result, Class<?> keyType) {
		Assert.assertEquals(keyType, result.keyType);
	}

	private static void matchDocTypes(IndexedTypeInfo result, String keyType, String valueType) {
		Assert.assertEquals(keyType, result.keyDocType.describe());
		Assert.assertEquals(valueType, result.valueDocType.describe());
	}

	public static void matchConstantValue(IndexedTypeInfo result, Class<?> valueType) {
		Assert.assertEquals(valueType, result.getValueType(null, "blarg"));
		Assert.assertEquals(valueType, result.getValueType(null, ""));
		Assert.assertEquals(valueType, result.getValueType(null, 4));
	}

	public static void matchFieldValue(IndexedTypeInfo result, String name, Class<?> type) {
		Assert.assertEquals(type, result.getValueType(null, name));
	}

	@Test
	public void testAllDeducedMap() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(MAP_TYPE);
		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, "string", "number");
		matchKeyType(result, String.class);
		matchConstantValue(result, Integer.class);
	}

	@Test
	public void testAllDeducedList() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(LIST_TYPE);
		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, "number", "boolean");
		matchKeyType(result, Index.class);
		matchConstantValue(result, Boolean.class);
	}

	@Test
	public void testAllDeducedArrayObject() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(String[].class);
		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, "number", "string");
		matchKeyType(result, Index.class);
		matchConstantValue(result, String.class);
	}

	@Test
	public void testAllDeducedArrayPrimitive() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(boolean[].class);
		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, "number", "boolean");
		matchKeyType(result, Index.class);
		matchConstantValue(result, boolean.class);
	}

	@Test
	public void testAllDeducedUniformStruct() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(UniformStruct.class);
		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, "string", "number");
		matchUniformStruct(result);
	}

	private static void matchUniformStruct(final IndexedTypeInfo result) {
		matchKeyType(result, String.class);
		matchFieldValue(result, "a", int.class);
		matchFieldValue(result, "b", int.class);
		matchFieldValue(result, "c", int.class);
	}

	@Test
	public void testAllDeducedVariaStruct() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(VariaStruct.class);
		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, "string", "*");
		matchVariedStruct(result);
	}

	private static void matchVariedStruct(final IndexedTypeInfo result) {
		matchKeyType(result, String.class);
		matchFieldValue(result, "a", int.class);
		matchFieldValue(result, "b", String.class);
		matchFieldValue(result, "c", boolean.class);
	}

	@Test
	public void testAllDeducedCustomProperty() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(DefaultCustom.class);
		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, "boolean", "number");
		matchKeyType(result, Boolean.class);
		matchConstantValue(result, Short.class);
	}

	@Test
	public void testAllDeducedCustomPropertyTypeProvider() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(ProviderCustom.class);
		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, "number", "string");
		matchKeyType(result, Short.class);
		matchDelegatingValue(result);
	}

	private static void matchDelegatingValue(final IndexedTypeInfo result) {
		ProviderCustom target = Mockito.mock(ProviderCustom.class);

		final String keyA = "aaa";
		final String keyB = "bbb";
		final String keyC = "zzz";

		Mockito.when(target.getType(keyA)).thenReturn(Integer.class);
		Mockito.when(target.getType(keyB)).thenReturn(String.class);
		Mockito.when(target.getType(keyC)).thenReturn(Boolean.class);

		Assert.assertEquals(Integer.class, result.getValueType(target, keyA));
		Assert.assertEquals(String.class, result.getValueType(target, keyB));
		Assert.assertEquals(Boolean.class, result.getValueType(target, keyC));

		Mockito.verify(target).getType(keyA);
		Mockito.verify(target).getType(keyB);
		Mockito.verify(target).getType(keyC);
	}

	@Test
	public void testKeyAnnotatedCustomProperty() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(KeyAnnotatedCustom.class);
		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, "table", "number");
		matchConstantValue(result, Float.class);
	}

	@Test
	public void testValueAnnotatedCustomProperty() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(ValueAnnotatedCustom.class);
		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, "number", "void");
		matchKeyType(result, Short.class);
		matchConstantValue(result, Long.class);
	}

	@Test
	public void testAllAnnotatedCustomProperty() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(AllAnnotatedCustom.class);
		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, "object", "table");
		matchKeyType(result, String.class);
		matchConstantValue(result, String.class);
	}

	@Test
	public void testOverrideDocTypesSimple() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(MAP_TYPE);
		builder.overrideKeyDocType(TYPE_A);
		builder.overrideValueDocType(TYPE_B);

		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, TYPE_A_DESCRIPTION, TYPE_B_DESCRIPTION);
		matchKeyType(result, String.class);
		matchConstantValue(result, Integer.class);
	}

	@Test
	public void testOverrideDocTypesStruct() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(VariaStruct.class);
		builder.overrideKeyDocType(TYPE_A);
		builder.overrideValueDocType(TYPE_B);

		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, TYPE_A_DESCRIPTION, TYPE_B_DESCRIPTION);
		matchVariedStruct(result);
	}

	@Test
	public void testOverrideDocTypesCustomProperty() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(AllAnnotatedCustom.class);
		builder.overrideKeyDocType(TYPE_A);
		builder.overrideValueDocType(TYPE_B);

		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, TYPE_A_DESCRIPTION, TYPE_B_DESCRIPTION);
		matchKeyType(result, String.class);
		matchConstantValue(result, String.class);
	}

	@Test
	public void testOverrideKeyTypeSimple() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(MAP_TYPE);
		builder.overrideKeyType(Boolean.class);
		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, "boolean", "number");
		matchKeyType(result, Boolean.class);
		matchConstantValue(result, Integer.class);
	}

	@Test
	public void testOverrideKeyTypeStruct() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(VariaStruct.class);
		builder.overrideKeyType(Boolean.class);
		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, "boolean", "*");
		matchKeyType(result, Boolean.class);
		matchFieldValue(result, "a", int.class);
		matchFieldValue(result, "b", String.class);
		matchFieldValue(result, "c", boolean.class);
	}

	@Test
	public void testOverrideKeyTypeCustomProperty() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(AllAnnotatedCustom.class);
		builder.overrideKeyType(Long.class);
		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, "number", "table");
		matchKeyType(result, Long.class);
		matchConstantValue(result, String.class);
	}

	@Test
	public void testOverrideKeyTypeCustomTypeProviderProperty() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(ProviderCustom.class);
		builder.overrideKeyType(Long.class);
		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, "number", "string");
		matchKeyType(result, Long.class);
		matchDelegatingValue(result);
	}

	@Test
	public void testOverrideValueTypeSimple() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(LIST_TYPE);
		builder.overrideValueType(Boolean.class);
		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, "number", "boolean");
		matchKeyType(result, Index.class);
		matchConstantValue(result, Boolean.class);
	}

	@Test
	public void testOverrideValueTypeStruct() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(VariaStruct.class);
		builder.overrideValueType(boolean.class);
		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, "string", "boolean");
		matchKeyType(result, String.class);
		matchFieldValue(result, "a", boolean.class);
		matchFieldValue(result, "b", boolean.class);
		matchFieldValue(result, "c", boolean.class);
	}

	@Test
	public void testOverrideValueTypeCustomProperty() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(AllAnnotatedCustom.class);
		builder.overrideValueType(Integer.class);
		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, "object", "number");
		matchKeyType(result, String.class);
		matchConstantValue(result, Integer.class);
	}

	@Test
	public void testOverrideValueTypeCustomTypeProviderProperty() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(ProviderCustom.class);
		builder.overrideValueType(Long.class);
		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, "number", "number");
		matchKeyType(result, Short.class);
		matchConstantValue(result, Long.class);
	}

	@Test
	public void testOverrideKeyValueTypeSimple() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(int[].class);
		builder.overrideKeyType(UniformStruct.class);
		builder.overrideValueType(boolean.class);
		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, "{a:number,b:number,c:number}", "boolean");
		matchKeyType(result, UniformStruct.class);
		matchConstantValue(result, boolean.class);
	}

	@Test
	public void testOverrideKeyValueTypeStruct() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(VariaStruct.class);
		builder.overrideKeyType(UniformStruct.class);
		builder.overrideValueType(Boolean.class);
		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, "{a:number,b:number,c:number}", "boolean");
		matchKeyType(result, UniformStruct.class);
		matchFieldValue(result, "a", Boolean.class);
		matchFieldValue(result, "b", Boolean.class);
		matchFieldValue(result, "c", Boolean.class);
	}

	@Test
	public void testOverrideAllTypeSimple() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(LIST_TYPE);
		builder.overrideKeyType(String.class);
		builder.overrideValueType(double.class);
		builder.overrideKeyDocType(TYPE_B);
		builder.overrideValueDocType(TYPE_A);
		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, TYPE_B_DESCRIPTION, TYPE_A_DESCRIPTION);
		matchKeyType(result, String.class);
		matchConstantValue(result, double.class);
	}

	@Test
	public void testOverrideAllTypeStruct() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(VariaStruct.class);
		builder.overrideKeyType(int.class);
		builder.overrideValueType(Float.class);
		builder.overrideKeyDocType(TYPE_B);
		builder.overrideValueDocType(TYPE_A);
		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, TYPE_B_DESCRIPTION, TYPE_A_DESCRIPTION);
		matchKeyType(result, int.class);
		matchFieldValue(result, "a", Float.class);
		matchFieldValue(result, "b", Float.class);
		matchFieldValue(result, "c", Float.class);
	}

	@Test
	public void testOverrideAllTypeUknownType() {
		IndexedTypeInfoBuilder builder = new IndexedTypeInfoBuilder(Object.class);
		builder.overrideKeyType(int.class);
		builder.overrideValueType(Float.class);
		builder.overrideKeyDocType(TYPE_B);
		builder.overrideValueDocType(TYPE_A);
		final IndexedTypeInfo result = builder.build();
		matchDocTypes(result, TYPE_B_DESCRIPTION, TYPE_A_DESCRIPTION);
		matchKeyType(result, int.class);
		matchFieldValue(result, "a", Float.class);
		matchFieldValue(result, "b", Float.class);
		matchFieldValue(result, "c", Float.class);
	}
}
