package openperipheral.api.meta;

import java.util.Map;
import net.minecraft.item.ItemStack;

public interface IItemStackPartialMetaBuilder extends IItemStackMetaBuilder, IPartialMetaBuilder<ItemStack> {
	public Map<String, Object> getBasicItemStackMetadata(ItemStack stack);

	public Object getItemStackMetadata(String key, ItemStack stack);

	public IMetaProviderProxy createProxy(ItemStack stack);
}
