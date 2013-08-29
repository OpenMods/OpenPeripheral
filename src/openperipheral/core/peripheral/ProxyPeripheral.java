package openperipheral.core.peripheral;

import net.minecraft.nbt.NBTTagCompound;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IHostedPeripheral;
import dan200.computer.api.ILuaContext;
import dan200.computer.api.IPeripheral;

public class ProxyPeripheral implements IHostedPeripheral {

	private IPeripheral peripheral;
	private int backSide;

	public ProxyPeripheral(IPeripheral per, int backSide) {
		peripheral = per;
		this.backSide = backSide;
	}

	@Override
	public String getType() {
		if (peripheral == null) { return null; }
		return peripheral.getType();
	}

	@Override
	public String[] getMethodNames() {
		if (peripheral == null) { return null; }
		return peripheral.getMethodNames();
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
		if (peripheral == null) { return null; }
		return peripheral.callMethod(computer, context, method, arguments);
	}

	@Override
	public boolean canAttachToSide(int side) {
		return side == backSide;
	}

	@Override
	public void attach(IComputerAccess computer) {
		if (peripheral == null) { return; }
		peripheral.attach(computer);
	}

	@Override
	public void detach(IComputerAccess computer) {
		if (peripheral == null) { return; }
		peripheral.detach(computer);
	}

	@Override
	public void update() {
		if (peripheral != null && peripheral instanceof IHostedPeripheral) {
			((IHostedPeripheral)peripheral).update();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		if (peripheral != null && peripheral instanceof IHostedPeripheral) {
			((IHostedPeripheral)peripheral).readFromNBT(nbttagcompound);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		if (peripheral != null && peripheral instanceof IHostedPeripheral) {
			((IHostedPeripheral)peripheral).writeToNBT(nbttagcompound);
		}
	}

}
