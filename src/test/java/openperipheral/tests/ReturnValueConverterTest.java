package openperipheral.tests;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import openperipheral.adapter.method.ReturnValueConverter;
import openperipheral.api.adapter.method.IMultipleReturnsHelper.IReturnTuple2;
import openperipheral.api.adapter.method.IReturnTuple;
import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.IGenericInboundTypeConverter;
import openperipheral.api.converter.IGenericTypeConverter;
import openperipheral.api.converter.IInboundTypeConverter;
import openperipheral.api.converter.IOutboundTypeConverter;
import openperipheral.api.converter.ITypeConverter;
import org.junit.Assert;
import org.junit.Test;

public class ReturnValueConverterTest {

	private static class ConvertedValue {
		private final Object value;

		public ConvertedValue(Object value) {
			this.value = value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((value == null)? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj instanceof ConvertedValue) {
				ConvertedValue other = (ConvertedValue)obj;
				return Objects.equal(this.value, other.value);
			}

			return false;
		}

		@Override
		public String toString() {
			return "converted[" + value + "]";
		}

	}

	public static Object wrap(Object value) {
		return new ConvertedValue(value);
	}

	public static final IConverter DUMMY_CONVERTER = new IConverter() {

		@Override
		public void registerIgnored(Class<?> ignored, boolean includeSubclasses) {}

		@Override
		public void register(ITypeConverter converter) {}

		@Override
		public void register(IGenericTypeConverter converter) {}

		@Override
		public void register(IOutboundTypeConverter converter) {}

		@Override
		public void register(IGenericInboundTypeConverter converter) {}

		@Override
		public void register(IInboundTypeConverter converter) {}

		@Override
		public <T> T toJava(Object obj, Class<? extends T> cls) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object toJava(Object obj, Type expected) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object fromJava(Object obj) {
			return wrap(obj);
		}
	};

	private static void testGeneric(Class<?> typeDonor, boolean isVarReturn, String docType, Object input, Object... expected) {
		try {
			final Method method = typeDonor.getMethod("target");
			final Type returnType = method.getGenericReturnType();
			test(returnType, isVarReturn, docType, input, expected);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void test(Type returnType, boolean isVarReturn, String docType, Object input, Object... expected) {
		final ReturnValueConverter converter = ReturnValueConverter.create(returnType, isVarReturn);

		Assert.assertEquals(docType, converter.getDocType().describe());

		final Object[] result = converter.convertReturns(DUMMY_CONVERTER, input);
		Assert.assertArrayEquals(expected, result);
	}

	@Test
	public void testNoReturns() {
		test(void.class, false, "void", 5);
	}

	@Test
	public void testSingleReturn() {
		test(Integer.class, false, "number", 5, wrap(5));
	}

	@Test
	public void testArrayReturnNoMulti() {
		final int[] input = new int[] { 1, 2, 3 };
		test(int[].class, false, "[number]", input, wrap(input));
	}

	@Test
	public void testArrayReturnAsMulti() {
		final int[] input = new int[] { 1, 2, 3 };
		test(int[].class, true, "number*", input, wrap(1), wrap(2), wrap(3));
	}

	private interface FloatList {
		public List<Float> target();
	}

	@Test
	public void testCollectionReturnNoMulti() {
		final List<Float> input = Lists.newArrayList(3.0f, 4.0f, 5.0f);
		testGeneric(FloatList.class, false, "[number]", input, wrap(input));
	}

	@Test
	public void testCollectionReturnAsMulti() {
		final List<Float> input = Lists.newArrayList(3.0f, 4.0f, 5.0f);
		testGeneric(FloatList.class, true, "number*", input, wrap(3.0f), wrap(4.0f), wrap(5.0f));
	}

	private interface TupleReturn2 {
		public IReturnTuple2<Float, String> target();
	}

	@Test
	public void testTupleReturn() {
		IReturnTuple input = new IReturnTuple() {

			@Override
			public Object[] values() {
				return new Object[] { 4.0f, "hello" };
			}
		};

		testGeneric(TupleReturn2.class, false, "number,string", input, wrap(4.0f), wrap("hello"));
	}
}
