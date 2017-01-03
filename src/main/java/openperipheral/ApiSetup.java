package openperipheral;

import com.google.common.base.Preconditions;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import openmods.Log;
import openmods.access.ApiFactory;
import openmods.access.ApiProviderBase;
import openmods.access.ApiProviderRegistry;
import openperipheral.adapter.AdapterRegistryWrapper;
import openperipheral.adapter.FeatureGroupManager;
import openperipheral.adapter.PeripheralTypeProvider;
import openperipheral.adapter.TileEntityBlacklist;
import openperipheral.adapter.types.classifier.TypeClassifier;
import openperipheral.api.ApiHolder;
import openperipheral.api.Constants;
import openperipheral.api.IApiInterface;
import openperipheral.converter.TypeConvertersProvider;
import openperipheral.interfaces.oc.ModuleOpenComputers;
import openperipheral.meta.EntityMetadataBuilder;
import openperipheral.meta.ItemStackMetadataBuilder;

public class ApiSetup {

	private final ApiProviderRegistry<IApiInterface> registry = new ApiProviderRegistry<IApiInterface>(IApiInterface.class);

	ApiSetup() {}

	public void setupApis() {
		registry.registerClass(AdapterRegistryWrapper.Peripherals.class);
		registry.registerClass(AdapterRegistryWrapper.Objects.class);
		registry.registerClass(EntityMetadataBuilder.class);
		registry.registerClass(ItemStackMetadataBuilder.class);

		registry.registerInstance(TypeConvertersProvider.INSTANCE);
		registry.registerInstance(TileEntityBlacklist.INSTANCE);
		registry.registerInstance(PeripheralTypeProvider.INSTANCE);
		registry.registerInstance(TypeClassifier.INSTANCE);
		registry.registerInstance(ArchitectureChecker.INSTANCE);
		registry.registerInstance(FeatureGroupManager.INSTANCE);

		// if (ArchitectureChecker.INSTANCE.isEnabled(Constants.ARCH_COMPUTER_CRAFT)) ModuleComputerCraft.installAPI(registry);
		if (ArchitectureChecker.INSTANCE.isEnabled(Constants.ARCH_OPEN_COMPUTERS)) ModuleOpenComputers.installAPI(registry);

		registry.freeze();
	}

	public void installHolderAccess(ASMDataTable table) {
		ApiFactory.instance.createApi(ApiHolder.class, IApiInterface.class, table, registry);
	}

	private static class LegacyApiAccess extends ApiProviderBase<IApiInterface> implements openperipheral.api.ApiAccess.ApiProvider {
		public LegacyApiAccess(ApiProviderRegistry<IApiInterface> apiRegistry) {
			super(apiRegistry);
		}
	}

	void installProviderAccess() {
		final String presentApiVersion;
		try {
			presentApiVersion = openperipheral.api.ApiAccess.API_VERSION;
		} catch (Throwable t) {
			throw new IllegalStateException("Failed to get OpenPeripheralCore API version, class missing?", t);
		}

		String apiSource;
		try {
			apiSource = openperipheral.api.ApiAccess.class.getProtectionDomain().getCodeSource().getLocation().toString();
		} catch (Throwable t) {
			apiSource = "<unknown, see logs>";
			Log.severe(t, "Failed to get OpenPeripheralCore API source");
		}

		Preconditions.checkState(ModInfo.API_VERSION.equals(presentApiVersion),
				"OpenPeripheralCore version mismatch, should be %s, is %s (ApiAccess source: %s)",
				ModInfo.API_VERSION, presentApiVersion, apiSource);

		try {
			openperipheral.api.ApiAccess.init(new LegacyApiAccess(registry));
		} catch (Throwable t) {
			throw new IllegalStateException(String.format("Failed to register OpenPeripheralCore API provider (ApiAccess source: %s)", apiSource), t);
		}

		Log.info("OPC API v. %s provideded by OpenPeripheralCore, (ApiAccess source: %s)", presentApiVersion, apiSource);
	}

}
