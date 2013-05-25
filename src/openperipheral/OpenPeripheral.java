package openperipheral;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import argo.jdom.JdomParser;
import argo.jdom.JsonField;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;

import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import dan200.computer.api.ComputerCraftAPI;


@Mod( modid = "OpenPeripheral", name = "OpenPeripheral", version = "0.0.1")
public class OpenPeripheral
{
	
	public static ArrayList<MethodDefinition> peripheralMethods = new ArrayList<MethodDefinition>();
	public static HashMap<Class, ArrayList<MethodDefinition>> methodCache = new HashMap<Class, ArrayList<MethodDefinition>>();
	
	public static HashMap<String, IReplacement> replacements = new HashMap<String,IReplacement>();
	
	@Instance( value = "OpenPeripheral" )
	public static OpenPeripheral instance;

	@Mod.PreInit
	public void preInit( FMLPreInitializationEvent evt )
	{
		initializeReplacements();
	}

	@Mod.Init
	public void init( FMLInitializationEvent evt )
	{
		URL url;
		try {
			url = new URL("https://raw.github.com/mikeemoo/OpenPeripheral/master/methods.json");
			URLConnection con = url.openConnection();
			Reader r = new InputStreamReader(con.getInputStream(), "UTF-8");
		    JdomParser parser = new JdomParser();
		    JsonRootNode root = parser.parse(r);
		    for (JsonNode element : root.getElements()) {
		    	parseJson(element);
		    }
		    
			TickRegistry.registerTickHandler(new TickHandler(), Side.SERVER);
			ComputerCraftAPI.registerExternalPeripheral(TileEntity.class, new PeripheralHandler());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseJson(JsonNode node) {
		String className = node.getStringValue("class");
		Class clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			
		}
		if (clazz == null) {
			return;
		}

		HashMap<String, String> obfuscatedNames = new HashMap<String, String>();
		for (JsonField field : node.getNode("obfuscated").getFieldList()) {
			obfuscatedNames.put(field.getName().getText(), field.getValue().getText());
		}
		
		ArrayList<String> allowedMethods = new ArrayList<String>();
		HashMap<String, JsonNode> jsonMethods = new HashMap<String, JsonNode>();
		for (JsonField field : node.getNode("methods").getFieldList()) {
			jsonMethods.put(field.getName().getText(), field.getValue());
		}
		
		for (Method method : clazz.getMethods()) {
			if (jsonMethods.containsKey(method.getName())) {
				peripheralMethods.add(new MethodDefinition(method.getName(), method, jsonMethods.get(method.getName())));
			}else if (obfuscatedNames.containsKey(method.getName())) {
				String obfuscatedName = obfuscatedNames.get(method.getName());
				peripheralMethods.add(new MethodDefinition(obfuscatedName, method, jsonMethods.get(obfuscatedName)));	
			}
		}
	}

	public static ArrayList<MethodDefinition> getMethodsForClass(
			Class<? extends TileEntity> clazz) {
		
		if (!methodCache.containsKey(clazz)) {
			ArrayList<MethodDefinition> methods = new ArrayList<MethodDefinition>();
			for (MethodDefinition method : peripheralMethods) {
				if (method.isValidForClass(clazz)) {
					methods.add(method);
				}
			}
			methodCache.put(clazz, methods);
		}
		return methodCache.get(clazz);
		
	}
	
	private void initializeReplacements() {
		replacements.put("x", new IReplacement() {
			@Override
			public Object replace(TileEntity tile) {
				return tile.xCoord;
			}
		});
		replacements.put("y", new IReplacement() {
			@Override
			public Object replace(TileEntity tile) {
				return tile.yCoord;
			}
		});
		replacements.put("z", new IReplacement() {
			@Override
			public Object replace(TileEntity tile) {
				return tile.zCoord;
			}
		});
		replacements.put("world", new IReplacement() {
			@Override
			public Object replace(TileEntity tile) {
				return tile.worldObj;
			}
		});

	}
}