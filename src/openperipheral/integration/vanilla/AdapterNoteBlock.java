package openperipheral.integration.vanilla;

import net.minecraft.tileentity.TileEntityNote;
import openperipheral.api.*;

import com.google.common.base.Objects;

public class AdapterNoteBlock implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return TileEntityNote.class;
	}

	@LuaMethod(returnType = LuaType.VOID, description = "Increment the pitch of the noteblock")
	public void incrementPitch(TileEntityNote noteblock) {
		noteblock.changePitch();
	}

	@LuaMethod(returnType = LuaType.VOID, description = "Play the current note on the noteblock")
	public void triggerNote(TileEntityNote noteblock) {
		noteblock.triggerNote(noteblock.worldObj, noteblock.xCoord, noteblock.yCoord, noteblock.zCoord);
	}

	@LuaMethod(returnType = LuaType.VOID, description = "Set the note on the noteblock",
			args = {
					@Arg(type = LuaType.NUMBER, name = "note", description = "The note you want. From 0 to 25")
			})
	public void setPitch(TileEntityNote noteblock, int note) {
		noteblock.note = (byte)(note % 25);
		noteblock.onInventoryChanged();
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Get the note currently set on this noteblock")
	public byte getNote(TileEntityNote noteblock) {
		return noteblock.note;
	}

	@LuaCallable(description = "Plays a minecraft sound")
	public void playSound(TileEntityNote noteblock,
			@Arg(type = LuaType.STRING, name = "sound", description = "The identifier for the sound") String name,
			@Arg(type = LuaType.NUMBER, name = "pitch", description = "The pitch from 0 to 1") float pitch,
			@Arg(type = LuaType.NUMBER, name = "volume", description = "The volume from 0 to 1") float volume,
			@Optionals @Arg(type = LuaType.NUMBER, name = "x", description = "X coordinate od sound (relative to block)") Double dx,
			@Arg(type = LuaType.NUMBER, name = "y", description = "Y coordinate of sound (relative to block)") Double dy,
			@Arg(type = LuaType.NUMBER, name = "z", description = "Z coordinate of sound (relative to block)") Double dz) {
		noteblock.worldObj.playSoundEffect(
				noteblock.xCoord + 0.5 + Objects.firstNonNull(dx, 0.0),
				noteblock.yCoord + 0.5 + Objects.firstNonNull(dy, 0.0),
				noteblock.zCoord + 0.5 + Objects.firstNonNull(dz, 0.0),
				name, volume, pitch);
	}
}
