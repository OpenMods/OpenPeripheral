package openperipheral.core.adapter;

import net.minecraft.item.ItemStack;
import dan200.computer.api.IComputerAccess;
import appeng.api.exceptions.AppEngTileMissingException;
import appeng.api.me.util.ICraftRequest;
import appeng.api.me.util.IGridInterface;
import openperipheral.api.IPeripheralAdapter;

public class AdapterGridInterface implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return IGridInterface.class;
	}
	
	public void requestCrafting(IComputerAccess computer, IGridInterface grid, ItemStack stack) throws AppEngTileMissingException {
		ICraftRequest request = grid.craftingRequest(stack);
	}

}
