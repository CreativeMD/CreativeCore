package team.creative.creativecore.common.gui;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;

import team.creative.creativecore.common.util.type.itr.ConsecutiveIterator;

/** Manages all controls including lists for the use of one destination. */
public class GuiControls implements Iterable<GuiControl> {
    
    protected final List<GuiControl> controls = new CopyOnWriteArrayList<>();
    protected final List<GuiControlDistHandler> distControls = new CopyOnWriteArrayList<>();
    
    protected final List<GuiControl> hoverControls = new CopyOnWriteArrayList<>();
    protected final List<GuiControlDistHandler> distHoverControls = new CopyOnWriteArrayList<>();
    
    public <T extends GuiControl> T get(String name) {
        GuiControl result = get(name, controls);
        if (result != null)
            return (T) result;
        return (T) get(name, hoverControls);
    }
    
    private GuiControl get(String name, List<GuiControl> collection) {
        for (int i = 0; i < collection.size(); i++) {
            GuiControl control = collection.get(i);
            if (control.name.equalsIgnoreCase(name))
                return control;
            else if (control instanceof GuiParent parent) {
                if (control.name.isBlank()) {
                    GuiControl result = parent.get(name);
                    if (result != null)
                        return result;
                } else if (name.startsWith(control.name + "."))
                    return parent.get(name.substring(control.name.length() + 1));
            }
        }
        return null;
    }
    
    /** inserts the given the control before the parameter
     *
     * @param reference
     *            the reference to search for the correct position
     * @param toInsert
     *            the control to be added
     * @return null if the reference could not be found */
    public <T extends GuiControl> T insertControlBefore(GuiControl reference, T toInsert) {
        int index = controls.indexOf(reference);
        if (index == -1)
            return null;
        controls.add(index, toInsert);
        distControls.add(index, toInsert.dist);
        return toInsert;
    }
    
    /** inserts the given the control after the parameter
     *
     * @param reference
     *            the reference to search for the correct position
     * @param toInsert
     *            the control to be added
     * @return null if the reference could not be found */
    public <T extends GuiControl> T insertControlAfter(GuiControl reference, T toInsert) {
        int index = controls.indexOf(reference);
        if (index == -1)
            return null;
        index++;
        if (index == controls.size()) {
            controls.add(toInsert);
            distControls.add(toInsert.dist);
        } else {
            controls.add(index, toInsert);
            distControls.add(index, toInsert.dist);
        }
        return toInsert;
    }
    
    public void add(GuiControl control) {
        controls.add(control);
        distControls.add(control.dist);
    }
    
    public void addHover(GuiControl control) {
        hoverControls.add(control);
        distHoverControls.add(control.dist);
    }
    
    public boolean remove(GuiControl control) {
        int index = controls.indexOf(control);
        if (index != -1) {
            controls.remove(index);
            distControls.remove(index);
            return true;
        }
        index = hoverControls.indexOf(control);
        if (index != -1) {
            hoverControls.remove(index);
            distHoverControls.remove(index);
            return true;
        }
        return false;
    }
    
    public boolean replace(GuiControl oldControl, GuiControl newControl) {
        for (int i = 0; i < controls.size(); i++)
            if (controls.get(i) == oldControl) {
                controls.set(i, newControl);
                distControls.set(i, newControl.dist);
                return true;
            }
        for (int i = 0; i < hoverControls.size(); i++)
            if (hoverControls.get(i) == oldControl) {
                hoverControls.set(i, newControl);
                distHoverControls.set(i, newControl.dist);
                return true;
            }
        return false;
    }
    
    public void remove(String... include) {
        int i = 0;
        while (i < controls.size()) {
            if (ArrayUtils.contains(include, controls.get(i).name)) {
                controls.remove(i);
                distControls.remove(i);
            } else
                i++;
        }
        
        i = 0;
        while (i < hoverControls.size()) {
            if (ArrayUtils.contains(include, hoverControls.get(i).name)) {
                hoverControls.remove(i);
                distHoverControls.remove(i);
            } else
                i++;
        }
    }
    
    public void removeExclude(String... exclude) {
        int i = 0;
        while (i < controls.size()) {
            if (!ArrayUtils.contains(exclude, controls.get(i).name)) {
                controls.remove(i);
                distControls.remove(i);
            } else
                i++;
        }
        
        i = 0;
        while (i < hoverControls.size()) {
            if (!ArrayUtils.contains(exclude, hoverControls.get(i).name)) {
                hoverControls.remove(i);
                distHoverControls.remove(i);
            } else
                i++;
        }
    }
    
    public boolean isEmpty() {
        return controls.isEmpty() && hoverControls.isEmpty();
    }
    
    public void clear() {
        controls.clear();
        distControls.clear();
        hoverControls.clear();
        distHoverControls.clear();
    }
    
    public int controlSize() {
        return controls.size();
    }
    
    public int hoverSize() {
        return hoverControls.size();
    }
    
    public int totalSize() {
        return controls.size() + hoverControls.size();
    }
    
    @Override
    public Iterator<GuiControl> iterator() {
        if (hoverControls.isEmpty()) // Performance optimisation
            return controls.iterator();
        return new ConsecutiveIterator<>(hoverControls, controls);
    }
    
    public Iterable<GuiControlDistHandler> distControls() {
        return distControls;
    }
    
    public Iterable<GuiControlDistHandler> distHoverControls() {
        return distHoverControls;
    }
    
    public Iterable<GuiControlDistHandler> distAll() {
        return new ConsecutiveIterator<>(distControls, distHoverControls);
    }
    
    public Stream<GuiControlDistHandler> streamDistControls() {
        return distControls.stream();
    }
    
    public Stream<GuiControlDistHandler> streamDistHoverControls() {
        return distHoverControls.stream();
    }
    
    public ListIterator<GuiControlDistHandler> distListControls(int index) {
        return distControls.listIterator(index);
    }
    
    public ListIterator<GuiControlDistHandler> distHoverListControls(int index) {
        return distHoverControls.listIterator(index);
    }
    
}