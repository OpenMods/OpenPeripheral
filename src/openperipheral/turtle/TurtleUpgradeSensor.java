package openperipheral.turtle;

import openperipheral.OpenPeripheral;
import openperipheral.core.peripheral.HostedPeripheral;
import openperipheral.sensor.SensorPeripheral;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import dan200.computer.api.IHostedPeripheral;
import dan200.turtle.api.ITurtleAccess;
import dan200.turtle.api.ITurtleUpgrade;
import dan200.turtle.api.TurtleSide;
import dan200.turtle.api.TurtleUpgradeType;
import dan200.turtle.api.TurtleVerb;

public class TurtleUpgradeSensor implements ITurtleUpgrade {

	@Override
	public int getUpgradeID() {
		return 180;
	}

	@Override
	public String getAdjective() {
		String translation = LanguageRegistry.instance().getStringLocalization("openperipheral.turtle.sensor.adjective");
		return translation == "" ? LanguageRegistry.instance().getStringLocalization("openperipheral.turtle.sensor.adjective", "en_US") : translation;
	}

	@Override
	public TurtleUpgradeType getType() {
		return TurtleUpgradeType.Peripheral;
	}

	@Override
	public ItemStack getCraftingItem() {
		return new ItemStack(OpenPeripheral.Blocks.sensor);
	}

	@Override
	public boolean isSecret() {
		return false;
	}

	@Override
	public IHostedPeripheral createPeripheral(ITurtleAccess turtle,
			TurtleSide side) {
		return new SensorPeripheral(new TurtleSensorEnvironment(turtle));
	}

	@Override
	public boolean useTool(ITurtleAccess turtle, TurtleSide side,
			TurtleVerb verb, int direction) {
		return false;
	}

	@Override
	public Icon getIcon(ITurtleAccess turtle, TurtleSide side) {
		return OpenPeripheral.Blocks.sensor.turtleIcon;
	}

}
