package cofh.util;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;

/**
 * Contains various helper functions to assist with String manipulation.
 * 
 * @author King Lemming
 * 
 */
public final class StringHelper {

	private StringHelper() {

	}

	public static boolean isAltKeyDown() {

		return Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU);
	}

	public static boolean isControlKeyDown() {

		return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
	}

	public static boolean isShiftKeyDown() {

		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}

	public static int getSplitStringHeight(FontRenderer fontRenderer, String input, int width) {

		List stringRows = fontRenderer.listFormattedStringToWidth(input, width);
		return stringRows.size() * fontRenderer.FONT_HEIGHT;
	}

	public static String camelCase(String input) {

		return input.substring(0, 1).toLowerCase() + input.substring(1);
	}

	public static String titleCase(String input) {

		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}

	public static String localize(String key) {

		return StatCollector.translateToLocal(key);
	}

	/** When formatting a string, always apply color before font modification. */
	public static final String BLACK = (char) 167 + "0";
	public static final String BLUE = (char) 167 + "1";
	public static final String GREEN = (char) 167 + "2";
	public static final String TEAL = (char) 167 + "3";
	public static final String RED = (char) 167 + "4";
	public static final String PURPLE = (char) 167 + "5";
	public static final String ORANGE = (char) 167 + "6";
	public static final String LIGHT_GRAY = (char) 167 + "7";
	public static final String GRAY = (char) 167 + "8";
	public static final String LIGHT_BLUE = (char) 167 + "9";
	public static final String BRIGHT_GREEN = (char) 167 + "a";
	public static final String BRIGHT_BLUE = (char) 167 + "b";
	public static final String LIGHT_RED = (char) 167 + "c";
	public static final String PINK = (char) 167 + "d";
	public static final String YELLOW = (char) 167 + "e";
	public static final String WHITE = (char) 167 + "f";

	public static final String OBFUSCATED = (char) 167 + "k";
	public static final String BOLD = (char) 167 + "l";
	public static final String STRIKETHROUGH = (char) 167 + "m";
	public static final String UNDERLINE = (char) 167 + "n";
	public static final String ITALIC = (char) 167 + "o";
	public static final String END = (char) 167 + "r";

	public static String shiftForInfo = StringHelper.GRAY + localize("message.cofh.holdShift1") + " " + StringHelper.BRIGHT_GREEN + StringHelper.ITALIC
			+ localize("message.cofh.holdShift2") + " " + StringHelper.END + StringHelper.GRAY + localize("message.cofh.holdShift3");

}
