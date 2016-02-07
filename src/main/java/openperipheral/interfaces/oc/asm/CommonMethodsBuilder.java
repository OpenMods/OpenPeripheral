package openperipheral.interfaces.oc.asm;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.util.DocUtils;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.Method;

import com.google.common.base.Optional;

public class CommonMethodsBuilder {

	public static final String TARGET_FIELD_NAME = "target";

	public static final String METHODS_FIELD_NAME = "methods";

	private static final Type OBJECT_TYPE = Type.getType(Object.class);

	private static final Type STRING_TYPE = Type.getType(String.class);

	public static final Type EXECUTOR_TYPE = Type.getType(IMethodExecutor.class);

	public static final Type EXECUTORS_TYPE = Type.getType(IMethodExecutor[].class);

	private static final Type OBJECTS_TYPE = Type.getType(Object[].class);

	private static final Type CONTEXT_TYPE = Type.getType(Context.class);

	private static final Type ARGUMENTS_TYPE = Type.getType(Arguments.class);

	private static final Type CALLBACK_TYPE = Type.getType(Callback.class);

	private static final Type METHOD_STORE_TYPE = Type.getType(MethodsStore.class);

	private static final Type BASE_TYPE = Type.getType(ICallerBase.class);

	private static final Type SIGNALLING_BASE_TYPE = Type.getType(ISignallingCallerBase.class);

	private static final Type METHOD_STORE_COLLECT_TYPE = Type.getMethodType(EXECUTORS_TYPE, Type.INT_TYPE);

	public static final Type WRAP_TYPE = Type.getMethodType(OBJECTS_TYPE, CONTEXT_TYPE, ARGUMENTS_TYPE);

	public static final Type CALLER_METHOD_TYPE = Type.getMethodType(OBJECTS_TYPE, OBJECT_TYPE, EXECUTOR_TYPE, CONTEXT_TYPE, ARGUMENTS_TYPE);

	public static final Type SIGNALLING_CALLER_METHOD_TYPE = Type.getMethodType(OBJECTS_TYPE, OBJECT_TYPE, EXECUTOR_TYPE, STRING_TYPE, CONTEXT_TYPE, ARGUMENTS_TYPE);

	private static final Type INVALID_STATE_TYPE = Type.getMethodType(OBJECTS_TYPE);

	private static final Type CLINIT_TYPE = Type.getMethodType(Type.VOID_TYPE);

	private final ClassWriter writer;

	private final String clsName;

	private final Type targetType;

	public CommonMethodsBuilder(ClassWriter writer, String clsName, Type targetType) {
		this.writer = writer;
		this.clsName = clsName;
		this.targetType = targetType;
	}

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

	public void addMethodsField() {
		writer.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, METHODS_FIELD_NAME, EXECUTORS_TYPE.getDescriptor(), null, null);
	}

	public void addTargetField() {
		writer.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, TARGET_FIELD_NAME, targetType.getDescriptor(), null, null);
	}

	public void createScriptMethodWrapper(String methodName, int methodIndex, IMethodExecutor executor) {

		final String generatedMethodName = methodName.replaceAll("[^A-Za-z0-9_]", "_") + "$" + Integer.toString(methodIndex);

		final MethodVisitor wrap = writer.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC, generatedMethodName, WRAP_TYPE.getDescriptor(), null, null);

		final Optional<String> returnSignal = executor.getReturnSignal();

		final AnnotationVisitor av = wrap.visitAnnotation(CALLBACK_TYPE.getDescriptor(), true);
		av.visit("value", methodName);
		// functions with return signal always return immediately
		av.visit("direct", executor.isAsynchronous() || returnSignal.isPresent());
		av.visit("doc", DocUtils.doc(executor.description()));
		av.visitEnd();
		// TODO: getter/setter

		av.visitEnd();

		wrap.visitCode();

		wrap.visitVarInsn(Opcodes.ALOAD, 0); // this
		wrap.visitInsn(Opcodes.DUP); // this, this
		wrap.visitFieldInsn(Opcodes.GETFIELD, clsName, TARGET_FIELD_NAME, targetType.getDescriptor()); // this, target

		Label skip = new Label();
		wrap.visitInsn(Opcodes.DUP); // this, target, target
		wrap.visitJumpInsn(Opcodes.IFNONNULL, skip); // this, target
		wrap.visitInsn(Opcodes.POP); // this
		wrap.visitMethodInsn(Opcodes.INVOKEINTERFACE, BASE_TYPE.getInternalName(), "invalidState", INVALID_STATE_TYPE.getDescriptor(), true); // result
		wrap.visitInsn(Opcodes.ARETURN);
		wrap.visitLabel(skip);

		wrap.visitFieldInsn(Opcodes.GETSTATIC, clsName, METHODS_FIELD_NAME, EXECUTORS_TYPE.getDescriptor()); // this, target, methods[]
		visitIntConst(wrap, methodIndex); // this, target, methods[], methodIndex
		wrap.visitInsn(Opcodes.AALOAD); // this, target, executor

		if (returnSignal.isPresent()) wrap.visitLdcInsn(returnSignal.get());
		wrap.visitVarInsn(Opcodes.ALOAD, 1); // this, target, executor, (returnSignal), context
		wrap.visitVarInsn(Opcodes.ALOAD, 2); // this, target, executor, (returnSignal), context, args

		if (returnSignal.isPresent()) {
			final String baseCallName = executor.isAsynchronous()? "callSignallingAsync" : "callSignallingSync";
			wrap.visitMethodInsn(Opcodes.INVOKEINTERFACE, SIGNALLING_BASE_TYPE.getInternalName(), baseCallName, SIGNALLING_CALLER_METHOD_TYPE.getDescriptor(), true);
		} else {
			wrap.visitMethodInsn(Opcodes.INVOKEINTERFACE, BASE_TYPE.getInternalName(), "call", CALLER_METHOD_TYPE.getDescriptor(), true);
		}
		wrap.visitInsn(Opcodes.ARETURN);

		wrap.visitMaxs(0, 0);

		wrap.visitEnd();
	}

	public void addExposedMethodBypass(Method method, Type sourceInterface) {
		MethodVisitor mv = writer.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC, method.getName(), method.getDescriptor(), null, null);

		mv.visitCode();

		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, clsName, TARGET_FIELD_NAME, targetType.getDescriptor());

		Type[] args = method.getArgumentTypes();
		for (int i = 0; i < args.length; i++)
			mv.visitVarInsn(args[i].getOpcode(Opcodes.ILOAD), i + 1);

		mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, sourceInterface.getInternalName(), method.getName(), method.getDescriptor(), true);
		Type returnType = method.getReturnType();
		mv.visitInsn(returnType.getOpcode(Opcodes.IRETURN));

		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	public void addClassInit(int methodsDropboxId) {
		MethodVisitor mv = writer.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC | Opcodes.ACC_STATIC, "<clinit>", CLINIT_TYPE.getDescriptor(), null, null);

		mv.visitCode();

		visitIntConst(mv, methodsDropboxId);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, METHOD_STORE_TYPE.getInternalName(), "collect", METHOD_STORE_COLLECT_TYPE.getDescriptor(), false);
		mv.visitFieldInsn(Opcodes.PUTSTATIC, clsName, METHODS_FIELD_NAME, EXECUTORS_TYPE.getDescriptor());
		mv.visitInsn(Opcodes.RETURN);

		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

}
