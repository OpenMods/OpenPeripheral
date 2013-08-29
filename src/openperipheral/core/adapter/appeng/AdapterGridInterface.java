package openperipheral.core.adapter.appeng;

import net.minecraft.item.ItemStack;
import openperipheral.api.Arg;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import appeng.api.exceptions.AppEngTileMissingException;
import appeng.api.me.util.ICraftRequest;
import appeng.api.me.util.IGridInterface;
import dan200.computer.api.IComputerAccess;

public class AdapterGridInterface implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return IGridInterface.class;
	}

	@LuaMethod(
			description = "Request crafting of a specific item",
			returnType = LuaType.VOID,
			args = { @Arg(
					type = LuaType.TABLE,
					name = "stack",
					description = "A table representing the item stack") })
	public void requestCrafting(IComputerAccess computer, IGridInterface grid, ItemStack stack) throws AppEngTileMissingException {
		ICraftRequest request = grid.craftingRequest(stack);
	}

}
