package openperipheral.common.robotupgrades.movement;

import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.common.entity.EntityRobot;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;

public class EntityAIGotoLocation extends EntityAIBase {

	private IRobot robot;
	private MovementUpgrade instance;
	private PathNavigate navigator;
	
	public static final String NO_PATH_AVAILABLE = "no_path_available";
	public static final String PATH_FINISHED = "path_finished";
	
	public EntityAIGotoLocation(MovementUpgrade instance, IRobot robot) {
		this.robot = robot;
		this.instance = instance;
		this.navigator = robot.getEntity().getNavigator();
	}
	
	@Override
	public boolean shouldExecute() {
		return instance.shouldMoveToTarget();
	}

    public boolean continueExecuting()
    {
		boolean hasPath = !navigator.noPath();
		instance.setShouldMoveToTarget(hasPath);
		if (!hasPath) {
			PathEntity path = navigator.getPath();
			if (path == null) {
				robot.fireEvent(NO_PATH_AVAILABLE);
			}else if (path.isFinished()) {
				robot.fireEvent(PATH_FINISHED);
			}
		}
        return hasPath;
    }

    public void startExecuting()
    {
        navigator.tryMoveToXYZ(instance.getTargetLocationX(), instance.getTargetLocationY(), instance.getTargetLocationZ(), robot.getMoveSpeed());
    }
}
