package openperipheral.integration.mystcraft;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import openmods.utils.BlockUtils;
import openmods.utils.InventoryUtils;
import openmods.utils.ReflectionHelper;
import openperipheral.api.*;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import dan200.computer.api.IComputerAccess;

public class AdapterWritingDesk implements IPeripheralAdapter {
	private static final Class<?> DESK_CLASS = ReflectionHelper.getClass("com.xcompwiz.mystcraft.tileentity.TileEntityDesk");

	@Override
	public Class<?> getTargetClass() {
		return DESK_CLASS;
	}

	@LuaMethod(description = "Get the maximum number of notebooks this desk can store", returnType = LuaType.NUMBER)
	public int getMaxNotebookCount(IComputerAccess computer, Object tileEntityDesk) {
		return ReflectionHelper.call(tileEntityDesk, "getMaxNotebookCount");
	}

	@LuaMethod(description = "Get the name of a notebook", returnType = LuaType.STRING,
			args = {
					@Arg(name = "slot", type = LuaType.NUMBER, description = "The writing desk slot you are interested in")
			}
			)
			public String getNotebookName(IComputerAccess computer, Object desk, int deskSlot) {
		return createInventoryWrapper(desk, deskSlot).getInvName();
	}

	@LuaMethod(description = "Get the number of pages in a notebook", returnType = LuaType.NUMBER,
			args = {
					@Arg(name = "slot", type = LuaType.NUMBER, description = "The writing desk slot you are interested in")
			}
			)
			public Integer getNotebookSize(IComputerAccess computer, Object desk, int deskSlot) {
		return createInventoryWrapper(desk, deskSlot).callOnNotebook("getItemCount");
	}

	@LuaMethod(description = "Get the contents of a slot in a notebook", returnType = LuaType.NUMBER,
			args = {
					@Arg(name = "deskSlot", type = LuaType.NUMBER, description = "The writing desk slot you are interested in"),
					@Arg(name = "notebookSlot", type = LuaType.NUMBER, description = "The notebook slot you are interested in")
			}
			)
			public ItemStack getNotebookStackInSlot(IComputerAccess computer, Object desk, int deskSlot, int notebookSlot) {
		return createInventoryWrapper(desk, deskSlot).getStackInSlot(notebookSlot - 1);
	}

	@LuaMethod(description = "Get the last slot index in a notebook", returnType = LuaType.NUMBER,
			args = {
					@Arg(name = "slot", type = LuaType.NUMBER, description = "The writing desk slot you are interested in")
			}
			)
			public Integer getLastNotebookSlot(IComputerAccess computer, Object desk, int deskSlot) {
		return createInventoryWrapper(desk, deskSlot).getSizeInventory() - 1;
	}

	@LuaMethod(description = "Swap notebook slots", returnType = LuaType.VOID,
			args = {
					@Arg(name = "deskSlot", type = LuaType.NUMBER, description = "The writing desk slot you are interested in"),
					@Arg(type = LuaType.NUMBER, name = "from", description = "The first slot"),
					@Arg(type = LuaType.NUMBER, name = "to", description = "The other slot")
			}
			)
			public void swapNotebookPages(IComputerAccess computer, Object desk, int deskSlot, int from, int to) {
		InventoryUtils.swapStacks(createInventoryWrapper(desk, deskSlot), from - 1, to - 1);
	}

	@Freeform
	@LuaCallable(
			returnTypes = LuaType.NUMBER,
			description = "Push a page from the notebook into a specific slot in external inventory. Returns the amount of items moved")
	public int pushNotebookPage(
			@Named("target") Object desk,
			@Arg(type = LuaType.NUMBER, name = "deskSlot", description = "The notebook slot you are interested in") int deskSlot,
			@Arg(type = LuaType.STRING, name = "direction", description = "The direction of the other inventory. (north, south, east, west, up or down)") ForgeDirection direction,
			@Arg(type = LuaType.NUMBER, name = "fromSlot", description = "The page slot in inventory that you're pushing from") int fromSlot,
			@Optionals @Arg(type = LuaType.NUMBER, name = "intoSlot", description = "The slot in the other inventory that you want to push into") Integer intoSlot) {
		IInventory source = createInventoryWrapper(desk, deskSlot);
		IInventory target = getTargetTile(desk, direction);

		return InventoryUtils.moveItemInto(source, fromSlot - 1, target, Objects.firstNonNull(intoSlot, 0) - 1, 64, direction.getOpposite(), true);
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Pull an item from the target inventory into any slot in the current one. Returns the amount of items moved",
			args = {
					@Arg(type = LuaType.NUMBER, name = "deskSlot", description = "The writing desk slot you are interested in"),
					@Arg(type = LuaType.STRING, name = "direction", description = "The direction of the other inventory. (north, south, east, west, up or down)"),
					@Arg(type = LuaType.NUMBER, name = "fromSlot", description = "The slot in the other inventory that you're pulling from")
			})
	public int pullNotebookPage(IComputerAccess computer, Object desk, int deskSlot, ForgeDirection direction, int notebookSlot) {
		IInventory source = getTargetTile(desk, direction);
		IInventory target = createInventoryWrapper(desk, deskSlot);
		return InventoryUtils.moveItemInto(source, notebookSlot - 1, target, -1, 1, direction.getOpposite(), true, false);
	}

	private static IInventory getTargetTile(Object target, ForgeDirection direction) {
		Preconditions.checkArgument(direction != ForgeDirection.UNKNOWN, "Invalid direction");
		Preconditions.checkArgument(target instanceof TileEntity);
		TileEntity targetTile = BlockUtils.getTileInDirection((TileEntity)target, direction);
		Preconditions.checkArgument(targetTile instanceof IInventory, "Target direction is not a valid inventory");
		return (IInventory)targetTile;
	}

	private static NotebookIInventoryWrapper createInventoryWrapper(Object tile, int number) {
		ItemStack notebook = ReflectionHelper.call(DESK_CLASS, tile, "getNotebook", ReflectionHelper.primitive((byte)(number - 1)));
		return new NotebookIInventoryWrapper(notebook);
	}
}
