package openperipheral.common.entity;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import openperipheral.codechicken.core.vec.Matrix4;
import openperipheral.codechicken.core.vec.Rotation;
import openperipheral.codechicken.core.vec.Vector3;
import openperipheral.common.util.BlockUtils;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.common.registry.IThrowableEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityLazer extends Entity implements IThrowableEntity, IEntityAdditionalSpawnData {

	public EntityLiving shootingEntity;
	private int ticksAlive;
	public double directionX;
	public double directionY;
	public double directionZ;
	public boolean isExplosive = false;
	private Entity thrower;

	public EntityLazer(World world) {
		super(world);
	}

	public EntityLazer(World world, EntityCreature robot) {
		super(world);

		double radPitch = Math.toRadians(robot.rotationPitch);
		double radYaw = -Math.toRadians(robot.rotationYawHead);
		
		Vector3 velocity = new Vector3(0, 0, 1).
							apply(new Rotation(radPitch, 1, 0, 0)
								.with(new Rotation(radYaw, 0, 1, 0)));

		Vector3 pos = new Vector3(-20/16D, 2D + (2D/16), 0)
						.apply(new Rotation(radYaw, 0, 1, 0))
						.add(Vector3.fromEntity(robot));

		this.setLocationAndAngles(pos.x, pos.y, pos.z, -robot.rotationYawHead, robot.rotationPitch);
		this.setPosition(this.posX, this.posY, this.posZ);
		this.yOffset = 0.0F;
		this.motionX = this.motionY = this.motionZ = 0.0D;

		this.directionX = velocity.x;
		this.directionY = velocity.y;
		this.directionZ = velocity.z;
	}

	public void setExplosive(boolean explosive) {
		isExplosive = explosive;
	}
	
	@Override
	public Entity getThrower() {
		return thrower;
	}

	@Override
	public void setThrower(Entity entity) {
		thrower = entity;
	}

	@Override
	protected void entityInit() {
	}

	/**
	 * temp
	 */
	@Override
	public boolean isInRangeToRenderDist(double par1) {
		return true;
	}

	public void onUpdate() {
		super.onUpdate();
		
		if (++this.ticksAlive > 100) {
			this.setDead();
		}

		Vec3 vec3 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
		Vec3 vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
		MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks(vec3, vec31);
		vec3 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
		vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

		if (movingobjectposition != null) {
			vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord,
					movingobjectposition.hitVec.zCoord);
		}

		Entity entity = null;
		List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this,
				this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
		double d0 = 0.0D;

		for (int j = 0; j < list.size(); ++j) {
			Entity entity1 = (Entity) list.get(j);

			if (entity1.canBeCollidedWith() && (!entity1.isEntityEqual(this.shootingEntity) || this.ticksAlive >= 25)) {
				float f = 0.3F;
				AxisAlignedBB axisalignedbb = entity1.boundingBox.expand((double) f, (double) f, (double) f);
				MovingObjectPosition movingobjectposition1 = axisalignedbb.calculateIntercept(vec3, vec31);

				if (movingobjectposition1 != null) {
					double d1 = vec3.distanceTo(movingobjectposition1.hitVec);

					if (d1 < d0 || d0 == 0.0D) {
						entity = entity1;
						d0 = d1;
					}
				}
			}
		}

		if (entity != null) {
			movingobjectposition = new MovingObjectPosition(entity);
		}

		if (movingobjectposition != null) {
			this.onImpact(movingobjectposition);
		}

		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;
		float f1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
		float f2 = this.getMotionFactor();
		
		this.motionX = directionX;
		this.motionY = directionY;
		this.motionZ = directionZ;
		
		this.motionX *= (double) f2;
		this.motionY *= (double) f2;
		this.motionZ *= (double) f2;
		this.setPosition(this.posX, this.posY, this.posZ);
	}

	/**
	 * Return the motion factor for this projectile. The factor is multiplied by
	 * the original motion.
	 */
	protected float getMotionFactor() {
		return 1.2F;
	}

	/**
	 * Called when this EntityFireball hits a block or entity.
	 */
	protected void onImpact(MovingObjectPosition mop) {
		if (mop == null) {
			return;
		}
		if (mop.typeOfHit == EnumMovingObjectType.TILE) {
			onBlockHit(mop);
		}else if (mop.typeOfHit == EnumMovingObjectType.ENTITY) {
			onEntityHit(mop);
		}
		worldObj.createExplosion(this, posX, posY, posZ, 2, true);
	}

	private void onEntityHit(MovingObjectPosition mop) {
		
	}

	private void onBlockHit(MovingObjectPosition mop) {
		int x = mop.blockX;
		int y = mop.blockY;
		int z = mop.blockZ;
		int bid = worldObj.getBlockId(x, y, z);
	    Block block = Block.blocksList[bid];
	    if ((bid == 0) || (bid == Block.bedrock.blockID) || (block.getBlockHardness(worldObj, x, y, z) <= -1.0F))
	    {
	    	return;
	    }
	    int metadata = worldObj.getBlockMetadata(x, y, z);
	    ArrayList<ItemStack> stacks = block.getBlockDropped(worldObj, x, y, z, metadata, 0);
	    for (ItemStack stack : stacks) {
	    	BlockUtils.dropItemStackInWorld(worldObj, x, y, z, stack);
	    }
	    worldObj.setBlockToAir(x, y, z);
	    worldObj.playAuxSFX(2001, x, y, z, bid + metadata * 4096);
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		par1NBTTagCompound.setTag("direction", this.newDoubleNBTList(new double[] { this.motionX, this.motionY, this.motionZ }));
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		if (par1NBTTagCompound.hasKey("direction")) {
			NBTTagList nbttaglist = par1NBTTagCompound.getTagList("direction");
			this.motionX = ((NBTTagDouble) nbttaglist.tagAt(0)).data;
			this.motionY = ((NBTTagDouble) nbttaglist.tagAt(1)).data;
			this.motionZ = ((NBTTagDouble) nbttaglist.tagAt(2)).data;
		} else {
			this.setDead();
		}
	}

	/**
	 * Returns true if other Entities should be prevented from moving through
	 * this Entity.
	 */
	public boolean canBeCollidedWith() {
		return true;
	}

	public float getCollisionBorderSize() {
		return 1.0F;
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource par1DamageSource, int par2) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	public float getShadowSize() {
		return 0.0F;
	}

	/**
	 * Gets how bright this entity is.
	 */
	public float getBrightness(float par1) {
		return 1.0F;
	}

	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float par1) {
		return 15728880;
	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {
		try {
			writeStreamData(data);
		} catch (IOException e) {
		}
	}
	
	private void writeStreamData(DataOutput data) throws IOException {
		data.writeBoolean(isExplosive);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		isExplosive = data.readBoolean();
	}
}
