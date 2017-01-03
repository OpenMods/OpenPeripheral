package openperipheral.util;

import java.util.Map;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class NameUtils {

	private static Map<String, Class<? extends TileEntity>> teNameToClass;
	private static Map<Class<? extends TileEntity>, String> teClassToName;

	public static Map<Class<? extends TileEntity>, String> getClassToNameMap() {
		if (teClassToName == null) teClassToName = ReflectionHelper.getPrivateValue(TileEntity.class, null, "classToNameMap", "field_145853_j");
		return teClassToName;
	}

	public static Map<String, Class<? extends TileEntity>> getNameToClassMap() {
		if (teNameToClass == null) teNameToClass = ReflectionHelper.getPrivateValue(TileEntity.class, null, "nameToClassMap", "field_145855_i");
		return teNameToClass;
	}

	public static String grumize(Class<?> targetCls) {
		return targetCls.getName().replace('.', '\u2603');
	}

	public static String degrumize(String obfClsName) {
		return obfClsName.replace('\u2603', '.');
	}
}
