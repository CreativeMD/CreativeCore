package team.creative.creativecore;

public enum Side {
    
    CLIENT {
        @Override
        public boolean isClient() {
            return true;
        }
        
        @Override
        public boolean isServer() {
            return false;
        }
    },
    SERVER {
        @Override
        public boolean isClient() {
            return false;
        }
        
        @Override
        public boolean isServer() {
            return true;
        }
    };
    
    public abstract boolean isClient();
    
    public abstract boolean isServer();
    
}
