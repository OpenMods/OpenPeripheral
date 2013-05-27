package openperipheral.definition;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.tileentity.TileEntity;

import openperipheral.IRestriction;
import openperipheral.RestrictionFactory;

import argo.jdom.JsonField;
import argo.jdom.JsonNode;

public class DefinitionMethod {

	public enum CallType {
		METHOD,
		GET_PROPERTY,
		SET_PROPERTY
	}
	
	private String name;
	private String obfuscated;
	private String propertyName;
	private CallType callType = CallType.METHOD;
	
	private Field field = null;
	private Method method = null;
	
	private HashMap<Integer, ArrayList<IRestriction>> restrictions;
	
	public DefinitionMethod(Class klazz, JsonNode json) {
		
		restrictions = new HashMap<Integer, ArrayList<IRestriction>>();
		
		name = json.getStringValue("name");
		
		if (json.isNode("obfuscated")) {
			obfuscated = json.getStringValue("obfuscated");
		}
		
		if (json.isNode("propertyName")) {
			propertyName = json.getStringValue("propertyName");
		}
		
		if (json.isNode("callType")) {
			String _callType = json.getStringValue("callType");
			if (_callType == "method") {
				callType = CallType.METHOD;
			}else if (_callType == "get") {
				callType = CallType.GET_PROPERTY;
			}else if (_callType == "set") {
				callType = CallType.SET_PROPERTY;
			}
		}
		
		if (json.isNode("restrictions")) {
			
			for(JsonField restrictionField : json.getNode("restrictions").getFieldList()) {
				
				String stringParamId = restrictionField.getName().getText();
				JsonNode fields = restrictionField.getValue();

				int paramId = -1;
				try {
					paramId = Integer.parseInt(stringParamId);
				}catch(NumberFormatException e) { }
				
				if (paramId != -1) {
					
					ArrayList<IRestriction> paramRestrictions = new ArrayList<IRestriction>();
					
					for (JsonField field : fields.getFieldList()) {
						
						IRestriction restriction = RestrictionFactory.createFromJson(field);
						
						if (restriction != null) {
							paramRestrictions.add(restriction);
						}
						
					}
					
					if (paramRestrictions.size() > 0) {
						restrictions.put(paramId, paramRestrictions);
					}
					
				}
			}
		}
		
		if (callType == CallType.GET_PROPERTY || callType == CallType.SET_PROPERTY) {
			
			try {
				field = klazz.getDeclaredField(propertyName);
			} catch (Exception e) {
			}
			if (field == null && obfuscated != null) {
				try {
					field = klazz.getDeclaredField(obfuscated);
				} catch (Exception e) {
					
				}
			}
			
			if (field != null) {
				field.setAccessible(true);
			}
		}else {
			
			for (Method m : klazz.getDeclaredMethods()) {
				if (m.getName() == name || m.getName() == obfuscated) {
					method = m;
					break;
				}
			}
			
		}
	}
	
	public CallType getCallType() {
		return callType;
	}
	
	public Class[] getRequiredParameters() {
		if (callType == CallType.METHOD) {
			return method.getParameterTypes();
		}else if (callType == CallType.SET_PROPERTY) {
			return new Class[] { field.getType() };
		}
		return new Class[] { };
	}
	
	public String getLuaName() {
		return name;
	}

	public boolean isValid() {
		return field != null || method != null;
	}
	
	public ArrayList<IRestriction> getRestrictions(int index) {
		return restrictions.get(index);
	}

	public Method getMethod() {
		return method;
	}

	public Object execute(TileEntity tile, Object[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (callType == CallType.METHOD) {
			return method.invoke(tile, args);
		}else if (callType == CallType.GET_PROPERTY) {
			return field.get(tile);
		}else if (callType == CallType.SET_PROPERTY) {
			field.set(tile, args[0]);
			return true;
		}
		return null;
	}
}
