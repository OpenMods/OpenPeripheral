package openperipheral.core.integration.openperipheral;

import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.glasses.block.TileEntityGlassesBridge;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaObject;

public class AdapterGlassesBridge implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return TileEntityGlassesBridge.class;
	}
	
	@LuaMethod(onTick=false)
	public void clear(IComputerAccess computer, TileEntityGlassesBridge bridge) {
		bridge.clear();
	}
	
	@LuaMethod(onTick=false)
	public ILuaObject addText(IComputerAccess computer, TileEntityGlassesBridge bridge, int x, int y, String text, int color) {
		return bridge.addText(x, y, text, color);
	}

	@LuaMethod(onTick=false)
	public ILuaObject addBox(IComputerAccess computer, TileEntityGlassesBridge bridge, int x, int y, int width, int height, int color, double alpha) throws InterruptedException {
		return bridge.addBox(x, y, width, height, color, alpha);
	}

	@LuaMethod(onTick=false)
	public ILuaObject addGradientBox(IComputerAccess computer, TileEntityGlassesBridge bridge, int x, int y, int width, int height, int color, double alpha, int color2, double alpha2, byte gradient) throws InterruptedException {
		return bridge.addGradientBox(x, y, width, height, color, alpha, color2, alpha2, gradient);
	}
	
	@LuaMethod(onTick=false)
	public Short[] getAllIds(IComputerAccess computer, TileEntityGlassesBridge bridge) {
		return bridge.getAllIds();
	}
	
	@LuaMethod(onTick=false)
	public String[] getUsers(IComputerAccess computer, TileEntityGlassesBridge bridge) {
		return bridge.getUsers();
	}
	
	@LuaMethod(onTick=false)
	public void resetGuid(IComputerAccess computer, TileEntityGlassesBridge bridge) {
		bridge.resetGuid();
	}
	
	@LuaMethod(onTick=false)
	public String getGuid(IComputerAccess computer, TileEntityGlassesBridge bridge) {
		return bridge.getGuid();
	}
	
	@LuaMethod(onTick=false)
	public int getStringWidth(IComputerAccess computer, TileEntityGlassesBridge bridge, String text) {
		return bridge.getStringWidth(text);
	}

}
