package openperipheral.common.integration.vanilla;

import java.util.ArrayList;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IClassDefinition;
import openperipheral.common.config.ConfigSettings;
import openperipheral.common.interfaces.IPeripheralMethodDefinition;

public class InventoryClassDefinition implements IClassDefinition {

	private ArrayList<IPeripheralMethodDefinition> methods = new ArrayList<IPeripheralMethodDefinition>();
	public InventoryClassDefinition() {
		if (ConfigSettings.enabledExtendedInventory) {
			methods.add(new InventoryMoveIntoMethod("pullIntoSlot", true));
			methods.add(new InventoryMoveMethod("pull", true));
			methods.add(new InventoryMoveIntoMethod("pushIntoSlot", false));
			methods.add(new InventoryMoveMethod("push", false));
			methods.add(new InventoryCondenseMethod());
			methods.add(new InventorySwapMethod());
			methods.add(new InventoryStackInSlotMethod());
		}
	}
	
	@Override
	public Class getJavaClass() {
		return IInventory.class;
	}

	@Override
	public ArrayList<IPeripheralMethodDefinition> getMethods(TileEntity tile) {
		return methods;
	}

}
