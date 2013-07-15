package openperipheral.core.adapter.vanilla;

import net.minecraft.tileentity.TileEntityNote;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import dan200.computer.api.IComputerAccess;
import openperipheral.api.Arg;

public class AdapterNoteBlock implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return TileEntityNote.class;
	}

	@LuaMethod(
		returnType=LuaType.VOID,
		description="Increment the pitch of the noteblock"
	)
	public void incrementPitch(IComputerAccess computer,
			TileEntityNote noteblock) {
		noteblock.changePitch();
	}

	@LuaMethod(
		returnType=LuaType.VOID,
		description="Play the current note on the noteblock"
	)
	public void triggerNote(IComputerAccess computer, TileEntityNote noteblock) {
		noteblock.triggerNote(noteblock.worldObj, noteblock.xCoord,
				noteblock.yCoord, noteblock.zCoord);
	}

	@LuaMethod(
		returnType=LuaType.VOID,
		description="Set the note on the noteblock",
		args = {
			@Arg(type=LuaType.NUMBER, name="note", description="The note you want. From 0 to 25")
		}
	)
	public void setPitch(IComputerAccess computer, TileEntityNote noteblock,
			int note) {
		noteblock.note = (byte) (note % 25);
		noteblock.onInventoryChanged();
	}

	@LuaMethod(
		returnType=LuaType.NUMBER,
		description="Get the note currently set on this noteblock"
	)
	public byte getNote(IComputerAccess computer, TileEntityNote noteblock) {
		return noteblock.note;
	}
}
