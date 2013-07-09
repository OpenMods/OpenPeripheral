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
import openperipheral.api.RobotUpgradeManager;
import openperipheral.core.MethodDeclaration;
import openperipheral.core.TickHandler;
import openperipheral.core.converter.TypeConversionRegistry;
import openperipheral.core.peripheral.HostedPeripheral;
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
		methodNames = new String[methods.size()];
		for (int i = 0; i < methods.size(); i++) {
			methodNames[i] = methods.get(i).getLuaName();
		}
		type = "robot";
	}

	@Override
	public Object[] callMethod(final IComputerAccess computer, ILuaContext context,
			int index, Object[] arguments) throws Exception {
		
		final MethodDeclaration method = methods.get(index);
		
		if (method instanceof RobotMethodDeclaration) {
			
			Class[] requiredParameters = method.getRequiredParameters();

			if (requiredParameters.length != arguments.length) {
				throw new Exception(String.format("Invalid number of parameters. Expected %s", requiredParameters.length));
			}
			
			for (int i = 0; i < arguments.length; i++) {
				arguments[i] = TypeConversionRegistry.fromLua(arguments[i], requiredParameters[i]);
			}
			
			Object[] formattedParametersTmp = new Object[arguments.length - 1];
			System.arraycopy(arguments, 1, formattedParametersTmp, 0, formattedParametersTmp.length);
			
			final Object[] formattedParameters = formattedParametersTmp;
			
			int robotId = (Integer) arguments[0];
			
			TileEntityRobot controller = (TileEntityRobot) target;
			
			World worldObj = controller.worldObj;
			
			EntityRobot robot = controller.getRobotById(robotId);
			
			final IRobotUpgradeInstance upgradeInstance = robot.getInstanceForLuaMethod(method.getLuaName());
			
			if (upgradeInstance == null) {
				throw new Exception("Unable to find the relevant upgrade.");
			}
			
			if (method.onTick()) {
				Future callback = TickHandler.addTickCallback(worldObj, new Callable() {
					@Override
					public Object call() throws Exception {
						Object[] response = formatResponse(method.getMethod().invoke(upgradeInstance, formattedParameters));
						computer.queueEvent("openperipheral_response", response);
						return null;
					}
				});
				Object[] event = context.pullEvent("openperipheral_response");
				Object[] response = new Object[event.length - 1];
				System.arraycopy(event, 1, response, 0, response.length);
				return response;
			}else {
				return formatResponse(method.getMethod().invoke(upgradeInstance, formattedParameters));
			}
			
			
		} else {
			return super.callMethod(computer, context, index, arguments);
		}
	}
}
