package openperipheral.common.drawable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import openperipheral.api.IDrawable;
import openperipheral.common.tileentity.TileEntityGlassesBridge;
import openperipheral.common.util.ByteUtils;
import openperipheral.common.util.FontSizeChecker;

import org.lwjgl.opengl.GL11;

public class DrawableText extends BaseDrawable implements IDrawable {

	private short x;
	private short y;
	private String text;
	private int color;
	private float scale = 1f;

	public static final int X_CHANGED = 1;
	public static final int Y_CHANGED = 2;
	public static final int TEXT_CHANGED = 3;
	public static final int COLOR_CHANGED = 4;
	public static final int Z_CHANGED = 5;
	public static final int SCALE_CHANGED = 6;

	public DrawableText() {
		super();
	}

	public DrawableText(TileEntityGlassesBridge parent, int x, int y, String text, int color) {
		super(parent);
		this.x = (short) x;
		this.y = (short) y;
		this.text = text;
		this.color = color;
		this.methodNames = new String[] { "setX", "getX", "setY", "getY", "setColor", "getColor", "setText", "getText", "setZIndex", "getZIndex", "setScale", "getScale",
				"getWidth", "delete" };
	}

	@Override
	public void draw(float partialTicks) {
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, 0);
		GL11.glPushMatrix();
		GL11.glScalef(scale, scale, scale);
		fontRenderer.drawString(text, 0, 0, color);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

	public int getColor() {
		return color;
	}

	public String getText() {
		return text;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	public int getWidth() {
		return FontSizeChecker.instance.getStringWidth(getText());
	}

	public float getScale() {
		return scale;
	}

	public int getZIndex() {
		return zIndex;
	}

	@Override
	public void readFrom(DataInputStream stream, Short changeMask) {
		try {

			if (ByteUtils.get(changeMask, X_CHANGED))
				x = stream.readShort();

			if (ByteUtils.get(changeMask, Y_CHANGED))
				y = stream.readShort();

			if (ByteUtils.get(changeMask, TEXT_CHANGED))
				text = stream.readUTF();

			if (ByteUtils.get(changeMask, COLOR_CHANGED))
				color = stream.readInt();

			if (ByteUtils.get(changeMask, Z_CHANGED))
				zIndex = stream.readByte();

			if (ByteUtils.get(changeMask, SCALE_CHANGED))
				scale = stream.readFloat();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int setColor(int c) {
		if (color == c) {
			return -1;
		}
		color = c;
		return COLOR_CHANGED;
	}

	public int setText(String t) {
		if (t.equals(text)) {
			return -1;
		}
		text = t;
		return TEXT_CHANGED;
	}

	public int setX(short _x) {
		if (x == _x) {
			return -1;
		}
		x = _x;
		return X_CHANGED;
	}

	public int setY(short _y) {
		if (y == _y) {
			return -1;
		}
		y = _y;
		return Y_CHANGED;
	}

	public int setZIndex(byte z) {
		if (zIndex == z) {
			return -1;
		}
		zIndex = z;
		return Z_CHANGED;
	}

	public int setScale(float s) {
		if (scale == s) {
			return -1;
		}
		scale = s;
		return SCALE_CHANGED;
	}

	@Override
	public void writeTo(DataOutputStream stream, Short changeMask) {
		try {
			if (ByteUtils.get(changeMask, X_CHANGED))
				stream.writeShort((short) x);

			if (ByteUtils.get(changeMask, Y_CHANGED))
				stream.writeShort((short) y);

			if (ByteUtils.get(changeMask, TEXT_CHANGED))
				stream.writeUTF(text);

			if (ByteUtils.get(changeMask, COLOR_CHANGED))
				stream.writeInt(color);

			if (ByteUtils.get(changeMask, Z_CHANGED))
				stream.writeByte(zIndex);

			if (ByteUtils.get(changeMask, SCALE_CHANGED))
				stream.writeFloat(scale);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
