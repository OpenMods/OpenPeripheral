package openperipheral.sensor;

import dan200.turtle.api.ITurtleAccess;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import openperipheral.core.interfaces.ISensorEnvironment;

public class TurtleSensorEnvironment implements ISensorEnvironment {

	private ITurtleAccess turtle;
	
	public TurtleSensorEnvironment(ITurtleAccess turtle) {
		this.turtle = turtle;
	}
	
	@Override
	public boolean isTurtle() {
		return true;
	}

	@Override
	public Vec3 getLocation() {
		return turtle.getPosition();
	}

	@Override
	public World getWorld() {
		return turtle.getWorld();
	}

	@Override
	public int getSensorRange() {
		return 30;
	}

}
