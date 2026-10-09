package team.creative.creativecore.common.util.math.origin;

import team.creative.creativecore.common.util.math.vec.Vec3d;

public interface IVecOrigin {
    
    public double offX();
    
    public double offY();
    
    public double offZ();
    
    public double rotX();
    
    public double rotY();
    
    public double rotZ();
    
    public double offXLast();
    
    public double offYLast();
    
    public double offZLast();
    
    public double rotXLast();
    
    public double rotYLast();
    
    public double rotZLast();
    
    public boolean isRotated();
    
    public void setLast(double offX, double offY, double offZ, double rotX, double rotY, double rotZ);
    
    public void set(double offX, double offY, double offZ, double rotX, double rotY, double rotZ);
    
    public default void set(IVecOrigin origin) {
        setLast(origin.offXLast(), origin.offYLast(), origin.offZLast(), origin.rotXLast(), origin.rotYLast(), origin.rotZLast());
        set(origin.offX(), origin.offY(), origin.offZ(), origin.rotX(), origin.rotY(), origin.rotZ());
        setChanged();
    }
    
    public Vec3d deltaMovement();
    
    public void deltaMovement(Vec3d value);
    
    public Vec3d center();
    
    public void setCenter(Vec3d vec);
    
    public Vec3d collisionCenterCopy();
    
    public void tick();
    
    public IVecOrigin getParent();
    
    public IOriginPose pose();
    
    public IOriginPose pose(float partialTick);
    
    public boolean hasChanged();
    
    public void setChanged();
    
    public void processedChange();
    
    public IVecOrigin copy();
    
    public int idPose();
    
    public default IVecOrigin unwrap() {
        return this;
    }
    
}
