package openpdoc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static argo.jdom.JsonNodeBuilders.*;
import argo.format.JsonFormatter;
import argo.format.PrettyJsonFormatter;
import argo.jdom.JsonArrayNodeBuilder;
import argo.jdom.JsonObjectNodeBuilder;
import argo.jdom.JsonRootNode;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.src.BaseMod;
import net.minecraft.src.ModLoader;
import net.minecraft.tileentity.TileEntity;
import openperipheral.OpenPeripheral;
import openperipheral.definition.DefinitionMethod;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod( modid = "OpenPeripheralDocs", name = "OpenPeripheralDocs", version = "0.1.0")
public class OpenPeripheralDocs implements ICommand {

	private static final JsonFormatter JSON_FORMATTER = new PrettyJsonFormatter();
	
	@Instance( value = "OpenPeripheralDocs" )
	public static OpenPeripheralDocs instance;
	

	@Mod.PreInit
	public void preInit( FMLPreInitializationEvent evt )
	{
		
	}
	
	@Mod.ServerStarting
	public void serverStarting(FMLServerStartingEvent event) {
		ServerCommandManager serverCommandManager = (ServerCommandManager) event.getServer().getCommandManager();
		serverCommandManager.registerCommand(this);
	}
	
	@Mod.Init
	public void init( FMLInitializationEvent evt )
	{
		
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

		try {
			Map m = null;
			Field f;
			try {
				f = TileEntity.class.getDeclaredField("nameToClassMap");
			}catch(Exception ex) {
				f = TileEntity.class.getDeclaredField("field_70326_a");
			}
			f.setAccessible(true);
			m = (Map)(f.get(null));
			
			JsonArrayNodeBuilder builder = anArrayBuilder();
			
			for (Object o : m.entrySet()) {
				Entry e = (Entry)o;
				Class c = (Class) e.getValue();
				ArrayList<DefinitionMethod> methods = OpenPeripheral.getMethodsForClass(c);
				if  (methods.size() > 0) {
					JsonObjectNodeBuilder object = anObjectBuilder();
					
					object.withField("tile", aStringBuilder(c.getName()));
					JsonArrayNodeBuilder jsonMethods = anArrayBuilder();
					for (DefinitionMethod method : methods) {
						JsonObjectNodeBuilder jsonMethod = anObjectBuilder();
						jsonMethod.withField("name", aStringBuilder(method.getLuaName()));
						jsonMethod.withField("returnType", aStringBuilder(method.getReturnType().getName()));
						JsonArrayNodeBuilder params = anArrayBuilder();
						int index = 0;
						for (Class param : method.getRequiredParameters()) {
							if (!method.paramNeedsReplacing(index++)) {
								params.withElement(aStringBuilder(param.getSimpleName()));
							}
						}
						jsonMethod.withField("args", params);
						jsonMethods.withElement(jsonMethod);
					}
					object.withField("methods", jsonMethods);
					builder.withElement(object);
				}
			}
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
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender icommandsender,
			String[] astring) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i) {
		return false;
	}
}
