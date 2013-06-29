package openperipheral.common.entity;

import openperipheral.common.config.ConfigSettings;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class EntityRobot extends EntityCreature {

	public EntityRobot(World par1World) {
		super(par1World);
		this.health = this.getMaxHealth();
		this.setSize(1F, 3F);
        this.moveSpeed = 0.4F;

        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIWander(this, 0.23F));
        this.tasks.addTask(2, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(3, new EntityAILookIdle(this));
        this.texture = String.format("%s/models/robot.png", ConfigSettings.TEXTURES_PATH);
        //System.out.println(texture);
	}

	protected boolean isAIEnabled() {
        return true;
    }
	
	@Override
	public int getMaxHealth() {
		return 100;
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

}
