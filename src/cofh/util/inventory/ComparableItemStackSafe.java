package cofh.util.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ComparableItemStackSafe extends ComparableItemStack {

	static final String BLOCK = "block";
	static final String ORE = "ore";
	static final String DUST = "dust";
	static final String INGOT = "ingot";
	static final String NUGGET = "nugget";

	public static boolean safeOreType(String oreName) {

		return oreName.startsWith(BLOCK) || oreName.startsWith(ORE) || oreName.startsWith(DUST) || oreName.startsWith(INGOT) || oreName.startsWith(NUGGET);
	}

	public static int getOreID(ItemStack stack) {

		int id = OreDictionary.getOreID(stack);

		if (!safeOreType(OreDictionary.getOreName(id))) {
			return -1;
		}
		return id;
	}

	public static int getOreID(String oreName) {

		if (!safeOreType(oreName)) {
			return -1;
		}
		return OreDictionary.getOreID(oreName);
	}

	public ComparableItemStackSafe(ItemStack stack) {

		super(stack);
		oreID = getOreID(stack);
	}

	public ComparableItemStackSafe(int itemID, int damage, int stackSize) {

		super(itemID, damage, stackSize);
		this.oreID = getOreID(this.toItemStack());
	}

	@Override
	public ComparableItemStackSafe set(ItemStack stack) {

		super.set(stack);
		oreID = getOreID(stack);

		return this;
	}

}
