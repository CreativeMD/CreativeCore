package org.spongepowered.common.interfaces.world;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;

public interface IMixinWorld {
	
	long getWeatherStartTime();

    void setWeatherStartTime(long weatherStartTime);

    @Nullable
    EntityPlayer getClosestPlayerToEntityWhoAffectsSpawning(net.minecraft.entity.Entity entity, double d1tance);

    @Nullable
    EntityPlayer getClosestPlayerWhoAffectsSpawning(double x, double y, double z, double distance);
}
