package openperipheral.common.integration.sgcraft.method;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IRestriction;
import openperipheral.common.interfaces.IPeripheralMethodDefinition;
import openperipheral.common.util.ReflectionHelper;
import openperipheral.common.util.ReflectionWrapper;

public class SGCraftDisconnect implements IPeripheralMethodDefinition {

	@Override
	public HashMap<Integer, String> getReplacements() {
		return null;
	}

	@Override
	public String getPostScript() {
		return null;
	}

	@Override
	public boolean getCauseTileUpdate() {
		return false;
	}

	@Override
	public Class[] getRequiredParameters() {
		return null;
	}

	@Override
	public boolean isInstant() {
		return false;
	}

	@Override
	public String getLuaName() {
		return "disconnect";
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public boolean needsSanitize() {
		return false;
	}

	@Override
	public ArrayList<IRestriction> getRestrictions(int index) {
		return null;
	}

	@Override
	public Object execute(Object tile, Object[] args) throws Exception {
		ReflectionWrapper tileWrapped = new ReflectionWrapper(tile);
		Object connectedLocation = tileWrapped.get("connectedLocation").getRaw();
		Object localSGBaseTE = ReflectionHelper.callMethod("gcewing.sg.SGBaseTE", null, new String[] { "at" }, connectedLocation);
		if (localSGBaseTE != null) {
		   ReflectionHelper.callMethod("", localSGBaseTE, new String[] { "clearConnection" });
		}
		tileWrapped.call(new String[] { "clearConnection" });
		return true;
	}

}
