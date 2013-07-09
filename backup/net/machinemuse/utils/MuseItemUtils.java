package net.machinemuse.utils;

import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.machinemuse.api.IModularItem;
import net.machinemuse.api.IPowerModule;
import net.machinemuse.api.ModuleManager;
import net.machinemuse.api.moduletrigger.IRightClickModule;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.*;

public class MuseItemUtils {

    public static final String NBTPREFIX = "mmmpsmod";
    
    public static boolean itemHasActiveModule(ItemStack itemStack, String moduleName) {
    	return false;
    }
    
    public static NBTTagCompound getMuseItemTag(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        NBTTagCompound stackTag;
        if (stack.hasTagCompound()) {
            stackTag = stack.getTagCompound();
        } else {
            stackTag = new NBTTagCompound();
            stack.setTagCompound(stackTag);
        }

        NBTTagCompound properties;
        if (stackTag.hasKey(NBTPREFIX)) {
            properties = stackTag.getCompoundTag(NBTPREFIX);
        } else {
            properties = new NBTTagCompound();
            stackTag.setCompoundTag(NBTPREFIX, properties);
        }
        return properties;
    }

}
