package openperipheral.common.postchange;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IMethodDefinition;
import openperipheral.api.IPostChangeHandler;

public class PostChangeRegistry {

	private static ArrayList<IPostChangeHandler> changeHandlers = new ArrayList<IPostChangeHandler>();

	public static void registerChangeHandler(IPostChangeHandler handler) {
		changeHandlers.add(handler);
	}

	public static void onPostChange(Object target, IMethodDefinition luaMethod, Object[] values) {
		for (IPostChangeHandler handler : changeHandlers) {
			handler.execute(target, luaMethod, values);
		}
	}
}
