package openperipheral.tests;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

import openperipheral.adapter.method.TypeQualifier;
import openperipheral.adapter.types.IType;
import openperipheral.api.struct.ScriptStruct;
import openperipheral.api.struct.StructField;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Preconditions;

public class TypeQualifierTest {

	public static void testQualifier(String expected, Type type) {
		final TypeQualifier qualifier = new TypeQualifier();
		final IType qualified = qualifier.qualifyType(type);
		Assert.assertEquals(expected, qualified.describe());
	}

	@Test
	public void testPrimitive() {
		testQualifier("boolean", Boolean.class);

		testQualifier("string", String.class);
		testQualifier("string", UUID.class);

		testQualifier("void", void.class);
		testQualifier("void", Void.class);

		testQualifier("number", int.class);
		testQualifier("number", Integer.class);
		testQualifier("number", float.class);
		testQualifier("number", Float.class);
	}

	public static enum TestEnum {
		A,
		B,
		C,
		D;
	}

	public static enum TestInstancedEnum {
		E {},
		F {},
		G {},
		H {};
	}

	@Test
	public void testEnum() {
		testQualifier("string{A,B,C,D}", TestEnum.class);
		testQualifier("string{E,F,G,H}", TestInstancedEnum.class);
	}

	@ScriptStruct
	public static class Struct {
		@StructField
		public int a;

		@StructField
		public String b;
	}

	@Test
	public void testStruct() {
		testQualifier("table", Struct.class);
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface ExpectedConversion {
		public String value();
	}

	public static class TestFields {
		@ExpectedConversion("{string->number}")
		public Map<String, Integer> mapStringInteger;

		@ExpectedConversion("{boolean->table}")
		public Map<Boolean, Struct> mapStringStruct;

		@ExpectedConversion("{string{A,B,C,D}->number}")
		public Map<TestEnum, Integer> mapEnumInteger;

		@ExpectedConversion("{number->number}")
		public Map<Float, Integer> mapFloatInteger;

		@ExpectedConversion("{number->{string->string{A,B,C,D}}}")
		public Map<Float, Map<String, TestEnum>> mapNested;

		@ExpectedConversion("{number->[number]}")
		public Map<Float, int[]> mapFloatArray;

		@ExpectedConversion("table")
		public Map<Integer, ?> mapRawValue;

		@ExpectedConversion("table")
		public Map<?, String> mapRawKey;

		@ExpectedConversion("table")
		public Map<?, ?> mapRaw;

		@ExpectedConversion("[number]")
		public List<Integer> listInteger;

		@ExpectedConversion("[string]")
		public List<String> listString;

		@ExpectedConversion("[table]")
		public List<Struct> listStruct;

		@ExpectedConversion("[[table]]")
		public List<Struct[]> listArrayStruct;

		@ExpectedConversion("[string{A,B,C,D}]")
		public List<TestEnum> listEnum;

		@ExpectedConversion("[[string{A,B,C,D}]]")
		public List<Collection<TestEnum>> listNested;

		@ExpectedConversion("table")
		public List<?> listRaw;

		@ExpectedConversion("{number}")
		public Set<Integer> setInteger;

		@ExpectedConversion("{string}")
		public Set<String> setString;

		@ExpectedConversion("{string{A,B,C,D}}")
		public Set<TestEnum> setEnum;

		@ExpectedConversion("{{string{A,B,C,D}}}")
		public Set<Set<TestEnum>> setNested;

		@ExpectedConversion("table")
		public Set<?> setRaw;

		@ExpectedConversion("[[number]]")
		List<Integer>[] arrayList;

		@ExpectedConversion("[{number}]")
		Set<Number>[] arraySet;

		@ExpectedConversion("[table]")
		Set<?>[] arrayRawSet;

		@ExpectedConversion("{[string]->{string{A,B,C,D}}}")
		public Map<List<String>, Set<TestEnum>> mapListSet;
	}

	@Test
	public void testGenericFields() {
		for (Field f : TestFields.class.getFields()) {
			ExpectedConversion ann = f.getAnnotation(ExpectedConversion.class);
			final String name = f.getName();
			Preconditions.checkNotNull(ann, "Field without annotation: " + name);

			final TypeQualifier qualifier = new TypeQualifier();

			final IType qualified;
			try {
				qualified = qualifier.qualifyType(f.getGenericType());
			} catch (Exception e) {
				throw new RuntimeException("Field " + name, e);
			}

			Assert.assertEquals("Field " + name, ann.value(), qualified.describe());
		}
	}

	@Test
	public void testArray() {
		testQualifier("[number]", int[].class);
		testQualifier("[boolean]", boolean[].class);
		testQualifier("[boolean]", Boolean[].class);
		testQualifier("table", Object[].class);
		testQualifier("[[number]]", int[][].class);
		testQualifier("[table]", Struct[].class);
	}

	@Test
	public void testRaws() {
		testQualifier("table", Map.class);
		testQualifier("table", List.class);
		testQualifier("table", Set.class);
	}
}
