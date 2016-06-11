package openperipheral.api.architecture;

import java.util.Set;
import openperipheral.api.IApiInterface;

public interface IFeatureGroupManager extends IApiInterface {

	public Set<String> knownFeatureGroups();

	public void disable(String featureGroup, String architecture);

	public void enable(String featureGroup, String architecture);

	public boolean isEnabled(String featureGroup, String architecture);

}
