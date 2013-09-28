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
        if (x == this.y)
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
                renderRotatingBlockIntoGUI(renderer, clientStack, this.x, this.y, this.zIndex, (float)this.scale, (float)this.angle);
            }else{
                renderItemIntoGUI(renderer, clientStack, this.x, this.y, this.zIndex, (float)this.scale);
            }
        }
    }
    
    /* Thanks Pahi- for your helpers :) P.S. I love your Dev Environment, and congratulations also. -NC */
    private static void renderRotatingBlockIntoGUI(FontRenderer fontRenderer, ItemStack stack, int x, int y, float zLevel, float scale, float angle) {

        RenderBlocks renderBlocks = new RenderBlocks();

        Block block = Block.blocksList[stack.itemID];
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        GL11.glPushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
        GL11.glTranslatef(x - 2, y + 3, -3.0F + zLevel);
        GL11.glScalef(10.0F, 10.0F, 10.0F);
        GL11.glTranslatef(1.0F, 0.5F, 1.0F);
        GL11.glScalef(1.0F * scale, 1.0F * scale, -1.0F);
        GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(0F + 1 * angle, 0.0F, 1.0F, 0.0F);
        angle = (angle + 1) % 360;

        int var10 = Item.itemsList[stack.itemID].getColorFromItemStack(stack, 0);
        float var16 = (var10 >> 16 & 255) / 255.0F;
        float var12 = (var10 >> 8 & 255) / 255.0F;
        float var13 = (var10 & 255) / 255.0F;

        GL11.glColor4f(var16, var12, var13, 1f);

        GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
        renderBlocks.useInventoryTint = true;
        renderBlocks.renderBlockAsItem(block, stack.getItemDamage(), 1.0F);
        renderBlocks.useInventoryTint = false;
        RenderHelper.disableStandardItemLighting();
        GL11.glPopMatrix();
    }

    private static void renderItemIntoGUI(FontRenderer fontRenderer, ItemStack itemStack, int x, int y, float zLevel, float scale) {
        Icon icon = itemStack.getIconIndex();
        GL11.glDisable(GL11.GL_LIGHTING);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        int overlayColour = itemStack.getItem().getColorFromItemStack(itemStack, 0);
        float red = (overlayColour >> 16 & 255) / 255.0F;
        float green = (overlayColour >> 8 & 255) / 255.0F;
        float blue = (overlayColour & 255) / 255.0F;
        GL11.glColor4f(red, green, blue, 1f);
        drawTexturedQuad(x, y, icon, 16 * scale, 16 * scale, zLevel);
        GL11.glEnable(GL11.GL_LIGHTING);

    }

    private static void drawTexturedQuad(int x, int y, Icon icon, float width, float height, double zLevel) {

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x + 0, y + height, zLevel, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(x + width, y + height, zLevel, icon.getMaxU(), icon.getMaxV());
        tessellator.addVertexWithUV(x + width, y + 0, zLevel, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(x + 0, y + 0, zLevel, icon.getMinU(), icon.getMinV());
        tessellator.draw();
    }

}
