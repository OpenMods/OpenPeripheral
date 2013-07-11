package openperipheral.robots;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import net.minecraft.world.World;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobotUpgradeProvider;
import openperipheral.api.LuaMethod;
import openperipheral.core.MethodDeclaration;
import openperipheral.core.TickHandler;
import openperipheral.core.TypeConversionRegistry;
import openperipheral.core.peripheral.HostedPeripheral;
import openperipheral.core.util.MiscUtils;
import openperipheral.robots.block.TileEntityRobot;
import openperipheral.robots.entity.EntityRobot;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;

public class RobotPeripheral extends HostedPeripheral {

	public static ArrayList<RobotMethodDeclaration> getMethodsForProvider(IRobotUpgradeProvider provider) {
		ArrayList<RobotMethodDeclaration> methods = new ArrayList<RobotMethodDeclaration>();
		for (Method method : provider.getUpgradeClass().getMethods()) {
			LuaMethod annotation = method.getAnnotation(LuaMethod.class);
			if (annotation != null) {
				methods.add(new RobotMethodDeclaration(annotation, method, provider));
			}
		}
		return methods;
	}
	
	public RobotPeripheral(Object target, World worldObj) {
		super(target, worldObj);
	}

	@Override
	public void initialize() {
		
		methods = new ArrayList<MethodDeclaration>();
		List<IRobotUpgradeProvider> providers = RobotUpgradeManager.getProviders();
		
		for (IRobotUpgradeProvider provider : providers) {
			methods.addAll(getMethodsForProvider(provider));
		}
		
		methodNames = new String[methods.size() + 1];
		
		methodNames[0] = "listMethods";
		for (int i = 0; i < methods.size(); i++) {
			methodNames[i + 1] = methods.get(i).getLuaName();
		}
		
		type = "robot";
	}

	@Override
	public Object[] callMethod(final IComputerAccess computer, ILuaContext context,
			int index, Object[] arguments) throws Exception {

		if (index == 0) {
			return new Object[] { MiscUtils.documentMethods(methods) };
		}

		index--;
		
		final MethodDeclaration method = methods.get(index);
		
		// if this method is robot method, we dont want to bother sending in
		// the computer and the robot target. these are already defined within
		// the robot instance
		if (method instanceof RobotMethodDeclaration) {

			// so we get the required parameters. Keep in mind that the robotId is automagically
			// prepended to the list of required parameters
			Class[] requiredParameters = method.getRequiredParameters();

			// if we dont have the correct number of params, throw an exception
			if (requiredParameters.length != arguments.length) {
				throw new Exception(String.format("Invalid number of parameters. Expected %s", requiredParameters.length));
			}
			
			// for each of the parameters, check it's the right type
			for (int i = 0; i < arguments.length; i++) {
				arguments[i] = TypeConversionRegistry.fromLua(arguments[i], requiredParameters[i]);
			}
			
			// now we make a new array but excluding the robotId (the first argument)
			Object[] formattedParametersTmp = new Object[arguments.length - 1];
			System.arraycopy(arguments, 1, formattedParametersTmp, 0, formattedParametersTmp.length);
			
			// stick it in a new object marked as final
			final Object[] formattedParameters = formattedParametersTmp;

			// grab the robot id from the previous array
			int robotId = (Integer) arguments[0];

			// get the controller and its world object
			TileEntityRobot controller = (TileEntityRobot) target;
			
			worldObj = controller.worldObj;
			
			// get a reference to the robot using this ID
			EntityRobot robot = controller.getRobotById(robotId);

			// find which upgrade instance is responsible for this lua method
			final IRobotUpgradeInstance upgradeInstance = robot.getInstanceForLuaMethod(method.getLuaName());

			if (upgradeInstance == null) {
				throw new Exception("Unable to find the relevant upgrade.");
			}

			// finally, we call the method!
			return callOnTarget(computer, context, method, upgradeInstance, formattedParameters);
			
		} else {
			return super.callMethod(computer, context, index, arguments);
		}
	}
}
