package openperipheral.adapter;

import java.util.Set;

import openmods.Log;
import openmods.access.ApiSingleton;
import openmods.config.properties.ConfigurationChange;
import openperipheral.Config;
import openperipheral.api.peripheral.IPeripheralBlacklist;
import openperipheral.api.peripheral.Ignore;

import com.google.common.collect.Sets;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@ApiSingleton
public class TileEntityBlacklist implements IPeripheralBlacklist {

	public static final TileEntityBlacklist INSTANCE = new TileEntityBlacklist();

	private final Set<String> imcBlacklist = Sets.newHashSet();

	private Set<String> fullBlacklist = Sets.newHashSet();

	@SubscribeEvent
	public void onConfigChange(ConfigurationChange evt) {
		if (evt.check("integration", "disableClasses")) {
			fullBlacklist = Sets.newHashSet(imcBlacklist);
			for (String cls : Config.teBlacklist)
				fullBlacklist.add(cls.toLowerCase());
		}
	}

	@Override
	public boolean isBlacklisted(Class<?> teClass) {
		final String teClassName = teClass.getName().toLowerCase();
		if (fullBlacklist.contains(teClassName)) return true;

		if (teClass.isAnnotationPresent(Ignore.class)) {
			fullBlacklist.add(teClassName);
			return true;
		}

		try {
			teClass.getField("OPENPERIPHERAL_IGNORE");
			fullBlacklist.add(teClassName);
			return true;
		} catch (NoSuchFieldException e) {
			// uff, we are not ignored
		} catch (Throwable t) {
			Log.warn(t, "Class %s doesn't cooperate", teClass);
		}

		return false;
	}

	@Override
	public void addToBlacklist(String className) {
		imcBlacklist.add(className.toLowerCase());
	}

	@Override
	public void addToBlacklist(Class<?> cls) {
		addToBlacklist(cls.getName());
	}
}
