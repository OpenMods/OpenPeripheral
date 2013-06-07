package openperipheral.common.terminal;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import dan200.computer.core.ILuaObject;

public interface IDrawable extends ILuaObject {
	
	public int getX();

	public int getY();

	public int getZIndex();

	public int setZIndex(byte z);

	public void writeTo(DataOutputStream stream, Short changeMask);

	public void readFrom(DataInputStream stream, Short changeMask);

	public void draw(float partialTicks, int mouseX, int mouseY);
}
