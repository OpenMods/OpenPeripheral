package openperipheral.adapter.vanilla;

import net.minecraft.tileentity.TileEntitySign;
import openperipheral.api.Arg;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;

import org.apache.commons.lang3.StringUtils;

import dan200.computer.api.IComputerAccess;

public class AdapterSign implements IPeripheralAdapter {
	@Override
	public Class<?> getTargetClass() {
		return TileEntitySign.class;
	}

	@LuaMethod(returnType = LuaType.VOID, onTick = false, description = "Sets the text on the sign",
			args = {
					@Arg(name = "line", type = LuaType.NUMBER, description = "The line number to set the text on the sign"),
					@Arg(name = "text", type = LuaType.STRING, description = "The text to display on the sign") })
	public void setLine(IComputerAccess computer, TileEntitySign sign, int line, String text) throws Exception {
		int max = sign.signText.length;
		if (line < 1 || line > max) throw new Exception(String.format("Invalid line number should be 1-%d", max));
		System.out.println(line + ":" + text);
		sign.signText[--line] = text.length() < 15? text : text.substring(0, 15);
		sign.worldObj.markBlockForUpdate(sign.xCoord, sign.yCoord, sign.zCoord);
	}

	@LuaMethod(returnType = LuaType.STRING, onTick = false, description = "Gets the text from the supplied line of the sign",
			args = { @Arg(name = "line", type = LuaType.NUMBER, description = "The line number to get from the sign") })
	public String getLine(IComputerAccess computer, TileEntitySign sign, int line) throws Exception {
		int max = sign.signText.length;
		if (line < 1 || line > max) throw new Exception(String.format("Invalid line number should be 1-%d", max));

		return sign.signText[--line];
	}

	@LuaMethod(returnType = LuaType.VOID, onTick = false, description = "Sets the text on the sign",
			args = @Arg(name = "text", type = LuaType.STRING, description = "The text to display on the sign"))
	public void setText(IComputerAccess computer, TileEntitySign sign, String text) throws Exception {
		String[] lines = text.split("\n");

		int maxLength = sign.signText.length;
		int currLength = lines.length;

		if (currLength < 0 || lines.length > maxLength) throw new Exception(String.format("Invalid number of lines maximum is %d", maxLength));

		for (int i = 0; i < maxLength; ++i) {
			setLine(computer, sign, i + 1, i < currLength? lines[i] : "");
		}
	}

	@LuaMethod(returnType = LuaType.STRING, onTick = false, description = "Gets the text on the sign")
	public String getText(IComputerAccess computer, TileEntitySign sign) {
		return StringUtils.join(sign.signText, '\n');
	}
}