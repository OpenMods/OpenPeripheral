package openperipheral.adapter.mystcraft;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import openmods.utils.BlockUtils;
import openmods.utils.InventoryUtils;
import openperipheral.adapter.vanilla.AdapterInventory;
import openperipheral.api.Arg;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import openperipheral.util.CallWrapper;
import openperipheral.util.ReflectionHelper;
import dan200.computer.api.IComputerAccess;

public class AdapterWritingDesk implements IPeripheralAdapter {
	private static final Class<?> DESK_CLAZZ = ReflectionHelper.getClass("com.xcompwiz.mystcraft.tileentity.TileEntityDesk");

	@Override
	public Class<?> getTargetClass() {
		return DESK_CLAZZ;
	}

	@LuaMethod(description = "Get the maximum number of notebooks this desk can store", returnType = LuaType.NUMBER)
	public int getMaxNotebookCount(IComputerAccess computer, Object tileEntityDesk) {
		return new CallWrapper<Integer>().call(tileEntityDesk, "getMaxNotebookCount");
	}


	@LuaMethod(description = "Get the name of a notebook", returnType = LuaType.STRING, 
			args={
			@Arg(name = "slot", type = LuaType.NUMBER, description = "The writing desk slot you are interested in")
	}
			)
	public String getNotebookName(IComputerAccess computer, Object tileEntityDesk, int number) throws Exception {
		NotebookWrapper notebookWrapper = new NotebookWrapper(tileEntityDesk, number);
		return notebookWrapper.<String>callStatic("getName"); // Yes, this is actual valid syntax. I'm sorry. 
	}

	@LuaMethod(description = "Get the number of pages in a notebook", returnType = LuaType.NUMBER, 
			args={
			@Arg(name = "slot", type = LuaType.NUMBER, description = "The writing desk slot you are interested in")
	}
			)
	public Integer getNotebookSize(IComputerAccess computer, Object tileEntityDesk, int number) throws Exception {
		NotebookWrapper notebookWrapper = new NotebookWrapper(tileEntityDesk, number);
		return notebookWrapper.<Integer>callStatic("getItemCount");
	}

	@LuaMethod(description = "Get the contents of a slot in a notebook", returnType = LuaType.NUMBER, 
			args={
			@Arg(name = "deskSlot", type = LuaType.NUMBER, description = "The writing desk slot you are interested in"),
			@Arg(name = "notebookSlot", type = LuaType.NUMBER, description = "The notebook slot you are interested in")
	}
			)
	public ItemStack getNotebookStackInSlot(IComputerAccess computer, Object tileEntityDesk, int deskSlot, int notebookSlot) throws Exception {
		NotebookWrapper notebookWrapper = new NotebookWrapper(tileEntityDesk, deskSlot);
		return notebookWrapper.<ItemStack>callStatic("getItem",notebookSlot - 1); 
	}

	@LuaMethod(description = "Get the last slot index in a notebook", returnType = LuaType.NUMBER, 
			args={
			@Arg(name = "slot", type = LuaType.NUMBER, description = "The writing desk slot you are interested in")
	}
			)
	public Integer getLastNotebookSlot(IComputerAccess computer, Object tileEntityDesk, int number) throws Exception {
		NotebookWrapper notebookWrapper = new NotebookWrapper(tileEntityDesk, number);
		return notebookWrapper.<Integer>callStatic("getLargestSlotId")+1;  
	}

	@LuaMethod(description = "Swap notebook slots", returnType = LuaType.BOOLEAN, 
			args={
			@Arg(name = "deskSlot", type = LuaType.NUMBER, description = "The writing desk slot you are interested in"),
			@Arg(type = LuaType.NUMBER, name = "from", description = "The first slot"),
			@Arg(type = LuaType.NUMBER, name = "to", description = "The other slot")
	}
			)
	public boolean swapNotebookPages(IComputerAccess computer, Object tileEntityDesk, int deskSlot, int from, int to) throws Exception {
		NotebookWrapper notebookWrapper = new NotebookWrapper(deskSlot, deskSlot);
		return new AdapterInventory().swapStacks(computer, notebookWrapper.getinventoryWrapper(), from, to);
	}


