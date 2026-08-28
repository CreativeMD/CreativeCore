package team.creative.creativecore.common.util.math.origin;

import org.joml.Quaterniond;

import team.creative.creativecore.common.util.math.vec.Vec3d;

public class VecOrigin implements IVecOrigin {
    
    protected boolean rotated = false;
    
    private final Vec3d center;
    
    private IOriginPose pose;
    private IOriginPose renderPose;
    private float renderPoseTick = -1;
    
    protected double rotX;
    protected double rotY;
    protected double rotZ;
    protected double rotXLast;
    protected double rotYLast;
    protected double rotZLast;
    
    protected double offX;
    protected double offY;
    protected double offZ;
    protected double offXLast;
    protected double offYLast;
    protected double offZLast;
    
    protected Vec3d deltaMovement;
    
    public VecOrigin(Vec3d center) {
        this.center = center;
        updatePose();
    }
    
    @Override
    public double offX() {
        return offX;
    }
    
    @Override
    public double offY() {
        return offY;
    }
    
    @Override
    public double offZ() {
        return offZ;
    }
    
    @Override
    public double rotX() {
        return rotX;
    }
    
    @Override
    public double rotY() {
        return rotY;
    }
    
    @Override
    public double rotZ() {
        return rotZ;
    }
    
    @Override
    public boolean isRotated() {
        return rotated;
    }
    
    @Override
    public void setLast(double offX, double offY, double offZ, double rotX, double rotY, double rotZ) {
        this.offXLast = offX;
        this.offYLast = offY;
        this.offZLast = offZ;
        this.rotXLast = rotX;
        this.rotYLast = rotY;
        this.rotZLast = rotZ;
    }
    
    @Override
    public void set(double offX, double offY, double offZ, double rotX, double rotY, double rotZ) {
        this.offX = offX;
        this.offY = offY;
        this.offZ = offZ;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        
        updatePose();
    }
    
    protected void updatePose() {
        rotated = rotX % 360 != 0 || rotY % 360 != 0 || rotZ % 360 != 0;
        pose = generatePose(offX, offY, offZ, rotX, rotY, rotZ);
        renderPose = null;
        renderPoseTick = -1;
    }
    
    protected IOriginPose generatePose(double offX, double offY, double offZ, double rotX, double rotY, double rotZ) {
        return new OriginPose(new Vec3d(offX, offY, offZ), new Quaterniond().rotationXYZ(rotX, rotY, rotZ), center.copy());
    }
    
    protected IOriginPose generatePose(double offX, double offY, double offZ, double rotX, double rotY, double rotZ, float partialTick) {
        return new OriginPose(new Vec3d(offX, offY, offZ), new Quaterniond().rotationXYZ(rotX, rotY, rotZ), center.copy());
    }
    
    @Override
    public IOriginPose pose() {
        return pose;
    }
    
    @Override
    public IOriginPose pose(float partialTick) {
        if (partialTick == renderPoseTick)
            return renderPose;
        if (partialTick == 1)
            return pose;
        
        renderPose = generatePose(offXLast + (offX - offXLast()) * partialTick, offYLast + (offY - offYLast()) * partialTick, offZLast + (offZ - offZLast()) * partialTick,
            rotXLast + (rotX - rotXLast()) * partialTick, rotYLast + (rotY - rotYLast()) * partialTick, rotZLast + (rotZ - rotZLast()) * partialTick, partialTick);
        renderPoseTick = partialTick;
        
        return renderPose;
    }
    
    @Override
    public Vec3d deltaMovement() {
        return deltaMovement;
    }
    
    @Override
    public void deltaMovement(Vec3d value) {
        this.deltaMovement = value;
    }
    
    @Override
    public Vec3d center() {
        return center;
    }
    
    @Override
    public void setCenter(Vec3d vec) {
        this.center.set(vec);
    }
    
    @Override
    public double offXLast() {
        return offXLast;
    }
    
    @Override
    public double offYLast() {
        return offYLast;
    }
    
    @Override
    public double offZLast() {
        return offZLast;
    }
    
    @Override
    public double rotXLast() {
        return rotXLast;
    }
    
    @Override
    public double rotYLast() {
        return rotYLast;
    }
    
    @Override
    public double rotZLast() {
        return rotZLast;
    }
    
    @Override
    public void tick() {
        rotXLast = rotX;
        rotYLast = rotY;
        rotZLast = rotZ;
        offXLast = offX;
        offYLast = offY;
        offZLast = offZ;
    }
    
    protected VecOrigin createInternalCopy() {
        return new VecOrigin(new Vec3d(this.center));
    }
    
    @Override
    public IVecOrigin copy() {
        VecOrigin copy = createInternalCopy();
        copy.set(this);
        return copy;
    }
    
    @Override
    public IVecOrigin getParent() {
        return null;
    }
}
