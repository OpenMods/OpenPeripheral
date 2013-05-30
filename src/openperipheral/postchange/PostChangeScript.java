package openperipheral.postchange;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.bouncycastle.util.encoders.Base64;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

import net.minecraft.tileentity.TileEntity;
import openperipheral.IPostChangeHandler;
import openperipheral.definition.DefinitionMethod;
import openperipheral.util.ReflectionHelper;

public class PostChangeScript implements IPostChangeHandler {
	
	private ScriptEngineManager factory = null;
	protected ScriptEngine engine = null;
	
	public PostChangeScript() {
		try {
			factory = new ScriptEngineManager();
	        engine = factory.getEngineByName("JavaScript");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void execute(TileEntity tile, DefinitionMethod luaMethod,
			Object[] values) {
		Class c = tile.getClass();
		String script = luaMethod.getPostScript();
		if (script != null) {
			script = new String(Base64.decode(script));
			try {
				this.engine.put("tile", tile);
				this.engine.put("xCoord", tile.xCoord);
				this.engine.put("yCoord", tile.yCoord);
				this.engine.put("zCoord", tile.zCoord);
				this.engine.put("luaMethod", luaMethod);
				this.engine.put("values", values);
				this.engine.put("worldObj", tile.worldObj);
				this.engine.put("env", this);
				this.engine.eval(script);
			} catch (ScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setProperty(String className, Object instance, Object value, String ... fields) {
		ReflectionHelper.setProperty(className, instance, value, fields);
	}
	
	public Object getProperty(String className, Object instance, String ... fields) {
		return ReflectionHelper.getProperty(className, instance, fields);
	}

	public Object callMethod(String className, Object instance, String[] methodNames, Object ... args) {
		return ReflectionHelper.callMethod(className, instance, methodNames, args);
	}
	
	public void print(Object o) {
		if (o == null) { 
			System.out.println("void");
		}else {
			System.out.println(o.toString());
		}
	}
	
}
