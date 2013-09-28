package openperipheral.core.interfaces;

import dan200.computer.api.ILuaObject;

public interface ISurface {

    public abstract ILuaObject addIcon(int x, int y, int id, int meta);
    
    public abstract Short getKeyForDrawable(IDrawable d);

    public abstract void setDeleted(IDrawable d);

    public abstract void markChanged(IDrawable d, int slot);

    public abstract ILuaObject addBox(int x, int y, int width, int height,
            int color, double alpha) throws InterruptedException;

    public abstract ILuaObject addGradientBox(int x, int y, int width,
            int height, int color, double alpha, int color2, double alpha2,
            byte gradient) throws InterruptedException;

    public abstract ILuaObject getById(int id);

    public abstract ILuaObject addText(int x, int y, String text, int color);
    
    public abstract ILuaObject addLiquid(int x, int y, int width, int height, int id);

    public abstract Short[] getAllIds();

    public abstract void clear();

}