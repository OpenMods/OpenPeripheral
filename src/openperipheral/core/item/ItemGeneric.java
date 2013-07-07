package openperipheral.core.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import openperipheral.OpenPeripheral;
import openperipheral.core.ConfigSettings;
import openperipheral.core.interfaces.IMetaItem;
import openperipheral.core.item.meta.MetaGeneric;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemGeneric extends Item {

	private HashMap<Integer, IMetaItem> metaitems = new HashMap<Integer, IMetaItem>();
	
	public enum Metas {
		thinWire(),
		ribbonCable(),
		coiledWire(),
		electrode(),
		resistor(),
		silislime(),
		duckAntenna(),
		transistor(),
		lcdScreen(),
		pcb(),
		rawPlastic(),
		plasticSheet(),
		preparedPCB(),
		led(),
		capacitor(),
		optoisolator(),
		microcontroller(),
		carbon(),
		solarCell(),
		lightEnergyCell(),
		mediumEnergyCell(),
		heavyEnergyCell(),
		opticalLense(),
		focusLense(),
		lazerSight(),
		tier1targeting(),
		tier2targeting(),
		tier3targeting();
		
		Metas() {
			
		}
		
		public ItemStack newItemStack(int amount) {
			return OpenPeripheral.Items.generic.newItemStack(this, amount);
		}
		
		public ItemStack newItemStack() {
			return OpenPeripheral.Items.generic.newItemStack(this);
		}
		
	};

	public ItemGeneric() {
		super(ConfigSettings.genericItemId);
		setHasSubtypes(true);
		setMaxDamage(0);
		setMaxStackSize(64);
		setCreativeTab(OpenPeripheral.tabOpenPeripheral);
		metaitems.put(Metas.thinWire.ordinal(), new MetaGeneric("thinwire", new Object[] { 1, Metas.thinWire, Metas.ribbonCable }, new Object[] { 9, Metas.thinWire, Metas.coiledWire }));
		metaitems.put(Metas.ribbonCable.ordinal(), new MetaGeneric("ribboncable", 1, "www", "www", "www", 'w', Metas.thinWire));
		metaitems.put(Metas.coiledWire.ordinal(), new MetaGeneric("coiledwire", 1, Metas.thinWire));
		metaitems.put(Metas.electrode.ordinal(), new MetaGeneric("electrode", 1, "t", "i", "i", 't', new ItemStack(Block.torchRedstoneActive), 'i', new ItemStack(Item.ingotIron)));
		metaitems.put(Metas.resistor.ordinal(), new MetaGeneric("resistor", 1, "wcw", "w w", 'w', Metas.thinWire, 'c', Metas.carbon));
		metaitems.put(Metas.silislime.ordinal(), new MetaGeneric("silislimerubber", Item.slimeBall.itemID, 0, Metas.silislime, 0.5f));
		metaitems.put(Metas.duckAntenna.ordinal(), new MetaGeneric("duckantenna", 1, "ss", "sw", "sw", 's', Metas.silislime, 'w', Metas.coiledWire));
		metaitems.put(Metas.transistor.ordinal(), new MetaGeneric("transistor", 1, " c ", "www", 'c', Metas.carbon, 'w', Metas.thinWire));
		metaitems.put(Metas.lcdScreen.ordinal(), new MetaGeneric("lcdscreen", 1, "ggg", "eee", "wpt", 'g', new ItemStack(Block.thinGlass), 'e', Metas.electrode, 'w', Metas.ribbonCable, 'p', Metas.preparedPCB, 't', Metas.thinWire));
		metaitems.put(Metas.pcb.ordinal(), new MetaGeneric("pcb", 1, "rrr", "ppp", 'r', new ItemStack(Item.redstone), 'p', Metas.plasticSheet));
		metaitems.put(Metas.rawPlastic.ordinal(), new MetaGeneric("rawplastic", itemID, Metas.rawPlastic.ordinal(), Metas.rawPlastic, 0.5f));
		metaitems.put(Metas.plasticSheet.ordinal(), new MetaGeneric("plasticsheets", 4, "rr", "rr", 'r', Metas.rawPlastic));
		metaitems.put(Metas.preparedPCB.ordinal(), new MetaGeneric("preparedboard", 1, Metas.pcb, Metas.resistor, Metas.transistor, Metas.led, Metas.capacitor, Metas.optoisolator, Metas.thinWire, Metas.microcontroller));
		metaitems.put(Metas.led.ordinal(), new MetaGeneric("led", 1, " g ", "w w", 'g', new ItemStack(Block.glass), 'w', Metas.thinWire));
		metaitems.put(Metas.capacitor.ordinal(), new MetaGeneric("capacitor", 1, "srs", "srs", "w w", 's', Metas.plasticSheet, 'r', Metas.silislime, 'w', Metas.thinWire));
		metaitems.put(Metas.optoisolator.ordinal(), new MetaGeneric("optoisolator", 1, "wcw", "l s", "wcw", 'w', Metas.thinWire, 'c', Metas.carbon, 'l', Metas.led, 's', Metas.solarCell));
		metaitems.put(Metas.microcontroller.ordinal(), new MetaGeneric("microcontroller", 1, "www", "cic", "www", 'i', new ItemStack(Item.comparator), 'c', Metas.carbon, 'w', Metas.thinWire));
		metaitems.put(Metas.carbon.ordinal(), new MetaGeneric("carbon", Item.coal.itemID, 0, Metas.carbon, 0.5f));
		metaitems.put(Metas.solarCell.ordinal(), new MetaGeneric("solarcell", 9, new ItemStack(Block.daylightSensor)));
		metaitems.put(Metas.lightEnergyCell.ordinal(), new MetaGeneric("lightenergycell", ConfigSettings.cellsPerRedstone, Metas.plasticSheet, new ItemStack(Item.redstone)));
		metaitems.put(Metas.mediumEnergyCell.ordinal(), new MetaGeneric("mediumenergycell", 1, Metas.plasticSheet, Metas.lightEnergyCell, new ItemStack(Item.lightStoneDust)));
		metaitems.put(Metas.heavyEnergyCell.ordinal(), new MetaGeneric("heavyenergycell", 1, Metas.plasticSheet, Metas.mediumEnergyCell, new ItemStack(Item.gunpowder)));
		metaitems.put(Metas.opticalLense.ordinal(), new MetaGeneric("opticallense", 1, "ppp", "pgp", "ppp", 'p', Metas.plasticSheet,  'g', new ItemStack(Block.thinGlass)));
		metaitems.put(Metas.focusLense.ordinal(), new MetaGeneric("focuslense", 1, "pmp", "o o", "pcp", 'p', Metas.plasticSheet, 'o', Metas.opticalLense, 'm', Metas.microcontroller, 'c', Metas.preparedPCB));
		metaitems.put(Metas.lazerSight.ordinal(), new MetaGeneric("lazersight", 1, "spp", "lrc", "ppp", 's', Metas.solarCell, 'p', Metas.plasticSheet, 'l', Metas.led, 'r', Metas.ribbonCable, 'c', Metas.preparedPCB));
		metaitems.put(Metas.tier1targeting.ordinal(), new MetaGeneric("tier1targeting", 1, "ppp", "pop", "pmp", 'p', Metas.plasticSheet, 'o', Metas.opticalLense, 'm', Metas.microcontroller));
		metaitems.put(Metas.tier2targeting.ordinal(), new MetaGeneric("tier2targeting", 1, Metas.tier1targeting, Metas.focusLense));
		metaitems.put(Metas.tier3targeting.ordinal(), new MetaGeneric("tier3targeting", 1, Metas.tier2targeting, Metas.lazerSight));
	}
	
	public void initRecipes() {
		for (IMetaItem item : metaitems.values()) {
			item.addRecipe();
		}
	}

	@Override
	public Icon getIconFromDamage(int i) {
		IMetaItem meta = getMeta(i);
		if (meta != null) {
			return meta.getIcon();
		}
		return null;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		IMetaItem meta = getMeta(stack.getItemDamage());
		if (meta != null) {
			return "item."+meta.getUnlocalizedName(stack);
		}
		return "";
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float par8, float par9, float par10) {
		IMetaItem meta = getMeta(itemStack.getItemDamage());
		if (meta != null) {
			return meta.onItemUse(itemStack, player, world, x, y, z, side, par8, par9, par10);
		}
		return true;
	}

	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		IMetaItem meta = getMeta(itemStack.getItemDamage());
		if (meta != null) {
			return meta.onItemRightClick(itemStack, player, world);
		}
		return itemStack;
	}

	@Override
	public void registerIcons(IconRegister register) {
		for (IMetaItem item : metaitems.values()) {
			item.registerIcons(register);
		}
	}

	@Override
	public boolean hitEntity(ItemStack itemStack, EntityLiving target, EntityLiving player) {
		IMetaItem meta = getMeta(itemStack.getItemDamage());
		if (meta != null) {
			return meta.hitEntity(itemStack, target, player);
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs tab, List subItems) {
		for (Entry<Integer, IMetaItem> entry : metaitems.entrySet()) {
			if (entry.getValue().displayInCreative()) {
				subItems.add(new ItemStack(id, 1, entry.getKey()));
			}
		}
	}

	public IMetaItem getMeta(int id) {
		return metaitems.get(id);
	}

	public IMetaItem getMeta(ItemStack itemStack) {
		return getMeta(itemStack.getItemDamage());
	}

	public ItemStack newItemStack(int id) {
		return newItemStack(id, 1);
	}

	public ItemStack newItemStack(int id, int number) {
		return new ItemStack(this, number, id);
	}

	public ItemStack newItemStack(IMetaItem meta, int size) {
	    for (Entry<Integer, IMetaItem> o: metaitems.entrySet()) {
	    	if (o.getValue().equals(meta)) {
	    		return newItemStack(o.getKey(), size);
	    	}
	    }
	    return null;
	}
	
	public ItemStack newItemStack(Metas metaenum, int number) {
		return new ItemStack(this, number, metaenum.ordinal());
	}
	
	public ItemStack newItemStack(Metas metaenum) {
		return new ItemStack(this, 1, metaenum.ordinal());
	}

	public boolean isA(ItemStack stack, Metas meta) {
		return getMeta(stack) == metaitems.get(meta.ordinal());
	}
	
}
