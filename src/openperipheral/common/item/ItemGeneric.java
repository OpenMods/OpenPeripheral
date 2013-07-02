package openperipheral.common.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

import openperipheral.OpenPeripheral;
import openperipheral.common.config.ConfigSettings;
import openperipheral.common.interfaces.IMetaItem;
import openperipheral.common.item.meta.MetaCapacitor;
import openperipheral.common.item.meta.MetaCarbon;
import openperipheral.common.item.meta.MetaCoiledWire;
import openperipheral.common.item.meta.MetaDuckAntenna;
import openperipheral.common.item.meta.MetaElectrode;
import openperipheral.common.item.meta.MetaEnergyCell;
import openperipheral.common.item.meta.MetaLCDScreen;
import openperipheral.common.item.meta.MetaLED;
import openperipheral.common.item.meta.MetaMicroController;
import openperipheral.common.item.meta.MetaOptoIsolator;
import openperipheral.common.item.meta.MetaPCB;
import openperipheral.common.item.meta.MetaPlasticSheets;
import openperipheral.common.item.meta.MetaPreparedBoard;
import openperipheral.common.item.meta.MetaRawPlastic;
import openperipheral.common.item.meta.MetaResistor;
import openperipheral.common.item.meta.MetaRibbonCable;
import openperipheral.common.item.meta.MetaSilislimeRubber;
import openperipheral.common.item.meta.MetaSolarCell;
import openperipheral.common.item.meta.MetaThinWire;
import openperipheral.common.item.meta.MetaTransistor;

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
		energyCell();
		
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
		metaitems.put(Metas.thinWire.ordinal(), new MetaThinWire());
		metaitems.put(Metas.ribbonCable.ordinal(), new MetaRibbonCable());
		metaitems.put(Metas.coiledWire.ordinal(), new MetaCoiledWire());
		metaitems.put(Metas.electrode.ordinal(), new MetaElectrode());
		metaitems.put(Metas.resistor.ordinal(), new MetaResistor());
		metaitems.put(Metas.silislime.ordinal(), new MetaSilislimeRubber());
		metaitems.put(Metas.duckAntenna.ordinal(), new MetaDuckAntenna());
		metaitems.put(Metas.transistor.ordinal(), new MetaTransistor());
		metaitems.put(Metas.lcdScreen.ordinal(), new MetaLCDScreen());
		metaitems.put(Metas.pcb.ordinal(), new MetaPCB());
		metaitems.put(Metas.rawPlastic.ordinal(), new MetaRawPlastic());
		metaitems.put(Metas.plasticSheet.ordinal(), new MetaPlasticSheets());
		metaitems.put(Metas.preparedPCB.ordinal(), new MetaPreparedBoard());
		metaitems.put(Metas.led.ordinal(), new MetaLED());
		metaitems.put(Metas.capacitor.ordinal(), new MetaCapacitor());
		metaitems.put(Metas.optoisolator.ordinal(), new MetaOptoIsolator());
		metaitems.put(Metas.microcontroller.ordinal(), new MetaMicroController());
		metaitems.put(Metas.carbon.ordinal(), new MetaCarbon());
		metaitems.put(Metas.solarCell.ordinal(), new MetaSolarCell());
		metaitems.put(Metas.energyCell.ordinal(), new MetaEnergyCell());
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

	public Entry<Integer, IMetaItem> getMetaEntry(Class klass) {
		for (Entry<Integer, IMetaItem> entry : this.metaitems.entrySet()) {
			if (entry.getValue().getClass().equals(klass)) {
				return entry;
			}
		}
		return null;
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

	public ItemStack newItemStack(Metas metaenum, int number) {
		return new ItemStack(this, number, metaenum.ordinal());
	}
	
	public ItemStack newItemStack(Metas metaenum) {
		return new ItemStack(this, 1, metaenum.ordinal());
	}

	public boolean isA(ItemStack stack, Class klazz) {
		IMetaItem meta = getMeta(stack);
		if (meta == null || !klazz.isAssignableFrom(meta.getClass())) {
			return false;
		}
		return true;
	}
	
}
