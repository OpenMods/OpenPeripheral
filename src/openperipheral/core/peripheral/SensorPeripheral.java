package openperipheral.core.peripheral;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import openperipheral.api.LuaMethod;
import openperipheral.core.definition.DefinitionLuaMethod;
import openperipheral.core.interfaces.IPeripheralMethodDefinition;
import openperipheral.core.interfaces.ISensorEnvironment;
import openperipheral.core.util.ThreadLock;
import openperipheral.sensor.MinecartData;
import openperipheral.sensor.MobData;
import openperipheral.sensor.PlayerData;

public class SensorPeripheral extends AbstractPeripheral {
	
	private ISensorEnvironment env;
	private HashMap<String, PlayerData> surroundingPlayers = new HashMap<String, PlayerData>();
	private HashMap<Integer, MobData> surroundingMobs = new HashMap<Integer, MobData>();
	private HashMap<Integer, MinecartData> surroundingMinecarts = new HashMap<Integer, MinecartData>();

	HashMap<String, PlayerData> tempPlayers = new HashMap<String, PlayerData>();
	HashMap<Integer, MinecartData> tempMinecarts = new HashMap<Integer, MinecartData>();
	HashMap<Integer, MobData> tempMobs = new HashMap<Integer, MobData>();
	
	private ThreadLock lock = new ThreadLock();
	private List<Object> exclusions;
	
	public SensorPeripheral(ISensorEnvironment env, Object ... exclude) {
		this.env = env;
		exclusions = Arrays.asList(exclude);
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
	

	public Integer[] getMobIds() {
		try {
			lock.lock();
			try {
				return surroundingMobs.keySet().toArray(new Integer[surroundingMobs.size()]);
			} finally {
				lock.unlock();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@LuaMethod
	public HashMap getMobData(int mobid) {
		try {
			lock.lock();
			try {
				if (surroundingMobs.containsKey(mobid)) {
					return surroundingMobs.get(mobid);
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
		tempMobs.clear();
		tempMinecarts.clear();
		try {
			lock.lock();
			try {
				
				int range = env.getSensorRange();
				
				World world = getWorldObject();
				Vec3 location = env.getLocation();
				if (location != null) {
					List<Entity> entities = world.getEntitiesWithinAABB(Entity.class,
							AxisAlignedBB.getAABBPool().getAABB(
									location.xCoord,
									location.yCoord,
									location.zCoord, 
									location.xCoord + 1,
									location.yCoord + 1,
									location.zCoord + 1).expand(range, range, range));
	
					for (Entity entity : entities) {
						if (exclusions.contains(entity)) {
							continue;
						}
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
							}else if (entity instanceof EntityLiving) {
								MobData newEntity = surroundingMobs.get(entity.entityId);
								if (newEntity == null) {
									newEntity = new MobData();
								}
								newEntity.fromEntity(env.getLocation(), (EntityLiving)entity);
								tempMobs.put(((EntityLiving) entity).entityId, newEntity);
								
							}
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				surroundingPlayers.clear();
				surroundingPlayers.putAll(tempPlayers);
				surroundingMinecarts.clear();
				surroundingMinecarts.putAll(tempMinecarts);
				surroundingMobs.clear();
				surroundingMobs.putAll(tempMobs);
			} finally {
				lock.unlock();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}

	@Override
	public World getWorldObject() {
		return env.getWorld();
	}


	@Override
	public ArrayList<IPeripheralMethodDefinition> getMethods() {
		return DefinitionLuaMethod.getLuaMethodsForObject(this);
	}

	@Override
	public Object getTargetObject(ArrayList args, IPeripheralMethodDefinition luaMethod) {
		return this;
	}

	
}
