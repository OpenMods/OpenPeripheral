package openperipheral.api;

import net.minecraft.entity.player.EntityPlayer;

public interface IHasSimpleGui {
	public int getGuiValue(int index);

	public int[] getGuiValues();

	public void onClientButtonClicked(int button);

	public void onServerButtonClicked(EntityPlayer player, int button);

	public void setGuiValue(int i, int value);
}