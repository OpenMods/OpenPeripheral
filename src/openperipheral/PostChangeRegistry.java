package openperipheral;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import openperipheral.definition.DefinitionMethod;

public class PostChangeRegistry {

	private static ArrayList<IPostChangeHandler> changeHandlers = new ArrayList<IPostChangeHandler>();

	public static void registerChangeHandler(IPostChangeHandler handler) {
		changeHandlers.add(handler);
	}

	public static void onPostChange(TileEntity tile,
			DefinitionMethod luaMethod, Object[] values) {
		for (IPostChangeHandler handler : changeHandlers) {
			handler.execute(tile, luaMethod, values);
		}
	}
}
