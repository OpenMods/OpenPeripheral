package cofh.util;

import net.minecraft.world.World;

public final class ServerHelper {

	private ServerHelper() {

	}

	public static final boolean isClientWorld(World world) {

		return world.isRemote;
	}

	public static final boolean isServerWorld(World world) {

		return !world.isRemote;
	}

}
