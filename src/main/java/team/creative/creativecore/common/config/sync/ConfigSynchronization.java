package team.creative.creativecore.common.config.sync;

import net.minecraftforge.api.distmarker.Dist;

public enum ConfigSynchronization {
    
    CLIENT {
        @Override
        public boolean useFolder(boolean forceSynchronization, Dist side) {
            if (forceSynchronization)
                return side.isDedicatedServer();
            return side.isClient();
        }
        
        @Override
        public boolean useValue(boolean forceSynchronization, Dist side) {
            if (forceSynchronization)
                return side.isDedicatedServer();
            return side.isClient();
        }
    },
    UNIVERSAL {
        @Override
        public boolean useFolder(boolean forceSynchronization, Dist side) {
            return true;
        }
        
        @Override
        public boolean useValue(boolean forceSynchronization, Dist side) {
            return side.isDedicatedServer();
        }
    },
    SERVER {
        @Override
        public boolean useFolder(boolean forceSynchronization, Dist side) {
            return side.isDedicatedServer();
        }
        
        @Override
        public boolean useValue(boolean forceSynchronization, Dist side) {
            return side.isDedicatedServer();
        }
    };
    
    public abstract boolean useFolder(boolean forceSynchronization, Dist side);
    
    public abstract boolean useValue(boolean forceSynchronization, Dist side);
    
}
