package openperipheral.core.integration.sgcraft;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IClassDefinition;
import openperipheral.core.interfaces.IPeripheralMethodDefinition;
import openperipheral.core.util.ReflectionHelper;

public class DefinitionBaseSGTileClass implements IClassDefinition {

	private Class klazz = null;
	
	private ArrayList<IPeripheralMethodDefinition> methods = new ArrayList<IPeripheralMethodDefinition>();
	
	public DefinitionBaseSGTileClass() {
		klazz = ReflectionHelper.getClass("gcewing.sg.SGBaseTE");
		methods.add(new MethodConnect());
		methods.add(new MethodDisconnect());
		methods.add(new MethodGetDialledAddress());
		methods.add(new MethodIsConnected());
		methods.add(new MethodIsInitiator());
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
