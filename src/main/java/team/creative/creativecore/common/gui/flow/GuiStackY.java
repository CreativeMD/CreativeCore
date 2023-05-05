package team.creative.creativecore.common.gui.flow;

import java.util.List;

import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.util.math.Maths;
import team.creative.creativecore.common.util.type.list.MarkIterator;
import team.creative.creativecore.common.util.type.list.MarkList;

public class GuiStackY extends GuiFlow {
    
    @Override
    public int minWidth(List<GuiChildControl> controls, int spacing, int availableWidth) {
        int width = 0;
        for (GuiChildControl child : controls)
            width = Math.max(width, child.getMinWidth(availableWidth));
        return width;
    }
    
    @Override
    public int preferredWidth(List<GuiChildControl> controls, int spacing, int availableWidth) {
        int width = 0;
        for (GuiChildControl child : controls)
            width = Math.max(width, child.getPreferredWidth(availableWidth));
        return width;
    }
    
    @Override
    public int minHeight(List<GuiChildControl> controls, int spacing, int width, int availableHeight) {
        int height = -spacing;
        boolean has = false;
        for (GuiChildControl child : controls) {
            int min = child.getMinHeight(availableHeight);
            if (min != -1) {
                height += min;
                has = true;
            }
            height += spacing;
        }
        return has ? height : -1;
    }
    
    @Override
    public int preferredHeight(List<GuiChildControl> controls, int spacing, int width, int availableHeight) {
        int height = -spacing;
        for (GuiChildControl child : controls)
            height += child.getPreferredHeight(availableHeight) + spacing;
        return height;
    }
    
    @Override
    public void flowX(List<GuiChildControl> controls, int spacing, Align align, int width, int preferred, boolean endless) {
        int maxWidth = width;
        if (endless && preferred < width)
            maxWidth = preferred;
        boolean expandable = areChildrenExpandableX(controls);
        if (align == Align.LEFT && !expandable) {
            for (GuiChildControl child : controls) {
                child.setX(0);
                child.setWidth(Math.min(maxWidth, child.getPreferredWidth(width)), width);
                child.flowX();
            }
        } else {
            if (align == Align.STRETCH || expandable) {
                for (GuiChildControl child : controls) {
                    if (child.isExpandableX() || align == Align.STRETCH)
                        child.setWidth(maxWidth, width);
                    else
                        child.setWidth(Math.min(maxWidth, child.getPreferredWidth(width)), width);
                    child.setX(0);
                    child.flowX();
                }
            } else if (align == Align.RIGHT) {
                for (GuiChildControl child : controls) {
                    child.setWidth(Math.min(maxWidth, child.getPreferredWidth(width)), width);
                    child.setX(maxWidth - child.getWidth());
                    child.flowX();
                }
            } else {
                for (GuiChildControl child : controls) {
                    child.setWidth(Math.min(maxWidth, child.getPreferredWidth(width)), width);
                    child.setX((int) Math.ceil(maxWidth / 2D - child.getWidth() / 2D));
                    child.flowX();
                }
            }
        }
    }
    
