package openperipheral.core.integration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import openperipheral.core.AdapterManager;
import openperipheral.core.adapter.mystcraft.AdapterWritingDesk;

import cpw.mods.fml.common.FMLLog;

public class ModuleMystcraft {

  public static void init() {
    AdapterManager.addPeripheralAdapter(new AdapterWritingDesk());

  }

  public static void appendMystcraftInfo(Map map, ItemStack stack) {
	  if (stack != null){
	    Item item = stack.getItem();
	    if (item != null){
	      
	      if ("item.myst.page".equals(item.getUnlocalizedName())){
	        addStringFromNBT(map, stack, "symbol", "symbol");
	      }else if ("item.myst.linkbook".equals(item.getUnlocalizedName())){
	        addStringFromNBT(map, stack, "destination", "agename");
	        addLinkingBookFlags(map, stack);
	      }
	    }
	  }
	}
	
  private static void addLinkingBookFlags(Map map, ItemStack stack) {
    Map<String,Boolean> flags = new HashMap<String,Boolean>();
    map.put("flags", flags);
    if (stack.hasTagCompound()) {
      NBTTagCompound tag = stack.getTagCompound();
      if (tag.hasKey("Flags")){
        for(NBTBase s:(Collection<NBTBase>)tag.getCompoundTag("Flags").getTags()){
          flags.put(s.getName(), Boolean.TRUE);
        }
        
      }
    }         
  }
  
  private static void addStringFromNBT(Map map, ItemStack stack, String outputName, String nbtTagName) {
    if (stack.hasTagCompound()) {
      NBTTagCompound tag = stack.getTagCompound();
      if (tag.hasKey(nbtTagName)) {
        map.put(outputName, tag.getString(nbtTagName));
      }
    }
  }
}
