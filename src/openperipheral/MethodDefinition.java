package openperipheral;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;

import argo.jdom.JsonField;
import argo.jdom.JsonNode;
import argo.jdom.JsonStringNode;

public class MethodDefinition {

	private Method method;
	private String name;
	private Class clazz;
	public HashMap<Integer, String> replacements = new HashMap<Integer,String>();
	public MethodDefinition(Method method, JsonNode json) {
		
		this.name = method.getName();
		this.method = method;
		this.clazz = method.getDeclaringClass();
		
		List<JsonField> fields = json.getFieldList();
		for (JsonField jfield : fields) {
			if (jfield.getName().getText().equals("replacements")) {
				JsonNode replacementsJson = jfield.getValue();
				for (JsonField field : replacementsJson.getFieldList()) {
					replacements.put(Integer.parseInt(field.getName().getText()), field.getValue().getText());
				}
			}
		}
	}
	
	public HashMap<Integer, String> getReplacements() {
		return replacements;
	}
	
	public boolean isValidForClass(Class<? extends TileEntity> clazz2) {
		return this.clazz.isAssignableFrom(clazz2);
	}
	
	public String getName() {
		return name;
	}
	public Method getMethod() {
		return method;
	}
	
	public Class getDeclaringClass() {
		return this.clazz;
	}

}
