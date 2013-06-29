package openperipheral.client.renderer;

import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.EntityLiving;

@SideOnly(Side.CLIENT)
public class RenderRobot extends RenderLiving {

	public RenderRobot(ModelBase par1ModelBase, float par2) {
		super(par1ModelBase, par2);
	}
	
}
