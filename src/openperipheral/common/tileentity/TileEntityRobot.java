package openperipheral.common.tileentity;

import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.util.Arrays;

import dan200.computer.api.IComputerAccess;
import openperipheral.api.IAttachable;
import openperipheral.common.entity.EntityRobot;
import openperipheral.common.util.MiscUtils;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityRobot extends TileEntity implements IAttachable {

	private int robotId;
	private List<IComputerAccess> computers = new ArrayList<IComputerAccess>();
	
	public static final String NO_PATH_AVAILABLE = "no_path_available";
	public static final String PATH_FINISHED = "path_finished";
	
	private boolean hasActionInProgress = false;
	
	public boolean spawnRobot() {
		if (!worldObj.isRemote && getRobot() == null) {
			EntityRobot npc = new EntityRobot(worldObj);
			npc.setController(this);
			npc.setLocationAndAngles(xCoord, yCoord + 1, zCoord, 0, 0);
			worldObj.spawnEntityInWorld(npc);
			robotId = npc.entityId;
			return true;
		}
		return false;
	}
	
	public float getYaw() throws Exception {
		EntityRobot robot = getRobot();
		if (robot == null) {
			throw new Exception("No robot available");
		}
		return robot.rotationYaw;
	}

	public void suck() throws Exception {
		EntityRobot robot = getRobot();
		if (robot == null) {
			throw new Exception("No robot available");
		}
		robot.suckUp();
	}
	
	public void dropAll() throws Exception {
		EntityRobot robot = getRobot();
		if (robot == null) {
			throw new Exception("No robot available");
		}
		robot.dropAll();
	}
	
	public void gotoLocation(double x, double y, double z) throws Exception {
		EntityRobot robot = getRobot();
		if (robot == null) {
			throw new Exception("No robot available");
		}
		hasActionInProgress = true;
		robot.setLocationTarget(x, y, z);
	}
	
	public double getX() throws Exception {
		EntityRobot robot = getRobot();
		if (robot == null) {
			throw new Exception("No robot available");
		}
		return robot.posX;
	}
	
	public double getY() throws Exception {
		EntityRobot robot = getRobot();
		if (robot == null) {
			throw new Exception("No robot available");
		}
		return robot.posY;
	}
	
	public double getZ() throws Exception {
		EntityRobot robot = getRobot();
		if (robot == null) {
			throw new Exception("No robot available");
		}
		return robot.posZ;
	}

	
	public void fire() throws Exception {
		EntityRobot robot = getRobot();
		if (robot == null) {
			throw new Exception("No robot available");
		}
		robot.playSound("openperipheral.lazer", 1F, worldObj.rand.nextFloat() + 0.4f);
		robot.fire();
	}
	

	public void look(float direction) throws Exception {
		EntityRobot robot = getRobot();
		if (robot == null) {
			throw new Exception("No robot available");
		}
		/// ARRGGHHHHHH
		robot.renderYawOffset = robot.prevRotationYawHead = robot.rotationYawHead = robot.prevRotationYaw = robot.rotationYaw = direction;
	}
	
	public void setPitch(float direction) throws Exception {
		EntityRobot robot = getRobot();
		if (robot == null) {
			throw new Exception("No robot available");
		}
		/// ARRGGHHHHHH
		robot.rotationPitch = direction;
	}
	
	public void jump() throws Exception {
		EntityRobot robot = getRobot();
		if (robot == null) {
			throw new Exception("No robot available");
		}
		robot.playSound("openperipheral.robotjump", 1F, 1F);
		robot.jump(1.2f);
	}
	
	protected EntityRobot getRobot() {
		Entity entity = worldObj.getEntityByID(robotId);
		if (entity instanceof EntityRobot) {
			return (EntityRobot) entity;
		}
		return null;
	}
	
	public void onNoPathAvailable() {
		hasActionInProgress = false;
		fireEvent(NO_PATH_AVAILABLE);
	}
	
	public void onPathFinished() {
		hasActionInProgress = false;
		fireEvent(PATH_FINISHED);
	}
	
	public void fireEvent(String eventName, Object ... args) {
		for (IComputerAccess computer : computers) {
			args = MiscUtils.append(args, computer.getAttachmentName());
			computer.queueEvent(eventName, args);
		}
	}

	@Override
	public void addComputer(IComputerAccess computer) {
		computers.add(computer);
	}

	@Override
	public void removeComputer(IComputerAccess computer) {
		computers.remove(computer);
	}
	
}
