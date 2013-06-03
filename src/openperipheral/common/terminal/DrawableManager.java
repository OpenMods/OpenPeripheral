package openperipheral.common.terminal;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent.Load;

public class DrawableManager {

	public static final int DELETE = 0;
	public static final int CHANGE = 1;
	public String currentGuid = null;
	
	@ForgeSubscribe
	public void onWorldLoad(Load loadEvent) {
		drawables.clear();
	}
	
	private HashMap<Integer, IDrawable> drawables = new HashMap<Integer, IDrawable>();
	
	public Collection<IDrawable> getDrawables() {
		return drawables.values();
	}

	public void handlePacket(Packet250CustomPayload packet) {
		
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
        
        try {
        		String guid = inputStream.readUTF();
        		if (currentGuid == null || !currentGuid.equals(guid)) {
        			drawables.clear();
        			currentGuid = guid;
        		}
                int drawableCount = inputStream.readInt(); 
                for (int i = 0; i < drawableCount; i++) {
                	byte changeType = inputStream.readByte();
                	int drawableId = inputStream.readInt();
                	switch(changeType) {
                	case DELETE:
            			drawables.remove(drawableId);
            			break;
                	case CHANGE:
                    	byte drawableType = inputStream.readByte();
                    	IDrawable drawable = null;
                		if (drawables.containsKey(drawableId)) {
                			drawable = drawables.get(drawableId);
                		}else {
                			switch(drawableType) {
                			case 0:
                				drawable = new DrawableText();
                				break;
                			default:
                				drawable = new DrawableBox();
                			}
                		}
            			if (drawable != null) {
            				drawable.readFrom(inputStream);
            				drawables.put(drawableId, drawable);
            			}
                	}
                }
                
        } catch (IOException e) {
                e.printStackTrace();
                return;
        }
        
	}
}
