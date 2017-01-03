package openperipheral.converter.outbound;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import java.util.Map;
import openperipheral.api.converter.IConverter;
import openperipheral.api.helpers.SimpleOutboundConverter;

public class ConverterGameProfileOutbound extends SimpleOutboundConverter<GameProfile> {

	@Override
	public Object convert(IConverter registry, GameProfile profile) {

		Map<String, Object> map = Maps.newHashMap();
		map.put("name", profile.getName());
		map.put("uuid", profile.getId().toString());
		return map;
	}

}
