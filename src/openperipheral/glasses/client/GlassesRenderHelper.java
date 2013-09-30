package openperipheral.glasses.client;

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

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

public final class GlassesRenderHelper {
    
    public static void renderRotatingBlockIntoGUI(FontRenderer fontRenderer, ItemStack stack, int x, int y, float scale, float angle) {

        RenderBlocks renderBlocks = new RenderBlocks();

        Block block = Block.blocksList[stack.itemID];
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        GL11.glPushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
        GL11.glTranslatef(x, y, 0.0f);
        GL11.glScalef(10.0F, 10.0F, 10.0F);
        GL11.glTranslatef(0.5F * scale, 0.5F * scale, 0.5F * scale);
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

    public static void renderItemIntoGUI(FontRenderer fontRenderer, ItemStack itemStack, int x, int y, float scale) {
        if(itemStack.getItem() == null) return;
        Icon icon = itemStack.getIconIndex();
        GL11.glDisable(GL11.GL_LIGHTING);
        if(itemStack.getItem() instanceof ItemBlock) {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        }else {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureMap.locationItemsTexture);
        }
        int overlayColour = itemStack.getItem().getColorFromItemStack(itemStack, 0);
        float red = (overlayColour >> 16 & 255) / 255.0F;
        float green = (overlayColour >> 8 & 255) / 255.0F;
        float blue = (overlayColour & 255) / 255.0F;
        GL11.glColor4f(red, green, blue, 1f);
        drawTexturedQuad(x, y, icon, 16 * scale, 16 * scale);
        GL11.glEnable(GL11.GL_LIGHTING);

    }
    
    public static void drawTexturedQuad(int x, int y, Icon icon, float width, float height) {
        drawTexturedQuadAdvanced(x, y, icon, width, height, 1f, 1f, 1f);
    }
    
    public static void drawTexturedQuadAdvanced(double x, double y, Icon icon, float width, float height, float uMax, float vMax, float alpha) {
        GL11.glPushMatrix();        
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);        
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        float textureU = icon.getMinU() + ( icon.getMaxU() - icon.getMinU()) * uMax;
        float textureV = icon.getMinV() + ( icon.getMaxV() - icon.getMinV()) * vMax;
        GL11.glColor4f(1f, 1f, 1f, alpha);
        tessellator.addVertexWithUV(x + 0, y + height, 0D, icon.getMinU(), textureV);
        tessellator.addVertexWithUV(x + width, y + height, 0D, textureU, textureV);
        tessellator.addVertexWithUV(x + width, y + 0, 0D, textureU, icon.getMinV());
        tessellator.addVertexWithUV(x + 0, y + 0, 0D, icon.getMinU(), icon.getMinV());
        tessellator.draw();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
