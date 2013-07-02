package openperipheral.api;

import java.util.List;

import net.minecraft.item.ItemStack;


public interface IRobotUpgradeProvider {
	
	/**
	 * Create a new instance of the robot upgrade.
	 * Please only ever supply one type of upgrade from this. If you want to make
	 * another upgrade, create another upgradedefinition!
	 * @return
	 */
	public IRobotUpgradeInstance provideUpgradeInstance(IRobot robot);
	
	/**
	 * A unique string ID
	 * @return
	 */
	public String getUpgradeId();
	
	/**
	 * The itemstack required for the upgrade
	 * @return
	 */
	public ItemStack getUpgradeItem();
	
	/**
	 * Is this module installed by default and unable to be removed?
	 * @return
	 */
	public boolean isForced();
	
	public List<IRobotMethod> getMethods();
}
