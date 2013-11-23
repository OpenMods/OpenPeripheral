package openperipheral.adapter.vanilla;

import net.minecraft.tileentity.TileEntityNote;
import openperipheral.api.Arg;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import dan200.computer.api.IComputerAccess;

public class AdapterNoteBlock implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return TileEntityNote.class;
	}

	@LuaMethod(returnType = LuaType.VOID, description = "Increment the pitch of the noteblock")
	public void incrementPitch(IComputerAccess computer, TileEntityNote noteblock) {
		noteblock.changePitch();
	}

	@LuaMethod(returnType = LuaType.VOID, description = "Play the current note on the noteblock")
	public void triggerNote(IComputerAccess computer, TileEntityNote noteblock) {
		noteblock.triggerNote(noteblock.worldObj, noteblock.xCoord, noteblock.yCoord, noteblock.zCoord);
	}

	@LuaMethod(returnType = LuaType.VOID, description = "Set the note on the noteblock",
			args = {
			@Arg(type = LuaType.NUMBER, name = "note", description = "The note you want. From 0 to 25")
	})
	public void setPitch(IComputerAccess computer, TileEntityNote noteblock, int note) {
		noteblock.note = (byte)(note % 25);
		noteblock.onInventoryChanged();
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Get the note currently set on this noteblock")
	public byte getNote(IComputerAccess computer, TileEntityNote noteblock) {
		return noteblock.note;
	}

	@LuaMethod(returnType = LuaType.VOID, description = "Plays a minecraft sound",
			args = {
			@Arg(type = LuaType.STRING, name = "sound", description = "The identifier for the sound"),
			@Arg(type = LuaType.NUMBER, name = "pitch", description = "The pitch from 0 to 1"),
			@Arg(type = LuaType.NUMBER, name = "volume", description = "The volume from 0 to 1")
	})
	public void playSound(IComputerAccess computer, TileEntityNote noteblock, String name, float pitch, float volume) {
		noteblock.worldObj.playSoundEffect(noteblock.xCoord + 0.5, noteblock.yCoord + 0.5, noteblock.zCoord + 0.5, name, volume, pitch);
	}
}
