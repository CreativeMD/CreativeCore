package team.creative.creativecore.common.config.premade;

import org.apache.commons.lang3.ArrayUtils;

public class SelectableConfig<T> {
    
    private T[] array;
    private int selected;
    private int defaultSelected;
    
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
    
}
