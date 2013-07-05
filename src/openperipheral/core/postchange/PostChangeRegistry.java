package openperipheral.core.postchange;

import java.util.ArrayList;

import openperipheral.core.interfaces.IPeripheralMethodDefinition;
import openperipheral.core.interfaces.IPostChangeHandler;

public class PostChangeRegistry {

	private static ArrayList<IPostChangeHandler> changeHandlers = new ArrayList<IPostChangeHandler>();

	public static void registerChangeHandler(IPostChangeHandler handler) {
		changeHandlers.add(handler);
	}

	public static void onPostChange(Object target, IPeripheralMethodDefinition luaMethod, Object[] values) {
		for (IPostChangeHandler handler : changeHandlers) {
			handler.execute(target, luaMethod, values);
		}
	}
}
