package openperipheral.core.adapter.chickenchunks;

import openperipheral.api.Arg;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import openperipheral.core.util.ReflectionHelper;
import dan200.computer.api.IComputerAccess;

public class AdapterTileChunkLoader implements IPeripheralAdapter {

  @Override
  public Class<?> getTargetClass() {
    return ReflectionHelper.getClass("codechicken.chunkloader.TileChunkLoader");
  }

  @LuaMethod(returnType = LuaType.NUMBER, description = "Get the radius of the loaded chunks")
  public int getRadius(IComputerAccess computer, Object target) {
    return (Integer)ReflectionHelper.getProperty(getTargetClass(), target, "radius");
  }

  @LuaMethod(returnType = LuaType.STRING, description = "Get the shape of the loaded chunks")
  public String getShape(IComputerAccess computer, Object target) {
    return ReflectionHelper.getProperty(getTargetClass(), target, "shape").toString();
  }

  @LuaMethod(returnType = LuaType.NUMBER, description = "Get the number of loaded chunks")
  public int countLoadedChunks(IComputerAccess computer, Object target) throws Exception {
    return (Integer)ReflectionHelper.callMethod(getTargetClass(), target, new String[]{"countLoadedChunks"});
  }
  
  @LuaMethod(returnType = LuaType.STRING, description = "Get the owner of this chunk loader")
  public String getOwner(IComputerAccess computer, Object target) {
    return ReflectionHelper.getProperty(getTargetClass(), target, "owner").toString();
  }

  @LuaMethod(
      returnType = LuaType.BOOLEAN, description = "Set the shape and size of the loaded chunks",
        args = {
        @Arg(name = "shape", type = LuaType.STRING, description = "The shape of the loaded area"),
        @Arg(name = "radius", type = LuaType.NUMBER, description = "The radius of the loaded area")})
  public boolean setShapeAndRadius(IComputerAccess computer, Object target, String shape, int radius) throws Exception{
    Object shapeObj = getShapeFromString(shape);
    if(shapeObj==null){
      throw new Exception("Invalid shape");
    }
    if(radius<1 || radius>10){
      throw new Exception("Invalid radius size");
    }
    return (Boolean)ReflectionHelper.callMethod(getTargetClass(), target, new String[]{"setShapeAndRadius"},shapeObj,radius);
  }

  @SuppressWarnings("rawtypes")
  private Object getShapeFromString(String shape) {
    Class chunkLoaderShape = ReflectionHelper.getClass("codechicken.chunkloader.ChunkLoaderShape");
    Object[] enumConstants = chunkLoaderShape.getEnumConstants();
    for(Object obj:enumConstants){
      Enum obje=(Enum)obj;
      if(obje.toString().equalsIgnoreCase(shape)){
        return obje;
      }
    }
    return null;
  }
}
