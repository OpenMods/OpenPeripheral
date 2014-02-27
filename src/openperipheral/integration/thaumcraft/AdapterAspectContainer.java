package openperipheral.integration.thaumcraft;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import openmods.Log;
import openmods.utils.ReflectionHelper;
import openperipheral.api.*;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.base.Throwables;

@Prefixed("target")
public class AdapterAspectContainer implements IPeripheralAdapter {
    private static final Class<?> TILE_JAR_FILLABLE_CLASS = ReflectionHelper.getClass("thaumcraft.common.tiles.TileJarFillable");
    @Override
    public Class<?> getTargetClass() {
        return IAspectContainer.class;
    }

    @LuaMethod(returnType = LuaType.TABLE, description = "Get the Aspects stored in the block")
    public List<Map<String, Object>> getAspects(IAspectContainer container) {
        if (container == null) return null;
        Aspect labelAspect=null;
        if(TILE_JAR_FILLABLE_CLASS.isInstance(container))
        {
            Field f=ReflectionHelper.getField(TILE_JAR_FILLABLE_CLASS, "aspectFilter");
            Object o= null;
            try {
                o = f.get(container);
            } catch (Throwable t) {
                throw Throwables.propagate(t);

            }
            if((!(o instanceof Aspect) && o!=null))
            {
                Log.severe("Label is not an aspect");
                return null;
            }
            labelAspect=(Aspect)o;
        }

        AspectList aspectList = container.getAspects();
        if (aspectList == null)
        {
            return null;
        }
        if(aspectList.size()==0)
        {
            if(labelAspect!=null)
            {
                List<Map<String, Object>> aspectNames = Lists.newArrayList();
                Map<String, Object> aspectDetails = Maps.newHashMap();
                aspectDetails.put("name", labelAspect.getName());
                aspectDetails.put("quantity", 0);
                aspectNames.add(aspectDetails);
                return aspectNames;
            }
            else
            {
                return null;
            }
        }
        List<Map<String, Object>> aspectNames = Lists.newArrayList();
        for (Aspect aspect : aspectList.getAspects()) {
            Map<String, Object> aspectDetails = Maps.newHashMap();
            aspectDetails.put("name", aspect.getName());
            aspectDetails.put("quantity", aspectList.getAmount(aspect));
            aspectNames.add(aspectDetails);
        }
        return aspectNames;
    }

    @LuaMethod(returnType = LuaType.TABLE, description = "Get the map of aspects stored in the block (summed, if there are multiple entries)")
    public Map<String, Integer> getAspectsSum(IAspectContainer container) {
        AspectList aspectList = container.getAspects();
        if (aspectList == null) return null;
        Map<String, Integer> result = Maps.newHashMap();
        for (Aspect aspect : aspectList.getAspects()) {
            String name = aspect.getName();
            int amount = Objects.firstNonNull(result.get(name), 0);
            result.put(name, amount + aspectList.getAmount(aspect));
        }
        return result;
    }
}
