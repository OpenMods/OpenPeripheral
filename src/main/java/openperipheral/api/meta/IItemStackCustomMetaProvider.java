package openperipheral.api.meta;

import net.minecraft.item.ItemStack;

public interface IItemStackCustomMetaProvider<C> extends IItemStackMetaProvider<C> {
	public boolean canApply(C target, ItemStack stack);
}
