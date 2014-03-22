package openperipheral.api;

import net.minecraft.world.World;
import dan200.computercraft.api.peripheral.IPeripheral;

public interface ISelfPeripheralProvider {
	public IPeripheral providePeripheral(World worldObj);
}
