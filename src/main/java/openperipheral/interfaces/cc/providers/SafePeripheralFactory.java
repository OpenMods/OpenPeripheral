package openperipheral.interfaces.cc.providers;

import java.util.Random;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import openmods.Log;
import openperipheral.adapter.composed.ComposedMethodsFactory.InvalidClassException;
import openperipheral.api.peripheral.IBrokenOpenPeripheral;

import org.apache.commons.lang3.ArrayUtils;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

abstract class SafePeripheralFactory implements IPeripheralFactory<TileEntity> {

	private static final Random RANDOM = new Random();

	private static final String[] BOGUS_METODS = new String[] {
			"help",
			"whats_going_on",
			"wtf",
			"lol_nope",
			"derp",
			"guru_meditation",
			"woof",
			"nothing_to_see_here",
			"kernel_panic",
			"hello_segfault",
			"i_see_dead_bytes",
			"xyzzy",
			"abort_retry_fail_continue"
	};

	private static class BrokenPeripheral implements IPeripheral, IBrokenOpenPeripheral {
		@Override
		public String getType() {
			return "broken_peripheral";
		}

		@Override
		public String[] getMethodNames() {
			return ArrayUtils.toArray(BOGUS_METODS[RANDOM.nextInt(BOGUS_METODS.length)]);
		}

		@Override
		public void detach(IComputerAccess computer) {}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) {
			return ArrayUtils.toArray("This peripheral is broken. You can show your log in #OpenMods");
		}

		@Override
		public void attach(IComputerAccess computer) {}

		@Override
		public boolean equals(IPeripheral other) {
			return other == this;
		}
	}

	private static final IPeripheral PLACEHOLDER = new BrokenPeripheral();

	public static final IPeripheralFactory<TileEntity> BROKEN_FACTORY = new IPeripheralFactory<TileEntity>() {
		@Override
		public IPeripheral getPeripheral(TileEntity obj, EnumFacing side) {
			return PLACEHOLDER;
		}
	};

	@Override
	public IPeripheral getPeripheral(TileEntity tile, EnumFacing side) {
		if (tile == null) return null;

		try {
			return createPeripheral(tile, side);
		} catch (InvalidClassException e) {
			Throwable cause = e.getCause();
			if (cause != null) {
				Log.severe(cause, "Can't create peripheral for TE %s @ (%s) in world %s due to error in class",
						tile.getClass(), tile.getPos(), tile.getWorld().provider.getDimensionId());
			} else {
				Log.severe("Can't create peripheral for TE %s @ (%s) in world %s due to error in class %s",
						tile.getClass(), tile.getPos(), tile.getWorld().provider.getDimensionId(), tile.getClass());
			}

		} catch (Throwable t) {
			Log.severe(t, "Can't create peripheral for TE %s @ (%s) in world %s",
					tile.getClass(), tile.getPos(), tile.getWorld().provider.getDimensionId());

		}
		return PLACEHOLDER;
	}

	protected abstract IPeripheral createPeripheral(TileEntity tile, EnumFacing side) throws Exception;

}