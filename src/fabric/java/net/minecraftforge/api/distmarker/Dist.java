package net.minecraftforge.api.distmarker;

public enum Dist {
    CLIENT {
        @Override
        public boolean isDedicatedServer() {
            return false;
        }
        
        @Override
        public boolean isClient() {
            return true;
        }
    },
    DEDICATED_SERVER {
        @Override
        public boolean isDedicatedServer() {
            return true;
        }
        
        @Override
        public boolean isClient() {
            return false;
        }
    };
    
    public abstract boolean isDedicatedServer();
    
    public abstract boolean isClient();
}
