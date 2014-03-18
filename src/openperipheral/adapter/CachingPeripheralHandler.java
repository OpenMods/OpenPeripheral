package openperipheral.adapter;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.tileentity.TileEntity;
import dan200.computer.api.IHostedPeripheral;

public abstract class CachingPeripheralHandler extends SafePeripheralHandler {
	private final Map<TileEntity, IHostedPeripheral> created = new WeakHashMap<TileEntity, IHostedPeripheral>();

	@Override
	public IHostedPeripheral getPeripheral(TileEntity tile) {
		if (tile == null) return null;

		IHostedPeripheral peripheral = created.get(tile);

		if (peripheral == null) {
			peripheral = super.getPeripheral(tile);
			created.put(tile, peripheral);
		}

		return peripheral;
	}
}