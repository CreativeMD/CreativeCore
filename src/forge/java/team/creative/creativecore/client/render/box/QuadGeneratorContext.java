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
        this.uvOffset = VertexFormatUtils.blockUvOffset();
        
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
        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.minZ = Math.min(minZ, maxZ);
        this.maxX = Math.max(minX, maxX);
        this.maxY = Math.max(minY, maxY);
        this.maxZ = Math.max(minZ, maxZ);
        
        this.sizeX = this.maxX - this.minX;
        this.sizeY = this.maxY - this.minY;
        this.sizeZ = this.maxZ - this.minZ;
    }
    
    public boolean hasBounds() {
        switch (facing.axis) {
            case X:
                return minY != 0 || maxY != 1 || minZ != 0 || maxZ != 1;
            case Y:
                return minX != 0 || maxX != 1 || minZ != 0 || maxZ != 1;
            case Z:
                return minX != 0 || maxX != 1 || minY != 0 || maxY != 1;
        }
        return false;
    }
    
    public void clear() {
        box = null;
        facing = null;
        format = null;
        quad = null;
    }
}
