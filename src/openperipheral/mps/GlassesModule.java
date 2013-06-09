package openperipheral.mps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import openperipheral.OpenPeripheral;
import openperipheral.common.tileentity.TileEntityGlassesBridge;
import openperipheral.common.util.ReflectionHelper;

import net.machinemuse.api.IModularItem;
import net.machinemuse.api.IPowerModule;
import net.machinemuse.api.IPropertyModifier;
import net.machinemuse.api.moduletrigger.IPlayerTickModule;
import net.machinemuse.api.moduletrigger.IRightClickModule;
import net.machinemuse.api.moduletrigger.IToggleableModule;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class GlassesModule implements IPowerModule
{
	private IModularItem helm;
	public GlassesModule() {
		helm = (IModularItem) ReflectionHelper.getProperty("net.machinemuse.powersuits.common.ModularPowersuits", null, new String[] { "powerArmorHead"});
	}

	private Icon itemIcon;
	
	@Override
	public List<ItemStack> getInstallCost() {
		List<ItemStack> stacks = new ArrayList<ItemStack>();
		stacks.add(new ItemStack(OpenPeripheral.Items.glasses));
		return stacks;
	}

	@Override
	public Icon getIcon(ItemStack item) {
		return itemIcon;
	}

	@Override
	public String getStitchedTexture(ItemStack item) {
	    return "/gui/items.png";
	}

	@Override
	public void registerIcon(IconRegister registry) {
		itemIcon = registry.registerIcon("openperipheral:glasses");
	}

	@Override
	public String getCategory() {
		return "Vision";
	}

	@Override
	public boolean isValidForItem(ItemStack stack, EntityPlayer player) {
		Item item = stack.getItem();
        return item instanceof IModularItem && item == helm;
	}

	@Override
	public String getName() {
		return "OpenPeripheral Terminal Module";
	}

	@Override
	public double applyPropertyModifiers(NBTTagCompound itemTag,
			String propertyName, double propertyValue) {
		return 0;
	}

	@Override
	public NBTTagCompound getNewTag() {
		return new NBTTagCompound();
	}

	@Override
	public String getDescription() {
		return "Lets you write to your HUD via an OpenPeripheral Bridge and a ComputerCraft computer";
	}

	@Override
	public Map<String, List<IPropertyModifier>> getPropertyModifiers() {
		return new HashMap<String, List<IPropertyModifier>>();
	}

	@Override
	public boolean isAllowed() {
		return true;
	}

}
