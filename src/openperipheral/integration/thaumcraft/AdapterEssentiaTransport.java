package openperipheral.integration.thaumcraft;

import net.minecraftforge.common.ForgeDirection;
import openperipheral.api.*;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;

@OnTick
public class AdapterEssentiaTransport implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IEssentiaTransport.class;
	}

	@LuaCallable(returnTypes = LuaType.NUMBER, description = "Returns the amount of suction in the tube")
	public int getSuctionAmount(IEssentiaTransport pipe,
			@Arg(type = LuaType.STRING, description = "Direction suction coming from", name = "direction") ForgeDirection direction) {
		return pipe.getSuctionAmount(direction);
	}

	@LuaCallable(returnTypes = LuaType.STRING, description = "Returns the type of essentia wished in the tube")
	public String getSuctionType(IEssentiaTransport pipe,
			@Arg(type = LuaType.STRING, description = "Direction suction coming from", name = "direction") ForgeDirection direction) {
		Aspect asp = pipe.getSuctionType(direction);
		return (asp != null)? asp.getTag() : "";
	}

	@LuaCallable(returnTypes = LuaType.NUMBER, description = "Returns the amount of essentia in the tube")
	public int getEssentiaAmount(IEssentiaTransport pipe,
			@Arg(type = LuaType.STRING, description = "Direction suction coming from", name = "direction") ForgeDirection direction) {
		return pipe.getEssentiaAmount(direction);
	}

	@LuaCallable(returnTypes = LuaType.STRING, description = "Returns the type of essentia in the tube")
	public String getEssentiaType(IEssentiaTransport pipe,
			@Arg(type = LuaType.STRING, description = "Direction suction coming from", name = "direction") ForgeDirection direction) {
		Aspect asp = pipe.getEssentiaType(direction);
		return (asp != null)? asp.getTag() : "";
	}

}
