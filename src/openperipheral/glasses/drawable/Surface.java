package openperipheral.glasses.drawable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import dan200.computer.api.ILuaContext;
import dan200.computer.api.ILuaObject;

import net.minecraft.network.packet.Packet;

import openperipheral.core.TypeConversionRegistry;
import openperipheral.core.interfaces.IDrawable;
import openperipheral.core.interfaces.ISurface;
import openperipheral.core.util.ByteUtils;
import openperipheral.core.util.FontSizeChecker;
import openperipheral.core.util.PacketChunker;
import openperipheral.core.util.ReflectionHelper;
import openperipheral.core.util.ThreadLock;
import openperipheral.glasses.block.TileEntityGlassesBridge;
import openperipheral.glasses.client.TerminalManager;

public class Surface implements ISurface, ILuaObject {
    
    public WeakReference<TileEntityGlassesBridge> parent;

    public HashMap<Short, IDrawable> drawables = new HashMap<Short, IDrawable>();
    public HashMap<Short, Short> changes = new HashMap<Short, Short>();

    private short count = 1;
    private ThreadLock lock = new ThreadLock();
    
    /**
     * This Surface belongs to the entire bridge, not just one user.
     */
    public boolean isGlobal;
    public String playerName = "GLOBAL";
    
    public Surface(TileEntityGlassesBridge parent) {
        this.parent = new WeakReference<TileEntityGlassesBridge>(parent);
        isGlobal = true;
    }
    
    public Surface(TileEntityGlassesBridge parent, String username) {
        this.parent = new WeakReference<TileEntityGlassesBridge>(parent);
        this.playerName = username;
    }
    
    // Should probably export these as part of the interface.. maybe
    
    public boolean hasChanges() {
        return changes.size() > 0;
    }
    
    public void clearChanges() {
        changes.clear();
    }
    
