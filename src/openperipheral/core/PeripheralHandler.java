package openperipheral.core;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import openperipheral.core.interfaces.IPeripheralProvider;
import openperipheral.core.peripheral.HostedPeripheral;
import dan200.computer.api.IHostedPeripheral;
import dan200.computer.api.IPeripheral;
import dan200.computer.api.IPeripheralHandler;

public class PeripheralHandler implements IPeripheralHandler {

	WeakHashMap<Object, IHostedPeripheral> peripherals = new WeakHashMap<Object, IHostedPeripheral>();

	public void invalidate(TileEntity tile) {
		peripherals.remove(tile);
	}

	public void invalidate(IPeripheral peripheral) {
		Iterator<Entry<Object, IHostedPeripheral>> iterator = peripherals.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Object, IHostedPeripheral> next = iterator.next();
			if (next.getValue() == peripheral) {
				iterator.remove();
				return;
			}
		}
	}

	@Override
	public IHostedPeripheral getPeripheral(TileEntity tile) {

		if (tile instanceof TileEntityCommandBlock) { return null; }

		if (tile instanceof IPeripheral) { return null; }

		if (tile == null) { return null; }

		if (tile instanceof IPeripheralProvider) { return ((IPeripheralProvider)tile).providePeripheral(tile.worldObj); }
		
		if (!peripherals.containsKey(tile) || tile.isInvalid()) {
			peripherals.put(tile, new HostedPeripheral(tile, tile.worldObj));
		}

		if (peripherals.get(tile).getMethodNames().length == 1) { return null; }

		return peripherals.get(tile);
	}

}
