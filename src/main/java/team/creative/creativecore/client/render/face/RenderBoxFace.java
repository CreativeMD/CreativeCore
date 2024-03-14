package team.creative.creativecore.client.render.face;

import java.util.List;

import team.creative.creativecore.common.util.math.geo.VectorFan;

public abstract class RenderBoxFace {
    
    public static final RenderBoxFace RENDER = new RenderBoxFaceStatic() {
        
        @Override
        public boolean shouldRender() {
            return true;
        }
    };
    
    public static final RenderBoxFace NOT_RENDER = new RenderBoxFaceStatic() {
        
        @Override
        public boolean shouldRender() {
            return false;
        }
    };
    
    public abstract boolean shouldRender();
    
    public abstract boolean hasCachedFans();
    
    public abstract List<VectorFan> getCachedFans();
    
    public abstract float getScale();
    
    private static abstract class RenderBoxFaceStatic extends RenderBoxFace {
        
        @Override
        public boolean hasCachedFans() {
            return false;
        }
        
        @Override
        public List<VectorFan> getCachedFans() {
            return null;
        }
        
        @Override
        public float getScale() {
            return 1;
        }
        
    }
}
