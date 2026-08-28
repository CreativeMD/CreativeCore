package team.creative.creativecore.common.util.math.origin;

import org.joml.Quaterniond;

import team.creative.creativecore.common.util.math.vec.Vec3d;

public class ChildVecOrigin extends VecOrigin {
    
    public IVecOrigin parent;
    
    public ChildVecOrigin(IVecOrigin parent, Vec3d center) {
        super(center);
        this.parent = parent;
        updatePose();
    }
    
    @Override
    protected IOriginPose generatePose(double offX, double offY, double offZ, double rotX, double rotY, double rotZ) {
        if (parent == null)
            return null;
        return new ChildOriginPose(parent.pose(), new Vec3d(offX, offY, offZ), new Quaterniond().rotationXYZ(rotX, rotY, rotZ), center().copy());
    }
    
    @Override
    protected IOriginPose generatePose(double offX, double offY, double offZ, double rotX, double rotY, double rotZ, float partialTick) {
        if (parent == null)
            return null;
        return new ChildOriginPose(parent.pose(partialTick), new Vec3d(offX, offY, offZ), new Quaterniond().rotationXYZ(rotX, rotY, rotZ), center().copy());
    }
    
    @Override
    public boolean hasChanged() {
        return super.hasChanged() || parent.hasChanged();
    }
    
    @Override
    public IVecOrigin getParent() {
        return parent;
    }
    
    @Override
    protected VecOrigin createInternalCopy() {
        return new ChildVecOrigin(parent, new Vec3d(center()));
    }
    
}
