package openperipheral.adapter.method;

import java.util.Collection;

import openmods.utils.TypeUtils;
import openperipheral.api.LuaArgType;
import dan200.computercraft.api.lua.ILuaObject;

public class LuaTypeQualifier {

	public static LuaArgType qualifyArgType(Class<?> cls) {
		cls = TypeUtils.toObjectType(cls);

		if (cls == String.class) return LuaArgType.STRING;
		if (cls == Boolean.class) return LuaArgType.BOOLEAN;
		if (cls == Void.class) return LuaArgType.VOID;
		if (Number.class.isAssignableFrom(cls)) return LuaArgType.NUMBER;
		if (Collection.class.isAssignableFrom(cls)) return LuaArgType.TABLE;
		if (cls.isArray()) return LuaArgType.TABLE;
		if (cls.isEnum()) return LuaArgType.STRING;
		if (ILuaObject.class.isAssignableFrom(cls)) return LuaArgType.OBJECT;

		throw new IllegalArgumentException(String.format("Can't categorize type '%s'", cls));
	}
}
