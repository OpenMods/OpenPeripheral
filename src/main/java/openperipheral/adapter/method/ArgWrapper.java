package openperipheral.adapter.method;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import openmods.utils.AnnotationMap;

public class ArgWrapper {

	public final TypeToken<?> type;

	public final AnnotationMap annotations;

	public ArgWrapper(TypeToken<?> ownerType, Type argType, Annotation[] annotations) {
		this.type = ownerType.resolveType(argType);
		this.annotations = new AnnotationMap(annotations);
	}

	public static List<ArgWrapper> fromMethod(Class<?> rootClass, Method method) {
		final TypeToken<?> scopeType = TypeToken.of(rootClass);
		final Type methodArgs[] = method.getGenericParameterTypes();
		final Annotation[][] argsAnnotations = method.getParameterAnnotations();

		final List<ArgWrapper> args = Lists.newArrayList();
		for (int i = 0; i < methodArgs.length; i++)
			args.add(new ArgWrapper(scopeType, methodArgs[i], argsAnnotations[i]));
		return args;
	}

}
