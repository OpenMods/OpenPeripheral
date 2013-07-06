package openperipheral.core.peripheral;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.world.World;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobotUpgradeProvider;
import openperipheral.api.RobotUpgradeManager;
import openperipheral.core.interfaces.IPeripheralMethodDefinition;
import openperipheral.robots.MethodGetRobots;
import openperipheral.robots.RobotPeripheralMethod;
import openperipheral.robots.block.TileEntityRobot;
import openperipheral.robots.entity.EntityRobot;

public class RobotPeripheral extends AbstractPeripheral {
	
	private ArrayList<IPeripheralMethodDefinition> methods;
	private TileEntityRobot robotTile;
	
	private MethodGetRobots getRobotsMethod = new MethodGetRobots();

	public RobotPeripheral(TileEntityRobot tile) {
		
		/**
		 * For all the robot upgrade suppliers, add all of their method
		 * Definitions into a list
		 */
		methods = new ArrayList<IPeripheralMethodDefinition>();
		methods.add(getRobotsMethod);
		for(IRobotUpgradeProvider provider : RobotUpgradeManager.getProviders()) {
			methods.addAll(RobotPeripheralMethod.getMethodsForProvider(provider));
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