    @Override
    public void flowY(List<GuiChildControl> controls, int spacing, VAlign valign, int width, int height, int preferred, boolean endless) {
        int available = height - spacing * (controls.size() - 1);
        MarkList<GuiChildControl> list = new MarkList<>(controls);
        if (height >= preferred) { // If there is enough space available
            if (valign == VAlign.STRETCH && !areChildrenExpandableY(controls)) { // force expansion
                
                for (GuiChildControl child : list) { // Make sure min dimensions are used
                    int min = child.getMinHeight(height);
                    if (min != -1) {
                        available -= min;
                        child.setHeight(min, height);
                    } else
                        child.setHeight(0, height);
                }
                
                while (available > 0 && !list.isEmpty()) { // add height to remaining controls which are smaller than their preferred height
                    int average = (int) Math.ceil((double) available / list.remaing());
                    for (MarkIterator<GuiChildControl> itr = list.iterator(); itr.hasNext();) {
                        GuiChildControl child = itr.next();
                        int toAdd = Maths.min(average, available, child.getPreferredHeight(height) - child.getHeight());
                        if (toAdd <= 0) {
                            itr.mark();
                            continue;
                        }
                        available -= child.addHeight(toAdd, height);
                        if (child.isMaxHeight(height))
                            itr.mark();
                    }
                }
                
                list.clear();
                while (available > 0 && !list.isEmpty()) { // add height to remaining controls until there is no space available or everything is at max
                    int average = (int) Math.ceil((double) available / list.remaing());
                    for (MarkIterator<GuiChildControl> itr = list.iterator(); itr.hasNext();) {
                        GuiChildControl child = itr.next();
                        available -= child.addHeight(Math.min(average, available), height);
                        if (child.isMaxHeight(height))
                            itr.mark();
                    }
                }
            } else { // let expandable controls take over the empty area
                for (MarkIterator<GuiChildControl> itr = list.iterator(); itr.hasNext();) {
                    GuiChildControl child = itr.next();
                    child.setHeight(child.getPreferredHeight(height), height);
                    if (!child.isExpandableY())
                        itr.mark();
                    available -= child.getHeight();
                }
                
                if (valign == VAlign.STRETCH || areChildrenExpandableY(controls))
                    while (available > 0 && !list.isEmpty()) { // add height to remaining controls until there is no space available or everything is at max
                        int average = (int) Math.ceil((double) available / list.remaing());
                        for (MarkIterator<GuiChildControl> itr = list.iterator(); itr.hasNext();) {
                            GuiChildControl child = itr.next();
                            available -= child.addHeight(Math.min(average, available), height);
                            if (child.isMaxHeight(height))
                                itr.mark();
                        }
                    }
            }
        } else if (endless) { // Used for scroll boxes
            for (GuiChildControl child : controls)
                child.setHeight(child.getPreferredHeight(height), height);
            valign = VAlign.TOP;
        } else { // If there is not enough space
            for (GuiChildControl child : list) { // Make sure min dimensions are used
                int min = child.getMinHeight(height);
                if (min != -1) {
                    available -= min;
                    child.setHeight(min, height);
                } else
                    child.setHeight(0, height);
            }
            
            while (available > 0 && !list.isEmpty()) { // add height to remaining controls until there is no space available or everything is at max
                int average = (int) Math.ceil((double) available / list.remaing());
                for (MarkIterator<GuiChildControl> itr = list.iterator(); itr.hasNext();) {
                    GuiChildControl child = itr.next();
                    int prefer = child.getPreferredHeight(height);
                    available -= child.addHeight(Math.min(average, Math.min(prefer - child.getHeight(), available)), height);
                    if (child.isMaxHeight(height) || prefer <= child.getHeight())
                        itr.mark();
                }
            }
            
            list.clear();
            
            while (available > 0 && !list.isEmpty()) { // add height to remaining controls until there is no space available or everything is at max
                int average = (int) Math.ceil((double) available / list.remaing());
                for (MarkIterator<GuiChildControl> itr = list.iterator(); itr.hasNext();) {
                    GuiChildControl child = itr.next();
                    available -= child.addHeight(Math.min(average, available), height);
                    if (child.isMaxHeight(height))
                        itr.mark();
                }
            }
        }
        
        for (GuiChildControl child : controls)
            child.flowY();
        
        if (available <= 0 || valign == VAlign.TOP) {
            int y = 0;
            for (GuiChildControl child : controls) {
                child.setY(y);
                y += child.getHeight() + spacing;
            }
        } else if (valign == VAlign.BOTTOM) {
            int y = height;
            for (GuiChildControl child : controls) {
                y -= child.getHeight();
                child.setY(y);
                y -= spacing;
            }
        } else if (valign == VAlign.CENTER || valign == VAlign.STRETCH) {
            int y = available / 2;
            for (GuiChildControl child : controls) {
                child.setY(y);
                y += child.getHeight() + spacing;
            }
        }
    }
    
}
