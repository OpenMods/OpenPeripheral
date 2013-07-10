package openperipheral.sensor;

import openperipheral.core.peripheral.HostedPeripheral;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IHostedPeripheral;
import dan200.computer.api.ILuaContext;

public class SensorPeripheral extends HostedPeripheral implements IHostedPeripheral {

	public SensorPeripheral(Object target, World worldObj) {
		super(target, worldObj);
	}

}
