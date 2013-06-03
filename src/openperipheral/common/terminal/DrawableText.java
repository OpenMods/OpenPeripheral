package openperipheral.common.terminal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import openperipheral.common.tileentity.TileEntityGlassesBridge;

public class DrawableText extends BaseDrawable implements IDrawable {

	private int x;
	private int y;
	private String text;
	private int color;
	
	public DrawableText(TileEntityGlassesBridge parent, int x, int y, String text, int color) {
		super(parent);
		this.x = x;
		this.y = y;
		this.text = text;
		this.color = color;
		this.methodNames = new String[] {
				"setX",
				"getX",
				"setY",
				"getY",
				"setColor",
				"getColor",
				"setText",
				"getText",
				"delete"
		};
	}
	
	public DrawableText() {
		super();
	}
	
	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}
	
	public void setX(int _x) {
		x = _x;
	}

	public void setY(int _y) {
		y = _y;
	}
	
	public String getText() {
		return text;
	}
	
	public int getColor() {
		return color;
	}
	
	public void setColor(int c) {
		color = c;
	}
	
	public void setText(String t) {
		text = t;
	}

	@Override
	public void draw(ItemStack stack, EntityPlayer player,
			ScaledResolution resolution, float partialTicks, boolean hasScreen,
			int mouseX, int mouseY) {
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		fontRenderer.drawString(text, x, y, color);
	}


	@Override
	public void writeTo(DataOutputStream stream) {
		try {
			stream.writeInt(x);
			stream.writeInt(y);
			stream.writeUTF(text);
			stream.writeInt(color);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void readFrom(DataInputStream stream) {
		try {
			x = stream.readInt();
			y = stream.readInt();
			text = stream.readUTF();
			color = stream.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	

}
