package openperipheral.util;

import java.util.List;
import java.util.Map;

import joptsimple.internal.Strings;
import openmods.utils.CachedFactory;
import openperipheral.adapter.IMethodDescription;
import openperipheral.adapter.IMethodDescription.IArgumentDescription;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.composed.IMethodMap;
import openperipheral.adapter.composed.IMethodMap.IMethodVisitor;
import openperipheral.api.adapter.Doc;
import openperipheral.api.adapter.method.ReturnType;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DocUtils {

	public static final String ARGS = "args";
	public static final String RETURN_TYPES = "returnTypes";
	public static final String DESCRIPTION = "description";
	public static final String SOURCE = "source";
	public static final String NAME = "name";
	public static final String TYPE = "type";

	public static final CachedFactory<Class<?>, String> DOC_TEXT_CACHE = new CachedFactory<Class<?>, String>() {
		@Override
		protected String create(Class<?> key) {
			Doc doc = key.getAnnotation(Doc.class);
			return doc != null? Joiner.on('\n').join(doc.value()) : "";
		}
	};

	public static Map<String, Object> describe(IMethodDescription desc) {
		Map<String, Object> result = Maps.newHashMap();
		result.put(DESCRIPTION, desc.description());
		result.put(SOURCE, desc.source());

		result.put(RETURN_TYPES, desc.returnTypes());

		{
			List<Map<String, Object>> args = Lists.newArrayList();
			for (IArgumentDescription arg : desc.arguments())
				args.add(describeArgument(arg));

			result.put(ARGS, args);
		}
		return result;
	}

	private static Map<String, Object> describeArgument(IArgumentDescription arg) {
		Map<String, Object> result = Maps.newHashMap();
		result.put(TYPE, arg.type());
		result.put(NAME, arg.name());
		result.put(DESCRIPTION, arg.description());

		if (arg.nullable()) result.put("nullable", true);
		if (arg.optional()) result.put("optional", true);
		if (arg.variadic()) result.put("vararg", true);

		return result;
	}

	public static String doc(IMethodDescription desc) {
		return "function" + createDocString(desc);
	}

	public static String doc(String name, IMethodDescription desc) {
		return "function " + name + createDocString(desc);
	}

	private static String createDocString(IMethodDescription desc) {
		// (arg:type[, optionArg:type]):resultType -- Description

		List<String> args = Lists.newArrayList();

		for (IArgumentDescription arg : desc.arguments())
			args.add(arg.name() + ":" + decorate(arg.type().getName(), arg));

		List<String> returns = Lists.newArrayList();

		for (ReturnType r : desc.returnTypes())
			returns.add(r.getName());

		String argsJoined = Joiner.on(',').join(args);
		String argsAndResult;
		if (returns.isEmpty()) {
			argsAndResult = String.format("(%s)", argsJoined);
		} else {
			String ret = returns.size() == 1? returns.get(0) : ("(" + Joiner.on(',').join(returns) + ")");

			argsAndResult = String.format("(%s):%s", argsJoined, ret);
		}

		return !Strings.isNullOrEmpty(desc.description())? argsAndResult + " -- " + desc.description() : argsAndResult;
	}

	private static String decorate(String id, IArgumentDescription arg) {
		if (arg.optional()) return id + "?";
		if (arg.variadic()) return id + "...";
		return id;
	}

	public static String signature(IMethodDescription desc) {
		List<String> tmp = Lists.newArrayList();
		for (IArgumentDescription arg : desc.arguments())
			tmp.add(decorate(arg.name(), arg));

		return "(" + Joiner.on(",").join(tmp) + ")";
	}

	public static void listMethods(final StringBuilder builder, IMethodMap methods) {
		methods.visitMethods(new IMethodVisitor() {
			@Override
			public void visit(String name, IMethodExecutor executor) {
				String methodDoc = doc(name, executor.description());
				builder.append(methodDoc);
				builder.append('\n');
			}
		});
	}

	public static String createPeripheralHelpText(Class<? extends Object> cls, String type, IMethodMap methods) {
		StringBuilder builder = new StringBuilder();
		builder.append("----OpenPeripheral doc----\n");
		builder.append("Peripheral type: ");
		builder.append(type);
		builder.append("\n\n");

		final String docText = DOC_TEXT_CACHE.getOrCreate(cls);
		if (!Strings.isNullOrEmpty(docText)) {
			builder.append(docText);
			builder.append("\n\n");
		}

		builder.append("---Methods---\n");

		listMethods(builder, methods);

		return builder.toString();
	}
}
