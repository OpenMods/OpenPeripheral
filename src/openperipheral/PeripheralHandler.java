package openperipheral;

import java.util.WeakHashMap;

import net.minecraft.tileentity.TileEntity;
import dan200.computer.api.IHostedPeripheral;
import dan200.computer.api.IPeripheralHandler;

public class PeripheralHandler implements IPeripheralHandler {

	WeakHashMap<TileEntity, IHostedPeripheral> peripherals = new WeakHashMap<TileEntity, IHostedPeripheral>();
	
	@Override
	public IHostedPeripheral getPeripheral(TileEntity tile) {

		if (!peripherals.containsKey(tile)) {
			peripherals.put(tile, new HostedPeripheral(tile));
		}
		
		return peripherals.get(tile);
	}

}
