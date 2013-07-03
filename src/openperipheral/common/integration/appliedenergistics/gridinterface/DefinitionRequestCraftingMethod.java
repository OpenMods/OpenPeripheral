package openperipheral.common.integration.appliedenergistics.gridinterface;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.item.ItemStack;
import openperipheral.api.IRestriction;
import openperipheral.common.interfaces.IPeripheralMethodDefinition;
import appeng.api.me.util.ICraftRequest;
import appeng.api.me.util.IGridInterface;

public class DefinitionRequestCraftingMethod implements IPeripheralMethodDefinition {

	@Override
	public HashMap<Integer, String> getReplacements() {
		return null;
	}

	@Override
	public String getPostScript() {
		return null;
	}

	@Override
	public boolean getCauseTileUpdate() {
		return false;
	}

	@Override
	public Class[] getRequiredParameters() {
		return new Class[] { ItemStack.class };
	}

	@Override
	public boolean isInstant() {
		return false;
	}

	@Override
	public String getLuaName() {
		return "requestCrafting";
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public boolean needsSanitize() {
		return true;
	}

	@Override
	public ArrayList<IRestriction> getRestrictions(int index) {
		return null;
	}

	@Override
	public Object execute(Object tile, Object[] args) throws Exception {
		if (tile instanceof IGridInterface) {
			ICraftRequest request = ((IGridInterface) tile).craftingRequest((ItemStack)args[0]);
		}
		return null;
	}
}
