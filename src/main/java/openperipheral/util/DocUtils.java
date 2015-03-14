package openperipheral.util;

import joptsimple.internal.Strings;
import openmods.utils.CachedFactory;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.composed.IMethodMap;
import openperipheral.adapter.composed.IMethodMap.IMethodVisitor;
import openperipheral.api.adapter.Doc;

import com.google.common.base.Joiner;

public class DocUtils {

	public static final CachedFactory<Class<?>, String> DOC_TEXT_CACHE = new CachedFactory<Class<?>, String>() {
		@Override
		protected String create(Class<?> key) {
			Doc doc = key.getAnnotation(Doc.class);
			return doc != null? Joiner.on('\n').join(doc.value()) : "";
		}
	};

	public static void listMethods(final StringBuilder builder, IMethodMap methods) {
		methods.visitMethods(new IMethodVisitor() {
			@Override
			public void visit(String name, IMethodExecutor executor) {
				String methodDoc = executor.description().doc(name);
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
