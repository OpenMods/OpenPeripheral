package openperipheral.interfaces.oc.asm.peripheral;

import java.util.Map;
import java.util.Set;

import li.cil.oc.api.network.Node;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.composed.IndexedMethodMap;
import openperipheral.api.architecture.IAttachable;
import openperipheral.api.architecture.oc.IOpenComputersAttachable;
import openperipheral.interfaces.oc.asm.CommonMethodsBuilder;
import openperipheral.interfaces.oc.asm.ICodeGenerator;
import openperipheral.interfaces.oc.asm.Utils;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.Method;

public class PeripheralCodeGenerator implements ICodeGenerator {

	private static final Type BASE_TYPE = Type.getType(PeripheralEnvironmentBase.class);

	private static final Type SIGNALLING_BASE_TYPE = Type.getType(TickablePeripheralEnvironmentBase.class);

	private static final Type ATTACHABLE_TYPE = Type.getType(IAttachable.class);

	private static final Type NODE_TYPE = Type.getType(Node.class);

	private static final Type OC_ATTACHABLE_TYPE = Type.getType(IOpenComputersAttachable.class);

	private static final Type OBJECT_TYPE = Type.getType(Object.class);

	private static final Type SUPER_CTOR_TYPE = Type.getMethodType(Type.VOID_TYPE, OBJECT_TYPE);

	private static final Type ATTACHABLE_WRAP_TYPE = Type.getMethodType(Type.VOID_TYPE, ATTACHABLE_TYPE, NODE_TYPE);

	private static final Type OC_ATTACHABLE_WRAP_TYPE = Type.getMethodType(Type.VOID_TYPE, OC_ATTACHABLE_TYPE, NODE_TYPE);

	private static final Type CONNECTIVITY_METHOD_TYPE = Type.getMethodType(Type.VOID_TYPE, NODE_TYPE);

	@Override
	public byte[] generate(String clsName, Class<?> targetClass, Set<Class<?>> exposedInterfaces, IndexedMethodMap methods, int methodsId) {
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

		Type baseType = getBaseClass(methods);

		writer.visit(Opcodes.V1_6,
				Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SYNTHETIC | Opcodes.ACC_SUPER,
				clsName, null, baseType.getInternalName(), Utils.getInterfaces(exposedInterfaces));

		Type targetType = Type.getType(targetClass);

		CommonMethodsBuilder builder = new CommonMethodsBuilder(writer, clsName, targetType);

		builder.addTargetField();
		builder.addMethodsField();

		builder.addClassInit(methodsId);
		createConstructor(writer, clsName, targetType, baseType);

		final Map<Method, Type> exposedMethods = Utils.getExposedMethods(exposedInterfaces);
		for (Map.Entry<Method, Type> e : exposedMethods.entrySet())
			builder.addExposedMethodBypass(e.getKey(), e.getValue());

		for (int i = 0; i < methods.size(); i++) {
			String name = methods.getMethodName(i);
			IMethodExecutor executor = methods.getMethod(i);
			builder.createScriptMethodWrapper(name, i, executor);
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

	private static Type getBaseClass(IndexedMethodMap methods) {
		for (IMethodExecutor e : methods.getMethods())
			if (e.getReturnSignal().isPresent()) return SIGNALLING_BASE_TYPE;

		return BASE_TYPE;
	}

	private static void createConstructor(ClassWriter writer, String clsName, Type targetType, Type baseType) {
		final Type ctorType = Type.getMethodType(Type.VOID_TYPE, targetType);

		MethodVisitor init = writer.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC, "<init>", ctorType.getDescriptor(), null, null);
		init.visitCode();
		init.visitVarInsn(Opcodes.ALOAD, 0);
		init.visitVarInsn(Opcodes.ALOAD, 1);
		init.visitInsn(Opcodes.DUP2);
		init.visitMethodInsn(Opcodes.INVOKESPECIAL, baseType.getInternalName(), "<init>", SUPER_CTOR_TYPE.getDescriptor(), false);
		init.visitFieldInsn(Opcodes.PUTFIELD, clsName, CommonMethodsBuilder.TARGET_FIELD_NAME, targetType.getDescriptor());
		init.visitInsn(Opcodes.RETURN);

		init.visitMaxs(0, 0);

		init.visitEnd();
	}

	protected void visitConnectivityMethod(String methodName, String clsName, ClassWriter writer, Type targetType, final boolean isAttachable, final boolean isOcAttachable) {
		MethodVisitor onConnect = writer.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC, methodName, CONNECTIVITY_METHOD_TYPE.getDescriptor(), null, null);

		onConnect.visitCode();

		if (isAttachable) {
			onConnect.visitVarInsn(Opcodes.ALOAD, 0);
			onConnect.visitInsn(Opcodes.DUP);
			onConnect.visitFieldInsn(Opcodes.GETFIELD, clsName, CommonMethodsBuilder.TARGET_FIELD_NAME, targetType.getDescriptor());
			onConnect.visitVarInsn(Opcodes.ALOAD, 1);
			onConnect.visitMethodInsn(Opcodes.INVOKEVIRTUAL, clsName, methodName, ATTACHABLE_WRAP_TYPE.getDescriptor(), false);
		}

		if (isOcAttachable) {
			onConnect.visitVarInsn(Opcodes.ALOAD, 0);
			onConnect.visitFieldInsn(Opcodes.GETFIELD, clsName, CommonMethodsBuilder.TARGET_FIELD_NAME, targetType.getDescriptor());
			onConnect.visitVarInsn(Opcodes.ALOAD, 1);
			onConnect.visitMethodInsn(Opcodes.INVOKESTATIC, clsName, methodName, OC_ATTACHABLE_WRAP_TYPE.getDescriptor(), false);
		}

		onConnect.visitInsn(Opcodes.RETURN);

		onConnect.visitMaxs(0, 0);
		onConnect.visitEnd();
	}
}
