package openperipheral.common.peripheral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
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
	private HashMap<Integer, MinecartData> surroundingMinecarts = new HashMap<Integer, MinecartData>();

	HashMap<String, PlayerData> tempPlayers = new HashMap<String, PlayerData>();
	HashMap<Integer, MinecartData> tempMinecarts = new HashMap<Integer, MinecartData>();
	
	private ThreadLock lock = new ThreadLock();
	
	private int range = 5;
	
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


	@LuaMethod
	public Integer[] getMinecartIds() {
		try {
			lock.lock();
			try {
				return surroundingMinecarts.keySet().toArray(new Integer[surroundingMinecarts.size()]);
			} finally {
				lock.unlock();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	@LuaMethod
	public HashMap getMinecartData(int id) {
		try {
			lock.lock();
			try {
				if (surroundingMinecarts.containsKey(id)) {
					return surroundingMinecarts.get(id);
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
		tempPlayers.clear();
		tempMinecarts.clear();
		try {
			lock.lock();
			try {
				
				World world = getWorldObject();
				Vec3 location = env.getLocation();
				
				List<Entity> entities = world.getEntitiesWithinAABB(Entity.class,
						AxisAlignedBB.getAABBPool().getAABB(
								location.xCoord,
								location.yCoord,
								location.zCoord, 
								location.xCoord + 1,
								location.yCoord + 1,
								location.zCoord + 1).expand(range, range, range));

				for (Entity entity : entities) {
					try {
						if (entity instanceof EntityPlayer) {
							PlayerData newEntity = surroundingPlayers.get(entity.entityId);
							if (newEntity == null) {
								newEntity = new PlayerData();
							}
							newEntity.fromEntity(env.getLocation(), (EntityPlayer)entity);
							tempPlayers.put(((EntityPlayer) entity).username, newEntity);
						}else if (entity instanceof EntityMinecart) {
							MinecartData newEntity = surroundingMinecarts.get(entity.entityId);
							if (newEntity == null) {
								newEntity = new MinecartData();
							}
							newEntity.fromEntity(env.getLocation(), (EntityMinecart)entity);
							tempMinecarts.put(((EntityMinecart) entity).entityId, newEntity);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				surroundingPlayers.clear();
				surroundingPlayers.putAll(tempPlayers);
				surroundingMinecarts.clear();
				surroundingMinecarts.putAll(tempMinecarts);
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
