package openperipheral.core.integration.gregtech;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import openperipheral.core.interfaces.IPeripheralMethodDefinition;
import openperipheral.core.util.ReflectionHelper;

public class DefinitionTeleporter extends DefinitionMetaClass {

	private ArrayList<IPeripheralMethodDefinition> methods = new ArrayList<IPeripheralMethodDefinition>();
	
	public DefinitionTeleporter() {
		methods.add(new DefinitionMetaMethod("getX", new Class[] { }, new IGregTechMetaMethodCall() {
			@Override
			public Object execute(DefinitionMetaMethod method, Object metatile, Object[] args) {
				return ReflectionHelper.getProperty("", metatile, new String[] { "mTargetX" });
			}
		}));
		methods.add(new DefinitionMetaMethod("getY", new Class[] { }, new IGregTechMetaMethodCall() {
			@Override
			public Object execute(DefinitionMetaMethod method, Object metatile, Object[] args) {
				return ReflectionHelper.getProperty("", metatile, new String[] { "mTargetY" });
			}
		}));
		methods.add(new DefinitionMetaMethod("getZ", new Class[] { }, new IGregTechMetaMethodCall() {
			@Override
			public Object execute(DefinitionMetaMethod method, Object metatile, Object[] args) {
				return ReflectionHelper.getProperty("", metatile, new String[] { "mTargetZ" });
			}
		}));
		methods.add(new DefinitionMetaMethod("setX", new Class[] { int.class }, new IGregTechMetaMethodCall() {
			@Override
			public Object execute(DefinitionMetaMethod method, Object metatile, Object[] args) {
				ReflectionHelper.setProperty("", metatile, args[0], new String[] { "mTargetX" });
				return true;
			}
		}));
		methods.add(new DefinitionMetaMethod("setY", new Class[] { int.class }, new IGregTechMetaMethodCall() {
			@Override
			public Object execute(DefinitionMetaMethod method, Object metatile, Object[] args) {
				ReflectionHelper.setProperty("", metatile, args[0], new String[] { "mTargetY" });
				return true;
			}
		}));
		methods.add(new DefinitionMetaMethod("setZ", new Class[] { int.class }, new IGregTechMetaMethodCall() {
			@Override
			public Object execute(DefinitionMetaMethod method, Object metatile, Object[] args) {
				ReflectionHelper.setProperty("", metatile, args[0], new String[] { "mTargetZ" });
				return true;
			}
		}));
	}
	
	@Override
	public ArrayList<IPeripheralMethodDefinition> getMethods(TileEntity tile) {
		Object metaTileEntity = getMetaTileEntity(tile);
		if (metaTileEntity != null) {
			if (metaTileEntity.getClass().getName() == "gregtechmod.common.tileentities.GT_MetaTileEntity_Teleporter") {
				return methods;
			}
		}
		return new ArrayList<IPeripheralMethodDefinition>();
	}

}
