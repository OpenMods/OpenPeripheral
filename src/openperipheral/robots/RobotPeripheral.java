package openperipheral.robots;

import java.util.ArrayList;

import net.minecraft.world.World;
import openperipheral.api.Arg;
import openperipheral.api.IAdapterBase;
import openperipheral.api.IRobotUpgradeAdapter;
import openperipheral.api.IRobotUpgradeProvider;
import openperipheral.core.AdapterManager;
import openperipheral.core.MethodDeclaration;
import openperipheral.core.TypeConversionRegistry;
import openperipheral.core.peripheral.HostedPeripheral;
import openperipheral.core.util.MiscUtils;
import openperipheral.robots.block.TileEntityRobot;
import openperipheral.robots.entity.EntityRobot;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;

public class RobotPeripheral extends HostedPeripheral {

	public RobotPeripheral(Object target, World worldObj) {
		super(target, worldObj);
	}

	@Override
	public void initialize() {
		methods = new ArrayList<MethodDeclaration>();
		methods.add(AdapterManager.getMethodByName("listMethods"));
		for (IRobotUpgradeProvider provider : RobotUpgradeManager.getProviders()) {
			for (MethodDeclaration method : AdapterManager.getMethodsForClass(provider.getUpgradeClass())) {
				if (!methods.contains(method)) {
					methods.add(method);
				}
			}
		}

		methodNames = new String[methods.size()];
		for (int i = 0; i < methods.size(); i++) {
			methodNames[i] = methods.get(i).getLuaName();
		}
		type = MiscUtils.getNameForTarget(targetObject);

	}

	@Override
	public Object[] callMethod(final IComputerAccess computer, ILuaContext context, int index, Object[] arguments) throws Exception {

		final MethodDeclaration method = methods.get(index);

		IAdapterBase adapter = null;

		Object[] _formattedParameters;
		if (method instanceof RobotMethodDeclaration) {
			adapter = method.getTargetAdapter();

			Arg[] requiredParameters = method.getRequiredParameters();

			if (arguments.length < 1 || !(arguments[0] instanceof Double)) { throw new Exception("First argument should be the robot ID"); }

			int robotId = (int)(double)(Double)arguments[0];

			TileEntityRobot robotController = (TileEntityRobot)targetObject;

			EntityRobot robot = robotController.getRobotById(robotId);

			if (robot == null) { throw new Exception("Bad robot ID specified. Are you sure this robot is linked to this controller?"); }

			final IRobotUpgradeAdapter upgradeInstance = robot.getInstanceForLuaMethod(method.getLuaName());

			if (upgradeInstance == null) { throw new Exception("Can not execute method. Do you have the required robot upgrade installed?"); }

			Object[] strippedArguments = new Object[arguments.length - 1];
			System.arraycopy(arguments, 1, strippedArguments, 0, strippedArguments.length);

			if (strippedArguments.length != requiredParameters.length) { throw new Exception(String.format("Invalid number of parameters. You should be passing %s parameters", strippedArguments.length + 1)); }

			for (int i = 0; i < strippedArguments.length; i++) {
				if (!requiredParameters[i].type().getJavaType().isAssignableFrom(strippedArguments[i].getClass())) { throw new Exception(String.format("Parameter number %s (%s) should be a %s", i + 2, requiredParameters[i].name(), requiredParameters[i].type().getName())); }
			}

			Class[] requiredJavaParameters = method.getRequiredJavaParameters();

			for (int i = 0; i < strippedArguments.length; i++) {
				strippedArguments[i] = TypeConversionRegistry.fromLua(strippedArguments[i], requiredJavaParameters[i]);
			}

			_formattedParameters = new Object[strippedArguments.length + 2];
			System.arraycopy(strippedArguments, 0, _formattedParameters, 2, strippedArguments.length);

			_formattedParameters[0] = computer;
			_formattedParameters[1] = robot;

			adapter = upgradeInstance;
		} else {
			adapter = method.getTargetAdapter();
			_formattedParameters = formatParameters(computer, method, arguments);
		}

		final Object[] formattedParameters = _formattedParameters;

		return callOnTarget(computer, context, method, worldObj, adapter, formattedParameters);
	}

}
