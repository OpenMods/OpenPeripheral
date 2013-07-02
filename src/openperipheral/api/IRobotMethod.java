package openperipheral.api;

import java.util.ArrayList;

public interface IRobotMethod {

	/**
	 * Do the values being passed into lua need to be sanatized?
	 * 
	 * @return
	 */
	public boolean needsSanitize();

	/**
	 * Parameter validation. Return null if you don't want to validate
	 * parameter types and argument count is automatically validated based
	 * on getRequiredParameters()
	 * 
	 * @param index
	 * @return
	 */
	public ArrayList<IRestriction> getRestrictions(int index);

	/**
	 * Get the name used in lua
	 * 
	 * @return
	 */
	public String getLuaName();

	/**
	 * Is this method instant? Only return true if you KNOW your method is
	 * thread safe If false, it'll wait until the next world tick before it
	 * executes
	 * 
	 * @return
	 */
	public boolean isInstant();

	/**
	 * Get the required parameters
	 * 
	 * @return
	 */
	public Class[] getRequiredParameters();

	/**
	 * Execute the method. This will only fire if everything else is valid To
	 * raise a lua error throw an exception
	 * 
	 * @param target
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public Object execute(IRobotUpgradeInstance instance,  Object[] args) throws Exception;
}
