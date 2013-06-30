package openperipheral.common.entity;

import java.util.List;

import openperipheral.common.config.ConfigSettings;
import openperipheral.common.core.OPInventory;
import openperipheral.common.entity.ai.EntityAIGotoLocation;
import openperipheral.common.tileentity.TileEntityRobot;
import openperipheral.common.util.BlockUtils;
import openperipheral.common.util.InventoryUtils;
import net.minecraft.entity.Entity;
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
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class EntityRobot extends EntityCreature {

	private int controllerX = 0;
	private int controllerY = 0;
	private int controllerZ = 0;
	private float weaponSpin = 0.f;

	public double locationTargetX = 0;
	public double locationTargetY = 0;
	public double locationTargetZ = 0;
	public boolean shouldMoveToTarget = false;
	
	protected IInventory inventory = new OPInventory("robot", false, 6);
	
	public EntityRobot(World par1World) {
		super(par1World);
		this.health = this.getMaxHealth();
		this.setSize(1F, 3F);
		this.moveSpeed = 0.22F;
		this.getNavigator().setAvoidsWater(true);
		this.tasks.addTask(0, new EntityAIGotoLocation(this));
		this.texture = String.format("%s/models/robot.png", ConfigSettings.TEXTURES_PATH);
	}

	public float getMoveSpeed() {
		return moveSpeed;
	}
	
	/**
	 * Path finding range
	 */
	@Override
    protected int func_96121_ay() {
        return 32;
    }
	
	/** 
	 * doesn't appear to work
	 */
	@Override
	public boolean canBePushed() {
		return false;
    }
	
	public void suckUp() {
		List<EntityItem> entities = worldObj.getEntitiesWithinAABB(EntityItem.class,
				AxisAlignedBB.getAABBPool().getAABB(
						posX - 2,
						posY - 2,
						posZ - 2, 
						posX + 3,
						posY + 3,
						posZ + 3));
		
		for (EntityItem entity : entities) {
		
			if (entity.isDead){
				continue;
			}
			
			ItemStack stack = entity.getEntityItem(); 
			
			if (stack != null) {
				
				InventoryUtils.insertItemIntoInventory(inventory, stack);
				if (stack.stackSize == 0) {
					entity.setDead();
				}	
			}
		}
	}
	
	public void onEntityUpdate() {
		super.onEntityUpdate();
		if (!worldObj.isRemote) {
			if (!this.isDead) {
				TileEntityRobot controller = getController();
				if (controller == null) {
					this.setDead();
				}
			}
		}
		else
		{
			this.weaponSpin += .1f;
		}
	}
	
	protected void updateAITasks()
    {
		float oldPitch = rotationPitch;
		super.updateAITasks();
		this.rotationPitch = oldPitch;
    }

	protected boolean isAIEnabled() {
		return true;
	}

	@Override
	protected void playStepSound(int par1, int par2, int par3, int par4) {
		this.playSound("openperipheral.robotstepping", 1F, 1F);
	}

	@Override
	protected String getDeathSound() {
		return "openperipheral.robotdead";
	}

	@Override
	protected String getHurtSound() {
		return "openperipheral.robothurt";
	}

	@Override
	public int getMaxHealth() {
		return 40;
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	public void setController(TileEntityRobot tileEntityRobot) {
		controllerX = tileEntityRobot.xCoord;
		controllerY = tileEntityRobot.yCoord;
		controllerZ = tileEntityRobot.zCoord;
	}

	public TileEntityRobot getController() {
		if (worldObj.blockExists(controllerX, controllerY, controllerZ)) {
			TileEntity tile = worldObj.getBlockTileEntity(controllerX, controllerY, controllerZ);
			if (tile != null && tile instanceof TileEntityRobot) {
				return (TileEntityRobot) tile;
			}
		}
		return null;
	}

	public void setLocationTarget(double x, double y, double z) {
		locationTargetX = x;
		locationTargetY = y;
		locationTargetZ = z;
		shouldMoveToTarget = true;
	}

	public void onNoPathAvailable() {
		TileEntityRobot controller = getController();
		if (controller != null) {
			controller.onNoPathAvailable();
		}
	}
	
	public void onPathFinished() {
		TileEntityRobot controller = getController();
		if (controller != null) {
			controller.onPathFinished();
		}
	}

	public void dropAll() {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack != null) {
				EntityItem entityitem = new EntityItem(worldObj, posX, posY, posZ, stack);
				Vec3 lookVec = this.getLookVec();
				entityitem.setVelocity(lookVec.xCoord * 0.3, lookVec.yCoord * 0.3, lookVec.zCoord * 0.3);
				if (stack.hasTagCompound()) {
					entityitem.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
				}
				entityitem.delayBeforeCanPickup = 10;
				worldObj.spawnEntityInWorld(entityitem);
			}
			inventory.setInventorySlotContents(i, null);
		}
		
	}

	@Override
    protected void jump()
    {
        jump(1);
    }
    
    public void jump(double factor)
    {
		this.motionY = 0.41999998688697815D;
		
		this.motionY *= factor;

        if (this.isSprinting())
        {
            float f = this.rotationYaw * 0.017453292F;
            this.motionX -= (double)(MathHelper.sin(f) * 0.2F);
            this.motionZ += (double)(MathHelper.cos(f) * 0.2F);
        }

        this.isAirBorne = true;
        ForgeHooks.onLivingJump(this);
    }

	public void fire() {
		EntityLazer lazer = new EntityLazer(worldObj, this);
		worldObj.spawnEntityInWorld(lazer);
	}

	public float getWeaponSpin()
	{
		return weaponSpin;
	}
	
}
