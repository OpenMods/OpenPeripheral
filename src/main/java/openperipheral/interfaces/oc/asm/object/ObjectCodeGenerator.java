package openperipheral.interfaces.oc.asm.object;

import java.util.Map;
import java.util.Set;

import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.composed.IndexedMethodMap;
import openperipheral.interfaces.oc.asm.CommonMethodsBuilder;
import openperipheral.interfaces.oc.asm.ICodeGenerator;
import openperipheral.interfaces.oc.asm.Utils;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.Method;

public class ObjectCodeGenerator implements ICodeGenerator {

	private static final Type BASE_TYPE = Type.getType(ObjectEnvironmentBase.class);

	private static final Type SUPER_CTOR_TYPE = Type.getMethodType(Type.VOID_TYPE);

	@Override
	public byte[] generate(String clsName, Class<?> targetClass, Set<Class<?>> exposedInterfaces, IndexedMethodMap methods, int methodsId) {
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

		writer.visit(Opcodes.V1_6,
				Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SYNTHETIC | Opcodes.ACC_SUPER,
				clsName, null, BASE_TYPE.getInternalName(), Utils.getInterfaces(exposedInterfaces));

		Type targetType = Type.getType(targetClass);

		CommonMethodsBuilder builder = new CommonMethodsBuilder(writer, clsName, targetType);

		builder.addTargetField();
		builder.addMethodsField();

		builder.addClassInit(methodsId);
		createConstructors(writer, clsName, targetType);

		final Map<Method, Type> exposedMethods = Utils.getExposedMethods(exposedInterfaces);
		for (Map.Entry<Method, Type> e : exposedMethods.entrySet())
			builder.addExposedMethodBypass(e.getKey(), e.getValue());

		for (int i = 0; i < methods.size(); i++) {
			String name = methods.getMethodName(i);
			IMethodExecutor executor = methods.getMethod(i);
			builder.createScriptMethodWrapper(name, i, executor);
		}

		writer.visitEnd();

		return writer.toByteArray();
	}

	private static void createConstructors(ClassWriter writer, String clsName, Type targetType) {
		createDefaultConstructor(writer, clsName, targetType);
		createDummyConstructor(writer, clsName, targetType);
	}

	private static void visitSuperCtor(MethodVisitor init) {
		init.visitVarInsn(Opcodes.ALOAD, 0);
		init.visitInsn(Opcodes.DUP);
		init.visitMethodInsn(Opcodes.INVOKESPECIAL, BASE_TYPE.getInternalName(), "<init>", SUPER_CTOR_TYPE.getDescriptor(), false);
	}

	private static void createDefaultConstructor(ClassWriter writer, String clsName, Type targetType) {
		final Type ctorType = Type.getMethodType(Type.VOID_TYPE, targetType);

		MethodVisitor init = writer.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC, "<init>", ctorType.getDescriptor(), null, null);
		init.visitCode();

		visitSuperCtor(init);

		init.visitVarInsn(Opcodes.ALOAD, 1);
		init.visitFieldInsn(Opcodes.PUTFIELD, clsName, CommonMethodsBuilder.TARGET_FIELD_NAME, targetType.getDescriptor());
		init.visitInsn(Opcodes.RETURN);

		init.visitMaxs(0, 0);

		init.visitEnd();
	}

	private static void createDummyConstructor(ClassWriter writer, String clsName, Type targetType) {
		final Type ctorType = Type.getMethodType(Type.VOID_TYPE);

		MethodVisitor init = writer.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC, "<init>", ctorType.getDescriptor(), null, null);
		init.visitCode();

		visitSuperCtor(init);

		init.visitInsn(Opcodes.ACONST_NULL);
		init.visitFieldInsn(Opcodes.PUTFIELD, clsName, CommonMethodsBuilder.TARGET_FIELD_NAME, targetType.getDescriptor());
		init.visitInsn(Opcodes.RETURN);

		init.visitMaxs(0, 0);

		init.visitEnd();
	}
}
