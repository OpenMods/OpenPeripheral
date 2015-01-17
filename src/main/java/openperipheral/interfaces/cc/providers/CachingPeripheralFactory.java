package openperipheral.interfaces.cc.providers;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.tileentity.TileEntity;
import dan200.computercraft.api.peripheral.IPeripheral;

public abstract class CachingPeripheralFactory extends SafePeripheralFactory {
	private final Map<TileEntity, IPeripheral> created = new WeakHashMap<TileEntity, IPeripheral>();

	@Override
	public IPeripheral getPeripheral(TileEntity tile, int side) {
		if (tile == null) return null;

		IPeripheral peripheral = created.get(tile);

		if (peripheral == null) {
			peripheral = super.getPeripheral(tile, side);
			created.put(tile, peripheral);
		}

		return peripheral;
	}
}