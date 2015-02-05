package openperipheral.converter;

import java.lang.reflect.Type;
import java.util.Deque;
import java.util.List;
import java.util.Set;

import openmods.Log;
import openperipheral.api.converter.*;
import openperipheral.converter.inbound.*;
import openperipheral.converter.outbound.*;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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

	protected TypeConverter() {
		inbound.add(new ConverterNumberInbound());
		inbound.add(new ConverterEnumInbound());
		inbound.add(new ConverterStringInbound());

		inbound.add(new ConverterArrayInbound());
		inbound.add(new ConverterListInbound());
		inbound.add(new ConverterMapInbound());
		inbound.add(new ConverterSetInbound());

		inbound.add(new ConverterBypass());

		inbound.add(new ConverterItemStackInbound());
		inbound.add(new ConverterUuid());

		outbound.add(new ConverterBoolean());
		outbound.add(new ConverterNumberOutbound());
		outbound.add(new ConverterEnumOutbound());

		outbound.add(new ConverterArrayOutbound());
		outbound.add(new ConverterListOutbound());
		outbound.add(new ConverterMapOutbound());
		outbound.add(new ConverterSetOutbound());

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
			Preconditions.checkArgument((expected instanceof Class) && !((Class<?>)expected).isPrimitive(), "This value cannot be nil");
			return null;
		}

		for (IGenericInboundTypeConverter converter : inbound) {
			try {
				Object response = converter.toJava(this, obj, expected);
				if (response != null) return response;
			} catch (Throwable e) {
				Log.warn(e, "Type converter %s failed", converter);
				throw Throwables.propagate(e);
			}
		}

		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T toJava(Object obj, Class<? extends T> cls) {
		Object result = toJava(obj, (Type)cls);
		Preconditions.checkArgument(result == null || cls.isInstance(result), "Conversion of %s to type %s failed", obj, cls);
		return (T)result;
	}

	@Override
	public Object fromJava(Object obj) {
		if (obj == null || isIgnored(obj.getClass())) return obj;

		for (IOutboundTypeConverter converter : outbound) {
			try {
				Object response = converter.fromJava(this, obj);
				if (response != null) return response;
			} catch (Throwable e) {
				Log.warn(e, "Type converter %s failed", converter);
				throw Throwables.propagate(e);
			}
		}

		// should never get here, since ConverterString is catch-all
		throw new IllegalStateException("Conversion failed on value " + obj);
	}

}
