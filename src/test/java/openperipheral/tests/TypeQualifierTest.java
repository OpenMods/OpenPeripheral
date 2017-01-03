package openperipheral.tests;

import com.google.common.base.Preconditions;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import openperipheral.adapter.types.classifier.TypeClassifier;
import openperipheral.api.adapter.IScriptType;
import openperipheral.api.struct.ScriptStruct;
import openperipheral.api.struct.StructField;
import org.junit.Assert;
import org.junit.Test;

public class TypeQualifierTest {

	public static void testQualifier(String expected, Type type) {
		final TypeClassifier qualifier = new TypeClassifier();
		final IScriptType qualified = qualifier.classifyType(type);
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
	public static class BasicStruct {
		@StructField
		public int a;

		@StructField
		public String b;
	}

	@Test
	public void testBasicStruct() {
		testQualifier("{a:number,b:string}", BasicStruct.class);
	}

	@ScriptStruct
	public static class OptionalStruct {
		@StructField
		public int a;

		@StructField
		public String b;

		@StructField(optional = true)
		public boolean c;
	}

	@Test
	public void testStructWithOptional() {
		testQualifier("{a:number,b:string,c:boolean?}", OptionalStruct.class);
	}

	@ScriptStruct
	public static class OrderedStruct {
		@StructField(index = 2)
		public int a;

		@StructField(index = 0)
		public String b;

		@StructField(index = 1)
		public boolean c;
	}

	@Test
	public void testOrderedStruct() {
		testQualifier("{b:string,c:boolean,a:number}", OrderedStruct.class);
	}

	@ScriptStruct
	public static class NestedStruct {
		@StructField
		public int a;

		@StructField
		public BasicStruct b;
	}

	@Test
	public void testNestedStruct() {
		testQualifier("{a:number,b:{a:number,b:string}}", NestedStruct.class);
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface ExpectedConversion {
		public String value();
	}

	public static class TestFields {
		@ExpectedConversion("{string->number}")
		public Map<String, Integer> mapStringInteger;

		@ExpectedConversion("{boolean->{a:number,b:string}}")
		public Map<Boolean, BasicStruct> mapStringStruct;

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

		@ExpectedConversion("[{a:number,b:string}]")
		public List<BasicStruct> listStruct;

		@ExpectedConversion("[[{a:number,b:string}]]")
		public List<BasicStruct[]> listArrayStruct;

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

			final TypeClassifier qualifier = new TypeClassifier();

			final IScriptType qualified;
			try {
				qualified = qualifier.classifyType(f.getGenericType());
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
		testQualifier("[{a:number,b:string}]", BasicStruct[].class);
		testQualifier("[[{a:number,b:string}]]", BasicStruct[][].class);
	}

	@Test
	public void testRaws() {
		testQualifier("table", Map.class);
		testQualifier("table", List.class);
		testQualifier("table", Set.class);
	}
}
