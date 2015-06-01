package openperipheral.tests;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import openperipheral.api.converter.IConverter;
import openperipheral.converter.inbound.*;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;

public class ConverterTests {

	private static final Object DUMMY = new Object();

	public Map<String, Double> mapParametrized;

	private static ArgumentMatcher<Type> isClass(final Class<?> cls) {
		return new ArgumentMatcher<Type>() {
			@Override
			public boolean matches(Object argument) {
				return cls.equals(TypeToken.of((Type)argument).getRawType());
			}
		};
	}

	private Type getVarType(String name) {
		try {
			return getClass().getField(name).getGenericType();
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	@Test
	public void MapParametrizedTest() {
		IConverter converter = mock(IConverter.class);

		ConverterMapInbound sut = new ConverterMapInbound();

		Map<String, Double> obj = ImmutableMap.of("aaaa", 1.0, "bbbb", 2.0);
		Type expected = getVarType("mapParametrized");

		Object result = sut.toJava(converter, obj, expected);
		Assert.assertTrue(result instanceof Map);

		verify(converter).toJava("aaaa", (Type)String.class);
		verify(converter).toJava("bbbb", (Type)String.class);
		verify(converter).toJava(1.0, (Type)Double.class);
		verify(converter).toJava(2.0, (Type)Double.class);
	}

	public Map<?, ?> mapWildcard;

	@Test
	public void MapWildcardTest() {
		IConverter converter = mock(IConverter.class);

		ConverterMapInbound sut = new ConverterMapInbound();

		Map<?, ?> obj = ImmutableMap.of("aaaa", 1.0, "bbbb", 2.0);
		Type expected = getVarType("mapWildcard");

		Object result = sut.toJava(converter, obj, expected);
		Assert.assertTrue(result instanceof Map);

		verify(converter).toJava(eq("aaaa"), argThat(isClass(Object.class)));
		verify(converter).toJava(eq("bbbb"), argThat(isClass(Object.class)));
		verify(converter).toJava(eq(1.0), argThat(isClass(Object.class)));
		verify(converter).toJava(eq(2.0), argThat(isClass(Object.class)));
	}

	public Map<? extends String, ? extends Double> mapBounded;

	@Test
	public void MapBoundedTest() {
		IConverter converter = mock(IConverter.class);

		ConverterMapInbound sut = new ConverterMapInbound();

		Map<?, ?> obj = ImmutableMap.of("aaaa", 1.0, "bbbb", 2.0);
		Type expected = getVarType("mapBounded");

		Object result = sut.toJava(converter, obj, expected);
		Assert.assertTrue(result instanceof Map);

		verify(converter).toJava(eq("aaaa"), argThat(isClass(String.class)));
		verify(converter).toJava(eq("bbbb"), argThat(isClass(String.class)));
		verify(converter).toJava(eq(1.0), argThat(isClass(Double.class)));
		verify(converter).toJava(eq(2.0), argThat(isClass(Double.class)));
	}

	public Map<Map<String, Double>, Double> mapNested;

	@Test
	public void MapNestedTest() {
		IConverter converter = mock(IConverter.class);

		ConverterMapInbound sut = new ConverterMapInbound();

		Map<?, ?> obj = ImmutableMap.of("aaaa", 1.0, "bbbb", 2.0);
		Type expected = getVarType("mapNested");
		Type nested = getVarType("mapParametrized");

		Object result = sut.toJava(converter, obj, expected);
		Assert.assertTrue(result instanceof Map);

		verify(converter).toJava(eq("aaaa"), eq(nested));
		verify(converter).toJava(eq("bbbb"), eq(nested));
		verify(converter).toJava(eq(1.0), argThat(isClass(Double.class)));
		verify(converter).toJava(eq(2.0), argThat(isClass(Double.class)));
	}

	@Test
	public void MapRawTest() {
		IConverter converter = mock(IConverter.class);

		ConverterMapInbound sut = new ConverterMapInbound();

		Map<?, ?> obj = ImmutableMap.of("aaaa", 1.0, "bbbb", 2.0);

		Object result = sut.toJava(converter, obj, Map.class);
		Assert.assertTrue(result instanceof Map);

		verify(converter).toJava(eq("aaaa"), argThat(isClass(Object.class)));
		verify(converter).toJava(eq("bbbb"), argThat(isClass(Object.class)));
		verify(converter).toJava(eq(1.0), argThat(isClass(Object.class)));
		verify(converter).toJava(eq(2.0), argThat(isClass(Object.class)));
	}

	public List<String> listParametrized;

	@Test
	public void ListParametrizedTest() {
		IConverter converter = mock(IConverter.class);
		when(converter.toJava(anyString(), any(Type.class))).thenReturn(DUMMY);

		ConverterListInbound sut = new ConverterListInbound(1);

		Map<Integer, String> obj = ImmutableMap.of(1, "a", 3, "b");

		Type expected = getVarType("listParametrized");

		Object result = sut.toJava(converter, obj, expected);

		Assert.assertTrue(result instanceof List);
		final List<?> tmp = (List<?>)result;
		Assert.assertEquals(3, tmp.size());

		Assert.assertEquals(DUMMY, tmp.get(0));
		Assert.assertNull(tmp.get(1));
		Assert.assertEquals(DUMMY, tmp.get(2));

		verify(converter).toJava("a", (Type)String.class);
		verify(converter).toJava("b", (Type)String.class);
	}

	public List<? extends Boolean> listBounded;

	@Test
	public void ListBoundedTest() {
		IConverter converter = mock(IConverter.class);
		when(converter.toJava(anyString(), any(Type.class))).thenReturn(DUMMY);

		ConverterListInbound sut = new ConverterListInbound(0);

		Map<Integer, String> obj = ImmutableMap.of(0, "a", 2, "b");

		Type expected = getVarType("listBounded");

		Object result = sut.toJava(converter, obj, expected);

		Assert.assertTrue(result instanceof List);
		final List<?> tmp = (List<?>)result;
		Assert.assertEquals(3, tmp.size());

		Assert.assertEquals(DUMMY, tmp.get(0));
		Assert.assertNull(tmp.get(1));
		Assert.assertEquals(DUMMY, tmp.get(2));

		verify(converter).toJava(eq("a"), argThat(isClass(Boolean.class)));
		verify(converter).toJava(eq("b"), argThat(isClass(Boolean.class)));
	}

	@SuppressWarnings("rawtypes")
	public List listRaw;

	@Test
	public void ListRawTest() {
		IConverter converter = mock(IConverter.class);
		when(converter.toJava(anyString(), any(Type.class))).thenReturn(DUMMY);

		ConverterListInbound sut = new ConverterListInbound(0);

		Map<Integer, String> obj = ImmutableMap.of(0, "a", 1, "b");

		Type expected = getVarType("listRaw");

		Object result = sut.toJava(converter, obj, expected);

		Assert.assertTrue(result instanceof List);
		Assert.assertEquals(2, ((List<?>)result).size());

		verify(converter).toJava(eq("a"), argThat(isClass(Object.class)));
		verify(converter).toJava(eq("b"), argThat(isClass(Object.class)));
	}

	public Set<Double> setParametrized;

	@Test
	public void SetParametrizedTest() {
		IConverter converter = mock(IConverter.class);

		ConverterSetInbound sut = new ConverterSetInbound();

		Map<Double, String> obj = ImmutableMap.of(1.0, "a", 3.0, "b", 2.0, "");

		Type expected = getVarType("setParametrized");

		Object result = sut.toJava(converter, obj, expected);

		Assert.assertTrue(result instanceof Set);

		verify(converter).toJava(1.0, (Type)Double.class);
		verify(converter).toJava(3.0, (Type)Double.class);
	}

	public Set<? extends Boolean> setBounded;

	@Test
	public void SetBoundedTest() {
		IConverter converter = mock(IConverter.class);

		ConverterSetInbound sut = new ConverterSetInbound();

		Map<Double, Boolean> obj = ImmutableMap.of(1.0, true, 2.0, true);

		Type expected = getVarType("setBounded");

		Object result = sut.toJava(converter, obj, expected);

		Assert.assertTrue(result instanceof Set);

		verify(converter).toJava(eq(1.0), argThat(isClass(Boolean.class)));
		verify(converter).toJava(eq(2.0), argThat(isClass(Boolean.class)));
	}

	@SuppressWarnings("rawtypes")
	public Set setRaw;

	@Test
	public void SetRawTest() {
		IConverter converter = mock(IConverter.class);

		ConverterSetInbound sut = new ConverterSetInbound();

		Map<String, Integer> obj = ImmutableMap.of("b", 1, "c", 2, "d", 3);

		Type expected = getVarType("setRaw");

		Object result = sut.toJava(converter, obj, expected);

		Assert.assertTrue(result instanceof Set);

		verify(converter).toJava(eq("b"), argThat(isClass(Object.class)));
		verify(converter).toJava(eq("c"), argThat(isClass(Object.class)));
	}

	public static class Generic<T> {}

	public Generic<Integer> genericParametrized;

	public Generic<Integer>[] genericParametrizedArray;

	@Test
	public void ArrayPrimitiveTest() {
		IConverter converter = mock(IConverter.class);
		when(converter.toJava(anyString(), any(Type.class))).thenReturn(5);

		ConverterArrayInbound sut = new ConverterArrayInbound(1);

		Map<Integer, String> obj = ImmutableMap.of(1, "a", 3, "b");

		Type expected = int[].class;

		Type component = int.class;

		Object result = sut.toJava(converter, obj, expected);

		Assert.assertTrue(result instanceof int[]);
		int[] tmp = (int[])result;
		Assert.assertEquals(tmp.length, 3);
		Assert.assertEquals(tmp[0], 5);
		Assert.assertEquals(tmp[1], 0);
		Assert.assertEquals(tmp[2], 5);

		verify(converter).toJava("a", component);
		verify(converter).toJava("b", component);
	}

	@Test
	public void ArrayParametrizerTest() {
		IConverter converter = mock(IConverter.class);
		when(converter.toJava(anyString(), any(Type.class))).thenReturn(new Generic<Integer>());

		ConverterArrayInbound sut = new ConverterArrayInbound(1);

		Map<Integer, String> obj = ImmutableMap.of(1, "a", 3, "b");

		Type expected = getVarType("genericParametrizedArray");

		Type component = getVarType("genericParametrized");

		Object result = sut.toJava(converter, obj, expected);

		Assert.assertTrue(result instanceof Generic[]);

		Generic<?>[] tmp = (Generic[])result;
		Assert.assertEquals(tmp.length, 3);
		Assert.assertNotNull(tmp[0]);
		Assert.assertNull(tmp[1]);
		Assert.assertNotNull(tmp[2]);

		verify(converter).toJava("a", component);
		verify(converter).toJava("b", component);
	}

}
