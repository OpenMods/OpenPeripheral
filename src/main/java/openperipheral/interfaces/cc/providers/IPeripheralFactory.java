package openperipheral.interfaces.cc.providers;

import net.minecraft.util.EnumFacing;
import dan200.computercraft.api.peripheral.IPeripheral;

public interface IPeripheralFactory<T> {
	public IPeripheral getPeripheral(T obj, EnumFacing side);
}
