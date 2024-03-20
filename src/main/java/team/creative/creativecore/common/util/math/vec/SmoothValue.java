package team.creative.creativecore.common.util.math.vec;

public class SmoothValue {
    
    protected double aimed;
    protected double current;
    protected double before;
    protected long timestamp;
    
    public final long time;
    
    public SmoothValue(long time, double initalValue) {
        this.time = time;
        setStart(initalValue);
    }
    
    public SmoothValue(long time) {
        this(time, 0);
    }
    
    public void setStart(double value) {
        this.aimed = value;
        this.current = value;
        this.before = value;
        this.timestamp = 0;
    }
    
    public void add(double value) {
        set(aimed + value);
    }
    
    public void set(double value) {
        timestamp = System.currentTimeMillis();
        this.aimed = value;
        before = this.current;
    }
    
    public void tick() {
        if (timestamp != 0) {
            if (timestamp + time <= System.currentTimeMillis()) {
                current = aimed;
                before = current;
                timestamp = 0;
            } else
                current = before + (aimed - before) * ((System.currentTimeMillis() - timestamp) / (double) time);
        }
    }
    
    public double current() {
        return current;
    }
    
    public double aimed() {
        return aimed;
    }
    
}
