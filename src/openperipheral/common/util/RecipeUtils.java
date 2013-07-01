package openperipheral.common.util;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openperipheral.OpenPeripheral;
import openperipheral.common.item.ItemGeneric;

public class RecipeUtils {

	public static String page1 = new StringBuilder().append("Thank you for choosing OpenPeripheral.\n\n")
			.append("OpenPeripheral turns the majority of blocks, from a variety of mods, into ComputerCraft peripherals.\n\n").toString();
	public static String page2 = new StringBuilder().append("To get started, try placing a computer to the right to a noteblock.\n\n")
			.append("Next, in the computer enter \"lua\" to access the lua console.\n\n")
			.append("The lua console allows you to type code that will be instantly executed.\n\n").toString();
	public static String page3 = new StringBuilder().append("Now that you're inside Lua, type:\n\n").append("nb = peripheral.wrap(\"left\")\n\n")
			.append("This will allow you to call functions on the noteblock.\n\n").toString();
	public static String page4 = new StringBuilder().append("To see what functions are available, type:\n\n").append("nb.listMethods()\n\n")
			.append("As you can see, there's the method you just called, but also 'changePitch' and 'triggerNote'.\n\n").toString();
	public static String page5 = new StringBuilder().append("Try calling:\n\n").append("nb.triggerNote()\n\n").append("Did you hear it play a note? Cool huh?\n\n")
			.toString();
	public static String page6 = new StringBuilder().append(
			"Now try some other blocks. It's not just vanilla blocks that work, a whole range of blocks from a wide variety of mods are supported!").toString();
	public static String page7 = new StringBuilder()
			.append("Peripheral Glasses\n")
			.append("-----------------\n\n")
			.append("Peripheral Glasses are a powerful new feature that let you publish information to the players HUD, and send commands via chat back to your computer. See the ComputerCraft forums for more information.")
			.toString();
	public static String page8 = new StringBuilder()
			.append("Peripheral Proxy\n")
			.append("-----------------\n\n")
			.append("You can't connect wired modems to non-solid blocks, so stick down a Peripheral Proxy next to you're block and attach your wired modem to that instead!")
			.toString();

	public static void addGlassesRecipe() {
		Block peripheral = getCCBlock("peripheral");
		Block cable = getCCBlock("cable");
		CraftingManager
				.getInstance()
				.getRecipeList()
				.add(new ShapedOreRecipe(new ItemStack(OpenPeripheral.Items.glasses), new Object[] { "mcm", Character.valueOf('m'), new ItemStack(peripheral, 1, 4),
						Character.valueOf('c'), new ItemStack(cable), }));
	}

	public static void addBridgeRecipe() {
		Block peripheral = getCCBlock("peripheral");
		Block cable = getCCBlock("cable");
		CraftingManager
				.getInstance()
				.getRecipeList()
				.add(new ShapedOreRecipe(new ItemStack(OpenPeripheral.Blocks.glassesBridge),
						new Object[] { "lwl", "wrw", "lwl", Character.valueOf('w'), new ItemStack(cable, 1, 1), Character.valueOf('r'),
								new ItemStack(Block.blockRedstone), Character.valueOf('l'), new ItemStack(peripheral, 1, 1), }));
	}

	public static void addTicketMachineRecipe() {
		CraftingManager
				.getInstance()
				.getRecipeList()
				.add(new ShapedOreRecipe(new ItemStack(OpenPeripheral.Blocks.ticketMachine), new Object[] { "iii", "iii", "igi", Character.valueOf('i'),
						new ItemStack(Item.ingotIron), Character.valueOf('g'), new ItemStack(Block.thinGlass), }));
	}

	public static void addProxyRecipe() {
		CraftingManager
				.getInstance()
				.getRecipeList()
				.add(new ShapedOreRecipe(new ItemStack(OpenPeripheral.Blocks.proxy), new Object[] { "iri", "rrr", "iri", Character.valueOf('i'),
						new ItemStack(Item.ingotIron), Character.valueOf('r'), new ItemStack(Item.redstone), }));
	}

	public static void addPIMRecipe() {
		CraftingManager
				.getInstance()
				.getRecipeList()
				.add(new ShapedOreRecipe(new ItemStack(OpenPeripheral.Blocks.playerInventory), new Object[] { "ooo", "rcr", Character.valueOf('o'),
						new ItemStack(Block.obsidian), Character.valueOf('r'), new ItemStack(Item.redstone), Character.valueOf('c'), new ItemStack(Block.chest), }));
	}

	private static Block getCCBlock(String fieldName) {
		Block block = null;
		try {
			Class cc = Class.forName("dan200.ComputerCraft$Blocks");
			if (cc != null) {
				Field peripheralField = cc.getDeclaredField(fieldName);
				if (peripheralField != null) {
					block = (Block) peripheralField.get(cc);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return block;
	}

	public static ItemStack getGuideItemStack() {
		ItemStack book = new ItemStack(Item.writtenBook);
		NBTTagCompound bookTag = new NBTTagCompound();
		bookTag.setString("title", "OpenPeripheral Guide");
		bookTag.setString("author", "Mikee & the OpenMods team");
		NBTTagList bookPages = new NBTTagList("pages");
		bookPages.appendTag(new NBTTagString("1", page1));
		bookPages.appendTag(new NBTTagString("2", page2));
		bookPages.appendTag(new NBTTagString("3", page3));
		bookPages.appendTag(new NBTTagString("4", page4));
		bookPages.appendTag(new NBTTagString("5", page5));
		bookPages.appendTag(new NBTTagString("6", page6));
		bookPages.appendTag(new NBTTagString("7", page7));
		bookPages.appendTag(new NBTTagString("8", page8));
		bookTag.setTag("pages", bookPages);
		book.setTagCompound(bookTag);
		return book;
	}

	public static void addBookRecipe() {
		Block cable = getCCBlock("cable");
		CraftingManager
				.getInstance()
				.getRecipeList()
				.add(new ShapedOreRecipe(getGuideItemStack(), new Object[] { "r", "c", "b", Character.valueOf('r'), new ItemStack(Item.redstone), Character.valueOf('c'),
						new ItemStack(cable), Character.valueOf('b'), new ItemStack(Item.book) }));
	}
	
	public static void addRemoteRecipe() {
		CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(
				new ItemStack(OpenPeripheral.Items.remote),
				new Object[] {
					"apa",
					"plp",
					" r ",
					Character.valueOf('l'), ItemGeneric.Metas.lcdScreen.newItemStack(),
					Character.valueOf('a'), ItemGeneric.Metas.duckAntenna.newItemStack(),
					Character.valueOf('p'), ItemGeneric.Metas.plasticSheet.newItemStack(),
					Character.valueOf('r'), ItemGeneric.Metas.ribbonCable.newItemStack(),
				}
		));
	}
}
