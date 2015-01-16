package openperipheral.adapter.composed;

import java.util.*;

import openperipheral.Config;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.MethodMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public abstract class ClassMethodsComposer<E extends IMethodExecutor> {

	protected abstract ClassMethodsListBuilder<E> createBuilder();

	public MethodMap<E> createMethodsList(Class<?> cls) {
		ClassMethodsListBuilder<E> builder = createBuilder();
		final List<Class<?>> classHierarchy = Lists.reverse(listSuperClasses(cls));

		Set<Class<?>> allSuperInterfaces = Sets.newHashSet();
		for (Class<?> c : classHierarchy)
			allSuperInterfaces.addAll(listSuperInterfaces(c));

		for (Class<?> c : allSuperInterfaces) {
			builder.addExternalAdapters(cls, c);
			builder.addInlineAdapter(c);
		}

		for (Class<?> c : classHierarchy) {
			builder.addExternalAdapters(cls, c);
			builder.addInlineAdapter(c);
		}

		if (Config.devMethods) builder.addMethodsFromObject(new LuaReflectionHelper(), "<reflection>");

		if (!builder.hasMethods()) return new MethodMap<E>();

		builder.addMethodsFromObject(new MethodsListerHelper<E>(builder.getMethodList(), builder.getSources()), "<meta>");

		return builder.create();
	}

	private static List<Class<?>> listSuperClasses(Class<?> cls) {
		List<Class<?>> superClasses = Lists.newArrayList();
		Class<?> currentClass = cls;
		while (currentClass != Object.class) {
			superClasses.add(currentClass);
			currentClass = currentClass.getSuperclass();
		}
		return superClasses;
	}

	private static Set<Class<?>> listSuperInterfaces(Class<?> cls) {
		Set<Class<?>> superInterfaces = Sets.newHashSet();
		Queue<Class<?>> tbd = Lists.newLinkedList();
		tbd.addAll(Arrays.asList(cls.getInterfaces()));

		Class<?> currentClass;
		while ((currentClass = tbd.poll()) != null) {
			superInterfaces.add(currentClass);
			tbd.addAll(Arrays.asList(currentClass.getInterfaces()));
		}
		return superInterfaces;
	}
}
