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
	public int getSuctionAmount(Object target, ForgeDirection direction)
	{
		IEssentiaTransport pipe = (IEssentiaTransport)target;
		return pipe.getSuctionAmount(direction);
	}

	@LuaMethod(returnType = LuaType.STRING, description = "Returns the type of essentia wished in the tube", args = { @Arg(type = LuaType.STRING, description = "Direction suction coming from", name = "direction") })
	public String getSuctionType(Object target, ForgeDirection direction)
	{
		IEssentiaTransport pipe = (IEssentiaTransport)target;
		Aspect asp = pipe.getSuctionType(direction);
		if (asp == null) return "";
		return asp.getTag();
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Returns the amount of essentia in the tube", args = { @Arg(type = LuaType.STRING, description = "Direction suction coming from", name = "direction") })
	public int getEssentiaAmount(Object target, ForgeDirection direction)
	{
		IEssentiaTransport pipe = (IEssentiaTransport)target;
		return pipe.getEssentiaAmount(direction);
	}

	@LuaMethod(returnType = LuaType.STRING, description = "Returns the type of essentia in the tube", args = { @Arg(type = LuaType.STRING, description = "Direction suction coming from", name = "direction") })
	public String getEssentiaType(Object target, ForgeDirection direction)
	{
		IEssentiaTransport pipe = (IEssentiaTransport)target;
		Aspect asp = pipe.getEssentiaType(direction);
		if (asp == null) return "";
		return asp.getTag();
	}

}
