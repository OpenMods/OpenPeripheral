package openperipheral.core;

import java.util.WeakHashMap;

import net.minecraft.tileentity.TileEntity;
import openperipheral.core.interfaces.IPeripheralProvider;
import openperipheral.core.peripheral.HostedPeripheral;
import dan200.computer.api.IHostedPeripheral;
import dan200.computer.api.IPeripheral;
import dan200.computer.api.IPeripheralHandler;

public class PeripheralHandler implements IPeripheralHandler {

	WeakHashMap<TileEntity, IHostedPeripheral> peripherals = new WeakHashMap<TileEntity, IHostedPeripheral>();

	@Override
	public IHostedPeripheral getPeripheral(TileEntity tile) {

		if (tile instanceof IPeripheral) {
			return null;
		}
		
		if (tile instanceof IPeripheralProvider) {
			return ((IPeripheralProvider) tile).providePeripheral();
		}

		if (!peripherals.containsKey(tile)) {
			peripherals.put(tile, new HostedPeripheral(tile));
		}

		return peripherals.get(tile);
	}

}
