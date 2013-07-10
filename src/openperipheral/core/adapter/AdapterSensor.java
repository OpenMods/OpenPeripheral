package openperipheral.core.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
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
	public ArrayList<String> getPlayerNames(IComputerAccess computer, ISensorEnvironment sensor) {
		List<EntityPlayer> players = sensor.getWorld().getEntitiesWithinAABB(EntityPlayer.class, getBoundingBox(sensor.getLocation(), sensor.getSensorRange()));
		ArrayList<String> names = new ArrayList<String>();
		for (EntityPlayer player : players) {
			names.add(player.username);
		}
		return names;
	}
	
	@LuaMethod(onTick=false)
	public Map getPlayerData(IComputerAccess computer, ISensorEnvironment sensor, String username) {
		ArrayList<String> surroundingPlayers = getPlayerNames(computer, sensor);
		if (surroundingPlayers.contains(username)) {
			EntityPlayer player = sensor.getWorld().getPlayerEntityByName(username);
			return EntityUtils.entityToMap(player, sensor.getLocation());
		}
		return null;
	}

	@LuaMethod(onTick=false)
	public ArrayList<Integer> getMobIds(IComputerAccess computer, ISensorEnvironment sensor) {
		List<EntityLiving> mobs = sensor.getWorld().getEntitiesWithinAABB(EntityLiving.class, getBoundingBox(sensor.getLocation(), sensor.getSensorRange()));
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
}
