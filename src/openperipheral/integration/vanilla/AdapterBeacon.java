package openperipheral.integration.vanilla;

import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityBeacon;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import dan200.computer.api.IComputerAccess;

public class AdapterBeacon implements IPeripheralAdapter {
	private static final String NONE = "None";

	@Override
	public Class<?> getTargetClass() {
		return TileEntityBeacon.class;
	}

	@LuaMethod(returnType = LuaType.STRING, description = "Get the primary effect of the beacon")
	public String getPrimaryEffect(IComputerAccess computer, TileEntityBeacon beacon) {
		Integer effectId = beacon.getPrimaryEffect();
		return getEffectName(effectId);
	}

	@LuaMethod(returnType = LuaType.STRING, description = "Get the secondary effect of the beacon")
	public String getSecondaryEffect(IComputerAccess computer, TileEntityBeacon beacon) {
		Integer effectId = beacon.getSecondaryEffect();
		return getEffectName(effectId);
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Get the height of the beacon's pyramid")
	public int getLevels(IComputerAccess computer, TileEntityBeacon beacon) {
		return beacon.getLevels();
	}

	private static String getEffectName(int effectId) {
		if (effectId != 0) {
			PotionEffect effect = new PotionEffect(effectId, 180, 0, true);
			return effect.getEffectName();
		}
		return NONE;
	}
}
