package team.creative.creativecore.client.render.box;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import team.creative.creativecore.client.render.VertexFormatUtils;
import team.creative.creativecore.common.util.math.base.Facing;

public class QuadGeneratorContext {
    
    public RenderBox box;
    public Facing facing;
    public int color;
    public VertexFormat format;
    public int uvOffset;
    public BlockPos offset;
    public boolean shouldOverrideColor;
    
    public BakedQuad quad;
    public boolean scaleAndOffset;
    
    public float offsetX;
    public float offsetY;
    public float offsetZ;
    
    public float scaleX;
    public float scaleY;
    public float scaleZ;
    
    public float minX;
    public float minY;
    public float minZ;
    public float maxX;
    public float maxY;
    public float maxZ;
    
    public float sizeX;
    public float sizeY;
    public float sizeZ;
    
    public boolean uvInverted;
    public float sizeU;
    public float sizeV;
    
    public QuadGeneratorContext() {}
    
    public void set(VertexFormat format, RenderBox box, Facing facing, int color) {
        this.color = color;
        this.format = format;
        this.facing = facing;
        this.uvOffset = VertexFormatUtils.blockUvOffset() / 4;
        
        this.box = box;
        scaleAndOffset = box.scaleAndOffsetQuads(facing);
        if (scaleAndOffset) {
            if (box.onlyScaleOnceNoOffset(facing)) {
                this.offsetX = this.offsetY = this.offsetZ = 0;
                this.scaleX = this.scaleY = this.scaleZ = box.getOverallScale(facing);
            } else {
                this.offsetX = box.getOffsetX();
                this.offsetY = box.getOffsetY();
                this.offsetZ = box.getOffsetZ();
                this.scaleX = box.getScaleX();
                this.scaleY = box.getScaleY();
                this.scaleZ = box.getScaleZ();
            }
            
        } else {
            this.offsetX = this.offsetY = this.offsetZ = 0;
            this.scaleX = this.scaleY = this.scaleZ = 0;
        }
    }
    
    public void setQuad(BakedQuad quad, boolean overrideTint, int defaultColor) {
        this.quad = quad;
        this.shouldOverrideColor = overrideTint && (defaultColor == -1 || quad.isTinted()) && color != -1;
    }
    
    public void setBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        
        this.sizeX = this.maxX - this.minX;
        this.sizeY = this.maxY - this.minY;
        this.sizeZ = this.maxZ - this.minZ;
    }
    
    public boolean hasBounds() {
        return switch (facing.axis) {
            case X -> minY != 0 || maxY != 1 || minZ != 0 || maxZ != 1;
            case Y -> minX != 0 || maxX != 1 || minZ != 0 || maxZ != 1;
            case Z -> minX != 0 || maxX != 1 || minY != 0 || maxY != 1;
        };
    }
    
    public void clear() {
        box = null;
        facing = null;
        format = null;
        quad = null;
    }
}
