package openperipheral.integration.thaumcraft;

import net.minecraftforge.common.ForgeDirection;
import openperipheral.api.*;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;

@Prefixed("target")
public class AdapterEssentiaTransport implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IEssentiaTransport.class;
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Returns the amount of suction in the tube", args = { @Arg(type = LuaType.STRING, description = "Direction suction coming from", name = "direction") })
	public int getSuctionAmount(IEssentiaTransport pipe, ForgeDirection direction) {
		return pipe.getSuctionAmount(direction);
	}

	@LuaMethod(returnType = LuaType.STRING, description = "Returns the type of essentia wished in the tube", args = { @Arg(type = LuaType.STRING, description = "Direction suction coming from", name = "direction") })
	public String getSuctionType(IEssentiaTransport pipe, ForgeDirection direction) {
		Aspect asp = pipe.getSuctionType(direction);
		return (asp != null)? asp.getTag() : "";
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Returns the amount of essentia in the tube", args = { @Arg(type = LuaType.STRING, description = "Direction suction coming from", name = "direction") })
	public int getEssentiaAmount(IEssentiaTransport pipe, ForgeDirection direction) {
		return pipe.getEssentiaAmount(direction);
	}

	@LuaMethod(returnType = LuaType.STRING, description = "Returns the type of essentia in the tube", args = { @Arg(type = LuaType.STRING, description = "Direction suction coming from", name = "direction") })
	public String getEssentiaType(IEssentiaTransport pipe, ForgeDirection direction) {
		Aspect asp = pipe.getEssentiaType(direction);
		return (asp != null)? asp.getTag() : "";
	}

}