	@LuaMethod(
			returnType = LuaType.NUMBER,
			description = "Push a page from the current inventory into a specific slot in the other one. Returns the amount of items moved",
			args = {
					@Arg(type = LuaType.NUMBER, name = "deskSlot", description = "The writing desk slot you are interested in"),
					@Arg(type = LuaType.STRING, name = "direction", description = "The direction of the other inventory. (north, south, east, west, up or down)"),
					@Arg(type = LuaType.NUMBER, name = "fromSlot", description = "The slot in the current inventory that you're pushing from"),
					@Arg(type = LuaType.NUMBER, name = "maxAmount", description = "The maximum amount of items you want to push"),
					@Arg(type = LuaType.NUMBER, name = "intoSlot", description = "The slot in the other inventory that you want to push into")
			})
	public int pushNotebookPageIntoSlot(IComputerAccess computer, Object tileEntityDesk, int deskSlot, ForgeDirection direction, int fromSlot, int maxAmount, int intoSlot) throws Exception {
		NotebookWrapper notebookWrapper = new NotebookWrapper(tileEntityDesk, deskSlot);
		return InventoryUtils.moveItemInto(notebookWrapper.getinventoryWrapper(), fromSlot - 1, notebookWrapper.getTargetTile(direction), intoSlot - 1, maxAmount, direction.getOpposite(), true);
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Push an item from the current inventory into any slot on the other one. Returns the amount of items moved",
			args = {
			@Arg(type = LuaType.NUMBER, name = "deskSlot", description = "The writing desk slot you are interested in"),
			@Arg(type = LuaType.STRING, name = "direction", description = "The direction of the other inventory. (north, south, east, west, up or down)"),
			@Arg(type = LuaType.NUMBER, name = "notebookSlot", description = "The slot in the current inventory that you're pushing from"),
			@Arg(type = LuaType.NUMBER, name = "maxAmount", description = "The maximum amount of items you want to push")
	})
	public int pushNotebookPage(IComputerAccess computer, Object desk, int deskSlot, ForgeDirection direction, int notebookSlot, int maxAmount) throws Exception {
		NotebookWrapper notebookWrapper = new NotebookWrapper(desk, deskSlot);
		return InventoryUtils.moveItemInto(notebookWrapper.getinventoryWrapper(), notebookSlot - 1, notebookWrapper.getTargetTile(direction), -1, maxAmount, direction.getOpposite(), true);
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Pull an item from the target inventory into any slot in the current one. Returns the amount of items moved",
			args = {
			@Arg(type = LuaType.NUMBER, name = "deskSlot", description = "The writing desk slot you are interested in"),      
			@Arg(type = LuaType.STRING, name = "direction", description = "The direction of the other inventory. (north, south, east, west, up or down)"),
			@Arg( type = LuaType.NUMBER, name = "notebookSlot", description = "The slot in the other inventory that you're pulling from"),
			@Arg(type = LuaType.NUMBER, name = "maxAmount", description = "The maximum amount of items you want to pull")
	})
	public int pullNotebookPage(IComputerAccess computer, Object desk, int deskSlot, ForgeDirection direction, int notebookSlot, int maxAmount) throws Exception {
		NotebookWrapper notebookWrapper = new NotebookWrapper(desk, deskSlot);
		return InventoryUtils.moveItemInto(notebookWrapper.getTargetTile(direction), notebookSlot - 1, notebookWrapper.getinventoryWrapper(), -1, maxAmount, direction.getOpposite(), true);
	}




	private static class NotebookWrapper{
		public ItemStack notebook;
		public Class<?> inventoryNotebookClass = ReflectionHelper.getClass("com.xcompwiz.mystcraft.inventory.InventoryNotebook");
		public Object tileEntityDesk;

		public NotebookWrapper(Object tile, int number) throws Exception{
			tileEntityDesk = tile;

			int slot = number + 4 - 1; // Slot numbers for notebooks are offset by the 4 plain slots, and -1 because it's 0 based. 
			IInventory inventory = (IInventory)tileEntityDesk;
			if (slot < 4 || slot > inventory.getSizeInventory()){
				throw new Exception("Invalid slot number");
			}
			notebook = inventory.getStackInSlot(slot);
		}

		public IInventory getTargetTile(ForgeDirection direction) throws Exception{
			if (direction == ForgeDirection.UNKNOWN) { 
				throw new Exception("Invalid direction"); 
			}
			TileEntity targetTile = BlockUtils.getTileInDirection((TileEntity)tileEntityDesk, direction);
			if (targetTile == null || !(targetTile instanceof IInventory)) { 
				throw new Exception("Target direction is not a valid inventory"); 
			}
			return (IInventory)targetTile;
		}

		public <T> T callStatic(String method, Object... extra_args){
			Object args[];
			if (extra_args.length == 0){
				args = new Object[]{notebook};
			}else{
				args = new Object[extra_args.length + 1];
				args[0] = notebook;
				System.arraycopy(extra_args, 0, args, 1, extra_args.length);
			}
			return new CallWrapper<T>().call(inventoryNotebookClass, method, args);
		}

		public IInventory getinventoryWrapper() throws ClassNotFoundException{
			return new NotebookIInventoryWrapper(notebook);
		}
	}

}
