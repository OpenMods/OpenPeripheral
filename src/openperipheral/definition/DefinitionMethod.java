package openperipheral.definition;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.bouncycastle.util.encoders.Base64;

import cpw.mods.fml.common.ObfuscationReflectionHelper;

import net.minecraft.tileentity.TileEntity;
import openperipheral.IRestriction;
import openperipheral.RestrictionFactory;
import openperipheral.util.ReflectionHelper;
import argo.jdom.JsonField;
import argo.jdom.JsonNode;

public class DefinitionMethod {

	private ScriptEngineManager factory = new ScriptEngineManager();
	protected ScriptEngine engine = factory.getEngineByName("JavaScript");
	
	public enum CallType {
		METHOD,
		GET_PROPERTY,
		SET_PROPERTY,
		SCRIPT
	}
	
	private String name;
	private String obfuscated;
	private String propertyName;
	private String script = null;
	private String postscript = null;
	private CallType callType = CallType.METHOD;
	private boolean causeTileUpdate = false;
	
	private Field field = null;
	private Method method = null;
	private int argumentCount = -1;
	
	private HashMap<Integer, String> replacements;
	
	private HashMap<Integer, ArrayList<IRestriction>> restrictions;
	
	public DefinitionMethod(Class klazz, JsonNode json) {
		
		restrictions = new HashMap<Integer, ArrayList<IRestriction>>();
		replacements = new HashMap<Integer, String>();
		
		name = json.getStringValue("name");
		
		if (json.isNode("obfuscated")) {
			obfuscated = json.getStringValue("obfuscated");
		}
		
		if (json.isNode("script")) {
			script = new String(json.getStringValue("script"));
		}
		
		if (json.isNode("postscript")) {
			postscript = new String(json.getStringValue("postscript"));
		}
		
		if (json.isNode("propertyName")) {
			propertyName = json.getStringValue("propertyName");
		}
		
		if (json.isNode("argumentCount")) {
			argumentCount = Integer.parseInt(json.getNumberValue("argumentCount"));
		}
		
		if (json.isNode("replacements")) {
			for (JsonField replacementField : json.getNode("replacements").getFieldList()) {
				replacements.put(Integer.parseInt(replacementField.getName().getText()), replacementField.getValue().getText());
			}
		}
		
		if (json.isNode("callType")) {
			String _callType = json.getStringValue("callType");
			if (_callType.equals("method")) {
				callType = CallType.METHOD;
			}else if (_callType.equals("get")) {
				callType = CallType.GET_PROPERTY;
			}else if (_callType.equals("set")) {
				callType = CallType.SET_PROPERTY;
			}else if (_callType.equals("script")) {
				callType = CallType.SCRIPT;
			}
		}
		if (json.isNode("causeUpdate")) {
			if (json.getStringValue("causeUpdate").equals("true")) {
				causeTileUpdate = true;
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
			field = ReflectionHelper.getField(klazz, propertyName, obfuscated);
		}else if (callType == CallType.METHOD){
			method = ReflectionHelper.getMethod(klazz, new String[] { name,  obfuscated }, argumentCount);
		}
	}
	
	public boolean paramNeedsReplacing(int index) {
		return replacements != null && replacements.containsKey(index);
	}
	
	public CallType getCallType() {
		return callType;
	}
	
	public String getPropertyName() {
		return propertyName;
	}
	
	public HashMap<Integer, String> getReplacements() {
		return replacements;
	}
	
	public String getScript() {
		return script;
	}
	
	public String getPostScript() {
		return postscript;
	}
	
	public boolean getCauseTileUpdate() {
		return causeTileUpdate;
	}
	
	public Class getReturnType() {
		if (getCallType() == CallType.METHOD) {
			return method.getReturnType();
		}else if (getCallType() == CallType.GET_PROPERTY) {
			return field.getType();
		}
		return Void.class;
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
		return field != null || method != null || callType == CallType.SCRIPT;
	}
	
	public ArrayList<IRestriction> getRestrictions(int index) {
		return restrictions.get(index);
	}

	public Method getMethod() {
		return method;
	}

	public Object execute(TileEntity tile, Object[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (callType == CallType.SCRIPT) {
			return executeScript(tile, args);
		}else if (callType == CallType.METHOD) {
			return method.invoke(tile, args);
		}else if (callType == CallType.GET_PROPERTY) {
			return field.get(tile);
		}else if (callType == CallType.SET_PROPERTY) {
			field.set(tile, args[0]);
			return true;
		}
		return null;
	}

	private Object executeScript(TileEntity tile, Object[] args) {

		String script = this.getScript();
		if (script != null) {
			script = new String(Base64.decode(script));
			try {
				this.engine.put("tile", tile);
				this.engine.put("xCoord", tile.xCoord);
				this.engine.put("yCoord", tile.yCoord);
				this.engine.put("zCoord", tile.zCoord);
				this.engine.put("values", args);
				this.engine.put("worldObj", tile.worldObj);
				this.engine.put("env", this);
				return this.engine.eval(script);
			} catch (ScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
}
