package openperipheral.api;

public interface ILazerRobot extends IRobot {
	public float getWeaponSpinSpeed();
	public void setWeaponSpinSpeed(float speed);
	public void modifyWeaponSpinSpeed(float speed);
}
