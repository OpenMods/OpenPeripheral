package openperipheral.core.interfaces;

import java.util.Map;

import openperipheral.api.IClassDefinition;

public interface IModDefinition {
	public String getModId();
	public Map<? extends Class, ? extends IClassDefinition> getValidClasses();
	
}
