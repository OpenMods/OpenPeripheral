package openperipheral;

import openmods.Log;
import openmods.Mods;
import openmods.access.ApiProviderBase;
import openperipheral.adapter.AdapterRegistryWrapper;
import openperipheral.adapter.TileEntityBlacklist;
import openperipheral.api.ApiAccess;
import openperipheral.api.IApiInterface;
import openperipheral.api.peripheral.PeripheralTypeProvider;
import openperipheral.converter.TypeConvertersProvider;
import openperipheral.interfaces.cc.ModuleComputerCraft;
import openperipheral.interfaces.oc.ModuleOpenComputers;
import openperipheral.meta.EntityMetadataBuilder;
import openperipheral.meta.ItemStackMetadataBuilder;

import com.google.common.base.Preconditions;

import cpw.mods.fml.common.Loader;

public class ApiProvider extends ApiProviderBase<IApiInterface> implements ApiAccess.ApiProvider {

	private ApiProvider() {
		registerClass(AdapterRegistryWrapper.Peripherals.class);
		registerClass(AdapterRegistryWrapper.Objects.class);
		registerClass(EntityMetadataBuilder.class);
		registerClass(ItemStackMetadataBuilder.class);

		registerInstance(TypeConvertersProvider.INSTANCE);
		registerInstance(TileEntityBlacklist.INSTANCE);
		registerInstance(PeripheralTypeProvider.INSTANCE);

		if (Loader.isModLoaded(Mods.COMPUTERCRAFT)) ModuleComputerCraft.installAPI(this);
		if (Loader.isModLoaded(Mods.OPENCOMPUTERS)) ModuleOpenComputers.installAPI(this);
	}

	static void installApi() {
		final String presentApiVersion;
		try {
			presentApiVersion = ApiAccess.API_VERSION;
		} catch (Throwable t) {
			throw new IllegalStateException("Failed to get OpenPeripheralCore API version, class missing?", t);
		}

		String apiSource;
		try {
			apiSource = ApiAccess.class.getProtectionDomain().getCodeSource().getLocation().toString();
		} catch (Throwable t) {
			apiSource = "<unknown, see logs>";
			Log.severe(t, "Failed to get OpenPeripheralCore API source");
		}

		Preconditions.checkState(OpenPeripheralCore.PROVIDED_API_VERSION.equals(presentApiVersion),
				"OpenPeripheralCore version mismatch, should be %s, is %s (ApiAccess source: %s)",
				OpenPeripheralCore.PROVIDED_API_VERSION, presentApiVersion, apiSource
				);

		try {
			ApiAccess.init(new ApiProvider());
		} catch (Throwable t) {
			throw new IllegalStateException(String.format("Failed to register OpenPeripheralCore API provider (ApiAccess source: %s)", apiSource), t);
		}

		Log.info("OPC API v. %s provideded by OpenPeripheralCore, (ApiAccess source: %s)", presentApiVersion, apiSource);
	}
}
