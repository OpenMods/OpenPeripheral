package openperipheral.interfaces.oc.asm;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Node;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.composed.IndexedMethodMap;
import openperipheral.api.architecture.IAttachable;
import openperipheral.api.architecture.oc.IOpenComputersAttachable;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.Method;

import com.google.common.collect.Maps;

public class EnvironmentCodeGenerator {

	private static final String TARGET_FIELD = "target";

	private static final Type BASE_TYPE = Type.getType(EnvironmentBase.class);

	public static final Type EXECUTOR_TYPE = Type.getType(IMethodExecutor.class);

	public static final Type EXECUTORS_TYPE = Type.getType(IMethodExecutor[].class);

	private static final Type OBJECT_TYPE = Type.getType(Object.class);

	private static final Type OBJECTS_TYPE = Type.getType(Object[].class);

	private static final Type CONTEXT_TYPE = Type.getType(Context.class);

	private static final Type ARGUMENTS_TYPE = Type.getType(Arguments.class);

	private static final Type CALLBACK_TYPE = Type.getType(Callback.class);

	public static final Type ATTACHABLE_TYPE = Type.getType(IAttachable.class);

	public static final Type NODE_TYPE = Type.getType(Node.class);

	public static final Type OC_ATTACHABLE_TYPE = Type.getType(IOpenComputersAttachable.class);

	public static final Type SUPER_CTOR_TYPE = Type.getMethodType(Type.VOID_TYPE, OBJECT_TYPE);

	public static final Type GET_METHOD_TYPE = Type.getMethodType(EXECUTOR_TYPE, Type.INT_TYPE);

	public static final Type CALL_TYPE = Type.getMethodType(OBJECTS_TYPE, OBJECT_TYPE, EXECUTOR_TYPE, CONTEXT_TYPE, ARGUMENTS_TYPE);

	public static final Type WRAP_TYPE = Type.getMethodType(OBJECTS_TYPE, CONTEXT_TYPE, ARGUMENTS_TYPE);

	public static final Type ATTACHABLE_WRAP_TYPE = Type.getMethodType(Type.VOID_TYPE, ATTACHABLE_TYPE, NODE_TYPE);

	public static final Type OC_ATTACHABLE_WRAP_TYPE = Type.getMethodType(Type.VOID_TYPE, OC_ATTACHABLE_TYPE, NODE_TYPE);

	public static final Type CONNECTIVITY_METHOD_TYPE = Type.getMethodType(Type.VOID_TYPE, NODE_TYPE);

