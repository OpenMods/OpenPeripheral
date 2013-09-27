package openperipheral.core.adapter;

import openperipheral.api.Arg;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import openperipheral.glasses.block.TileEntityGlassesBridge;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaObject;

public class AdapterGlassesBridge implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return TileEntityGlassesBridge.class;
	}

	@LuaMethod(returnType = LuaType.VOID, onTick = false, description = "Clear all the objects from the screen")
	public void clear(IComputerAccess computer, TileEntityGlassesBridge bridge) {
		bridge.clear();
	}

	@LuaMethod(
		returnType = LuaType.OBJECT, onTick = false, description = "Add a new text object to the screen",
		args = {
				@Arg(name = "x", description = "The x position from the top left", type = LuaType.NUMBER),
				@Arg(name = "y", description = "The y position from the top left", type = LuaType.NUMBER),
				@Arg(name = "text", description = "The text to display", type = LuaType.STRING),
				@Arg(name = "color", description = "The text color", type = LuaType.NUMBER) })
	public ILuaObject addText(IComputerAccess computer, TileEntityGlassesBridge bridge, int x, int y, String text, int color) {
		return bridge.addText(x, y, text, color);
	}

	@LuaMethod(
		returnType = LuaType.OBJECT, onTick = false, description = "Add a new box to the screen",
		args = {
				@Arg(name = "x", description = "The x position from the top left", type = LuaType.NUMBER), @Arg(name = "y", description = "The y position from the top left", type = LuaType.NUMBER),
				@Arg(name = "width", description = "The width of the box", type = LuaType.NUMBER),
				@Arg(name = "height", description = "The height of the box", type = LuaType.NUMBER),
				@Arg(name = "color", description = "The color of the box", type = LuaType.NUMBER),
				@Arg(name = "opacity", description = "The opacity of the box (from 0 to 1)", type = LuaType.NUMBER) })
	public ILuaObject addBox(IComputerAccess computer, TileEntityGlassesBridge bridge, int x, int y, int width, int height, int color, double opacity) throws InterruptedException {
		return bridge.addBox(x, y, width, height, color, opacity);
	}

	@LuaMethod(
		returnType = LuaType.OBJECT, onTick = false, description = "Add a new gradient box to the screen",
		args = {
				@Arg(name = "x", description = "The x position from the top left", type = LuaType.NUMBER),
				@Arg(name = "y", description = "The y position from the top left", type = LuaType.NUMBER),
				@Arg(name = "width", description = "The width of the box", type = LuaType.NUMBER),
				@Arg(name = "height", description = "The height of the box", type = LuaType.NUMBER),
				@Arg(name = "color", description = "The color of the box", type = LuaType.NUMBER),
				@Arg(name = "opacity", description = "The opacity of the box (from 0 to 1)", type = LuaType.NUMBER),
				@Arg(name = "color", description = "The color of the other side of the box", type = LuaType.NUMBER),
				@Arg(name = "opacity", description = "The opacity of the other side of the box (from 0 to 1)", type = LuaType.NUMBER),
				@Arg(name = "gradient", description = "The gradient direction (0 for none, 1 for horizontal, 2 for vertical)", type = LuaType.NUMBER) })
	public ILuaObject addGradientBox(IComputerAccess computer, TileEntityGlassesBridge bridge, int x, int y, int width, int height, int color, double alpha, int color2, double alpha2, byte gradient)
			throws InterruptedException {
		return bridge.addGradientBox(x, y, width, height, color, alpha, color2, alpha2, gradient);
	}
	
	@LuaMethod(
	        returnType = LuaType.OBJECT, onTick = false, description = "Add an icon of an item to the screen",
	        args = {
	                @Arg(name = "x", description = "The x position from the top left", type = LuaType.NUMBER),
	                @Arg(name = "y", description = "The y position from the top left", type = LuaType.NUMBER),
	                @Arg(name = "id", description = "The id of the item to draw", type = LuaType.NUMBER),
	                @Arg(name = "meta", description = "The meta of the item to draw", type = LuaType.NUMBER)
	        })
	public ILuaObject addIcon(IComputerAccess computer, TileEntityGlassesBridge bridge, int x, int y, int id, int meta) {
	    return bridge.addIcon(x, y, id, meta);
	}

	@LuaMethod(
		returnType = LuaType.TABLE, onTick = false, description = "Get the Ids of all the objects on the screen")
	public Short[] getAllIds(IComputerAccess computer, TileEntityGlassesBridge bridge) {
		return bridge.getAllIds();
	}

	@LuaMethod(returnType = LuaType.TABLE, onTick = false, description = "Get the names of all the users linked up to this bridge")
	public String[] getUsers(IComputerAccess computer, TileEntityGlassesBridge bridge) {
		return bridge.getUsers();
	}

	@LuaMethod(returnType = LuaType.VOID, onTick = false, description = "Reset the Guid of the bridge. This will unlink all users")
	public void resetGuid(IComputerAccess computer, TileEntityGlassesBridge bridge) {
		bridge.resetGuid();
	}

	@LuaMethod(returnType = LuaType.STRING, onTick = false, description = "Get the Guid of this bridge")
	public String getGuid(IComputerAccess computer, TileEntityGlassesBridge bridge) {
		return bridge.getGuid();
	}

	@LuaMethod(
		returnType = LuaType.NUMBER, onTick = false, description = "Get the display width of some text",
		args = {
			@Arg(name = "text", description = "The text you want to measure", type = LuaType.STRING) })
	public int getStringWidth(IComputerAccess computer, TileEntityGlassesBridge bridge, String text) {
		return bridge.getStringWidth(text);
	}
	
	@LuaMethod(
	        returnType = LuaType.OBJECT, onTick = false, description = "Get the surface of a user to draw privately on their screen",
	        args = {
	                @Arg(name = "username", description = "The username of the user to get the draw surface for", type = LuaType.STRING)
	        })
	public ILuaObject getUserSurface(IComputerAccess computer, TileEntityGlassesBridge bridge, String username) {
	    return bridge.getUserSurface(username);
	}

}
