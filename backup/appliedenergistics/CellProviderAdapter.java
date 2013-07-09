package openperipheral.core.integration.appliedenergistics;

import java.util.ArrayList;

import dan200.computer.api.IComputerAccess;

import appeng.api.me.tiles.ICellProvider;
import appeng.api.me.util.IMEInventoryHandler;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.core.interfaces.IPeripheralMethodDefinition;
import openperipheral.core.util.ReflectionHelper;

public class CellProviderAdapter implements IPeripheralAdapter {

	private Class klazz = null;

	public CellProviderAdapter() {
		klazz = ReflectionHelper.getClass("appeng.api.me.tiles.ICellProvider");
	}
	
	@Override
	public Class getTargetClass() {
		return klazz;
	}
	
	@LuaMethod
	public int getTotalBytes(IComputerAccess computer, Object target) throws Exception {
		return (Integer)callOnCell(target, "totalBytes");
	}

	@LuaMethod
	public int getFreeBytes(IComputerAccess computer, Object target) throws Exception {
		return (Integer)callOnCell(target, "freeBytes");
	}

	@LuaMethod
	public int getUsedBytes(IComputerAccess computer, Object target) throws Exception {
		return (Integer)callOnCell(target, "usedBytes");
	}

	@LuaMethod
	public int getUnusedItemCount(IComputerAccess computer, Object target) throws Exception {
		return (Integer)callOnCell(target, "unusedItemCount");
	}

	@LuaMethod
	public boolean canHoldNewItem(IComputerAccess computer, Object target) throws Exception {
		return (Boolean)callOnCell(target, "canHoldNewItem");
	}

	@LuaMethod
	public boolean isPreformatted(IComputerAccess computer, Object target) throws Exception {
		return (Boolean)callOnCell(target, "isPreformatted");
	}

	@LuaMethod
	public boolean isFuzzyPreformatted(IComputerAccess computer, Object target) throws Exception {
		return (Boolean)callOnCell(target, "isFuzzyPreformatted");
	}

	@LuaMethod
	public String getName(IComputerAccess computer, Object target) throws Exception {
		return (String)callOnCell(target, "getName");
	}

	@LuaMethod
	public String getStoredItemCount(IComputerAccess computer, Object target) throws Exception {
		return (String)callOnCell(target, "storedItemCount");
	}

	@LuaMethod
	public String getStoredItemTypes(IComputerAccess computer, Object target) throws Exception {
		return (String)callOnCell(target, "storedItemTypes");
	}

	@LuaMethod
	public String getRemainingItemCount(IComputerAccess computer, Object target) throws Exception {
		return (String)callOnCell(target, "remainingItemCount");
	}

	@LuaMethod
	public String getRemainingItemTypes(IComputerAccess computer, Object target) throws Exception {
		return (String)callOnCell(target, "remainingItemTypes");
	}

	@LuaMethod
	public String getTotalItemTypes(IComputerAccess computer, Object target) throws Exception {
		return (String)callOnCell(target, "getTotalItemTypes");
	}

	@LuaMethod
	public Object callOnCell(Object tile, String name) throws Exception {
		IMEInventoryHandler handler = ((ICellProvider) tile).provideCell();
		return ReflectionHelper.callMethod(false, "", handler, new String[] { name });	
	}

}
