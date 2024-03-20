package team.creative.creativecore.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;

@Mixin(VoxelShape.class)
public interface VoxelShapeAccessor {
    
    @Accessor
    @Mutable
    public void setShape(DiscreteVoxelShape shape);
    
}
