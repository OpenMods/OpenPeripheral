package openperipheral;

import java.lang.reflect.Field;
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
	private Field field;
	private String name;
	private Class clazz;
	private boolean isGet;
	private boolean isMethod = true;
	public HashMap<Integer, String> replacements = new HashMap<Integer,String>();
	public MethodDefinition(String name, Method method, JsonNode json) {
		
		this.name = name;
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
	
	public MethodDefinition(String name, Field field, boolean isGet) {
		this.name = name;
		this.field = field;
		this.isGet = isGet;
		this.clazz = field.getDeclaringClass();
		isMethod = false;
	}
	
	public boolean isMethod() {
		return isMethod;
	}
	
	public Field getField() {
		return field;
	}
	
	public boolean isGet() {
		return isGet;
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
