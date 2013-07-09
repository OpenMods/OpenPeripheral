package openperipheral.core.interfaces;

import java.util.Map;

import openperipheral.api.IPeripheralAdapter;

public interface IModDefinition {
	public String getModId();
	public Map<? extends Class, ? extends IPeripheralAdapter> getValidClasses();
	
}
