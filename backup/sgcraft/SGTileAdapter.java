package openperipheral.core.integration.sgcraft;

import java.util.ArrayList;

import dan200.computer.api.IComputerAccess;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.core.interfaces.IPeripheralMethodDefinition;
import openperipheral.core.util.ReflectionHelper;
import openperipheral.core.util.ReflectionWrapper;

public class SGTileAdapter implements IPeripheralAdapter {

	private Class klazz = null;
	
	public SGTileAdapter() {
		klazz = ReflectionHelper.getClass("gcewing.sg.SGBaseTE");
	}
	
	@Override
	public Class getTargetClass() {
		return klazz;
	}

	@LuaMethod
	public boolean connect(IComputerAccess computer, TileEntity tile, String address) throws Exception {
		String homeAddress = (String) ReflectionHelper.callMethod("", tile, new String[] { "findHomeAddress" });
		Object targetStargate = ReflectionHelper.callMethod(false, "gcewing.sg.SGAddressing", null, new String[] { "findAddressedStargate" }, address);
		if (targetStargate == null) {
			throw new Exception("Unable to find target gate");
		}
		if (targetStargate == tile) {
			throw new Exception("You can not connect to yourself");
		}
		Object state = ReflectionHelper.getProperty("", targetStargate, "state");
		int requiredFuel = (Integer)ReflectionHelper.getProperty("", tile, "fuelToOpen");
		boolean reloaded = (Boolean) ReflectionHelper.callMethod("", tile, new String[] { "reloadFuel" }, requiredFuel);
		if (!reloaded) {
			throw new Exception("Not enough fuel");
		}
		ReflectionHelper.callMethod("", tile, new String[] { "startDiallingStargate" }, address, targetStargate, true);
		ReflectionHelper.callMethod("", targetStargate, new String[] { "startDiallingStargate" }, homeAddress, tile, false);
		return true;
	}

	@LuaMethod
	public boolean disconnect(IComputerAccess computer, TileEntity tile) throws Exception {
		ReflectionWrapper tileWrapped = new ReflectionWrapper(tile);
		Object connectedLocation = tileWrapped.get("connectedLocation").getRaw();
		Object localSGBaseTE = ReflectionHelper.callMethod("gcewing.sg.SGBaseTE", null, new String[] { "at" }, connectedLocation);
		if (localSGBaseTE != null) {
		   ReflectionHelper.callMethod("", localSGBaseTE, new String[] { "clearConnection" });
		}
		tileWrapped.call(new String[] { "clearConnection" });
		return true;
	}

	@LuaMethod
	public String getDialledAddress(IComputerAccess computer, TileEntity tile) {
		return (String) ReflectionHelper.getProperty("", tile, "dialledAddress" );
	}
	
	@LuaMethod
	public boolean isConnected(IComputerAccess computer, TileEntity tile) throws Exception {
		return (Boolean) ReflectionHelper.callMethod("", tile, new String[] { "isConnected" });
	}
	
	@LuaMethod
	public boolean isInitiator(IComputerAccess computer, TileEntity tile) throws Exception {
		return (Boolean) ReflectionHelper.callMethod("", tile, new String[] { "isInitiator" });
	}
	
	
	
}
