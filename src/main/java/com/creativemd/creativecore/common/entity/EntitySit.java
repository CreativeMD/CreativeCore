package com.creativemd.creativecore.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntitySit extends Entity{
	
	public EntitySit(World world, double x, double y, double z) {
		super(world);
		noClip = true;
        preventEntitySpawning = true;
        width = 0.0F;
        height = 0.0F;
        setPosition(x, y, z);
	}
	
	public EntitySit(World world)
	{
		super(world);
		noClip = true;
        preventEntitySpawning = true;
        width = 0.0F;
        height = 0.0F;
	}
	
	@Override
	public boolean canBePushed()
    {
        return false;
    }
	
	@Override
	public void onUpdate()
    {
		super.onUpdate();
		if(!worldObj.isRemote && !isBeingRidden())
			this.setDead();
    }
	
	/*@Override
	@SideOnly(Side.CLIENT)
    public float getShadowSize()
    {
        return 0.0F;
    }*/
	
	@Override
	public double getMountedYOffset()
    {
        return 0;
    }
	
	protected boolean isAIEnabled()
    {
        return false;
    }
	
	@Override
	protected void entityInit() {
		
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		
	}
}
