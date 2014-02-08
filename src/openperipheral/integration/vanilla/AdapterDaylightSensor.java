package openperipheral.integration.vanilla;

import net.minecraft.tileentity.TileEntityDaylightDetector;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import openperipheral.api.*;

import com.google.common.base.Preconditions;

@Prefixed("target")
public class AdapterDaylightSensor implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return TileEntityDaylightDetector.class;
	}

	@LuaCallable(returnTypes = LuaType.BOOLEAN, description = "Get true if age has normal sky")
	public boolean hasSky(TileEntityDaylightDetector target) {
		World world = target.getWorldObj();
		return !world.provider.hasNoSky;
	}

	@LuaCallable(returnTypes = LuaType.NUMBER, description = "Get level of natural light (sky)")
	public int getSkyLight(TileEntityDaylightDetector target) {
		World world = target.getWorldObj();
		Preconditions.checkArgument(!world.provider.hasNoSky, "World has no sky");
		return world.getSavedLightValue(EnumSkyBlock.Sky, target.xCoord, target.yCoord, target.zCoord) - world.skylightSubtracted;
	}

	@LuaCallable(returnTypes = LuaType.NUMBER, description = "Get level of block light")
	public int getBlockLight(TileEntityDaylightDetector target) {
		World world = target.getWorldObj();
		return world.getSavedLightValue(EnumSkyBlock.Block, target.xCoord, target.yCoord, target.zCoord);
	}

	@LuaCallable(returnTypes = LuaType.NUMBER, description = "Get angle of sun (in degrees, 0 is zenith)")
	public float getCelestialAngle(TileEntityDaylightDetector target) {
		World world = target.getWorldObj();
		return world.getCelestialAngle(1) * 360;
	}
}
