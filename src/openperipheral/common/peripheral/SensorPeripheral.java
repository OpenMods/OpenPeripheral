package openperipheral.common.peripheral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.FakePlayer;

import openperipheral.api.IMethodDefinition;
import openperipheral.api.ISensorEnvironment;
import openperipheral.api.LuaMethod;
import openperipheral.common.definition.DefinitionLuaMethod;
import openperipheral.common.sensor.IEntityData;
import openperipheral.common.sensor.MinecartData;
import openperipheral.common.sensor.PlayerData;
import openperipheral.common.util.ThreadLock;

public class SensorPeripheral extends AbstractPeripheral {
	
	private ISensorEnvironment env;
	private HashMap<String, PlayerData> surroundingPlayers = new HashMap<String, PlayerData>();
	
	private ThreadLock lock = new ThreadLock();
	
	public SensorPeripheral(ISensorEnvironment env) {
		this.env = env;
	}
	
	@LuaMethod
	public String[] getPlayerNames() {
		try {
			lock.lock();
			try {
				return surroundingPlayers.keySet().toArray(new String[surroundingPlayers.size()]);
			} finally {
				lock.unlock();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	@LuaMethod
	public HashMap getPlayerData(String username) {
		try {
			lock.lock();
			try {
				if (surroundingPlayers.containsKey(username)) {
					return surroundingPlayers.get(username);
				}
			} finally {
				lock.unlock();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void replaceArguments(ArrayList<Object> args, HashMap<Integer, String> replacements) {
		
	}

	@Override
	public void update() {
		HashMap<String, PlayerData> tempPlayers = new HashMap<String, PlayerData>();
		try {
			lock.lock();
			try {
				
				World world = getWorldObject();
				Vec3 location = env.getLocation();
				
				List<Entity> entities = world.getEntitiesWithinAABB(Entity.class,
						AxisAlignedBB.getAABBPool().getAABB(
								location.xCoord - 5,
								location.yCoord - 5,
								location.zCoord - 5, 
								location.xCoord + 6,
								location.yCoord + 6,
								location.zCoord + 6));

				for (Entity entity : entities) {
					try {
						PlayerData newEntity = null;
						if (entity instanceof EntityPlayer) {
							newEntity = surroundingPlayers.get(entity.entityId);
							if (newEntity == null) {
								newEntity = new PlayerData();
							}
							newEntity.fromPlayer(env.getLocation(), (EntityPlayer)entity);
							tempPlayers.put(((EntityPlayer) entity).username, newEntity);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				surroundingPlayers.clear();
				surroundingPlayers.putAll(tempPlayers);
			} finally {
				lock.unlock();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	@Override
	public Object getTargetObject() {
		return this;
	}

	@Override
	public World getWorldObject() {
		return env.getWorld();
	}


	@Override
	public ArrayList<IMethodDefinition> getMethods() {
		return DefinitionLuaMethod.getLuaMethodsForObject(getTargetObject());
	}
	
}
