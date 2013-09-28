package openperipheral.glasses.drawable;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import openperipheral.core.util.ByteUtils;

import cpw.mods.fml.client.FMLClientHandler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.Icon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class DrawableLiquid extends BaseDrawable {

    private short x;
    private short y;
    private short width;
    private short height;
    private float alpha;
    private Fluid fluid;
    
    private static final int X_CHANGED = 1;
    private static final int Y_CHANGED = 2;
    private static final int Z_CHANGED = 3;
    private static final int W_CHANGED = 4;
    private static final int H_CHANGED = 5;
    private static final int F_CHANGED = 6;
    private static final int A_CHANGED = 7;
    
    public DrawableLiquid() {
        super();
    }
    
    public DrawableLiquid(Surface surface, int x, int y, int width,
            int height, int id) {
        super(surface);
        this.x = (short)x;
        this.y = (short)y;
        this.width = (short)width;
        this.height = (short)height;
        this.alpha = 1f;
        setFluidID(id);
        this.methodNames = new String[] { "getX", "getY", "getOpacity", "getWidth", "getHeight", "getFluid", "getZIndex", "setX", "setY", "setOpacity", "setWidth", "setHeight", "setFluid", "setZIndex"};
    }

    @Override
    public int getX() {
        return x;
    }
    
    public int setX(short x) {
        if(this.x == x) return -1;
        this.x = x;
        return X_CHANGED;
    }

    @Override
    public int getY() {
        return y;
    }
    
    public int setY(short y) {
        if(this.y == y) return -1;
        this.y = y;
        return Y_CHANGED;
    }

    @Override
    public int getZIndex() {
        return super.zIndex;
    }

    @Override
    public int setZIndex(byte z) {
        if(z == zIndex) return -1;
        this.zIndex = z;
        return Z_CHANGED;
    }
    
    public double getOpacity() {
        return alpha;
    }
    
    public int setOpacity(double alpha) {
        if(this.alpha == alpha) return -1;
        this.alpha = (float)alpha;
        return A_CHANGED;
    }
    
    public short getWidth() {
        return width;
    }
    
    public int setWidth(short width) {
        if(this.width == width) { return -1; }
        this.width = width;
        return W_CHANGED;
    }
    
    public short getHeight() {
        return height;
    }
    
    public int setHeight(short height) {
        if(this.height == height) { return -1; }
        this.height = height;
        return H_CHANGED;
    }
    
    private boolean setFluidID(int fluidId) {
        try{
            fluid = FluidRegistry.getFluid(fluidId);
            return fluid != null;
        }catch(Exception ex) {
            return false;
        }
    }
    
    public int setFluid(int fluidId) {
        if(fluid != null && fluid.getID() == fluidId) return -1;
        try{
            return setFluidID(fluidId) ? F_CHANGED : -1;
        }catch(Exception ex) {
            ex.printStackTrace();
        }        
        return -1;
    }
    
    public int getFluid() {
        if(fluid == null) return 0;
        return fluid.getID();
    }

    @Override
    public void writeTo(DataOutputStream stream, Short changeMask) {        
        try {
            if (ByteUtils.get(changeMask, X_CHANGED)) {
                stream.writeShort(x);
            }
            if(ByteUtils.get(changeMask, Y_CHANGED)) {
                stream.writeShort(y);
            }
            if(ByteUtils.get(changeMask, Z_CHANGED)) {
                stream.writeByte(zIndex);
            }
            if(ByteUtils.get(changeMask, W_CHANGED)) {
                stream.writeShort(width);
            }
            if(ByteUtils.get(changeMask, H_CHANGED)) {
                stream.writeShort(height);
            }
            if(ByteUtils.get(changeMask, F_CHANGED)) {
                if(fluid == null) stream.writeInt(0);
                else stream.writeInt(fluid.getID());
            }
            if(ByteUtils.get(changeMask, A_CHANGED)) {
                stream.writeFloat(alpha);
            }
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void readFrom(DataInputStream stream, Short changeMask) {
        try{
            if (ByteUtils.get(changeMask, X_CHANGED)) {
                this.x = stream.readShort();
            }
            if(ByteUtils.get(changeMask, Y_CHANGED)) {
                this.y = stream.readShort();
            }
            if(ByteUtils.get(changeMask, Z_CHANGED)) {
                this.zIndex = stream.readByte();
            }
            if(ByteUtils.get(changeMask, W_CHANGED)) {
                this.width = stream.readShort();
            }
            if(ByteUtils.get(changeMask, H_CHANGED)) {
                this.height = stream.readShort();
            }
            if(ByteUtils.get(changeMask, F_CHANGED)) {
                this.setFluidID(stream.readInt());
            }
            if(ByteUtils.get(changeMask, A_CHANGED)) {
                this.alpha = stream.readFloat();
            }
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void draw(float partialTicks) {
        Icon fluidIcon = null;
        if(fluid != null && fluid.getFlowingIcon() != null) {
            fluidIcon = fluid.getFlowingIcon();
        }
        if(fluidIcon != null && fluidIcon.getIconHeight() > 0 && fluidIcon.getIconWidth() > 0) {
            TextureManager render = FMLClientHandler.instance().getClient().renderEngine;
            render.bindTexture(TextureMap.locationBlocksTexture);
            float xIterations = getWidth() / (float)fluidIcon.getIconWidth();
            float yIterations = getHeight() / (float)fluidIcon.getIconHeight();
            for(float xIteration = 0; xIteration < xIterations; xIteration++) {
                for(float yIteration = 0; yIteration < yIterations; yIteration++) {
                    // Draw whole or partial
                    float xDrawSize = xIterations - xIteration;
                    float yDrawSize = yIterations - yIteration;
                    xDrawSize = xDrawSize > 1 ? 1f : xDrawSize;
                    yDrawSize = yDrawSize > 1 ? 1f : yDrawSize;
                    drawTexturedQuadAdvanced(x + xIteration * fluidIcon.getIconWidth(), y + yIteration * fluidIcon.getIconHeight(), fluidIcon, xDrawSize * fluidIcon.getIconWidth(), yDrawSize * fluidIcon.getIconHeight(), xDrawSize, yDrawSize, this.alpha);
                }
            }
        }
    }
    
}
