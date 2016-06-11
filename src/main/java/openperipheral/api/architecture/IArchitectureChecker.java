package openperipheral.api.architecture;

import java.util.Set;
import openperipheral.api.IApiInterface;

public interface IArchitectureChecker extends IApiInterface {

	public Set<String> knownArchitectures();

	public boolean isEnabled(String architecture);
}
