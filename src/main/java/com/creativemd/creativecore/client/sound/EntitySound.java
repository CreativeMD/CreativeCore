package com.creativemd.creativecore.client.sound;

import com.creativemd.creativecore.common.utils.mc.TickUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntitySound extends MovingSound {
	
	public Entity entity;
	
	public EntitySound(SoundEvent soundIn, Entity entity, float volume, float pitch, SoundCategory categoryIn) {
		super(soundIn, categoryIn);
		this.entity = entity;
		this.volume = volume;
		this.pitch = pitch;
	}
	
	@Override
	public void update() {
		Entity view = Minecraft.getMinecraft().getRenderViewEntity();
		float partialTicks = TickUtils.getPartialTickTime();
		
		Vec3d viewVec = view.getPositionEyes(partialTicks);
		AxisAlignedBB bb = entity.getEntityBoundingBox();
		
		if (bb.contains(viewVec)) {
			xPosF = (float) viewVec.x;
			yPosF = (float) viewVec.y;
			zPosF = (float) viewVec.z;
			return;
		}
		
		// Calculate closest point
		if (viewVec.x > bb.minX)
			if (viewVec.x > bb.maxX)
				xPosF = (float) bb.maxX;
			else
				xPosF = (float) viewVec.x;
		else
			xPosF = (float) bb.minX;
		
		if (viewVec.y > bb.minY)
			if (viewVec.y > bb.maxY)
				yPosF = (float) bb.maxY;
			else
				yPosF = (float) viewVec.y;
		else
			yPosF = (float) bb.minY;
		
		if (viewVec.z > bb.minZ)
			if (viewVec.z > bb.maxZ)
				zPosF = (float) bb.maxZ;
			else
				zPosF = (float) viewVec.z;
		else
			zPosF = (float) bb.minZ;
	}
	
}
