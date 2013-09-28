package openperipheral.glasses.drawable;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

import openperipheral.core.TypeConversionRegistry;
import openperipheral.core.interfaces.IDrawable;
import openperipheral.core.interfaces.ISurface;
import openperipheral.core.util.ReflectionHelper;
import openperipheral.glasses.block.TileEntityGlassesBridge;
import dan200.computer.api.ILuaContext;

public abstract class BaseDrawable implements IDrawable {

	protected String[] methodNames;

	private boolean deleted = false;
	protected byte zIndex = 0;

	private WeakReference<ISurface> surface;

	public BaseDrawable() {}

	public BaseDrawable(ISurface _bridge) {
	    surface = new WeakReference<ISurface>(_bridge);
	}

	@Override
	public String[] getMethodNames() {
		return methodNames;
	}

	@Override
	public Object[] callMethod(ILuaContext context, int methodId, Object[] arguments) throws Exception {

		if (deleted) { return null; }

		Method method = ReflectionHelper.getMethod(this.getClass(), new String[] { methodNames[methodId] }, arguments.length);

		ArrayList<Object> args = new ArrayList(Arrays.asList(arguments));

		if (method == null) { throw new Exception("Invalid number of arguments"); }

		Class[] requiredParameters = method.getParameterTypes();

		for (int i = 0; i < requiredParameters.length; i++) {
			Object converted = TypeConversionRegistry.fromLua(args.get(i), requiredParameters[i]);
			if (converted == null) { throw new Exception("Invalid parameter number " + (i + 1)); }
			args.set(i, converted);
		}

		final Object[] argsToUse = args.toArray(new Object[args.size()]);

		Object v = method.invoke(this, argsToUse);

		if (methodNames[methodId].startsWith("set")) {
			if (surface.get() != null) {
			    surface.get().markChanged(this, (Integer)v);
				return new Object[] {};
			}
		}

		return new Object[] { TypeConversionRegistry.toLua(v) };
	}

	public void delete() {
		deleted = true;
		if (surface.get() != null) {
		    surface.get().setDeleted(this);
		    surface.clear();
		}
	}
	
	/* Helpers! */
    
    /* Thanks Pahi- for your helpers :) P.S. I love your Dev Environment, and congratulations also. -NC */
    protected static void renderRotatingBlockIntoGUI(FontRenderer fontRenderer, ItemStack stack, int x, int y, float scale, float angle) {

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

    protected static void renderItemIntoGUI(FontRenderer fontRenderer, ItemStack itemStack, int x, int y, float scale) {
        Icon icon = itemStack.getIconIndex();
        GL11.glDisable(GL11.GL_LIGHTING);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        int overlayColour = itemStack.getItem().getColorFromItemStack(itemStack, 0);
        float red = (overlayColour >> 16 & 255) / 255.0F;
        float green = (overlayColour >> 8 & 255) / 255.0F;
        float blue = (overlayColour & 255) / 255.0F;
        GL11.glColor4f(red, green, blue, 1f);
        drawTexturedQuad(x, y, icon, 16 * scale, 16 * scale);
        GL11.glEnable(GL11.GL_LIGHTING);

    }
    
    protected static void drawTexturedQuad(int x, int y, Icon icon, float width, float height) {
        drawTexturedQuadAdvanced(x, y, icon, width, height, 1f, 1f, 1f);
    }
    
    protected static void drawTexturedQuadAdvanced(double x, double y, Icon icon, float width, float height, float uMax, float vMax, float alpha) {
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
