package openperipheral.interfaces.cc.providers;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.util.EnumFacing;

public interface IPeripheralFactory<T> {
	public IPeripheral getPeripheral(T obj, EnumFacing side);
}
