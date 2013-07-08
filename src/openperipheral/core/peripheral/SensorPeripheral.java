package openperipheral.core.peripheral;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
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
	
	@LuaMethod(onTick=false)
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
	
	@LuaMethod(onTick=false)
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

	@LuaMethod(onTick=false)
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


	@LuaMethod(onTick=false)
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
	public HashMap sonicScan() {
		int range = 1 + (int)(env.getSensorRange() / 6);
		World world = env.getWorld();
		HashMap results = new HashMap();
		Vec3 sensorPos = env.getLocation();
		int sx = (int) sensorPos.xCoord;
		int sy = (int) sensorPos.yCoord;
		int sz = (int) sensorPos.zCoord;
		int unknown = 0;
		int water = 1;
		int liquid = 2;
		int i = 0;
		for (int x = -range; x <= range; x++) {
			for (int y = -range; y <= range; y++) {
				for (int z = -range; z <= range; z++) {

					int type = 0;
							
					if (!(x == 0 && y == 0 && z == 0) && world.blockExists(sx + x, sy + y, sz + z)) {

						int bX = sx + x;
						int bY = sy + y;
						int bZ = sz + z;

						int id = world.getBlockId(bX, bY, bZ);

						Block block = Block.blocksList[id];

						if (!(id == 0 || block == null)) {
							Vec3 targetPos = Vec3.createVectorHelper(
									bX,
									bY,
									bZ
							);
							if (sensorPos.distanceTo(targetPos) <= range) {
								if (id == 0) {
									type = 1;
								}else if (block.blockMaterial.isLiquid()) {
									type = 2;
								}else if (block.blockMaterial.isSolid()) {
									type = 3;
								}
							}
						}
					}
					HashMap tmp = new HashMap();
					tmp.put("x", x);
					tmp.put("y", y);
					tmp.put("z", z);
					tmp.put("type", type);
					results.put(++i, tmp);
				}
			}
		}
		return results;
	}
	
	@LuaMethod(onTick=false)
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
						if (exclusions != null && exclusions.contains(entity)) {
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
