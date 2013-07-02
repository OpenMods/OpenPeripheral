package openpdoc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.ImmutableTable.Builder;
import com.google.common.collect.Table.Cell;

import static argo.jdom.JsonNodeBuilders.*;
import argo.format.JsonFormatter;
import argo.format.PrettyJsonFormatter;
import argo.jdom.JsonArrayNodeBuilder;
import argo.jdom.JsonObjectNodeBuilder;
import argo.jdom.JsonRootNode;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.src.BaseMod;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openperipheral.OpenPeripheral;
import openperipheral.common.definition.DefinitionManager;
import openperipheral.common.definition.DefinitionJsonMethod;
import openperipheral.common.interfaces.IPeripheralMethodDefinition;
import openperipheral.common.util.ReflectionHelper;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameData;
import dan200.computer.api.IPeripheral;

@Mod(modid = "OpenPeripheralDocs", name = "OpenPeripheralDocs", version = "0.1.0")
public class OpenPeripheralDocs implements ICommand {

	private static final JsonFormatter JSON_FORMATTER = new PrettyJsonFormatter();

	@Instance(value = "OpenPeripheralDocs")
	public static OpenPeripheralDocs instance;

	@Mod.PreInit
	public void preInit(FMLPreInitializationEvent evt) {

	}

	@Mod.ServerStarting
	public void serverStarting(FMLServerStartingEvent event) {
		ServerCommandManager serverCommandManager = (ServerCommandManager) event.getServer().getCommandManager();
		serverCommandManager.registerCommand(this);
	}

	@Mod.Init
	public void init(FMLInitializationEvent evt) {

	}

	@Override
	public int compareTo(Object arg0) {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "document";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return null;
	}

	@Override
	public List getCommandAliases() {
		return null;
	}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring) {
		HashMap<Integer, String> blockModMap = new HashMap<Integer,String>();
		
		
		ImmutableTable<String, String, Integer> modObjectTable = (ImmutableTable<String, String, Integer>)ReflectionHelper.getProperty(GameData.class, "", "modObjectTable");
		
		for (Cell<String, String, Integer> c : modObjectTable.cellSet())
        {
            if (c!=null)
            {
                int blockId = c.getValue();
                if (blockId >= Block.blocksList.length)
                {
                	continue;
                }
                blockModMap.put(blockId,  c.getRowKey());
            }
        }
		JsonArrayNodeBuilder builder = anArrayBuilder();
		try {
			System.out.println(astring);
			if (astring.length > 0) {
				for (int x1 = 0; x1 < 300; x1++) {
					for (int z1 = 0; z1 < 300; z1++) {
						EntityPlayer player = (EntityPlayer) icommandsender;
						
						int x = (int)player.posX + x1;
						int y = (int)player.posY;
						int z = (int)player.posZ + z1;
						
						World worldObj = player.worldObj;
						int blockId = worldObj.getBlockId(x, y, z);
						TileEntity te = worldObj.getBlockTileEntity(x, y, z);
						if (te != null) {

							Block blockType = te.getBlockType();
							//ItemStack stack = new ItemStack(blockType, 1, blockType.getDamageValue(te.worldObj, te.xCoord, te.yCoord, te.zCoord));

							JsonObjectNodeBuilder object = anObjectBuilder();
							object.withField("tile", aStringBuilder(te.getClass().getSimpleName()));
							if (blockModMap.containsKey(blockId)) {
								object.withField("mod", aStringBuilder(blockModMap.get(blockId)));
							}else {
								object.withField("mod", aStringBuilder("Vanilla"));
								
							}
							JsonArrayNodeBuilder jsonMethods = anArrayBuilder();
							ArrayList<IPeripheralMethodDefinition> methods = DefinitionManager.getMethodsForTile(te);
							for (IPeripheralMethodDefinition method : methods) {
								JsonObjectNodeBuilder jsonMethod = anObjectBuilder();
								jsonMethod.withField("name", aStringBuilder(method.getLuaName()));
								jsonMethods.withElement(jsonMethod);
							}
							object.withField("methods", jsonMethods);
							if (methods.size() > 0) {
								builder.withElement(object);
							}
						}
						worldObj.setBlockToAir(x, y, z);
					}
				}
			}else {
			
				
				if (icommandsender instanceof EntityPlayer) {
					EntityPlayer player =  ((EntityPlayer)icommandsender);
					World worldObj = player.worldObj;
					int placeX = 2;
					int placeZ = 0;
					String name = "";
					for (CreativeTabs tab : CreativeTabs.creativeTabArray) {
						name = tab.getTabLabel();
						ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
						tab.displayAllReleventItems(stacks);
						
						for (ItemStack stack : stacks) {
							try {
								Item item = stack.getItem();
								if (item instanceof ItemBlock) {
									ItemBlock b = (ItemBlock) stack.getItem();
									int blockId = b.getBlockID();
									Block block = Block.blocksList[blockId];
									if (block instanceof BlockContainer) {
										int x = (int)player.posX + 1;
										int y = (int)player.posY;
										int z = (int)player.posZ + 1;
										
										b.placeBlockAt(stack, player, worldObj, x, y, z, 0, 0, 0, 0, b.getMetadata(stack.getItemDamage()));
										TileEntity te = worldObj.getBlockTileEntity(x, y, z);
										if (te != null) {
											JsonObjectNodeBuilder object = anObjectBuilder();
											object.withField("tile", aStringBuilder(stack.getDisplayName()));
											if (blockModMap.containsKey(blockId)) {
												object.withField("mod", aStringBuilder(blockModMap.get(blockId)));
											}else {
												object.withField("mod", aStringBuilder("Vanilla"));
											}
											JsonArrayNodeBuilder jsonMethods = anArrayBuilder();
											ArrayList<IPeripheralMethodDefinition> methods = DefinitionManager.getMethodsForTile(te);
											for (IPeripheralMethodDefinition method : methods) {
												JsonObjectNodeBuilder jsonMethod = anObjectBuilder();
												jsonMethod.withField("name", aStringBuilder(method.getLuaName()));
												jsonMethods.withElement(jsonMethod);
											}
											object.withField("methods", jsonMethods);
											if (methods.size() > 0) {
												builder.withElement(object);
											}
										}
										worldObj.setBlockToAir(x, y, z);
									}
								}
							}catch(Exception f) {
							}
						}
					}
				}
			}
			

		}catch(Exception e) {
			e.printStackTrace();
		}
			
		try {
			JsonRootNode json = builder.build();
	
			String jsonText = JSON_FORMATTER.format(json);
	
			File file = new File("documentation.json");
	
			// if file doesnt exists, then create it
			if (!file.exists()) {
					file.createNewFile();
			}
			System.out.println(file.getAbsolutePath());
	
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(jsonText);
			bw.close();
		}catch (Exception f2) { }

	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender icommandsender, String[] astring) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i) {
		return false;
	}
}
