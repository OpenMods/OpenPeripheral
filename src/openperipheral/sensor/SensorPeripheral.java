package openperipheral.sensor;

import openperipheral.core.AdapterManager;
import openperipheral.core.peripheral.HostedPeripheral;
import openperipheral.core.util.MiscUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IHostedPeripheral;
import dan200.computer.api.ILuaContext;

public class SensorPeripheral extends HostedPeripheral implements IHostedPeripheral {

	public SensorPeripheral(Object target, World worldObj) {
		super(target, worldObj);
	}
	
	@Override
	public void initialize() {

		methods = AdapterManager.getMethodsForTarget(this);
		
		methodNames = new String[methods.size()];
		for (int i = 0; i < methods.size(); i++) {
			methodNames[i] = methods.get(i).getLuaName();
		}
		type = MiscUtils.getNameForTarget(target);
	}
	

}
