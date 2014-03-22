package openperipheral.integration.appeng;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import openmods.utils.ReflectionHelper;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class AdapterTileController implements IPeripheralAdapter {
	private static final Class<?> CLAZZ = ReflectionHelper.getClass("appeng.me.tile.TileController");

	@Override
	public Class<?> getTargetClass() {
		return CLAZZ;
	}

	@LuaMethod(description = "Get the current job list", returnType = LuaType.TABLE)
	public List<ItemStack> getJobList(IComputerAccess computer, TileEntity controller) {
		return ReflectionHelper.call(controller, "getJobList");
	}

}
