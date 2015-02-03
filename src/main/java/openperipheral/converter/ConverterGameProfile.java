package openperipheral.converter;

import java.util.Map;

import openperipheral.api.converter.IConverter;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;

public class ConverterGameProfile extends GenericConverterAdapter {

	@Override
	public Object fromLua(IConverter registry, Object obj, Class<?> expected) {
		return null;
	}

	@Override
	public Object toLua(IConverter registry, Object obj) {
		if (obj instanceof GameProfile) {
			GameProfile profile = (GameProfile)obj;
			Map<String, Object> map = Maps.newHashMap();
			map.put("name", profile.getName());
			map.put("uuid", profile.getId().toString());
			return map;
		}

		return null;
	}

}
