package openperipheral.core.integration.appliedenergistics;

import dan200.computer.api.IComputerAccess;
import net.minecraft.item.ItemStack;
import appeng.api.exceptions.AppEngTileMissingException;
import appeng.api.me.util.ICraftRequest;
import appeng.api.me.util.IGridInterface;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.core.util.ReflectionHelper;

public class GridInterfaceAdapter implements IPeripheralAdapter {

	private Class klazz = null;
	
	public GridInterfaceAdapter() {
		klazz = ReflectionHelper.getClass("appeng.api.me.util.IGridInterface");
	}

	@Override
	public Class getTargetClass() {
		return klazz;
	}
	
	public void requestCrafting(IComputerAccess computer, IGridInterface target, ItemStack stack) throws AppEngTileMissingException {
		ICraftRequest request = target.craftingRequest(stack);
	}

}
