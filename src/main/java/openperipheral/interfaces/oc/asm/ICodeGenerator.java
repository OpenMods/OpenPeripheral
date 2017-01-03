package openperipheral.interfaces.oc.asm;

import java.util.Set;
import openperipheral.adapter.composed.IndexedMethodMap;

public interface ICodeGenerator {
	public byte[] generate(String clsName, Class<?> targetClass, Set<Class<?>> exposedInterfaces, IndexedMethodMap methods, int methodsId);
}
