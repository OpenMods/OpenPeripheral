package openperipheral.common.integration.sgcraft;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IClassDefinition;
import openperipheral.common.integration.sgcraft.method.SGCraftConnect;
import openperipheral.common.integration.sgcraft.method.SGCraftDisconnect;
import openperipheral.common.integration.sgcraft.method.SGCraftGetDialledAddress;
import openperipheral.common.integration.sgcraft.method.SGCraftIsConnected;
import openperipheral.common.integration.sgcraft.method.SGCraftIsInitiator;
import openperipheral.common.interfaces.IPeripheralMethodDefinition;
import openperipheral.common.util.ReflectionHelper;

public class DefinitionBaseSGTileClass implements IClassDefinition {

	private Class klazz = null;
	
	private ArrayList<IPeripheralMethodDefinition> methods = new ArrayList<IPeripheralMethodDefinition>();
	
	public DefinitionBaseSGTileClass() {
		klazz = ReflectionHelper.getClass("gcewing.sg.SGBaseTE");
		methods.add(new SGCraftConnect());
		methods.add(new SGCraftDisconnect());
		methods.add(new SGCraftGetDialledAddress());
		methods.add(new SGCraftIsConnected());
		methods.add(new SGCraftIsInitiator());
	}
	
	@Override
	public Class getJavaClass() {
		return klazz;
	}

	@Override
	public ArrayList<IPeripheralMethodDefinition> getMethods(TileEntity tile) {
		return methods;
	}

}
