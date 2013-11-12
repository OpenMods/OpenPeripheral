package openperipheral.core.adapter.sgcraft;

import java.lang.reflect.Field;

import net.minecraft.tileentity.TileEntity;
import dan200.computer.api.IComputerAccess;
import openperipheral.api.Arg;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import openperipheral.core.util.ReflectionHelper;

public class AdapterStargate implements IPeripheralAdapter {
	private final Class<?> STARGATE_TILE_CLASS = ReflectionHelper.getClass("gcewing.sg.SGBaseTE");
	private final Class<?> SG_ADDRESSING_CLASS = ReflectionHelper.getClass("gcewing.sg.SGAddressing");
	
	@Override
	public Class<?> getTargetClass() {
		return STARGATE_TILE_CLASS;
	}
	
	@LuaMethod(returnType = LuaType.VOID, description = "connects the stargate to the supplied address",
			args = {@Arg(type = LuaType.STRING, description = "the address of the gate to connect to")})
	public void connect(IComputerAccess computer, TileEntity tile, String targetAddress) throws Exception {
		// make sure the gate is built
		checkGateComplete(tile);
		
		targetAddress = targetAddress.toUpperCase();
		validateAddress(tile, targetAddress);
		
		String homeAddress = (String) ReflectionHelper.callMethod(getTargetClass(), tile, new String[]{"findHomeAddress"});
		TileEntity targetStargate = (TileEntity) ReflectionHelper.callMethod(false, SG_ADDRESSING_CLASS, null, new String[] {"findAddressedStargate"}, new Object[] {targetAddress});
		
		boolean targetBusy = (Boolean) ReflectionHelper.callMethod(getTargetClass(), targetStargate, new String[]{"isConnected"});
		if (targetBusy) throw new Exception("Stargate at address " + targetAddress + " is busy");
		
		int requiredFuel = (Integer) ReflectionHelper.getProperty(getTargetClass(), tile, new String[] {"fuelToOpen"});
		boolean fuelReloaded = (Boolean) ReflectionHelper.callMethod(getTargetClass(), tile, new String[] {"reloadFuel"}, new Object[]{requiredFuel});
		if (!fuelReloaded) throw new Exception("Stargate has insufficient fuel");
		
		String[] method = new String[] {"startDiallingStargate"};
		ReflectionHelper.callMethod(getTargetClass(), tile, method, new Object[] {targetAddress, targetStargate, true});
		ReflectionHelper.callMethod(getTargetClass(), targetStargate, method, new Object[]{homeAddress, tile, false});
	}
	
	@LuaMethod(returnType = LuaType.STRING, description = "gets state of the stargate", onTick = false)
	public String getState(IComputerAccess computer, TileEntity tile) throws Exception {
		// you know it
		checkGateComplete(tile);
		
		Field field = ReflectionHelper.getField(getTargetClass(), "state");
		return field.get(tile).toString();
	}
	
	@LuaMethod(returnType = LuaType.NUMBER, description = "gets the number of locked chevrons", onTick = false)
	public int getLockedChevronCount(IComputerAccess computer, TileEntity tile) throws Exception {
		checkGateComplete(tile);
		Field field = ReflectionHelper.getField(getTargetClass(), "numEngagedChevrons");
		return field.getInt(tile);
	}
	
	@LuaMethod(returnType = LuaType.NUMBER, description = "gets the amount of buffered fuel", onTick = false)
	public int getFuelLevel(IComputerAccess computer, TileEntity tile) throws Exception {
		checkGateComplete(tile);
		Field field = ReflectionHelper.getField(getTargetClass(), "fuelBuffer");
		return field.getInt(tile);
	}
	
	@LuaMethod(returnType = LuaType.NUMBER, description = "gets maximum amount of buffered fuel", onTick = false)
	public int getMaxFuelLevel(IComputerAccess computer, TileEntity tile) throws Exception {
		checkGateComplete(tile);
		Field field = ReflectionHelper.getField(getTargetClass(), "maxFuelBuffer");
		return field.getInt(tile);
	}
	
