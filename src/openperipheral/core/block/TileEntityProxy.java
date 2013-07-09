package openperipheral.core.block;

import net.minecraft.tileentity.TileEntity;
import openperipheral.OpenPeripheral;
import openperipheral.core.interfaces.IPeripheralProvider;
import openperipheral.core.peripheral.ProxyPeripheral;
import dan200.computer.api.IHostedPeripheral;
import dan200.computer.api.IPeripheral;

public class TileEntityProxy extends TileEntity implements IPeripheralProvider {

	private IHostedPeripheral proxy;
	private TileEntity target;

	public void setTarget(TileEntity newTarget, int backSide) {
		if (newTarget == target) {
			return;
		}
		if (newTarget == null) {
			target = null;
			proxy = null;
		} else {
			if (newTarget instanceof IPeripheral) {
				proxy = new ProxyPeripheral((IPeripheral)newTarget, backSide);
			}else {
				IHostedPeripheral hosted = OpenPeripheral.peripheralHandler.getPeripheral(newTarget);
				if (hosted != null) {
					proxy = new ProxyPeripheral(hosted, backSide);
				}
			}
		}
		target = newTarget;
	}

	public TileEntity getTarget() {
		return target;
	}

	@Override
	public IHostedPeripheral providePeripheral() {
		return proxy;
	}

}
