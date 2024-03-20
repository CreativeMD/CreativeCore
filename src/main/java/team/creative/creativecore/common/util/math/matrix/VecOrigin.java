package team.creative.creativecore.common.util.math.matrix;

import team.creative.creativecore.common.util.math.collision.MatrixUtils;
import team.creative.creativecore.common.util.math.vec.Vec3d;

public class VecOrigin implements IVecOrigin {
    
    public VecOrigin(Vec3d center) {
        this.center = center;
    }
    
    protected boolean rotated = false;
    
    private final Vec3d center;
    private Vec3d translation = new Vec3d(0, 0, 0);
    private Matrix3 rotation = MatrixUtils.createIdentityMatrix();
    private final Matrix3 rotationInv = rotation;
    
    protected double rotX;
    protected double rotY;
    protected double rotZ;
    protected double rotXLast;
    protected double rotYLast;
    protected double rotZLast;
    
    protected double offsetX;
    protected double offsetY;
    protected double offsetZ;
    protected double offsetXLast;
    protected double offsetYLast;
    protected double offsetZLast;
    
    @Override
    public double offX() {
        return offsetX;
    }
    
    @Override
    public double offY() {
        return offsetY;
    }
    
    @Override
    public double offZ() {
        return offsetZ;
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
    
    protected void updateRotated() {
        rotated = rotX % 360 != 0 || rotY % 360 != 0 || rotZ % 360 != 0;
        rotation = MatrixUtils.createRotationMatrix(rotX, rotY, rotZ);
        rotationInv.invert(rotation);
    }
    
    protected void updateTranslation() {
        translation.set(offsetX, offsetY, offsetZ);
    }
    
    @Override
    public void offX(double value) {
        this.offsetX = value;
        updateTranslation();
    }
    
    @Override
    public void offY(double value) {
        this.offsetY = value;
        updateTranslation();
    }
    
    @Override
    public void offZ(double value) {
        this.offsetZ = value;
        updateTranslation();
    }
    
    @Override
    public void off(double x, double y, double z) {
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
        updateTranslation();
    }
    
    @Override
    public void rotX(double value) {
        this.rotX = value;
        updateRotated();
    }
    
    @Override
    public void rotY(double value) {
        this.rotY = value;
        updateRotated();
    }
    
    @Override
    public void rotZ(double value) {
        this.rotZ = value;
        updateRotated();
    }
    
    @Override
    public void rot(double x, double y, double z) {
        this.rotX = x;
        this.rotY = y;
        this.rotZ = z;
        updateRotated();
    }
    
    @Override
    public Vec3d center() {
        return center;
    }
    
    @Override
    public Matrix3 rotation() {
        return rotation;
    }
    
    @Override
    public Matrix3 rotationInv() {
        return rotationInv;
    }
    
    @Override
    public Vec3d translation() {
        return translation;
    }
    
    @Override
    public void setCenter(Vec3d vec) {
        this.center.set(vec);
    }
    
    @Override
    public double offXLast() {
        return offsetXLast;
    }
    
    @Override
    public double offYLast() {
        return offsetYLast;
    }
    
    @Override
    public double offZLast() {
        return offsetZLast;
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
        offsetXLast = offsetX;
        offsetYLast = offsetY;
        offsetZLast = offsetZ;
    }
    
    protected VecOrigin createInternalCopy() {
        return new VecOrigin(new Vec3d(this.center));
    }
    
    @Override
    public IVecOrigin copy() {
        VecOrigin copy = createInternalCopy();
        copy.translation = new Vec3d(translation);
        copy.rotated = this.rotated;
        
        copy.rotX = this.rotX;
        copy.rotY = this.rotY;
        copy.rotZ = this.rotZ;
        copy.rotXLast = this.rotXLast;
        copy.rotYLast = this.rotYLast;
        copy.rotZLast = this.rotZLast;
        
        copy.offsetX = this.offsetX;
        copy.offsetY = this.offsetY;
        copy.offsetZ = this.offsetZ;
        copy.offsetXLast = this.offsetXLast;
        copy.offsetYLast = this.offsetYLast;
        copy.offsetZLast = this.offsetZLast;
        
        copy.updateRotated();
        copy.updateTranslation();
        return copy;
    }
    
    @Override
    public IVecOrigin getParent() {
        return null;
    }
}
