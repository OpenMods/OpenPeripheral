package openperipheral.core.peripheral;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import openperipheral.api.IMultiReturn;
import openperipheral.core.AdapterManager;
import openperipheral.core.MethodDeclaration;
import openperipheral.core.TickHandler;
import openperipheral.core.TypeConversionRegistry;
import openperipheral.core.interfaces.IAttachable;
import openperipheral.core.util.MiscUtils;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IHostedPeripheral;
import dan200.computer.api.ILuaContext;

public class HostedPeripheral implements IHostedPeripheral {

	protected ArrayList<MethodDeclaration> methods;
	protected String[] methodNames;
	protected String type;
	protected Object target;
	protected World worldObj;
	
	public HostedPeripheral(Object target, World worldObj) {

		this.target = target;
		this.worldObj = worldObj;
		
		initialize();
		
	}
	
	public void initialize() {

		methods = AdapterManager.getMethodsForTarget(target);
		
		methodNames = new String[methods.size()];
		for (int i = 0; i < methods.size(); i++) {
			methodNames[i] = methods.get(i).getLuaName();
		}
		type = MiscUtils.getNameForTarget(target);
	}
	
	@Override
	public String getType() {
		return type;
	}

	@Override
	public String[] getMethodNames() {
		return methodNames;
	}
	
	public World getWorldObject() {
		return worldObj;
	}

	@Override
	public Object[] callMethod(final IComputerAccess computer, ILuaContext context,
			int index, Object[] arguments) throws Exception {
		
		final MethodDeclaration method = methods.get(index);
		
		final Object[] formattedParameters = formatParameters(computer, method, arguments);
		
		if (method.onTick()) {
			Future callback = TickHandler.addTickCallback(getWorldObject(), new Callable() {
				@Override
				public Object call() throws Exception {
					try {
						Object[] response = formatResponse(method.getMethod().invoke(method.getTarget(), formattedParameters));
						Object[] prefixedResponse = new Object[response.length + 1];
						System.arraycopy(response, 0, prefixedResponse, 1, response.length);
						prefixedResponse[0] = 1; // success
						computer.queueEvent("openperipheral_response", prefixedResponse);
					}catch(Throwable e) {
						if (e instanceof InvocationTargetException) {
							e = ((InvocationTargetException) e).getCause();
						}
						computer.queueEvent("openperipheral_response", new Object[] { 0, e.getMessage() });
					}
					return null;
				}
			});
			Object[] event = context.pullEvent("openperipheral_response");
			
			// if the object returned is an exception/error, lets throw it.
			if (((int)(double)(Double)event[1]) == 0) {
				throw new Exception((String)(event[2]));
			}
			
			// return the result
			Object[] response = new Object[event.length - 2];
			System.arraycopy(event, 2, response, 0, response.length);
			return response;
			
		}else {
			return formatResponse(method.getMethod().invoke(method.getTarget(), formattedParameters));
		}
	}
	
	protected Object[] formatParameters(IComputerAccess computer, MethodDeclaration method, Object[] arguments) throws Exception {
		
		Class[] requiredParameters = method.getRequiredParameters();
		
		if (requiredParameters.length != arguments.length) {
			throw new Exception(String.format("Invalid number of parameters. Expected %s", requiredParameters.length));
		}
		
		for (int i = 0; i < arguments.length; i++) {
			arguments[i] = TypeConversionRegistry.fromLua(arguments[i], requiredParameters[i]);
		}
		
		Object[] newArgs = new Object[arguments.length + 2];
		System.arraycopy(arguments, 0, newArgs, 2, arguments.length);
		
		newArgs[0] = computer;
		newArgs[1] = target;
		
		return newArgs;
	}
	
	protected Object[] formatResponse(Object response) {
		
		Object[] returnValues;
		
		if (response instanceof IMultiReturn) {
			returnValues = ((IMultiReturn) response).getObjects();
			for (int i = 0; i < returnValues.length; i++) {
				returnValues[i] = TypeConversionRegistry.toLua(returnValues[i]);
			}
		}else {
			returnValues = new Object[] { TypeConversionRegistry.toLua(response) };
		}
		
		return returnValues;
	}

	@Override
	public boolean canAttachToSide(int side) {
		return true;
	}

	@Override
	public void attach(IComputerAccess computer) {
		if (target instanceof IAttachable) {
			((IAttachable)target).addComputer(computer);
		}
	}

	@Override
	public void detach(IComputerAccess computer) {
		if (target instanceof IAttachable) {
			((IAttachable)target).removeComputer(computer);
		}
	}

	@Override
	public void update() {
		
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

	}
	

}
