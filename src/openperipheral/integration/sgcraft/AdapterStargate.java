package openperipheral.integration.sgcraft;

import java.lang.reflect.Field;

import net.minecraft.tileentity.TileEntity;
import openmods.utils.ReflectionHelper;
import openperipheral.api.*;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

import dan200.computer.api.IComputerAccess;

public class AdapterStargate implements IPeripheralAdapter {
	private static final Class<?> STARGATE_TILE_CLASS = ReflectionHelper.getClass("gcewing.sg.SGBaseTE");
	private static final Class<?> SG_ADDRESSING_CLASS = ReflectionHelper.getClass("gcewing.sg.SGAddressing");

	@Override
	public Class<?> getTargetClass() {
		return STARGATE_TILE_CLASS;
	}

	@OnTick
	@LuaCallable(description = "Connects the stargate to the supplied address")
	public void connect(IComputerAccess computer, TileEntity tile,
			@Arg(name = "targetAddress", type = LuaType.STRING, description = "the address of the gate to connect to") String targetAddress) {
		// make sure the gate is built
		checkGateComplete(tile);

		targetAddress = targetAddress.toUpperCase();
		validateAddress(tile, targetAddress);

		String homeAddress = ReflectionHelper.<String> call(tile, "findHomeAddress");
		TileEntity targetStargate = ReflectionHelper.<TileEntity> callStatic(SG_ADDRESSING_CLASS, "findAddressedStargate", targetAddress);

		boolean targetBusy = ReflectionHelper.<Boolean> call(targetStargate, "isConnected");
		Preconditions.checkState(!targetBusy, "Stargate at address %s is busy", targetAddress);

		int requiredFuel = (Integer)ReflectionHelper.getProperty(STARGATE_TILE_CLASS, tile, new String[] { "fuelToOpen" });
		boolean fuelReloaded = ReflectionHelper.<Boolean> call(tile, "reloadFuel", ReflectionHelper.primitive(requiredFuel));
		Preconditions.checkState(fuelReloaded, "Stargate has insufficient fuel");

		ReflectionHelper.call(tile, "startDiallingStargate", targetAddress, targetStargate, ReflectionHelper.primitive(true));
		ReflectionHelper.call(targetStargate, "startDiallingStargate", homeAddress, tile, ReflectionHelper.primitive(false));
	}

	@LuaMethod(returnType = LuaType.STRING, description = "Gets state of the stargate")
	public String getState(IComputerAccess computer, TileEntity tile) {
		checkGateComplete(tile);

		Field field = ReflectionHelper.getField(STARGATE_TILE_CLASS, "state");
		try {
			return field.get(tile).toString();
		} catch (Throwable t) {
			throw Throwables.propagate(t);
		}
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Gets the number of locked chevrons")
	public int getLockedChevronCount(IComputerAccess computer, TileEntity tile) {
		checkGateComplete(tile);
		Field field = ReflectionHelper.getField(STARGATE_TILE_CLASS, "numEngagedChevrons");
		try {
			return field.getInt(tile);
		} catch (Throwable t) {
			throw Throwables.propagate(t);
		}
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Gets the amount of buffered fuel")
	public int getFuelLevel(IComputerAccess computer, TileEntity tile) {
		checkGateComplete(tile);
		Field field = ReflectionHelper.getField(STARGATE_TILE_CLASS, "fuelBuffer");
		try {
			return field.getInt(tile);
		} catch (Throwable t) {
			throw Throwables.propagate(t);
		}
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Gets maximum amount of buffered fuel")
	public int getMaxFuelLevel(IComputerAccess computer, TileEntity tile) {
		checkGateComplete(tile);
		Field field = ReflectionHelper.getField(STARGATE_TILE_CLASS, "maxFuelBuffer");
		try {
			return field.getInt(tile);
		} catch (Throwable t) {
			throw Throwables.propagate(t);
		}
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Is the stargate connected to a controller")
	public boolean isDHDConnected(IComputerAccess computer, TileEntity tile) {
		checkGateComplete(tile);
		Field field = ReflectionHelper.getField(STARGATE_TILE_CLASS, "isLinkedToController");
		try {
			return field.getBoolean(tile);
		} catch (Throwable t) {
			throw Throwables.propagate(t);
		}
	}

	@LuaMethod(returnType = LuaType.VOID, description = "Disconnects the stargate")
	public void disconnect(IComputerAccess computer, TileEntity tile) {
		checkGateComplete(tile);

		Object connectedLocation = ReflectionHelper.getProperty(STARGATE_TILE_CLASS, tile, new String[] { "connectedLocation" });
		TileEntity connectedGate = (TileEntity)ReflectionHelper.call(tile, "at", connectedLocation);
		if (connectedGate != null) ReflectionHelper.call(connectedGate, "clearConnection");
		ReflectionHelper.call(tile, "clearConnection");
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "Whether or not the supplied address is a valid address",
			args = { @Arg(type = LuaType.STRING, description = "the address of the gate to validate") })
	public boolean isValidAddress(IComputerAccess computer, TileEntity tile, String address) {
		try {
			validateAddress(tile, address.toUpperCase());
			return true;
		} catch (Throwable t) {
			return false;
		}
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "Whether or not the stargate currently has a connection")
	public boolean isConnected(IComputerAccess computer, TileEntity tile) {
		checkGateComplete(tile);
		return (Boolean)ReflectionHelper.call(tile, "isConnected");
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "Whether or not the stargate created the connection")
	public boolean isInitiator(IComputerAccess computer, TileEntity tile) {
		checkGateComplete(tile);
		return (Boolean)ReflectionHelper.getProperty(STARGATE_TILE_CLASS, tile, new String[] { "isInitiator" });
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "Whether or not the connection is travelable from this side")
	public boolean canTravelFromThisEnd(IComputerAccess computer, TileEntity tile) {
		checkGateComplete(tile);
		return (Boolean)ReflectionHelper.call(tile, "canTravelFromThisEnd");
	}

	@LuaMethod(returnType = LuaType.STRING, description = "The address of the stargate the connection is linked")
	public String getDialledAddress(IComputerAccess computer, TileEntity tile) {
		checkGateComplete(tile);
		return (String)ReflectionHelper.getProperty(STARGATE_TILE_CLASS, tile, new String[] { "dialledAddress" });
	}

	@LuaMethod(returnType = LuaType.STRING, description = "The address of the this stargate")
	public String getHomeAddress(IComputerAccess computer, TileEntity tile) {
		checkGateComplete(tile);
		return (String)ReflectionHelper.call(tile, "findHomeAddress");
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "Whether or not the Stargate is completed")
	public boolean isCompleteGate(IComputerAccess computer, TileEntity tile) {
		try {
			checkGateComplete(tile);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private static void checkGateComplete(TileEntity tile) {
		boolean ringComplete = (Boolean)ReflectionHelper.getProperty(STARGATE_TILE_CLASS, tile, new String[] { "isMerged" });
		Preconditions.checkState(ringComplete, "Stargate damaged or incomplete");
	}

	private static void validateAddress(TileEntity tile, String address) {
		Preconditions.checkArgument(address.length() == 7, "Stargate addresses must be 7 letters");
		TileEntity targetStargate = (TileEntity)ReflectionHelper.callStatic(SG_ADDRESSING_CLASS, "findAddressedStargate", address);
		Preconditions.checkNotNull(targetStargate, "No Stargate at address %s", address);
		Preconditions.checkArgument(targetStargate != tile, "Stargate cannot connect to itself");
	}
}
