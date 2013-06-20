package openperipheral.common.integration.appliedenergistics.cellprovider;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IClassDefinition;
import openperipheral.api.IMethodDefinition;
import openperipheral.common.util.ReflectionHelper;

public class DefinitionCellProviderClass implements IClassDefinition {

	private Class klazz = null;
	private ArrayList<IMethodDefinition> methods = new ArrayList<IMethodDefinition>();
	
	public DefinitionCellProviderClass() {
		klazz = ReflectionHelper.getClass("appeng.api.me.tiles.ICellProvider");
		if (klazz != null) {
			addMethods();
		}
	}
	
	public void addMethods() {
		methods.add(new DefinitionCellProviderMethod("totalBytes", "getTotalBytes"));
		methods.add(new DefinitionCellProviderMethod("freeBytes", "getFreeBytes"));
		methods.add(new DefinitionCellProviderMethod("usedBytes", "getUsedBytes"));
		methods.add(new DefinitionCellProviderMethod("unusedItemCount", "getUnusedItemCount"));
		methods.add(new DefinitionCellProviderMethod("canHoldNewItem"));
		methods.add(new DefinitionCellProviderMethod("isPreformatted"));
		methods.add(new DefinitionCellProviderMethod("isFuzzyPreformatted"));
		methods.add(new DefinitionCellProviderMethod("getName"));
		methods.add(new DefinitionCellProviderMethod("storedItemTypes", "getStoredItemTypes"));
		methods.add(new DefinitionCellProviderMethod("storedItemCount", "getStoredItemCount"));
		methods.add(new DefinitionCellProviderMethod("remainingItemCount", "getRemainingItemCount"));
		methods.add(new DefinitionCellProviderMethod("remainingItemTypes", "getRemainingItemTypes"));
		methods.add(new DefinitionCellProviderMethod("getTotalItemTypes"));
	}
	
	@Override
	public Class getJavaClass() {
		return klazz;
	}

	@Override
	public ArrayList<IMethodDefinition> getMethods(TileEntity tile) {
		return methods;
	}

}
