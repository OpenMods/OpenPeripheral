package openperipheral.glasses.drawable;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

import openperipheral.core.interfaces.ISurface;
import openperipheral.core.util.ByteUtils;

public class DrawableIcon extends BaseDrawable {

    private short x, y;
    private double scale, angle;
    private int id, meta;
    private boolean updateItem = true;

    /* Item stack used client side */
    private ItemStack clientStack;
    
    public static final int X_CHANGED = 1;
    public static final int Y_CHANGED = 2;
    public static final int SCALE_CHANGED = 3;
    public static final int Z_CHANGED = 4;
    public static final int ANGLE_CHANGED = 5;
    public static final int ID_CHANGED = 6;
    public static final int META_CHANGED = 7;

    public DrawableIcon() {
        super();
    }
    
    public DrawableIcon(ISurface parent, int x, int y, int id, int meta) {
        super(parent);
        this.x = (short) x;
        this.y = (short) y;
        this.scale = 1;
        this.angle = 30;
        this.id = id;
        this.meta = meta;
        methodNames = new String[] { "getX", "setX", "getY", "setY",
                "getScale", "setScale", "getAngle", "setAngle",
                "setZIndex", "getZIndex", "setItem", "getItem", "setMeta", "getMeta", "delete" };
    }
    
    public int setItem(int item) {
        if(item == this.id) return -1;
        this.id = item;
        return ID_CHANGED;
    }
    
    public int setMeta(int meta) {
        if(meta == this.meta) return -1;
        this.meta = meta;
        return META_CHANGED;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public int setZIndex(byte z) {
        if (z == zIndex)
            return -1;
        zIndex = z;
        return Z_CHANGED;
    }

    public int setX(short x) {
        if (x == this.x)
            return -1;
        this.x = x;
        return X_CHANGED;
    }

    public int setY(short y) {
        if (y == this.y)
            return -1;
        this.y = y;
        return Y_CHANGED;
    }

    public double getAngle() {
        return angle;
    }

    public int setAngle(double angle) {
        
        if(angle > 359 || angle < 0) angle = angle % 360;
        if (angle == this.angle)
            return -1;
        this.angle = angle;
        return ANGLE_CHANGED;
    }

    public double getScale() {
        return scale;
    }

    public int setScale(double scale) {
        if(this.scale == 0) this.scale = 1;
        if(this.scale < 0.5) this.scale = 0.5;
        if(scale == this.scale)
            return -1; 
        this.scale = scale;
        return SCALE_CHANGED;
    }

    @Override
    public void writeTo(DataOutputStream stream, Short changeMask) {
        try {
            if (ByteUtils.get(changeMask, X_CHANGED)) {
                stream.writeShort(x);
            }
            if (ByteUtils.get(changeMask, Y_CHANGED)) {
                stream.writeShort(y);
            }
            if (ByteUtils.get(changeMask, Z_CHANGED)) {
                stream.writeByte(zIndex);
            }
            if (ByteUtils.get(changeMask, ANGLE_CHANGED)) {
                stream.writeDouble(angle);
            }
            if (ByteUtils.get(changeMask, SCALE_CHANGED)) {
                stream.writeDouble(scale);
            }
            if (ByteUtils.get(changeMask, ID_CHANGED)) {
                stream.writeInt(id);
            }
            if (ByteUtils.get(changeMask, META_CHANGED)) {
                stream.writeInt(meta);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void readFrom(DataInputStream stream, Short changeMask) {
        try {
            if (ByteUtils.get(changeMask, X_CHANGED)) {
                x = stream.readShort();
            }
            if (ByteUtils.get(changeMask, Y_CHANGED)) {
                y = stream.readShort();
            }
            if (ByteUtils.get(changeMask, Z_CHANGED)) {
                zIndex = stream.readByte();
            }
            if (ByteUtils.get(changeMask, ANGLE_CHANGED)) {
                angle = stream.readDouble();
            }
            if (ByteUtils.get(changeMask, SCALE_CHANGED)) {
                scale = stream.readDouble();
            }
            if (ByteUtils.get(changeMask, ID_CHANGED)) {
                id = stream.readInt();
                updateItem = true;
            }
            if (ByteUtils.get(changeMask, META_CHANGED)) {
                meta = stream.readInt();
                updateItem = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    
    private void updateID() {
        clientStack = new ItemStack(this.id, 1, this.meta);
        updateItem = false;
    }

    @Override
    public void draw(float partialTicks) {
        if(updateItem) {
            updateID();
        }
        
        try{
            if(id >= 0 && ( id < Item.itemsList.length && Item.itemsList[id] != null || id < Block.blocksList.length && Block.blocksList[id] != null)) {
                renderIcon();
            }
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void renderIcon() {
        FontRenderer renderer = FMLClientHandler.instance().getClient().fontRenderer;
        ItemStack stack = clientStack;
        if(stack != null && stack.getItem() != null) {
            if(stack.getItem() instanceof ItemBlock) {
                renderRotatingBlockIntoGUI(renderer, clientStack, this.x + 1, this.y + 1, (float)this.scale, (float)this.angle);
            }else{
                renderItemIntoGUI(renderer, clientStack, this.x, this.y, (float)this.scale);
            }
        }
    }

}
