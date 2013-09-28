package openperipheral.glasses.drawable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import openperipheral.core.interfaces.IDrawable;
import openperipheral.core.interfaces.ISurface;
import openperipheral.core.util.ByteUtils;
import openperipheral.core.util.FontSizeChecker;
import openperipheral.glasses.block.TileEntityGlassesBridge;

import org.lwjgl.opengl.GL11;

public class DrawableText extends BaseDrawable implements IDrawable {

	private short x;
	private short y;
	private String text;
	private int color;
	private double alpha = 1f;
	private float scale = 1f;

	public static final int X_CHANGED = 1;
	public static final int Y_CHANGED = 2;
	public static final int TEXT_CHANGED = 3;
	public static final int COLOR_CHANGED = 4;
	public static final int Z_CHANGED = 5;
	public static final int SCALE_CHANGED = 6;
	public static final int OPACITY_CHANGED = 7;

	public DrawableText() {
		super();
	}

	public DrawableText(ISurface parent, int x, int y, String text, int color) {
		super(parent);
		this.x = (short)x;
		this.y = (short)y;
		this.text = text;
		this.color = color;
		this.alpha = 1f;
		this.methodNames = new String[] { "setX", "getX", "setY", "getY", "setColor", "getColor", "setText", "getText", "setZIndex", "getZIndex", "setScale", "getScale", "getWidth", "getHeight", "getOpacity", "setOpacity", "delete" };
	}

	@Override
	public void draw(float partialTicks) {
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, 0);
		GL11.glPushMatrix();
		GL11.glScalef(scale, scale, scale);
		
		fontRenderer.drawString(text, 0, 0, ((int)(alpha*255) << 24 | color));
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

	public int getColor() {
		return color;
	}

	public float getScale() {
		return scale;
	}

	public String getText() {
		return text;
	}

	public int getWidth() {
		return (int)(FontSizeChecker.getInstance().getStringWidth(getText()) * getScale());
	}
	
	public int getHeight() {
		return (int)(FontSizeChecker.getInstance().getStringHeight(getText()) * getScale());
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	public int getZIndex() {
		return zIndex;
	}
	
	public double getOpacity() {
		return alpha;
	}

	@Override
	public void readFrom(DataInputStream stream, Short changeMask) {
		try {

			if (ByteUtils.get(changeMask, X_CHANGED)) x = stream.readShort();

			if (ByteUtils.get(changeMask, Y_CHANGED)) y = stream.readShort();

			if (ByteUtils.get(changeMask, TEXT_CHANGED)) text = stream.readUTF();

			if (ByteUtils.get(changeMask, COLOR_CHANGED)) color = stream.readInt();

			if (ByteUtils.get(changeMask, Z_CHANGED)) zIndex = stream.readByte();

			if (ByteUtils.get(changeMask, SCALE_CHANGED)) scale = stream.readFloat();
			
			if (ByteUtils.get(changeMask, OPACITY_CHANGED)) alpha = stream.readDouble();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int setColor(int c) {
		if (color == c) { return -1; }
		color = c;
		return COLOR_CHANGED;
	}

	public int setScale(float s) {
		if (scale == s) { return -1; }
		scale = s;
		return SCALE_CHANGED;
	}

	public int setText(String t) {
		if (t.equals(text)) { return -1; }
		text = t;
		return TEXT_CHANGED;
	}

	public int setX(short _x) {
		if (x == _x) { return -1; }
		x = _x;
		return X_CHANGED;
	}

	public int setY(short _y) {
		if (y == _y) { return -1; }
		y = _y;
		return Y_CHANGED;
	}

	public int setZIndex(byte z) {
		if (zIndex == z) { return -1; }
		zIndex = z;
		return Z_CHANGED;
	}
	
	public int setOpacity(double o) {
		if (alpha == o) { return -1; }
		if (o < 0 || o > 1.0) { return -1; }
		alpha = o;
		return OPACITY_CHANGED;
	}

	@Override
	public void writeTo(DataOutputStream stream, Short changeMask) {
		try {
			if (ByteUtils.get(changeMask, X_CHANGED)) stream.writeShort((short)x);

			if (ByteUtils.get(changeMask, Y_CHANGED)) stream.writeShort((short)y);

			if (ByteUtils.get(changeMask, TEXT_CHANGED)) stream.writeUTF(text);

			if (ByteUtils.get(changeMask, COLOR_CHANGED)) stream.writeInt(color);

			if (ByteUtils.get(changeMask, Z_CHANGED)) stream.writeByte(zIndex);

			if (ByteUtils.get(changeMask, SCALE_CHANGED)) stream.writeFloat(scale);
			
			if (ByteUtils.get(changeMask, OPACITY_CHANGED)) stream.writeDouble(alpha);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
