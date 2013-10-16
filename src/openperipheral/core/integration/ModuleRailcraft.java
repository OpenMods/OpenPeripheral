package openperipheral.core.integration;

import java.util.HashMap;

import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.carts.IEnergyTransfer;
import mods.railcraft.api.carts.IExplosiveCart;
import mods.railcraft.api.carts.ILinkableCart;
import mods.railcraft.api.carts.ILiquidTransfer;
import mods.railcraft.api.carts.IPaintedCart;
import mods.railcraft.api.carts.IRefuelableCart;
import mods.railcraft.api.carts.IRoutableCart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.Vec3;
import openperipheral.core.AdapterManager;
import openperipheral.core.adapter.AdapterTicketMachine;

public class ModuleRailcraft {

	public static void init() {
		AdapterManager.addPeripheralAdapter(new AdapterTicketMachine());
	}

	public static void entityToMap(Entity entity, HashMap map, Vec3 relativePos) {
		// Batbox, MFE, and MFSU carts
		if (entity instanceof IEnergyTransfer) {
			IEnergyTransfer cart = (IEnergyTransfer)entity;
			map.put("maxPower", cart.getCapacity());
			map.put("currentPower", cart.getEnergy());
			map.put("powerTier", cart.getTier());
			map.put("transferRate", cart.getTransferLimit());
		}
		
		if (entity instanceof IExplosiveCart) {
			IExplosiveCart cart = (IExplosiveCart)entity;
			map.put("primed", cart.isPrimed());
			map.put("fuse", cart.getFuse());
		}
		
		if (entity instanceof ILinkableCart) {
			ILinkableCart cart = (ILinkableCart)entity;
			EntityMinecart minecart = (EntityMinecart)entity;
			boolean linkable = cart.isLinkable();
			map.put("linkable", linkable);
			if (linkable) {
				int cartCount = CartTools.linkageManager.countCartsInTrain(minecart);
				map.put("cartsInTrain", cartCount);
			}
			boolean hasOwner = CartTools.doesCartHaveOwner(minecart);
			map.put("hasOwner", hasOwner);
			if (hasOwner) {
				map.put("owner", CartTools.getCartOwner(minecart));
			}
		}
		
		// tank carts
		if (entity instanceof ILiquidTransfer) {
			ILiquidTransfer cart = (ILiquidTransfer)entity;
			map.put("isFilling", cart.isFilling());
		}
		
		if (entity instanceof IPaintedCart) {
			IPaintedCart cart = (IPaintedCart)entity;
			map.put("primaryColor", cart.getPrimaryColor());
			map.put("secondaryColor", cart.getSecondaryColor());
		}
		
		if (entity instanceof IRefuelableCart) {
			IRefuelableCart cart = (IRefuelableCart)entity;
			map.put("needsRefuel", cart.needsRefuel());
		}
		
		if (entity instanceof IRoutableCart) {
			IRoutableCart cart = (IRoutableCart)entity;
			map.put("destination", cart.getDestination());
		}
	}
}
