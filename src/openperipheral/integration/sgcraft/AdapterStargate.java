package openperipheral.integration.sgcraft;

import java.lang.reflect.Field;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.*;
import openperipheral.util.ReflectionHelper;
import dan200.computer.api.IComputerAccess;

public class AdapterStargate implements IPeripheralAdapter {
	private final Class<?> STARGATE_TILE_CLASS = ReflectionHelper.getClass("gcewing.sg.SGBaseTE");
	private final Class<?> SG_ADDRESSING_CLASS = ReflectionHelper.getClass("gcewing.sg.SGAddressing");

	@Override
	public Class<?> getTargetClass() {
		return STARGATE_TILE_CLASS;
	}

	@OnTick
	@LuaCallable(returnTypes = LuaType.VOID, description = "connects the stargate to the supplied address")
	public void connect(IComputerAccess computer, TileEntity tile,
			@Arg(name = "targetAddress", type = LuaType.STRING, description = "the address of the gate to connect to") String targetAddress) throws Exception {
		// make sure the gate is built
		checkGateComplete(tile);

		targetAddress = targetAddress.toUpperCase();
		validateAddress(tile, targetAddress);

		String homeAddress = ReflectionHelper.<String> call(tile, "findHomeAddress");
		TileEntity targetStargate = ReflectionHelper.<TileEntity> callStatic(SG_ADDRESSING_CLASS, "findAddressedStargate", targetAddress);

		boolean targetBusy = ReflectionHelper.<Boolean> call(targetStargate, "isConnected");
		if (targetBusy) throw new Exception("Stargate at address " + targetAddress + " is busy");

		int requiredFuel = (Integer)ReflectionHelper.getProperty(getTargetClass(), tile, new String[] { "fuelToOpen" });
		boolean fuelReloaded = ReflectionHelper.<Boolean> call(tile, "reloadFuel", ReflectionHelper.primitive(requiredFuel));
		if (!fuelReloaded) throw new Exception("Stargate has insufficient fuel");

		ReflectionHelper.call(tile, "startDiallingStargate", targetAddress, targetStargate, ReflectionHelper.primitive(true));
		ReflectionHelper.call(targetStargate, "startDiallingStargate", homeAddress, tile, ReflectionHelper.primitive(false));
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

		Object connectedLocation = ReflectionHelper.getProperty(getTargetClass(), tile, new String[] { "connectedLocation" });
		TileEntity connectedGate = (TileEntity)ReflectionHelper.call(tile, "at", connectedLocation);
		if (connectedGate != null) ReflectionHelper.call(connectedGate, "clearConnection");
		ReflectionHelper.call(tile, "clearConnection");
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "whether or not the supplied address is a valid address",
			args = { @Arg(type = LuaType.STRING, description = "the address of the gate to validate") }, onTick = false)
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

		return (Boolean)ReflectionHelper.call(tile, "isConnected");
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "whether or not the stargate created the connection", onTick = false)
	public boolean isInitiator(IComputerAccess computer, TileEntity tile) throws Exception {
		// make sure the gate is built
		checkGateComplete(tile);

		return (Boolean)ReflectionHelper.getProperty(getTargetClass(), tile, new String[] { "isInitiator" });
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "whether or not the connection is travelable from this side", onTick = false)
	public boolean canTravelFromThisEnd(IComputerAccess computer, TileEntity tile) throws Exception {
		// make sure the gate is built
		checkGateComplete(tile);

		return (Boolean)ReflectionHelper.call(tile, "canTravelFromThisEnd");
	}

	@LuaMethod(returnType = LuaType.STRING, description = "the address of the stargate the connection is linked", onTick = false)
	public String getDialledAddress(IComputerAccess computer, TileEntity tile) throws Exception {
		// make sure the gate is built
		checkGateComplete(tile);

		return (String)ReflectionHelper.getProperty(getTargetClass(), tile, new String[] { "dialledAddress" });
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
		boolean ringComplete = (Boolean)ReflectionHelper.getProperty(getTargetClass(), tile, new String[] { "isMerged" });
		if (!ringComplete) throw new Exception("Stargate damaged or incomplete");
	}

	private void validateAddress(TileEntity tile, String address) throws Exception {
		if (address.length() != 7) throw new Exception("Stargate addresses must be 7 letters");

		TileEntity targetStargate = (TileEntity)ReflectionHelper.callStatic(SG_ADDRESSING_CLASS, "findAddressedStargate", address);

		if (targetStargate == null) throw new Exception("No Stargate at address " + address);
		if (targetStargate == tile) throw new Exception("Stargate cannot connect to itself");
	}
}
