package openperipheral.robots.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderRobotShadow extends RenderLiving {

	private static final ResourceLocation texture = new ResourceLocation("openperipheral", "textures/models/lazer.png");

	public RenderRobotShadow(ModelBase par1ModelBase, float par2) {
		super(par1ModelBase, par2);
	}

	@Override
	protected ResourceLocation func_110775_a(Entity entity) {
        return texture;
	}
	
}
