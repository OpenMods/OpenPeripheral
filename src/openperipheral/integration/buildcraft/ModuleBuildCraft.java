package openperipheral.integration.buildcraft;

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.ForgeDirection;
import openmods.Mods;
import openperipheral.adapter.AdapterManager;
import openperipheral.api.IIntegrationModule;
import buildcraft.api.transport.IPipeTile;

public class ModuleBuildCraft implements IIntegrationModule {

	@Override
	public String getModId() {
		return Mods.BUILDCRAFT;
	}

	@Override
	public void init() {
		AdapterManager.addPeripheralAdapter(new AdapterPowerReceptor());
		AdapterManager.addPeripheralAdapter(new AdapterPipe());
	}

	@Override
	public void appendEntityInfo(Map<String, Object> map, Entity entity, Vec3 relativePos) {}

	@Override
	public void appendItemInfo(Map<String, Object> map, ItemStack itemstack) {}

	public static int tryAcceptIntoPipe(TileEntity possiblePipe, ItemStack nextStack, ForgeDirection direction) {
		if (possiblePipe instanceof IPipeTile) { return ((IPipeTile)possiblePipe).injectItem(nextStack, true, direction.getOpposite()); }
		return 0;
	}
}
