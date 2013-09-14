package com.xcompwiz.mystcraft.api.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public abstract interface IItemRenameable
{
  public abstract String getDisplayName(EntityPlayer paramEntityPlayer, ItemStack paramItemStack);

  public abstract void setDisplayName(EntityPlayer paramEntityPlayer, ItemStack paramItemStack, String paramString);
}