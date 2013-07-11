package openperipheral.core.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import dan200.computer.api.IComputerAccess;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.core.interfaces.ISensorEnvironment;
import openperipheral.core.util.EntityUtils;
import openperipheral.sensor.block.TileEntitySensor;

public class AdapterSensor implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return ISensorEnvironment.class;
	}
	
	private AxisAlignedBB getBoundingBox(Vec3 location, double range) {
		return AxisAlignedBB.getAABBPool().getAABB(
				location.xCoord,
				location.yCoord,
				location.zCoord, 
				location.xCoord + 1,
				location.yCoord + 1,
				location.zCoord + 1).expand(range, range, range);
	}
	
	@LuaMethod(onTick=false)
	public ArrayList<String> getPlayerNames(IComputerAccess computer, ISensorEnvironment env) {
		List<EntityPlayer> players = env.getWorld().getEntitiesWithinAABB(EntityPlayer.class, getBoundingBox(env.getLocation(), env.getSensorRange()));
		ArrayList<String> names = new ArrayList<String>();
		for (EntityPlayer player : players) {
			names.add(player.username);
		}
		return names;
	}
	
	@LuaMethod(onTick=false)
	public Map getPlayerData(IComputerAccess computer, ISensorEnvironment env, String username) {
		ArrayList<String> surroundingPlayers = getPlayerNames(computer, env);
		if (surroundingPlayers.contains(username)) {
			EntityPlayer player = env.getWorld().getPlayerEntityByName(username);
			return EntityUtils.entityToMap(player, env.getLocation());
		}
		return null;
	}

	@LuaMethod(onTick=false)
	public ArrayList<Integer> getMobIds(IComputerAccess computer, ISensorEnvironment env) {
		List<EntityLiving> mobs = env.getWorld().getEntitiesWithinAABB(EntityLiving.class, getBoundingBox(env.getLocation(), env.getSensorRange()));
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (EntityLiving mob : mobs) {
			ids.add(mob.entityId);
		}
		return ids;
	}
	
	@LuaMethod(onTick=false)
	public Map getMobData(IComputerAccess computer, ISensorEnvironment sensor, int mobId) {
		ArrayList<Integer> surroundingMobs = getMobIds(computer, sensor);
		if (surroundingMobs.contains(mobId)) {
			Entity mob = sensor.getWorld().getEntityByID(mobId);
			return EntityUtils.entityToMap(mob, sensor.getLocation());
		}
		return null;
	}
	

	@LuaMethod(onTick=false)
	public ArrayList<Integer> getMinecartIds(IComputerAccess computer, ISensorEnvironment env) {
		List<EntityMinecart> minecarts = env.getWorld().getEntitiesWithinAABB(EntityMinecart.class, getBoundingBox(env.getLocation(), env.getSensorRange()));
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (EntityMinecart minecart : minecarts) {
			ids.add(minecart.entityId);
		}
		return ids;
	}
	
	@LuaMethod(onTick=false)
	public Map getMinecartData(IComputerAccess computer, ISensorEnvironment env, int minecartId) {
		ArrayList<Integer> surroundingCarts = getMobIds(computer, env);
		if (surroundingCarts.contains(minecartId)) {
			Entity cart = env.getWorld().getEntityByID(minecartId);
			return EntityUtils.entityToMap(cart, env.getLocation());
		}
		return null;
	}

	@LuaMethod
	public Map sonicScan(IComputerAccess computer, ISensorEnvironment env) {
		
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
}
