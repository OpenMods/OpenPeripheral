package openperipheral;

public class ModInfo {
	public static final String ID = "OpenPeripheralCore";
	public static final String API_ID = "OpenPeripheralApi";
	public static final String NAME = "OpenPeripheralCore";
	public static final String VERSION = "$VERSION$";
	public static final String API_VERSION = "$OP-API-VERSION$";
	public static final String PROXY_SERVER = "openperipheral.core.CommonProxy";
	public static final String PROXY_CLIENT = "openperipheral.core.client.ClientProxy";
	public static final String DEPENDENCIES = "required-after:OpenMods@[$LIB-VERSION$,$NEXT-LIB-VERSION$);after:ComputerCraft@[1.70,];after:OpenComputers@[1.5.0,];";
}