	private static void visitIntConst(MethodVisitor mv, int value) {
		switch (value) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
				mv.visitInsn(Opcodes.ICONST_0 + value);
				break;
			default:
				mv.visitLdcInsn(value);
				break;
		}
	}

	@SuppressWarnings("deprecation")
	public byte[] generate(String clsName, Class<?> targetClass, Set<Class<?>> exposedInterfaces, IndexedMethodMap methods) {
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

		writer.visit(Opcodes.V1_6,
				Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SYNTHETIC | Opcodes.ACC_SUPER,
				clsName, null, BASE_TYPE.getInternalName(), getInterfaces(exposedInterfaces));

		Type targetType = Type.getType(targetClass);

		writer.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, TARGET_FIELD, targetType.getDescriptor(), null, null);
		writer.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, EnvironmentBase.METHODS_FIELD, EXECUTORS_TYPE.getDescriptor(), null, null);

		{
			final Type ctorType = Type.getMethodType(Type.VOID_TYPE, targetType);

			MethodVisitor init = writer.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC, "<init>", ctorType.getDescriptor(), null, null);
			init.visitCode();
			init.visitVarInsn(Opcodes.ALOAD, 0);
			init.visitVarInsn(Opcodes.ALOAD, 1);
			init.visitInsn(Opcodes.DUP2);
			init.visitMethodInsn(Opcodes.INVOKESPECIAL, BASE_TYPE.getInternalName(), "<init>", SUPER_CTOR_TYPE.getDescriptor());
			init.visitFieldInsn(Opcodes.PUTFIELD, clsName, TARGET_FIELD, targetType.getDescriptor());
			init.visitInsn(Opcodes.RETURN);

			init.visitMaxs(0, 0);

			init.visitEnd();
		}

		final Map<Method, Type> exposedMethods = getExposedMethods(exposedInterfaces);
		for (Map.Entry<Method, Type> e : exposedMethods.entrySet()) {
			final Method method = e.getKey();
			final Type intf = e.getValue();
			MethodVisitor mv = writer.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC, method.getName(), method.getDescriptor(), null, null);

			mv.visitCode();

			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitFieldInsn(Opcodes.GETFIELD, clsName, TARGET_FIELD, targetType.getDescriptor());

			Type[] args = method.getArgumentTypes();
			for (int i = 0; i < args.length; i++)
				mv.visitVarInsn(args[i].getOpcode(Opcodes.ILOAD), i + 1);

			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, intf.getInternalName(), method.getName(), method.getDescriptor());
			Type returnType = method.getReturnType();
			mv.visitInsn(returnType.getOpcode(Opcodes.IRETURN));

			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}

		String[] names = methods.getMethodNames();

		for (int i = 0; i < names.length; i++) {
			IMethodExecutor executor = methods.getMethod(i);

			String name = names[i];
			String methodName = name.replaceAll("[^A-Za-z0-9_]", "_") + "$" + Integer.toString(i);

			MethodVisitor wrap = writer.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC, methodName, WRAP_TYPE.getDescriptor(), null, null);

			AnnotationVisitor av = wrap.visitAnnotation(CALLBACK_TYPE.getDescriptor(), true);
			av.visit("value", name);
			av.visit("direct", executor.isAsynchronous());
			av.visit("doc", executor.description().doc());
			av.visitEnd();
			// TODO: getter/setter

			av.visitEnd();

			wrap.visitCode();

			wrap.visitVarInsn(Opcodes.ALOAD, 0); // this
			wrap.visitInsn(Opcodes.DUP);
			wrap.visitFieldInsn(Opcodes.GETFIELD, clsName, TARGET_FIELD, targetType.getDescriptor());

			wrap.visitFieldInsn(Opcodes.GETSTATIC, clsName, EnvironmentBase.METHODS_FIELD, EXECUTORS_TYPE.getDescriptor());
			visitIntConst(wrap, i);
			wrap.visitInsn(Opcodes.AALOAD); // executor

			wrap.visitVarInsn(Opcodes.ALOAD, 1); // context
			wrap.visitVarInsn(Opcodes.ALOAD, 2); // args
			wrap.visitMethodInsn(Opcodes.INVOKEVIRTUAL, clsName, "call", CALL_TYPE.getDescriptor());
			wrap.visitInsn(Opcodes.ARETURN);

			wrap.visitMaxs(0, 0);

			wrap.visitEnd();
		}

		final boolean isAttachable = IAttachable.class.isAssignableFrom(targetClass);
		final boolean isOcAttachable = IOpenComputersAttachable.class.isAssignableFrom(targetClass);

		if (isAttachable || isOcAttachable) {
			visitConnectivityMethod("onConnect", clsName, writer, targetType, isAttachable, isOcAttachable);
			visitConnectivityMethod("onDisconnect", clsName, writer, targetType, isAttachable, isOcAttachable);
		}

		writer.visitEnd();

		return writer.toByteArray();
	}

	@SuppressWarnings("deprecation")
	protected void visitConnectivityMethod(String methodName, String clsName, ClassWriter writer, Type targetType, final boolean isAttachable, final boolean isOcAttachable) {
		MethodVisitor onConnect = writer.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC, methodName, CONNECTIVITY_METHOD_TYPE.getDescriptor(), null, null);

		onConnect.visitCode();

		if (isAttachable) {
			onConnect.visitVarInsn(Opcodes.ALOAD, 0);
			onConnect.visitInsn(Opcodes.DUP);
			onConnect.visitFieldInsn(Opcodes.GETFIELD, clsName, TARGET_FIELD, targetType.getDescriptor());
			onConnect.visitVarInsn(Opcodes.ALOAD, 1);
			onConnect.visitMethodInsn(Opcodes.INVOKEVIRTUAL, clsName, methodName, ATTACHABLE_WRAP_TYPE.getDescriptor());
		}

		if (isOcAttachable) {
			onConnect.visitVarInsn(Opcodes.ALOAD, 0);
			onConnect.visitFieldInsn(Opcodes.GETFIELD, clsName, TARGET_FIELD, targetType.getDescriptor());
			onConnect.visitVarInsn(Opcodes.ALOAD, 1);
			onConnect.visitMethodInsn(Opcodes.INVOKESTATIC, clsName, methodName, OC_ATTACHABLE_WRAP_TYPE.getDescriptor());
		}

		onConnect.visitInsn(Opcodes.RETURN);

		onConnect.visitMaxs(0, 0);
		onConnect.visitEnd();
	}

	private static String[] getInterfaces(Set<Class<?>> exposedInterfaces) {
		String[] result = new String[exposedInterfaces.size()];
		int i = 0;
		for (Class<?> cls : exposedInterfaces)
			result[i++] = Type.getInternalName(cls);

		return result;
	}

	private static Map<Method, Type> getExposedMethods(Collection<Class<?>> exposedInterfaces) {
		Map<Method, Type> result = Maps.newHashMap();

		for (Class<?> intf : exposedInterfaces) {
			Type intfType = Type.getType(intf);

			for (java.lang.reflect.Method m : intf.getMethods())
				result.put(Method.getMethod(m), intfType);
		}

		return result;
	}
}
