package team.creative.creativecore.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
@Environment(EnvType.CLIENT)
public class EntitySound extends AbstractTickableSoundInstance {
    
    private Entity entity;
    
    public EntitySound(SoundEvent event, Entity entity, float volume, float pitch, SoundSource category) {
        super(event, category);
        this.entity = entity;
        this.volume = volume;
        this.pitch = pitch;
    }
    
    @Override
    public void tick() {
        Minecraft mc = Minecraft.getInstance();
        Entity view = mc.cameraEntity;
        float partialTicks = mc.getFrameTime();
        
        if (view == null) {
            stop();
            return;
        }
        
        Vec3 viewVec = view.getEyePosition(partialTicks);
        AABB bb = entity.getBoundingBox();
        
        if (bb.contains(viewVec)) {
            x = (float) viewVec.x;
            y = (float) viewVec.y;
            z = (float) viewVec.z;
            return;
        }
        
        // Calculate closest point
        if (viewVec.x > bb.minX)
            if (viewVec.x > bb.maxX)
                x = (float) bb.maxX;
            else
                x = (float) viewVec.x;
        else
            x = (float) bb.minX;
        
        if (viewVec.y > bb.minY)
            if (viewVec.y > bb.maxY)
                y = (float) bb.maxY;
            else
                y = (float) viewVec.y;
        else
            y = (float) bb.minY;
        
        if (viewVec.z > bb.minZ)
            if (viewVec.z > bb.maxZ)
                z = (float) bb.maxZ;
            else
                z = (float) viewVec.z;
        else
            z = (float) bb.minZ;
    }
    
}
