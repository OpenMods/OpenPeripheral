package openperipheral.core.adapter.appeng;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import openperipheral.core.util.CallWrapper;
import dan200.computer.api.IComputerAccess;

public class AdapterTileController implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		try {
			return Class.forName("appeng.me.tile.TileController");
		}catch (Exception e) {
		}
		return null;
	}
	
	@LuaMethod(description="Get the current job list", returnType=LuaType.TABLE)
	public List<ItemStack> getJobList(IComputerAccess computer, TileEntity controller) {
		return new CallWrapper<List<ItemStack>>().call(controller, "getJobList");
	}

}
