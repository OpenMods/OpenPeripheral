package forceloader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "ForceLoader", name = "ForceLoader", version = "0.0.1", dependencies = "required-after:IC2;required-after:Forestry;required-before:mmmPowersuits")
@NetworkMod(serverSideRequired = true)
public class ForceLoader {

	@Instance(value = "ForceLoader")
	public static ForceLoader instance;
}
