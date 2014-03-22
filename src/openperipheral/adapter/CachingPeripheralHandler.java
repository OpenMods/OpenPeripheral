package openperipheral.adapter;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.tileentity.TileEntity;
import dan200.computercraft.api.peripheral.IPeripheral;

public abstract class CachingPeripheralHandler extends SafePeripheralHandler {
	private final Map<TileEntity, IPeripheral> created = new WeakHashMap<TileEntity, IPeripheral>();

	@Override
	public IPeripheral getPeripheralFromTile(TileEntity tile) {
		if (tile == null) return null;

		IPeripheral peripheral = created.get(tile);

		if (peripheral == null) {
			peripheral = super.getPeripheralFromTile(tile);
			created.put(tile, peripheral);
		}

		return peripheral;
	}
}