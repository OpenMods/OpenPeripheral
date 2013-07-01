package openperipheral.common.entity.ai;

import openperipheral.common.entity.EntityRobot;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;

public class EntityAIGotoLocation extends EntityAIBase {

	private EntityRobot robot;
	
	public EntityAIGotoLocation(EntityRobot robot) {
		this.robot = robot;
	}
	
	@Override
	public boolean shouldExecute() {
		return robot.shouldMoveToTarget;
	}

    public boolean continueExecuting()
    {
		boolean hasPath = !robot.getNavigator().noPath();
		robot.shouldMoveToTarget = hasPath;
		if (!hasPath) {
			PathEntity path = robot.getNavigator().getPath();
			if (path == null) {
				robot.onNoPathAvailable();
			}else if (path.isFinished()) {
				robot.onPathFinished();
			}
		}
        return hasPath;
    }

    public void startExecuting()
    {
        robot.getNavigator().tryMoveToXYZ(robot.locationTargetX, robot.locationTargetY, robot.locationTargetZ, robot.getMoveSpeed());
    }
}