    /* (non-Javadoc)
     * @see openperipheral.glasses.drawable.ISurface#getKeyForDrawable(openperipheral.core.interfaces.IDrawable)
     */
    @Override
    public Short getKeyForDrawable(IDrawable d) {
        Short rtn = -1;
        try {
            lock.lock();
            try {
                for (Entry<Short, IDrawable> entry : drawables.entrySet()) {
                    if (entry.getValue().equals(d)) {
                        rtn = entry.getKey();
                    }
                }
            } finally {
                lock.unlock();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return rtn;
    }

    /* (non-Javadoc)
     * @see openperipheral.glasses.drawable.ISurface#setDeleted(openperipheral.core.interfaces.IDrawable)
     */
    @Override
    public void setDeleted(IDrawable d) {
        try {
            lock.lock();
            try {
                Short key = getKeyForDrawable(d);
                if (key != -1) {
                    changes.put(key, (short)0);
                    drawables.remove(key);
                }
            } finally {
                lock.unlock();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see openperipheral.glasses.drawable.ISurface#markChanged(openperipheral.core.interfaces.IDrawable, int)
     */
    @Override
    public void markChanged(IDrawable d, int slot) {
        if (slot == -1) { return; }
        try {
            lock.lock();
            try {
                Short key = getKeyForDrawable(d);
                if (key != -1) {
                    Short current = changes.get(key);

                    if (current == null) {
                        current = 0;
                    }
                    current = ByteUtils.set(current, slot, true);
                    current = ByteUtils.set(current, 0, true);
                    changes.put(key, current);
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            } finally {
                lock.unlock();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public Packet[] createFullPackets() {

        Packet[] packets = null;

        try {
            lock.lock();
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
                DataOutputStream outputStream = new DataOutputStream(bos);

                /* If this surface is not global, then it is private to this player */
                byte flag = (byte) (TerminalManager.CHANGE_FLAG | (isGlobal ? 0 : TerminalManager.PRIVATE_FLAG));
                
                outputStream.writeByte(flag);
                outputStream.writeShort((short)drawables.size());

                for (Entry<Short, IDrawable> entries : drawables.entrySet()) {
                    Short drawableId = entries.getKey();
                    writeDrawableToStream(outputStream, drawableId, Short.MAX_VALUE);
                }

                packets = PacketChunker.instance.createPackets(bos.toByteArray());

            } catch (Exception e2) {
                e2.printStackTrace();

            } finally {
                lock.unlock();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return packets;
    }

    public Packet[] createChangePackets() {

        Packet[] packets = null;

        try {
            lock.lock();
            try {

                ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
                DataOutputStream outputStream = new DataOutputStream(bos);

                /* If this surface is not global, then it is private to this player */
                byte flag = (byte) (TerminalManager.CHANGE_FLAG | (isGlobal ? 0 : TerminalManager.PRIVATE_FLAG));
                
                // send the 'change' flag
                outputStream.writeByte(flag);

                // write the amount of drawables that have changed
                outputStream.writeShort((short)changes.size());

                // write each of the drawables
                for (Entry<Short, Short> change : changes.entrySet()) {
                    Short drawableId = change.getKey();
                    Short changeMask = change.getValue();
                    writeDrawableToStream(outputStream, drawableId, changeMask);

                }

                packets = PacketChunker.instance.createPackets(bos.toByteArray());

                changes.clear();
            } finally {
                lock.unlock();
            }
        } catch (Exception ex) {}
        return packets;
    }
    
    /* (non-Javadoc)
     * @see openperipheral.glasses.drawable.ISurface#addBox(int, int, int, int, int, double)
     */
    @Override
    public ILuaObject addBox(int x, int y, int width, int height, int color, double alpha) throws InterruptedException {
        return addGradientBox(x, y, width, height, color, alpha, color, alpha, (byte)0);
    }

    /* (non-Javadoc)
     * @see openperipheral.glasses.drawable.ISurface#addGradientBox(int, int, int, int, int, double, int, double, byte)
     */
    @Override
    public ILuaObject addGradientBox(int x, int y, int width, int height, int color, double alpha, int color2, double alpha2, byte gradient) throws InterruptedException {
        ILuaObject obj = null;

        try {
            lock.lock();
            try {
                drawables.put(count, new DrawableBox(this, x, y, width, height, color, alpha, color2, alpha2, gradient));
                changes.put(count, Short.MAX_VALUE);
                obj = drawables.get(count++);
            } catch (Exception e2) {
                e2.printStackTrace();
            } finally {
                lock.unlock();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return (ILuaObject)obj;
    }

    /* (non-Javadoc)
     * @see openperipheral.glasses.drawable.ISurface#getById(int)
     */
    @Override
    public ILuaObject getById(int id) {
        try {
            lock.lock();
            try {
                return (ILuaObject)drawables.get((short)id);
            } finally {
                lock.unlock();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see openperipheral.glasses.drawable.ISurface#addText(int, int, java.lang.String, int)
     */
    @Override
    public ILuaObject addText(int x, int y, String text, int color) {
        ILuaObject obj = null;
        try {
            lock.lock();
            try {
                drawables.put(count, new DrawableText(this, x, y, text, color));
                changes.put(count, Short.MAX_VALUE);
                obj = drawables.get(count++);
            } finally {
                lock.unlock();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return (ILuaObject)obj;
    }

    private void writeDrawableToStream(DataOutputStream outputStream, short drawableId, Short changeMask) throws IOException {

        // write the mask
        outputStream.writeShort(changeMask);

        // write the drawable Id
        outputStream.writeShort(drawableId);

        if (ByteUtils.get(changeMask, 0)) { // if its not deleted

            IDrawable drawable = drawables.get(drawableId);

            if (drawable instanceof DrawableText) {
                outputStream.writeByte((byte)0);
            } else {
                outputStream.writeByte((byte)1);
            }

            // write the rest of the drawable object
            drawable.writeTo(outputStream, changeMask);
        }
    }
    
    
    public static int getStringWidth(String str) {
        try {
            return FontSizeChecker.getInstance().getStringWidth(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str.length() * 8;
    }
    
    /* (non-Javadoc)
     * @see openperipheral.glasses.drawable.ISurface#getAllIds()
     */
    @Override
    public Short[] getAllIds() {
        try {
            lock.lock();
            try {
                return drawables.keySet().toArray(new Short[drawables.size()]);
            } finally {
                lock.unlock();
            }
        } catch (Exception ex) {}
        return null;

    }

    /* (non-Javadoc)
     * @see openperipheral.glasses.drawable.ISurface#clear()
     */
    @Override
    public void clear() {
        try {
            lock.lock();
            try {
                for (Short key : drawables.keySet()) {
                    changes.put(key, (short)0);
                }
                drawables.clear();
            } finally {
                lock.unlock();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String[] methodNames = new String[] { "getPlayerName", "clear", "getAllIds", "getById", "addBox", "addText", "addGradientBox" };
    
    @Override
    public String[] getMethodNames() {
        return methodNames;
    }

    @Override
    public Object[] callMethod(ILuaContext context, int methodId,
            Object[] arguments) throws Exception {
        
        // Don't mind me, I'll just yank this out of BaseDrawable ;)  -NC
        
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

        return new Object[] { TypeConversionRegistry.toLua(v) };
    }
}
