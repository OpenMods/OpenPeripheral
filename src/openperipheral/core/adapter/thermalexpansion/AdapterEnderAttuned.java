package openperipheral.core.adapter.thermalexpansion;

import cofh.api.transport.IEnderAttuned;
import dan200.computer.api.IComputerAccess;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.Arg;
import openperipheral.api.LuaType;

public class AdapterEnderAttuned implements IPeripheralAdapter{

	@Override
	public Class<?> getTargetClass() {
		return IEnderAttuned.class;
	}

	@LuaMethod(description = "Get the owner of the machine.", returnType = LuaType.STRING)
	public String getOwner(IComputerAccess computer, IEnderAttuned tileEntity) {
		return tileEntity.getOwnerString();
	}
	
	@LuaMethod(description = "Get the active frequency of the machine.", returnType = LuaType.NUMBER)
	public int getFrequency(IComputerAccess computer, IEnderAttuned tileEntity) {
		return tileEntity.getFrequency();
	}
	
	@LuaMethod(description = "set the active frequency of the machine.", returnType = LuaType.BOOLEAN,
			args={
				@Arg(name="frequency", type=LuaType.NUMBER, description="the frequency you want to set to.")
			})
	public boolean setFrequency(IComputerAccess computer, IEnderAttuned tileEntity, int frequency) {
		return tileEntity.setFrequency(frequency);
	}
	
	@LuaMethod(description="Can the machine output items via the ender net.", returnType = LuaType.BOOLEAN)
	public boolean canSendItems(IComputerAccess computer, IEnderAttuned tileEntity) {
		return tileEntity.canSendItems();
	}
	@LuaMethod(description="Can the machine output fluids via the ender net.", returnType = LuaType.BOOLEAN)
	public boolean canSendFluid(IComputerAccess computer, IEnderAttuned tileEntity) {
		return tileEntity.canSendFluid();
	}
	@LuaMethod(description="Can the machine output energy via the ender net.", returnType = LuaType.BOOLEAN)
	public boolean canSendEnergy(IComputerAccess computer, IEnderAttuned tileEntity) {
		return tileEntity.canSendEnergy();
	}
	
	@LuaMethod(description="Can the machine input items via the ender net.", returnType = LuaType.BOOLEAN)
	public boolean canReceiveItems(IComputerAccess computer, IEnderAttuned tileEntity) {
		return tileEntity.canReceiveItems();
	}
	
	@LuaMethod(description="Can the machine input fluids via the ender net.", returnType = LuaType.BOOLEAN)
	public boolean canReceiveFluid(IComputerAccess computer, IEnderAttuned tileEntity) {
		return tileEntity.canReceiveFluid();
	}
	
	@LuaMethod(description="Can the machine input energy via the ender net.", returnType = LuaType.BOOLEAN)
	public boolean canReceiveEnergy(IComputerAccess computer, IEnderAttuned tileEntity) {
		return tileEntity.canReceiveEnergy();
	}
}
