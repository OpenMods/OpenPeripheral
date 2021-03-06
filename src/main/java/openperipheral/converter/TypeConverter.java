package openperipheral.converter;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import openmods.Log;
import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.IGenericInboundTypeConverter;
import openperipheral.api.converter.IGenericTypeConverter;
import openperipheral.api.converter.IInboundTypeConverter;
import openperipheral.api.converter.IOutboundTypeConverter;
import openperipheral.api.converter.ITypeConverter;
import openperipheral.converter.inbound.ConverterArrayInbound;
import openperipheral.converter.inbound.ConverterBypass;
import openperipheral.converter.inbound.ConverterEnumInbound;
import openperipheral.converter.inbound.ConverterItemStackInbound;
import openperipheral.converter.inbound.ConverterListInbound;
import openperipheral.converter.inbound.ConverterMapInbound;
import openperipheral.converter.inbound.ConverterNumberInbound;
import openperipheral.converter.inbound.ConverterRawInbound;
import openperipheral.converter.inbound.ConverterSetInbound;
import openperipheral.converter.inbound.ConverterStringInbound;
import openperipheral.converter.inbound.ConverterStructInbound;
import openperipheral.converter.inbound.ConverterUuid;
import openperipheral.converter.outbound.ConverterArrayOutbound;
import openperipheral.converter.outbound.ConverterBoolean;
import openperipheral.converter.outbound.ConverterEnumOutbound;
import openperipheral.converter.outbound.ConverterFluidStackOutbound;
import openperipheral.converter.outbound.ConverterFluidTankInfoOutbound;
import openperipheral.converter.outbound.ConverterGameProfileOutbound;
import openperipheral.converter.outbound.ConverterItemStackOutbound;
import openperipheral.converter.outbound.ConverterListOutbound;
import openperipheral.converter.outbound.ConverterMapOutbound;
import openperipheral.converter.outbound.ConverterNumberOutbound;
import openperipheral.converter.outbound.ConverterSetOutbound;
import openperipheral.converter.outbound.ConverterStringOutbound;
import openperipheral.converter.outbound.ConverterStructOutbound;

public abstract class TypeConverter implements IConverter {

	protected final Deque<IGenericInboundTypeConverter> inbound = Lists.newLinkedList();

	protected final Deque<IOutboundTypeConverter> outbound = Lists.newLinkedList();

	private final Set<Class<?>> directlyIgnored = Sets.newHashSet();

	private final List<Class<?>> subclassIngored = Lists.newArrayList();

	@Override
	public void registerIgnored(Class<?> ignored, boolean includeSubclasses) {
		// I'm so cool!
		(includeSubclasses? subclassIngored : directlyIgnored).add(ignored);
	}

	private boolean isIgnored(Class<?> cls) {
		if (directlyIgnored.contains(cls)) return true;

		for (Class<?> ignored : subclassIngored)
			if (ignored.isAssignableFrom(cls)) return true;

		return false;
	}

	protected TypeConverter(int indexOffset) {
		inbound.add(new ConverterRawInbound());

		inbound.add(new ConverterItemStackInbound());
		inbound.add(new ConverterUuid());

		inbound.add(new ConverterNumberInbound(indexOffset));
		inbound.add(new ConverterEnumInbound());
		inbound.add(new ConverterStringInbound());

		inbound.add(new ConverterArrayInbound(indexOffset));
		inbound.add(new ConverterListInbound(indexOffset));
		inbound.add(new ConverterMapInbound());
		inbound.add(new ConverterSetInbound());
		inbound.add(new ConverterStructInbound(indexOffset));

		inbound.add(new ConverterBypass());

		outbound.add(new ConverterBoolean());
		outbound.add(new ConverterNumberOutbound());
		outbound.add(new ConverterEnumOutbound());

		outbound.add(new ConverterArrayOutbound(indexOffset));
		outbound.add(new ConverterListOutbound(indexOffset));
		outbound.add(new ConverterMapOutbound());
		outbound.add(new ConverterSetOutbound());
		outbound.add(new ConverterStructOutbound(indexOffset));

		outbound.add(new ConverterItemStackOutbound());
		outbound.add(new ConverterGameProfileOutbound());
		outbound.add(new ConverterFluidTankInfoOutbound());
		outbound.add(new ConverterFluidStackOutbound());

		outbound.add(new ConverterStringOutbound());
	}

	@Override
	public void register(ITypeConverter converter) {
		Log.trace("Registering type converter %s", converter);
		inbound.addFirst(new InboundTypeConverterAdapter(converter));
		outbound.addFirst(converter);
	}

	@Override
	public void register(IGenericTypeConverter converter) {
		Log.trace("Registering generic type converter %s", converter);
		inbound.addFirst(converter);
		outbound.addFirst(converter);
	}

	@Override
	public void register(IInboundTypeConverter converter) {
		inbound.addFirst(new InboundTypeConverterAdapter(converter));
	}

	@Override
	public void register(IGenericInboundTypeConverter converter) {
		inbound.addFirst(converter);
	}

	@Override
	public void register(IOutboundTypeConverter converter) {
		outbound.addFirst(converter);
	}

	@Override
	public Object toJava(Object obj, Type expected) {
		if (obj == null) {
			final TypeToken<?> type = TypeToken.of(expected);
			Preconditions.checkArgument(type.isPrimitive(), "Type %s cannot be nil", type);
			return null;
		}

		for (IGenericInboundTypeConverter converter : inbound) {
			Object response = converter.toJava(this, obj, expected);
			if (response != null) return response;
		}

		final TypeToken<?> type = TypeToken.of(expected);
		throw new IllegalArgumentException(String.format("No known conversion of value %s to %s", obj, type.getRawType().getSimpleName()));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T toJava(Object obj, Class<? extends T> cls) {
		Object result = toJava(obj, (Type)cls);
		Preconditions.checkArgument(cls.isInstance(result), "Conversion of %s to type %s failed", obj, cls);
		return (T)result;
	}

	@Override
	public Object fromJava(Object obj) {
		if (obj == null || isIgnored(obj.getClass())) return obj;

		for (IOutboundTypeConverter converter : outbound) {
			Object response = converter.fromJava(this, obj);
			if (response != null) return response;
		}

		// should never get here, since ConverterString is catch-all
		throw new IllegalArgumentException("Conversion failed on value " + obj);
	}

	public static Object nullableToJava(IConverter converter, Object value, Type expectedType) {
		return (value != null)? converter.toJava(value, expectedType) : null;
	}

	public static Object nullableToJava(IConverter converter, boolean nullable, Object value, Type expectedType) {
		if (value == null) {
			Preconditions.checkArgument(nullable, "This value cannot be null");
			return null;
		}

		return converter.toJava(value, expectedType);
	}

}
