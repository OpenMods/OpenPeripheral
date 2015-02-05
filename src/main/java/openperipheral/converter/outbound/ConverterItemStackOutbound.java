package openperipheral.converter.outbound;

import net.minecraft.item.ItemStack;
import openperipheral.api.converter.IConverter;
import openperipheral.api.helpers.SimpleOutboundConverter;
import openperipheral.meta.ItemStackMetadataBuilder;

public class ConverterItemStackOutbound extends SimpleOutboundConverter<ItemStack> {

	private ItemStackMetadataBuilder BUILDER = new ItemStackMetadataBuilder();

	@Override
	public Object convert(IConverter registry, ItemStack stack) {
		Object meta = BUILDER.getItemStackMetadata(stack);
		return registry.fromJava(meta);
	}

}
