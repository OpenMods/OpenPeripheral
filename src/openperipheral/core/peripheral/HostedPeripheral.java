package openperipheral.core.peripheral;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import openperipheral.OpenPeripheral;
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
import dan200.computer.api.IMount;

public class HostedPeripheral implements IHostedPeripheral {

	public static final String EVENT_SUCCESS = "openperipheral_success";
	public static final String EVENT_ERROR = "openperipheral_error";
	
	protected ArrayList<MethodDeclaration> methods;
	protected String[] methodNames;
	protected String type;
	protected Object target;
	protected World worldObj;
	
	private static HashMap<Integer, Integer> mountMap = new HashMap<Integer, Integer>();
	
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
		
		return callOnTarget(computer, context, method, method.getTarget(), formattedParameters);
	}
	
	protected Object[] callOnTarget(final IComputerAccess computer, ILuaContext context, final MethodDeclaration method, final Object target, final Object[] parameters) throws Exception {

		// if it's on the tick, lets add a callback to execute on the tick
		if (method.onTick()) {
			
			Future callback = TickHandler.addTickCallback(getWorldObject(), new Callable() {
				@Override
				public Object call() throws Exception {

					// on the tick, we execute the method, format the response, then stick it into an event
					try {
						Object[] response = formatResponse(method.getMethod().invoke(target, parameters));
						computer.queueEvent(EVENT_SUCCESS, response);

					}catch(Throwable e) {
						if (e instanceof InvocationTargetException) {
							e = ((InvocationTargetException) e).getCause();
						}
						computer.queueEvent(EVENT_ERROR, new Object[] { 0, e.getMessage() });
					}
					return null;
				}
			});
			
			// while we don't have an OpenPeripheral event
			while (true) {
				
				// pull the event
				Object[] event = context.pullEvent(null);
				
				// get the event name
				String eventName = (String) event[0];
				
				// if it's an error, throw an exception
				if (eventName.equals(EVENT_ERROR)) {
					throw new Exception((String) event[1]);
				
				// if it's a success, trim the event name from it and return the response
				}else if (eventName.equals(EVENT_SUCCESS)) {
					
					Object[] response = new Object[event.length - 1];
					System.arraycopy(event, 1, response, 0, response.length);
					return response;
				}
			}
			
		}else {
			// no thread safety needed, lets just call the method, format the 
			// response and return it straight away
			return formatResponse(method.getMethod().invoke(target, parameters));
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
		int id = computer.getID();
		
		int mountCount = 0;
		if (mountMap.containsKey(id)) {
			mountCount = mountMap.get(id);
		}
		if (mountCount < 1) {
			mountCount = 0;
			computer.mount("openp", OpenPeripheral.mount);
		}
		mountMap.put(id, mountCount+1);
		
		if (target instanceof IAttachable) {
			((IAttachable)target).addComputer(computer);
		}
	}

	@Override
	public void detach(IComputerAccess computer) {
		int id = computer.getID();
		int mountCount = 0;
		if (mountMap.containsKey(id)) {
			mountCount = mountMap.get(id);
		}
		mountCount--;
		if (mountCount < 1) {
			mountCount = 0;
			try {
				computer.unmount("openp");
			}catch(Exception e) {
				
			}
		}
		mountMap.put(id, mountCount);
		
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
