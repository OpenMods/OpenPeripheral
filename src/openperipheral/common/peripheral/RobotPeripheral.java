package openperipheral.common.peripheral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.world.World;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeDefinition;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.RobotUpgradeManager;
import openperipheral.common.entity.EntityRobot;
import openperipheral.common.interfaces.IPeripheralMethodDefinition;
import openperipheral.common.robotupgrades.RobotPeripheralMethod;
import openperipheral.common.tileentity.TileEntityRobot;

public class RobotPeripheral extends AbstractPeripheral {
	
	private ArrayList<IPeripheralMethodDefinition> methods;
	private TileEntityRobot robotTile;
	
	private MethodDefinitionGetRobots getRobotsMethod = new MethodDefinitionGetRobots();

	public RobotPeripheral(TileEntityRobot tile) {
		
		/**
		 * For all the robot upgrade suppliers, add all of their method
		 * Definitions into a list
		 */
		methods = new ArrayList<IPeripheralMethodDefinition>();
		methods.add(getRobotsMethod);
		for(IRobotUpgradeDefinition supplier : RobotUpgradeManager.getSuppliers()) {
			List<IRobotMethod> robotMethods = supplier.getMethods();
			if (robotMethods != null) {
				for (IRobotMethod method : robotMethods) {
					methods.add(new RobotPeripheralMethod(method));
				}
			}
		}
		
		robotTile = tile;
	}

	@Override
	public ArrayList<IPeripheralMethodDefinition> getMethods() {
		return methods;
	}

	@Override
	protected void replaceArguments(ArrayList<Object> args, HashMap<Integer, String> replacements) {
	}

	@Override
	public World getWorldObject() {
		return robotTile.worldObj;
	}
	
	/**
	 * Remove the first argument (robot id) before we pass it across
	 */
	@Override
	public void preExecute(IPeripheralMethodDefinition method, ArrayList args) {
		if (method != getRobotsMethod) {
			args.remove(0);
		}
	}
	
	/**
	 * Get the object this method will be executed against. For this, we need to
	 * Grab the robot (based on the first parameters of the lua args) then find which
	 * upgrade handles the method with this lua name
	 */
	@Override
	public Object getTargetObject(ArrayList args, IPeripheralMethodDefinition luaMethod) throws Exception {
		if (luaMethod == getRobotsMethod) {
			return robotTile;
		}
		EntityRobot robot = robotTile.getRobotById((Integer) args.get(0));
		IRobotUpgradeInstance instance = robot.getInstanceForLuaMethod(luaMethod.getLuaName());
		if (instance == null) {
			throw new Exception("Unable to execute command. Do you have the correct upgrade enabled?");
		}
		return instance;
	}
}
