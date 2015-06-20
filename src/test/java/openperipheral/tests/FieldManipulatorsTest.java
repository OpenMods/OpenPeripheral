package openperipheral.tests;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import openperipheral.adapter.property.IIndexedFieldManipulator;
import openperipheral.adapter.property.IndexedManipulatorProvider;
import openperipheral.api.helpers.Index;
import openperipheral.api.struct.ScriptStruct;
import openperipheral.api.struct.StructField;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class FieldManipulatorsTest {

	public static class Holder<T> {
		public T target;

		public Holder(T target) {
			this.target = target;
		}
	}

	public final Field targetField;

	{
		try {
			targetField = Holder.class.getField("target");
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	public static Index index(int index) {
		return new Index(index, 1);
	}

	public <T> T testSetField(IIndexedFieldManipulator manipulator, T container, Object index, Object value) {
		Holder<T> holder = new Holder<T>(container);
		manipulator.setField(holder, targetField, index, value);
		return holder.target;
	}

	private <T> void testSetFieldFail(IIndexedFieldManipulator manipulator, T container, Object index, Object value) {
		try {
			testSetField(manipulator, container, index, value);
			Assert.fail("Exception not thrown");
		} catch (Exception e) {}
	}

	public <T> Object testGetField(IIndexedFieldManipulator manipulator, T value, Object index) {
		Holder<T> holder = new Holder<T>(value);
		return manipulator.getField(holder, targetField, index);
	}

	public <T> Object testGetIndexedField(IIndexedFieldManipulator manipulator, T value, int index) {
		return testGetField(manipulator, value, index(index));
	}

	public <T> void testGetFieldFail(IIndexedFieldManipulator manipulator, T value, Object index) {
		try {
			testGetField(manipulator, value, index);
			Assert.fail("Exception not thrown");
		} catch (Exception e) {}
	}

	private int[] testIntArray(IIndexedFieldManipulator manipulator, int index, int value, int[] input, int... template) {
		int[] result = testSetField(manipulator, input, index(index), value);
		Assert.assertArrayEquals(template, result);
		return result;
	}

	private <T> T[] testObjectArray(IIndexedFieldManipulator manipulator, int index, T value, T[] input, T... template) {
		T[] result = testSetField(manipulator, input, index(index), value);
		Assert.assertArrayEquals(template, result);
		return result;
	}

	@Test
	public void testArrayManipulatorGetPrimitive() {
		int[] target = new int[] { 5, 6, 8, 9 };
		Assert.assertEquals(5, testGetIndexedField(IndexedManipulatorProvider.ARRAY_MANIPULATOR, target, 1));
		Assert.assertEquals(8, testGetIndexedField(IndexedManipulatorProvider.ARRAY_MANIPULATOR, target, 3));
		Assert.assertEquals(9, testGetIndexedField(IndexedManipulatorProvider.ARRAY_MANIPULATOR, target, 4));

		Assert.assertEquals(null, testGetIndexedField(IndexedManipulatorProvider.ARRAY_MANIPULATOR, target, -1));
		Assert.assertEquals(null, testGetIndexedField(IndexedManipulatorProvider.ARRAY_MANIPULATOR, target, 5));
	}

	@Test
	public void testArrayManipulatorGetObject() {
		String[] target = new String[] { "a", "bb", null, "ccc" };
		Assert.assertEquals("a", testGetIndexedField(IndexedManipulatorProvider.ARRAY_MANIPULATOR, target, 1));
		Assert.assertEquals(null, testGetIndexedField(IndexedManipulatorProvider.ARRAY_MANIPULATOR, target, 3));
		Assert.assertEquals("ccc", testGetIndexedField(IndexedManipulatorProvider.ARRAY_MANIPULATOR, target, 4));

		Assert.assertEquals(null, testGetIndexedField(IndexedManipulatorProvider.ARRAY_MANIPULATOR, target, -1));
		Assert.assertEquals(null, testGetIndexedField(IndexedManipulatorProvider.ARRAY_MANIPULATOR, target, 5));
	}

	@Test
	public void testArrayManipulatorSetPrimitive() {
		int[] target = new int[] { 5, 6, 8, 9 };
		target = testIntArray(IndexedManipulatorProvider.ARRAY_MANIPULATOR, 1, 4, target, 4, 6, 8, 9);
		target = testIntArray(IndexedManipulatorProvider.ARRAY_MANIPULATOR, 4, 12, target, 4, 6, 8, 12);

		testSetFieldFail(IndexedManipulatorProvider.ARRAY_MANIPULATOR, target, -1, 2);
		testSetFieldFail(IndexedManipulatorProvider.ARRAY_MANIPULATOR, target, 5, 2);
	}

	@Test
	public void testArrayManipulatorSetObject() {
		String[] target = new String[] { "a", "bb", null, "ccc" };
		target = testObjectArray(IndexedManipulatorProvider.ARRAY_MANIPULATOR, 1, ":D", target, ":D", "bb", null, "ccc");
		target = testObjectArray(IndexedManipulatorProvider.ARRAY_MANIPULATOR, 3, "XD", target, ":D", "bb", "XD", "ccc");

		testSetFieldFail(IndexedManipulatorProvider.ARRAY_MANIPULATOR, target, -1, "nope");
		testSetFieldFail(IndexedManipulatorProvider.ARRAY_MANIPULATOR, target, 5, "nope");
	}

	@Test
	public void testExpandingArrayManipulatorGetPrimitive() {
		int[] target = new int[] { 5, 6, 8, 9 };
		Assert.assertEquals(5, testGetIndexedField(IndexedManipulatorProvider.ARRAY_EXPANDING_MANIPULATOR, target, 1));
		Assert.assertEquals(8, testGetIndexedField(IndexedManipulatorProvider.ARRAY_EXPANDING_MANIPULATOR, target, 3));
		Assert.assertEquals(9, testGetIndexedField(IndexedManipulatorProvider.ARRAY_EXPANDING_MANIPULATOR, target, 4));

		Assert.assertEquals(null, testGetIndexedField(IndexedManipulatorProvider.ARRAY_EXPANDING_MANIPULATOR, target, -1));
		Assert.assertEquals(null, testGetIndexedField(IndexedManipulatorProvider.ARRAY_EXPANDING_MANIPULATOR, target, 5));
	}

	@Test
	public void testExpandingArrayManipulatorGetObject() {
		String[] target = new String[] { "a", "bb", null, "ccc" };
		Assert.assertEquals("a", testGetIndexedField(IndexedManipulatorProvider.ARRAY_EXPANDING_MANIPULATOR, target, 1));
		Assert.assertEquals(null, testGetIndexedField(IndexedManipulatorProvider.ARRAY_EXPANDING_MANIPULATOR, target, 3));
		Assert.assertEquals("ccc", testGetIndexedField(IndexedManipulatorProvider.ARRAY_EXPANDING_MANIPULATOR, target, 4));

		Assert.assertEquals(null, testGetIndexedField(IndexedManipulatorProvider.ARRAY_EXPANDING_MANIPULATOR, target, -1));
		Assert.assertEquals(null, testGetIndexedField(IndexedManipulatorProvider.ARRAY_EXPANDING_MANIPULATOR, target, 5));
	}

	@Test
	public void testExpandingArrayManipulatorSetPrimitive() {
		int[] target = new int[] { 5, 6, 8, 9 };
		target = testIntArray(IndexedManipulatorProvider.ARRAY_EXPANDING_MANIPULATOR, 1, 4, target, 4, 6, 8, 9);
		target = testIntArray(IndexedManipulatorProvider.ARRAY_EXPANDING_MANIPULATOR, 4, 12, target, 4, 6, 8, 12);
		target = testIntArray(IndexedManipulatorProvider.ARRAY_EXPANDING_MANIPULATOR, 5, 32, target, 4, 6, 8, 12, 32);
		target = testIntArray(IndexedManipulatorProvider.ARRAY_EXPANDING_MANIPULATOR, 7, 44, target, 4, 6, 8, 12, 32, 0, 44);

		testSetFieldFail(IndexedManipulatorProvider.ARRAY_EXPANDING_MANIPULATOR, target, -1, 5);
	}

	@Test
	public void testExpandingArrayManipulatorSetObject() {
		String[] target = new String[] { "a", "bb", null, "ccc" };
		target = testObjectArray(IndexedManipulatorProvider.ARRAY_EXPANDING_MANIPULATOR, 1, ":D", target, ":D", "bb", null, "ccc");
		target = testObjectArray(IndexedManipulatorProvider.ARRAY_EXPANDING_MANIPULATOR, 3, "XD", target, ":D", "bb", "XD", "ccc");
		target = testObjectArray(IndexedManipulatorProvider.ARRAY_EXPANDING_MANIPULATOR, 5, "D:", target, ":D", "bb", "XD", "ccc", "D:");
		target = testObjectArray(IndexedManipulatorProvider.ARRAY_EXPANDING_MANIPULATOR, 7, ":|", target, ":D", "bb", "XD", "ccc", "D:", null, ":|");

		testSetFieldFail(IndexedManipulatorProvider.ARRAY_EXPANDING_MANIPULATOR, target, -1, "nope");
	}

	@Test
	public void testListManipulatorGet() {
		List<Integer> target = Collections.unmodifiableList(Lists.newArrayList(1, 3, 5, null, 9));
		Assert.assertEquals(null, testGetIndexedField(IndexedManipulatorProvider.LIST_MANIPULATOR, target, 0));
		Assert.assertEquals(1, testGetIndexedField(IndexedManipulatorProvider.LIST_MANIPULATOR, target, 1));
		Assert.assertEquals(3, testGetIndexedField(IndexedManipulatorProvider.LIST_MANIPULATOR, target, 2));
		Assert.assertEquals(5, testGetIndexedField(IndexedManipulatorProvider.LIST_MANIPULATOR, target, 3));
		Assert.assertEquals(null, testGetIndexedField(IndexedManipulatorProvider.LIST_EXPANDING_MANIPULATOR, target, 4));
		Assert.assertEquals(9, testGetIndexedField(IndexedManipulatorProvider.LIST_MANIPULATOR, target, 5));
		Assert.assertEquals(null, testGetIndexedField(IndexedManipulatorProvider.LIST_MANIPULATOR, target, 6));
	}

	private <T> List<T> testListSet(IIndexedFieldManipulator manipulator, List<T> target, int index, T value, T... expected) {
		List<T> result = testSetField(manipulator, target, index(index), value);
		List<T> template = Lists.newArrayList(expected);
		Assert.assertEquals(template, result);
		return result;
	}

	@Test
	public void testListManipulatorSet() {
		List<Integer> target = Lists.newArrayList(1, 3, 5, 6, 9);
		target = testListSet(IndexedManipulatorProvider.LIST_MANIPULATOR, target, 1, 3, 3, 3, 5, 6, 9);
		target = testListSet(IndexedManipulatorProvider.LIST_MANIPULATOR, target, 5, 0, 3, 3, 5, 6, 0);
		target = testListSet(IndexedManipulatorProvider.LIST_MANIPULATOR, target, 4, null, 3, 3, 5, null, 0);

		testSetFieldFail(IndexedManipulatorProvider.LIST_MANIPULATOR, target, -1, 3);
		testSetFieldFail(IndexedManipulatorProvider.LIST_MANIPULATOR, target, 6, 4);
	}

	@Test
	public void testExpandingListManipulatorGet() {
		List<Integer> target = Collections.unmodifiableList(Lists.newArrayList(1, 3, 5, null, 9));
		Assert.assertEquals(null, testGetIndexedField(IndexedManipulatorProvider.LIST_EXPANDING_MANIPULATOR, target, 0));
		Assert.assertEquals(1, testGetIndexedField(IndexedManipulatorProvider.LIST_EXPANDING_MANIPULATOR, target, 1));
		Assert.assertEquals(3, testGetIndexedField(IndexedManipulatorProvider.LIST_EXPANDING_MANIPULATOR, target, 2));
		Assert.assertEquals(5, testGetIndexedField(IndexedManipulatorProvider.LIST_EXPANDING_MANIPULATOR, target, 3));
		Assert.assertEquals(null, testGetIndexedField(IndexedManipulatorProvider.LIST_EXPANDING_MANIPULATOR, target, 4));
		Assert.assertEquals(9, testGetIndexedField(IndexedManipulatorProvider.LIST_EXPANDING_MANIPULATOR, target, 5));
		Assert.assertEquals(null, testGetIndexedField(IndexedManipulatorProvider.LIST_EXPANDING_MANIPULATOR, target, 6));
	}

	@Test
	public void testExpandingListManipulatorSet() {
		List<Integer> target = Lists.newArrayList(1, 3, 5, 6, 9);
		target = testListSet(IndexedManipulatorProvider.LIST_EXPANDING_MANIPULATOR, target, 1, 3, 3, 3, 5, 6, 9);
		target = testListSet(IndexedManipulatorProvider.LIST_EXPANDING_MANIPULATOR, target, 5, 0, 3, 3, 5, 6, 0);
		target = testListSet(IndexedManipulatorProvider.LIST_EXPANDING_MANIPULATOR, target, 6, 42, 3, 3, 5, 6, 0, 42);
		target = testListSet(IndexedManipulatorProvider.LIST_EXPANDING_MANIPULATOR, target, 8, 5, 3, 3, 5, 6, 0, 42, null, 5);

		testSetFieldFail(IndexedManipulatorProvider.LIST_EXPANDING_MANIPULATOR, target, -1, 3);
	}

	@Test
	public void testMapManipulatorGetStringKey() {
		Map<String, Integer> target = Maps.newHashMap();
		target.put("a", 2);
		target.put("b", 6);
		target.put("c", null);
		target.put("f", -1);
		target = Collections.unmodifiableMap(target);

		Assert.assertEquals(null, testGetField(IndexedManipulatorProvider.MAP_MANIPULATOR, target, 42));
		Assert.assertEquals(null, testGetField(IndexedManipulatorProvider.MAP_MANIPULATOR, target, "z"));
		Assert.assertEquals(null, testGetField(IndexedManipulatorProvider.MAP_MANIPULATOR, target, "c"));
		Assert.assertEquals(2, testGetField(IndexedManipulatorProvider.MAP_MANIPULATOR, target, "a"));
		Assert.assertEquals(6, testGetField(IndexedManipulatorProvider.MAP_MANIPULATOR, target, "b"));
		Assert.assertEquals(-1, testGetField(IndexedManipulatorProvider.MAP_MANIPULATOR, target, "f"));
	}

	@Test
	public void testMapManipulatorGetIntegerKey() {
		Map<Integer, String> target = Maps.newHashMap();
		target.put(2, "a");
		target.put(6, "b");
		target.put(3, null);
		target.put(8, "f");
		target = Collections.unmodifiableMap(target);

		Assert.assertEquals(null, testGetField(IndexedManipulatorProvider.MAP_MANIPULATOR, target, 42));
		Assert.assertEquals(null, testGetField(IndexedManipulatorProvider.MAP_MANIPULATOR, target, "z"));
		Assert.assertEquals(null, testGetField(IndexedManipulatorProvider.MAP_MANIPULATOR, target, 3));
		Assert.assertEquals("a", testGetField(IndexedManipulatorProvider.MAP_MANIPULATOR, target, 2));
		Assert.assertEquals("b", testGetField(IndexedManipulatorProvider.MAP_MANIPULATOR, target, 6));
		Assert.assertEquals("f", testGetField(IndexedManipulatorProvider.MAP_MANIPULATOR, target, 8));
	}

	private <K, V> Map<K, V> testMapSet(IIndexedFieldManipulator manipulator, Map<K, V> target, K key, V value, Map<K, V> expected) {
		expected.put(key, value);
		Map<K, V> result = testSetField(manipulator, target, key, value);
		Assert.assertEquals(expected, result);
		return result;
	}

	@Test
	public void testMapManipulatorSetIntegerKey() {
		Map<Integer, String> target = Maps.newHashMap();
		target.put(2, "a");
		target.put(6, "b");
		target.put(3, null);
		target.put(8, "f");

		Map<Integer, String> expected = Maps.newHashMap(target);
		target = testMapSet(IndexedManipulatorProvider.MAP_MANIPULATOR, target, 2, "b", expected);
		target = testMapSet(IndexedManipulatorProvider.MAP_MANIPULATOR, target, 3, "xyz", expected);
		target = testMapSet(IndexedManipulatorProvider.MAP_MANIPULATOR, target, 8, null, expected);

		target = Collections.unmodifiableMap(target);
		testSetFieldFail(IndexedManipulatorProvider.MAP_MANIPULATOR, target, -1, 3);
		testSetFieldFail(IndexedManipulatorProvider.MAP_MANIPULATOR, target, "aaaa", 3);
	}

	@Test
	public void testMapManipulatorSetStringKey() {
		Map<String, Integer> target = Maps.newHashMap();
		target.put("a", 2);
		target.put("b", 6);
		target.put("c", null);
		target.put("f", -1);

		Map<String, Integer> expected = Maps.newHashMap(target);
		target = testMapSet(IndexedManipulatorProvider.MAP_MANIPULATOR, target, "b", 2, expected);
		target = testMapSet(IndexedManipulatorProvider.MAP_MANIPULATOR, target, "c", 3, expected);
		target = testMapSet(IndexedManipulatorProvider.MAP_MANIPULATOR, target, "f", null, expected);

		target = Collections.unmodifiableMap(target);
		testSetFieldFail(IndexedManipulatorProvider.MAP_MANIPULATOR, target, -1, 3);
		testSetFieldFail(IndexedManipulatorProvider.MAP_MANIPULATOR, target, "aaaa", 3);
	}

	@Test
	public void testExpandingMapManipulatorGetStringKey() {
		Map<String, Integer> target = Maps.newHashMap();
		target.put("a", 2);
		target.put("b", 6);
		target.put("c", null);
		target.put("f", -1);
		target = Collections.unmodifiableMap(target);

		Assert.assertEquals(null, testGetField(IndexedManipulatorProvider.MAP_EXPANDING_MANIPULATOR, target, 42));
		Assert.assertEquals(null, testGetField(IndexedManipulatorProvider.MAP_EXPANDING_MANIPULATOR, target, "z"));
		Assert.assertEquals(null, testGetField(IndexedManipulatorProvider.MAP_EXPANDING_MANIPULATOR, target, "c"));
		Assert.assertEquals(2, testGetField(IndexedManipulatorProvider.MAP_EXPANDING_MANIPULATOR, target, "a"));
		Assert.assertEquals(6, testGetField(IndexedManipulatorProvider.MAP_EXPANDING_MANIPULATOR, target, "b"));
		Assert.assertEquals(-1, testGetField(IndexedManipulatorProvider.MAP_EXPANDING_MANIPULATOR, target, "f"));
	}

	@Test
	public void testExpandingMapManipulatorGetIntegerKey() {
		Map<Integer, String> target = Maps.newHashMap();
		target.put(2, "a");
		target.put(6, "b");
		target.put(3, null);
		target.put(8, "f");
		target = Collections.unmodifiableMap(target);

		Assert.assertEquals(null, testGetField(IndexedManipulatorProvider.MAP_EXPANDING_MANIPULATOR, target, 42));
		Assert.assertEquals(null, testGetField(IndexedManipulatorProvider.MAP_EXPANDING_MANIPULATOR, target, "z"));
		Assert.assertEquals(null, testGetField(IndexedManipulatorProvider.MAP_EXPANDING_MANIPULATOR, target, 3));
		Assert.assertEquals("a", testGetField(IndexedManipulatorProvider.MAP_EXPANDING_MANIPULATOR, target, 2));
		Assert.assertEquals("b", testGetField(IndexedManipulatorProvider.MAP_EXPANDING_MANIPULATOR, target, 6));
		Assert.assertEquals("f", testGetField(IndexedManipulatorProvider.MAP_EXPANDING_MANIPULATOR, target, 8));
	}

	@Test
	public void testExpandingMapManipulatorSetIntegerKey() {
		Map<Integer, String> target = Maps.newHashMap();
		target.put(2, "a");
		target.put(6, "b");
		target.put(3, null);
		target.put(8, "f");

		Map<Integer, String> expected = Maps.newHashMap(target);
		target = testMapSet(IndexedManipulatorProvider.MAP_EXPANDING_MANIPULATOR, target, 2, "b", expected);
		target = testMapSet(IndexedManipulatorProvider.MAP_EXPANDING_MANIPULATOR, target, 3, "xyz", expected);
		target = testMapSet(IndexedManipulatorProvider.MAP_EXPANDING_MANIPULATOR, target, 8, null, expected);
		target = testMapSet(IndexedManipulatorProvider.MAP_EXPANDING_MANIPULATOR, target, -1, "8", expected);
		target = testMapSet(IndexedManipulatorProvider.MAP_EXPANDING_MANIPULATOR, target, 10, null, expected);
	}

	@Test
	public void testExpandingMapManipulatorSetStringKey() {
		Map<String, Integer> target = Maps.newHashMap();
		target.put("a", 2);
		target.put("b", 6);
		target.put("c", null);
		target.put("f", -1);

		Map<String, Integer> expected = Maps.newHashMap(target);
		testMapSet(IndexedManipulatorProvider.MAP_EXPANDING_MANIPULATOR, target, "b", 2, expected);
		testMapSet(IndexedManipulatorProvider.MAP_EXPANDING_MANIPULATOR, target, "c", 3, expected);
		testMapSet(IndexedManipulatorProvider.MAP_EXPANDING_MANIPULATOR, target, "f", null, expected);
		testMapSet(IndexedManipulatorProvider.MAP_EXPANDING_MANIPULATOR, target, "g", 58, expected);
		testMapSet(IndexedManipulatorProvider.MAP_EXPANDING_MANIPULATOR, target, "4", null, expected);
	}

	@ScriptStruct
	public static class SimpleStruct {
		@StructField
		public String string;

		@StructField
		public int integer;
	}

	@Test
	public void testStructManipulatorGet() {
		IIndexedFieldManipulator manipulator = IndexedManipulatorProvider.createStructManipulator(SimpleStruct.class);

		SimpleStruct test = new SimpleStruct();
		final String stringValue = "hellosfdfdf";
		final int intValue = 4352;

		test.string = stringValue;
		test.integer = intValue;

		Assert.assertEquals(stringValue, testGetField(manipulator, test, "string"));
		Assert.assertEquals(intValue, testGetField(manipulator, test, "integer"));

		testGetFieldFail(manipulator, test, "aaaaa");
		testGetFieldFail(manipulator, test, "");
		testGetFieldFail(manipulator, test, 2);
	}

	@Test
	public void testStructManipulatorSet() {
		IIndexedFieldManipulator manipulator = IndexedManipulatorProvider.createStructManipulator(SimpleStruct.class);

		SimpleStruct test = new SimpleStruct();
		final String stringValue = "hellosfdfdf";
		final int intValue = 4352;

		Assert.assertEquals(intValue, testSetField(manipulator, test, "integer", intValue).integer);
		Assert.assertEquals(stringValue, testSetField(manipulator, test, "string", stringValue).string);
		Assert.assertEquals(null, testSetField(manipulator, test, "string", null).string);

		testSetFieldFail(manipulator, test, "aaaaa", 0);
		testSetFieldFail(manipulator, test, "", 0);
		testSetFieldFail(manipulator, test, 2, 0);
		testSetFieldFail(manipulator, test, "integer", stringValue);
		testSetFieldFail(manipulator, test, "integer", null);
		testSetFieldFail(manipulator, test, "string", intValue);
	}
}
