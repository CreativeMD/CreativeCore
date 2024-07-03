package team.creative.creativecore.common.config.premade;

import org.apache.commons.lang3.ArrayUtils;

import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.api.IConfigObject;

public class SelectableConfig<T> implements IConfigObject {
    
    private T[] array;
    private int selected;
    private final int defaultSelected;
    
    public SelectableConfig(int selected, T... array) {
        this.selected = selected;
        this.defaultSelected = selected;
        this.array = array;
    }
    
    public void reset() {
        this.selected = defaultSelected;
    }
    
    public void select(int index) {
        if (selected >= 0 && selected < array.length)
            selected = index;
        else
            reset();
    }
    
    public T get() {
        if (selected >= 0 && selected < array.length)
            return array[selected];
        return null;
    }
    
    public T[] getArray() {
        return array;
    }
    
    public int getSelected() {
        return selected;
    }
    
    public void updateArray(T[] array, T fallBack) {
        T pre = get();
        this.array = array;
        T post = get();
        if (pre == null || post == null)
            select(ArrayUtils.indexOf(array, fallBack));
        else if (!pre.equals(post)) {
            int index = ArrayUtils.indexOf(array, pre);
            if (index == -1)
                select(ArrayUtils.indexOf(array, fallBack));
            else
                select(index);
        }
    }
    
    @Override
    public boolean isDefault(Side side) {
        return selected == defaultSelected;
    }
    
    @Override
    public void restoreDefault(Side side, boolean ignoreRestart) {
        reset();
    }
    
    @Override
    public void configured(Side side) {}
    
}
