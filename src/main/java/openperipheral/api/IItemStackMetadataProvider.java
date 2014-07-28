package openperipheral.api;

import java.util.Map;

import net.minecraft.item.ItemStack;

public interface IItemStackMetadataProvider<C> extends IMetaProvider<C> {

	public void buildMeta(Map<String, Object> output, C target, ItemStack stack);

}
