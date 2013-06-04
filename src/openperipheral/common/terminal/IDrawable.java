package openperipheral.common.terminal;


import java.io.DataInputStream;
import java.io.DataOutputStream;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import dan200.computer.core.ILuaObject;

public interface IDrawable extends ILuaObject {

	public int getX();
	public int getY();
	public int getZIndex();
	public void setZIndex(int z);
	public void writeTo(DataOutputStream stream);
	public void readFrom(DataInputStream stream);
	public void draw(ItemStack stack, EntityPlayer player, ScaledResolution resolution, float partialTicks, boolean hasScreen, int mouseX, int mouseY);
}
