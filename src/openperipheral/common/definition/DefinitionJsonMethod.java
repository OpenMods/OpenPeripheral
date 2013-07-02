package openperipheral.common.definition;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IRestriction;
import openperipheral.common.interfaces.IPeripheralMethodDefinition;
import openperipheral.common.restriction.RestrictionFactory;
import openperipheral.common.util.ReflectionHelper;

import org.bouncycastle.util.encoders.Base64;

import argo.jdom.JsonField;
import argo.jdom.JsonNode;

public class DefinitionJsonMethod implements IPeripheralMethodDefinition {

	private ScriptEngineManager factory = new ScriptEngineManager();
	protected ScriptEngine engine = factory.getEngineByName("JavaScript");

	public enum CallType {
		METHOD, GET_PROPERTY, SET_PROPERTY, SCRIPT
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

	private boolean isInstant = false;

	private HashMap<Integer, String> replacements;

	private HashMap<Integer, ArrayList<IRestriction>> restrictions;

	public DefinitionJsonMethod(Class klazz, JsonNode json) {

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
			} else if (_callType.equals("get")) {
				callType = CallType.GET_PROPERTY;
			} else if (_callType.equals("set")) {
				callType = CallType.SET_PROPERTY;
			} else if (_callType.equals("script")) {
				callType = CallType.SCRIPT;
			}
		}

		if (json.isNode("causeUpdate")) {
			if (json.getStringValue("causeUpdate").equals("true")) {
				causeTileUpdate = true;
			}
		}

		if (json.isNode("instant")) {
			if (json.getStringValue("instant").equals("true")) {
				isInstant = true;
			}
		}

		if (json.isNode("restrictions")) {

			for (JsonField restrictionField : json.getNode("restrictions").getFieldList()) {

				String stringParamId = restrictionField.getName().getText();
				JsonNode fields = restrictionField.getValue();

				int paramId = -1;
				try {
					paramId = Integer.parseInt(stringParamId);
				} catch (NumberFormatException e) {
				}

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
		} else if (callType == CallType.METHOD) {
			method = ReflectionHelper.getMethod(klazz, new String[] { name, obfuscated }, argumentCount);
		}
	}

	public CallType getCallType() {
		return callType;
	}

	public String getPropertyName() {
		return propertyName;
	}

	@Override
	public HashMap<Integer, String> getReplacements() {
		return replacements;
	}

	public String getScript() {
		return script;
	}

	@Override
	public String getPostScript() {
		return postscript;
	}

	@Override
	public boolean getCauseTileUpdate() {
		return causeTileUpdate;
	}

	@Override
	public Class[] getRequiredParameters() {
		if (callType == CallType.METHOD) {
			return method.getParameterTypes();
		} else if (callType == CallType.SET_PROPERTY) {
			return new Class[] { field.getType() };
		}
		return new Class[] {};
	}

	@Override
	public boolean isInstant() {
		return isInstant;
	}

	@Override
	public String getLuaName() {
		return name;
	}

	@Override
	public boolean isValid() {
		return field != null || method != null || callType == CallType.SCRIPT;
	}

	@Override
	public ArrayList<IRestriction> getRestrictions(int index) {
		return restrictions.get(index);
	}

	@Override
	public Object execute(Object target, Object[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (callType == CallType.METHOD) {
			return method.invoke(target, args);
		} else if (callType == CallType.GET_PROPERTY) {
			return field.get(target);
		} else if (callType == CallType.SET_PROPERTY) {
			field.set(target, args[0]);
			return true;
		}
		return null;
	}


	@Override
	public boolean needsSanitize() {
		return getCallType() != CallType.SCRIPT;
	}

}
