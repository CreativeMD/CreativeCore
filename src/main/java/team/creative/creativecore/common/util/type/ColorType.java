package team.creative.creativecore.common.util.type;

public enum ColorType {
    
    RED {
        
        @Override
        public int getBrightest() {
            return 0xFF000000;
        }
    },
    GREEN {
        
        @Override
        public int getBrightest() {
            return 0x00FF0000;
        }
    },
    BLUE {
        
        @Override
        public int getBrightest() {
            return 0x0000FF00;
        }
    },
    ALPHA {
        @Override
        public int getBrightest() {
            return 0x000000FF;
        }
    };
    
    public abstract int getBrightest();
    
}
