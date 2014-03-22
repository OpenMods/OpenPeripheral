package openperipheral.adapter;

import java.util.Random;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmods.Log;

import org.apache.commons.lang3.ArrayUtils;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;




abstract class SafePeripheralHandler implements IPeripheralProvider {
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


	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		if (world == null) return null;
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		return getPeripheralFromTile(tile);
	}
	
	public IPeripheral getPeripheralFromTile(TileEntity tile) {

		if (tile == null) return null;

		try {
			return createPeripheral(tile);
		} catch (Throwable t) {
			Log.severe(t, "Can't create peripheral for TE %s @ (%d,%d,%d) in world %s",
					tile.getClass(), tile.xCoord, tile.yCoord, tile.zCoord, tile.worldObj.provider.dimensionId);
			return PlaceholderPeripheral.getInstance();
		}
	}

	protected abstract IPeripheral createPeripheral(TileEntity tile);

	public static final class PlaceholderPeripheral implements IPeripheral{

		private static final PlaceholderPeripheral instance = new PlaceholderPeripheral();
		
		public static PlaceholderPeripheral getInstance() {
			return instance;
		}

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
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
			return ArrayUtils.toArray("This peripheral is broken. You can show your log in #OpenMods");
		}

		@Override
		public void attach(IComputerAccess computer) {}

		@Override
		public boolean equals(IPeripheral other) {
			// TODO Auto-generated method stub
			return other!=null && other instanceof PlaceholderPeripheral;
		}

	}
}