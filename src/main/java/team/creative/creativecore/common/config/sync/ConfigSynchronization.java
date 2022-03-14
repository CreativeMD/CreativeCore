package team.creative.creativecore.common.config.sync;

import team.creative.creativecore.Side;

public enum ConfigSynchronization {
    
    CLIENT {
        @Override
        public boolean useFolder(boolean forceSynchronization, Side side) {
            if (forceSynchronization)
                return side.isServer();
            return side.isClient();
        }
        
        @Override
        public boolean useValue(boolean forceSynchronization, Side side) {
            if (forceSynchronization)
                return side.isServer();
            return side.isClient();
        }
    },
    UNIVERSAL {
        @Override
        public boolean useFolder(boolean forceSynchronization, Side side) {
            return true;
        }
        
        @Override
        public boolean useValue(boolean forceSynchronization, Side side) {
            return side.isServer();
        }
    },
    SERVER {
        @Override
        public boolean useFolder(boolean forceSynchronization, Side side) {
            return side.isServer();
        }
        
        @Override
        public boolean useValue(boolean forceSynchronization, Side side) {
            return side.isServer();
        }
    };
    
    public abstract boolean useFolder(boolean forceSynchronization, Side side);
    
    public abstract boolean useValue(boolean forceSynchronization, Side side);
    
}
