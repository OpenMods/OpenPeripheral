package openperipheral.common.robotupgrades.sensors;

import java.util.ArrayList;

import openperipheral.api.IRestriction;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.common.peripheral.SensorPeripheral;

public abstract class MethodSensorProxy implements IRobotMethod {

	private String luaName;
	private Class[] required;
	
	public MethodSensorProxy(String luaName, Class ... required) {
		this.luaName = luaName;
		this.required = required;
	}
	
	@Override
	public boolean needsSanitize() {
		return true;
	}

	@Override
	public ArrayList<IRestriction> getRestrictions(int index) {
		return null;
	}

	@Override
	public String getLuaName() {
		return luaName;
	}

	@Override
	public boolean isInstant() {
		return true;
	}

	@Override
	public Class[] getRequiredParameters() {
		return required;
	}

	public abstract Object executeOnSensor(SensorPeripheral sensor, Object[] args) throws Exception;
	
	@Override
	public Object execute(IRobotUpgradeInstance instance, Object[] args) throws Exception {
		return executeOnSensor(((InstanceSensorUpgrade) instance).getSensor(), args);
	}

}
