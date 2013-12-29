package openperipheral.core.integration;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import openperipheral.core.AdapterManager;
import openperipheral.core.adapter.buildcraft.AdapterMachine;
import openperipheral.core.adapter.buildcraft.AdapterPipe;
import openperipheral.core.adapter.buildcraft.AdapterPowerReceptor;
import buildcraft.api.transport.IPipeTile;

public class ModuleBuildCraft {

	public static void init() {
		AdapterManager.addPeripheralAdapter(new AdapterMachine());
		AdapterManager.addPeripheralAdapter(new AdapterPowerReceptor());
		AdapterManager.addPeripheralAdapter(new AdapterPipe());
	}

	public static int tryAcceptIntoPipe(TileEntity possiblePipe, ItemStack nextStack, ForgeDirection direction) {
		if (possiblePipe instanceof IPipeTile) {
			return ((IPipeTile)possiblePipe).injectItem(nextStack, true, direction.getOpposite());
		}
		return 0;
	}
}
