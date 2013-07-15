package openperipheral.core.interfaces;

import net.minecraft.world.World;
import dan200.computer.api.IHostedPeripheral;

public interface IPeripheralProvider {
	public IHostedPeripheral providePeripheral(World worldObj);
}
