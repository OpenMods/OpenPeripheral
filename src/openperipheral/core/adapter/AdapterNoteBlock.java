package openperipheral.core.adapter;

import net.minecraft.tileentity.TileEntityNote;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import dan200.computer.api.IComputerAccess;

public class AdapterNoteBlock implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return TileEntityNote.class;
	}

	@LuaMethod
	public void incrementPitch(IComputerAccess computer,
			TileEntityNote noteblock) {
		noteblock.changePitch();
	}

	@LuaMethod
	public void triggerNote(IComputerAccess computer, TileEntityNote noteblock) {
		noteblock.triggerNote(noteblock.worldObj, noteblock.xCoord,
				noteblock.yCoord, noteblock.zCoord);
	}

	@LuaMethod
	public void setNote(IComputerAccess computer, TileEntityNote noteblock,
			int note) {
		noteblock.note = (byte) (note % 25);
		noteblock.onInventoryChanged();
	}

	@LuaMethod
	public byte getNote(IComputerAccess computer, TileEntityNote noteblock) {
		return noteblock.note;
	}
}
