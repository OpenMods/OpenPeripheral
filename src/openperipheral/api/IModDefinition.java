package openperipheral.api;

import java.util.Map;

public interface IModDefinition {
	public String getModId();
	public Map<? extends Class, ? extends IClassDefinition> getValidClasses();
	
}
