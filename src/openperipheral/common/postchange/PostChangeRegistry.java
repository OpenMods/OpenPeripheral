package openperipheral.common.postchange;

import java.util.ArrayList;

import openperipheral.common.interfaces.IPeripheralMethodDefinition;
import openperipheral.common.interfaces.IPostChangeHandler;

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