	@LuaMethod(returnType = LuaType.NUMBER, description = "is the stargate connected to a controller", onTick = false)
	public boolean isDHDConnected(IComputerAccess computer, TileEntity tile) throws Exception {
		checkGateComplete(tile);
		Field field = ReflectionHelper.getField(getTargetClass(), "isLinkedToController");
		return field.getBoolean(tile);
	}
	
	@LuaMethod(returnType = LuaType.VOID, description = "disconnects the stargate")
	public void disconnect(IComputerAccess computer, TileEntity tile) throws Exception {
		// make sure the gate is built
		checkGateComplete(tile);
		
		Object connectedLocation = ReflectionHelper.getProperty(getTargetClass(), tile, new String[] {"connectedLocation"});
		TileEntity connectedGate = (TileEntity) ReflectionHelper.callMethod(getTargetClass(), null, new String[] {"at"}, new Object[]{connectedLocation});
		String[] method = new String[] {"clearConnection"};
		if (connectedGate != null) ReflectionHelper.callMethod(getTargetClass(), connectedGate, method);
		ReflectionHelper.callMethod(getTargetClass(), tile, method);
	}
	
	@LuaMethod(returnType = LuaType.BOOLEAN, description = "whether or not the supplied address is a valid address",
			args = {@Arg(type = LuaType.STRING, description = "the address of the gate to validate")}, onTick = false)
	public boolean isValidAddress(IComputerAccess computer, TileEntity tile, String address) {
		try {
			validateAddress(tile, address.toUpperCase());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@LuaMethod(returnType = LuaType.BOOLEAN, description = "whether or not the stargate currently has a connection", onTick = false)
	public boolean isConnected(IComputerAccess computer, TileEntity tile) throws Exception {
		// make sure the gate is built
		checkGateComplete(tile);
		
		return (Boolean) ReflectionHelper.callMethod(getTargetClass(), tile, new String[]{"isConnected"});
	}
	
	@LuaMethod(returnType = LuaType.BOOLEAN, description = "whether or not the stargate created the connection", onTick = false)
	public boolean isInitiator(IComputerAccess computer, TileEntity tile) throws Exception {
		// make sure the gate is built
		checkGateComplete(tile);
		
		return (Boolean) ReflectionHelper.getProperty(getTargetClass(), tile, new String[]{"isInitiator"});
	}
	
	@LuaMethod(returnType = LuaType.BOOLEAN, description = "whether or not the connection is travelable from this side", onTick = false)
	public boolean canTravelFromThisEnd(IComputerAccess computer, TileEntity tile) throws Exception {
		// make sure the gate is built
		checkGateComplete(tile);
		
		return (Boolean) ReflectionHelper.callMethod(getTargetClass(), tile, new String[]{"canTravelFromThisEnd"});
	}
	
	@LuaMethod(returnType = LuaType.STRING, description = "the address of the stargate the connection is linked", onTick = false)
	public String getDialledAddress(IComputerAccess computer, TileEntity tile) throws Exception {
		// make sure the gate is built
		checkGateComplete(tile);
		
		return (String) ReflectionHelper.getProperty(getTargetClass(), tile, new String[]{"dialledAddress"});
	}
	
	@LuaMethod(returnType = LuaType.BOOLEAN, description = "whether or not the Stargate is completed", onTick = false)
	public boolean isCompleteGate(IComputerAccess computer, TileEntity tile) {
		try {
			checkGateComplete(tile);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private void checkGateComplete(TileEntity tile) throws Exception {
		boolean ringComplete = (Boolean) ReflectionHelper.getProperty(getTargetClass(), tile, new String[]{"isMerged"});
		if (!ringComplete) throw new Exception("Stargate damaged or incompelte");
	}
	
	private void validateAddress(TileEntity tile, String address) throws Exception {
		if (address.length() != 7) throw new Exception("Stargate addresses must be 7 letters");
		
		TileEntity targetStargate = (TileEntity) ReflectionHelper.callMethod(false, SG_ADDRESSING_CLASS, null, new String[] {"findAddressedStargate"}, new Object[] {address});
		
		if (targetStargate == null) throw new Exception("No Stargate at address " + address);
		if (targetStargate == tile) throw new Exception("Stargate cannot connect to itself");
	}
}
