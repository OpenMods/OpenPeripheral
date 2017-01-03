package openperipheral.tests;

import java.lang.reflect.Type;
import openperipheral.adapter.property.SingleTypeInfo;
import openperipheral.adapter.property.SingleTypeInfoBuilder;
import openperipheral.adapter.types.SingleType;
import openperipheral.api.adapter.IScriptType;
import openperipheral.api.adapter.method.ArgType;
import openperipheral.api.property.ISingleCustomProperty;
import openperipheral.api.property.ISingleTypedCustomProperty;
import openperipheral.api.property.PropertyValueDocType;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class SingleTypeInfoBuilderTest {

	private static final String DUMMY_TYPE_DESCRIPTION = "TEST_TYPE_A";

	private static final IScriptType DUMMY_TYPE = new SingleType(DUMMY_TYPE_DESCRIPTION);

	private static void matchDocType(SingleTypeInfo result, String template) {
		Assert.assertEquals(template, result.valueDocType.describe());
	}

	private static void matchConstantType(SingleTypeInfo result, Type type) {
		Assert.assertEquals(type, result.getValueType(null));
	}

	public abstract static class BasicProperty implements ISingleCustomProperty<Boolean> {}

	@Test
	public void testBasicProperty() {
		SingleTypeInfoBuilder builder = new SingleTypeInfoBuilder(BasicProperty.class);
		final SingleTypeInfo result = builder.build();
		matchDocType(result, "boolean");
		matchConstantType(result, Boolean.class);
	}

	@Test
	public void testBasicPropertyTypeOverride() {
		SingleTypeInfoBuilder builder = new SingleTypeInfoBuilder(BasicProperty.class);
		builder.overrideValueType(Integer.class);
		final SingleTypeInfo result = builder.build();
		matchDocType(result, "number");
		matchConstantType(result, Integer.class);
	}

	@Test
	public void testBasicPropertyDocOverride() {
		SingleTypeInfoBuilder builder = new SingleTypeInfoBuilder(BasicProperty.class);
		builder.overrideValueDocType(DUMMY_TYPE);
		final SingleTypeInfo result = builder.build();
		matchDocType(result, DUMMY_TYPE_DESCRIPTION);
		matchConstantType(result, Boolean.class);
	}

	@Test
	public void testBasicPropertyAllOverride() {
		SingleTypeInfoBuilder builder = new SingleTypeInfoBuilder(BasicProperty.class);
		builder.overrideValueType(Integer.class);
		builder.overrideValueDocType(DUMMY_TYPE);
		final SingleTypeInfo result = builder.build();
		matchDocType(result, DUMMY_TYPE_DESCRIPTION);
		matchConstantType(result, Integer.class);
	}

	public abstract static class TypedProperty implements ISingleTypedCustomProperty<String> {}

	private static void matchDelegatingType(SingleTypeInfo result) {
		final TypedProperty target = Mockito.mock(TypedProperty.class);
		Mockito.when(target.getType()).thenReturn(Integer.class);
		Assert.assertEquals(Integer.class, result.getValueType(target));
		Mockito.verify(target).getType();
	}

	@Test
	public void testTypedProperty() {
		SingleTypeInfoBuilder builder = new SingleTypeInfoBuilder(TypedProperty.class);
		final SingleTypeInfo result = builder.build();
		matchDocType(result, "string");
		matchDelegatingType(result);
	}

	@Test
	public void testTypedPropertyTypeOverride() {
		SingleTypeInfoBuilder builder = new SingleTypeInfoBuilder(TypedProperty.class);
		builder.overrideValueType(Long.class);
		final SingleTypeInfo result = builder.build();
		matchDocType(result, "number");
		matchConstantType(result, Long.class);
	}

	@Test
	public void testTypedPropertyDocOverride() {
		SingleTypeInfoBuilder builder = new SingleTypeInfoBuilder(TypedProperty.class);
		builder.overrideValueDocType(DUMMY_TYPE);
		final SingleTypeInfo result = builder.build();
		matchDocType(result, DUMMY_TYPE_DESCRIPTION);
		matchDelegatingType(result);
	}

	@PropertyValueDocType(ArgType.TABLE)
	public abstract static class AnnotatedProperty implements ISingleCustomProperty<Short> {}

	@Test
	public void testAnnotatedProperty() {
		SingleTypeInfoBuilder builder = new SingleTypeInfoBuilder(AnnotatedProperty.class);
		final SingleTypeInfo result = builder.build();
		matchDocType(result, "table");
		matchConstantType(result, Short.class);
	}

	@Test
	public void testAnnotatedPropertyTypeOverride() {
		SingleTypeInfoBuilder builder = new SingleTypeInfoBuilder(AnnotatedProperty.class);
		builder.overrideValueType(String.class);
		final SingleTypeInfo result = builder.build();
		matchDocType(result, "string");
		matchConstantType(result, String.class);
	}

	@Test
	public void testAnnotatedPropertyDocOverride() {
		SingleTypeInfoBuilder builder = new SingleTypeInfoBuilder(AnnotatedProperty.class);
		builder.overrideValueDocType(DUMMY_TYPE);
		final SingleTypeInfo result = builder.build();
		matchDocType(result, DUMMY_TYPE_DESCRIPTION);
		matchConstantType(result, Short.class);
	}

	@PropertyValueDocType(ArgType.VOID)
	public abstract static class TypedAnnotatedProperty implements ISingleTypedCustomProperty<Long> {}

	@Test
	public void testTypedAnnotatedProperty() {
		SingleTypeInfoBuilder builder = new SingleTypeInfoBuilder(TypedAnnotatedProperty.class);
		final SingleTypeInfo result = builder.build();
		matchDocType(result, "void");
		matchDelegatingType(result);
	}

	@Test
	public void testTypedAnnotatedPropertyTypeOverride() {
		SingleTypeInfoBuilder builder = new SingleTypeInfoBuilder(TypedAnnotatedProperty.class);
		builder.overrideValueType(Boolean.class);
		final SingleTypeInfo result = builder.build();
		matchDocType(result, "boolean");
		matchConstantType(result, Boolean.class);
	}

	@Test
	public void testTypedAnnotatedPropertyDocOverride() {
		SingleTypeInfoBuilder builder = new SingleTypeInfoBuilder(TypedAnnotatedProperty.class);
		builder.overrideValueDocType(DUMMY_TYPE);
		final SingleTypeInfo result = builder.build();
		matchDocType(result, DUMMY_TYPE_DESCRIPTION);
		matchDelegatingType(result);
	}
}
