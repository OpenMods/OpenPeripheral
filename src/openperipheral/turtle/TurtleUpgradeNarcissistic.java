package openperipheral.turtle;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import openperipheral.OpenPeripheral;
import openperipheral.core.item.ItemGeneric.Metas;
import cpw.mods.fml.common.registry.LanguageRegistry;
import dan200.computer.api.IHostedPeripheral;
import dan200.turtle.api.ITurtleAccess;
import dan200.turtle.api.ITurtleUpgrade;
import dan200.turtle.api.TurtleSide;
import dan200.turtle.api.TurtleUpgradeType;
import dan200.turtle.api.TurtleVerb;

public class TurtleUpgradeNarcissistic implements ITurtleUpgrade {

	@Override
	public int getUpgradeID() {
		return 181;
	}

	@Override
	public String getAdjective() {
		String translation = LanguageRegistry.instance().getStringLocalization("openperipheral.turtle.narcissistic.adjective");
		return translation == ""? LanguageRegistry.instance().getStringLocalization("openperipheral.turtle.narcissistic.adjective", "en_US") : translation;
	}

	@Override
	public TurtleUpgradeType getType() {
		return TurtleUpgradeType.Peripheral;
	}

	@Override
	public ItemStack getCraftingItem() {
		return Metas.duckAntenna.newItemStack();
	}

	@Override
	public boolean isSecret() {
		return false;
	}

	@Override
	public IHostedPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
		return new NarcissisticTurtlePeripheral(turtle);
	}

	@Override
	public boolean useTool(ITurtleAccess turtle, TurtleSide side, TurtleVerb verb, int direction) {
		return false;
	}

	@Override
	public Icon getIcon(ITurtleAccess turtle, TurtleSide side) {
		return OpenPeripheral.Blocks.sensor.turtleIcon;
	}

}
