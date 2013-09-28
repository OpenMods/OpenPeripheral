package openperipheral.glasses.drawable;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import cpw.mods.fml.client.FMLClientHandler;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.Icon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class DrawableLiquid extends BaseDrawable {

    private short x;
    private short y;
    private short width;
    private short height;
    private FluidStack fluidStack;
    
    private static final int X_CHANGED = 1;
    private static final int Y_CHANGED = 2;
    private static final int Z_CHANGED = 3;
    private static final int W_CHANGED = 4;
    private static final int H_CHANGED = 5;
    private static final int L_CHANGED = 6;
    
    private 
    
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
        return super.zIndex;
    }

    @Override
    public int setZIndex(byte z) {
        if(z == zIndex) return -1;
        return 
    }

    @Override
    public void writeTo(DataOutputStream stream, Short changeMask) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void readFrom(DataInputStream stream, Short changeMask) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void draw(float partialTicks) {
        Icon fluidIcon = null;
        if(fluidStack != null && fluidStack.getFluid() != null) {
            Fluid fluid = fluidStack.getFluid();
            if(fluid != null && fluid.getFlowingIcon() != null) {
                fluidIcon = fluid.getFlowingIcon();
            }
        }
        if(fluidIcon != null && fluidIcon.getIconHeight() > 0 && fluidIcon.getIconWidth() > 0) {
            int targetY = 0;
            TextureManager render = FMLClientHandler.instance().getClient().renderEngine;
            render.bindTexture(TextureMap.locationBlocksTexture);
                while (true) {
                    int targetX = 0;

                    if (squaled > 16) {
                        targetX = 16;
                        squaled -= 16;
                    } else {
                        targetX = squaled;
                        squaled = 0;
                    }

                    drawTexturedQuad(targetX + x, targetY + y, fluidIcon, fluidIcon.getIconWidth(), fluidIcon.getIconHeight());
                    
                    start = start + 16;

                    if (targetX == 0 || squaled == 0) {
                        break;
                    }
                }
            
        }
    }
    
}
