package openperipheral.turtle;

import dan200.turtle.api.ITurtleAccess;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import openperipheral.core.AdapterManager;
import openperipheral.core.peripheral.HostedPeripheral;
import openperipheral.core.util.MiscUtils;
import openperipheral.core.util.ReflectionHelper;

public class NarcissisticTurtlePeripheral extends HostedPeripheral {

	public NarcissisticTurtlePeripheral(Object targetObject) {
		super(targetObject);
	}

	public void initialize() {
		
		Class klazz = ReflectionHelper.getClass("dan200.turtle.shared.TileEntityTurtle");
		
		methods = AdapterManager.getMethodsForClass(klazz);
		
		methodNames = new String[methods.size()];
		for (int i = 0; i < methods.size(); i++) {
			methodNames[i] = methods.get(i).getLuaName();
		}
		
	}
	
	@Override
	public World getWorldObject() {
		return ((ITurtleAccess)targetObject).getWorld();
	}

	@Override
	public String getType() {
		return "narcissistic";
	}
	
	@Override
	public Object getTargetObject() {
		Vec3 position = ((ITurtleAccess) targetObject).getPosition();
		return getWorldObject().getBlockTileEntity((int)position.xCoord, (int)position.yCoord, (int)position.zCoord);
	}
}
