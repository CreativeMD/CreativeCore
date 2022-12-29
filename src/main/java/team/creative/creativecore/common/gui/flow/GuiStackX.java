package team.creative.creativecore.common.gui.flow;

import java.util.List;

import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.util.math.Maths;
import team.creative.creativecore.common.util.type.list.MarkIterator;
import team.creative.creativecore.common.util.type.list.MarkList;

public class GuiStackX extends GuiFlow {
    
    @Override
    public int minWidth(List<GuiChildControl> controls, int spacing, int availableWidth) {
        boolean has = false;
        int width = -spacing;
        for (GuiChildControl child : controls) {
            int min = child.getMinWidth(availableWidth);
            if (min != -1) {
                width += min;
                has = true;
            }
            width += spacing;
        }
        return has ? width : -1;
    }
    
    @Override
    public int preferredWidth(List<GuiChildControl> controls, int spacing, int availableWidth) {
        int width = -spacing;
        for (GuiChildControl child : controls)
            width += child.getPreferredWidth(availableWidth) + spacing;
        return width;
    }
    
    @Override
    public int minHeight(List<GuiChildControl> controls, int spacing, int width, int availableHeight) {
        int height = -1;
        for (GuiChildControl child : controls)
            height = Math.max(height, child.getMinHeight(availableHeight));
        return height;
    }
    
    @Override
    public int preferredHeight(List<GuiChildControl> controls, int spacing, int width, int availableHeight) {
        int height = 0;
        for (GuiChildControl child : controls)
            height = Math.max(height, child.getPreferredHeight(availableHeight));
        return height;
    }
    
    @Override
    public void flowX(List<GuiChildControl> controls, int spacing, Align align, int width, int preferred) {
        int available = width - spacing * (controls.size() - 1);
        MarkList<GuiChildControl> list = new MarkList<>(controls);
        if (width >= preferred) { // If there is enough space available
            if (align == Align.STRETCH && !areChildrenExpandableX(controls)) { // force expansion
                
                for (GuiChildControl child : list) { // Make sure min dimensions are used
                    int min = child.getMinWidth(width);
                    if (min != -1) {
                        available -= min;
                        child.setWidth(min, width);
                    } else
                        child.setWidth(0, width);
                }
                
                while (available > 0 && !list.isEmpty()) { // add width to remaining controls which are smaller than their preferred width
                    int average = (int) Math.ceil((double) available / list.remaing());
                    for (MarkIterator<GuiChildControl> itr = list.iterator(); itr.hasNext();) {
                        GuiChildControl child = itr.next();
                        int toAdd = Maths.min(average, available, child.getPreferredWidth(width) - child.getWidth());
                        if (toAdd <= 0) {
                            itr.mark();
                            continue;
                        }
                        available -= child.addWidth(toAdd, width);
                        if (child.isMaxWidth(width))
                            itr.mark();
                    }
                }
                
                list.clear();
                while (available > 0 && !list.isEmpty()) { // add width to remaining controls until there is no space available or everything is at max
                    int average = (int) Math.ceil((double) available / list.remaing());
                    for (MarkIterator<GuiChildControl> itr = list.iterator(); itr.hasNext();) {
                        GuiChildControl child = itr.next();
                        available -= child.addWidth(Math.min(average, available), width);
                        if (child.isMaxWidth(width))
                            itr.mark();
                    }
                }
            } else { // let expandable controls take over the empty area
                for (MarkIterator<GuiChildControl> itr = list.iterator(); itr.hasNext();) {
                    GuiChildControl child = itr.next();
                    child.setWidth(child.getPreferredWidth(width), width);
                    if (!child.control.isExpandableX())
                        itr.mark();
                    available -= child.getWidth();
                }
                
                if (align == Align.STRETCH || areChildrenExpandableX(controls))
                    while (available > 0 && !list.isEmpty()) { // add width to remaining controls until there is no space available or everything is at max
                        int average = (int) Math.ceil((double) available / list.remaing());
                        for (MarkIterator<GuiChildControl> itr = list.iterator(); itr.hasNext();) {
                            GuiChildControl child = itr.next();
                            available -= child.addWidth(Math.min(average, available), width);
                            if (child.isMaxWidth(width))
                                itr.mark();
                        }
                    }
            }
        } else { // If there is not enough space
            for (GuiChildControl child : list) { // Make sure min dimensions are used
                int min = child.getMinWidth(width);
                if (min != -1) {
                    available -= min;
                    child.setWidth(min, width);
                } else
                    child.setWidth(0, width);
            }
            
            while (available > 0 && !list.isEmpty()) { // add width to remaining controls until there is no space available or everything is at max
                int average = (int) Math.ceil((double) available / list.remaing());
                for (MarkIterator<GuiChildControl> itr = list.iterator(); itr.hasNext();) {
                    GuiChildControl child = itr.next();
                    int prefer = child.getPreferredWidth(width);
                    available -= child.addWidth(Math.min(average, Math.min(prefer - child.getWidth(), available)), width);
                    if (child.isMaxWidth(width) || prefer <= child.getWidth())
                        itr.mark();
                }
            }
            
            list.clear();
            
            while (available > 0 && !list.isEmpty()) { // add width to remaining controls until there is no space available or everything is at max
                int average = (int) Math.ceil((double) available / list.remaing());
                for (MarkIterator<GuiChildControl> itr = list.iterator(); itr.hasNext();) {
                    GuiChildControl child = itr.next();
                    available -= child.addWidth(Math.min(average, available), width);
                    if (child.isMaxWidth(width))
                        itr.mark();
                }
            }
        }
        
        for (GuiChildControl child : controls)
            child.flowX();
        
        if (available <= 0 || align == Align.LEFT) {
            int x = 0;
            for (GuiChildControl child : controls) {
                child.setX(x);
                x += child.getWidth() + spacing;
            }
        } else if (align == Align.RIGHT) {
            int x = width;
            for (GuiChildControl child : controls) {
                x -= child.getWidth();
                child.setX(x);
                x -= spacing;
            }
        } else if (align == Align.CENTER || align == Align.STRETCH) {
            int contentWidth = width - (available + spacing * (controls.size() - 1));
            int x = width / 2 - contentWidth / 2;
            for (GuiChildControl child : controls) {
                child.setX(x);
                x += child.getWidth() + spacing;
            }
        }
    }
    
    @Override
    public void flowY(List<GuiChildControl> controls, int spacing, VAlign valign, int width, int height, int preferred) {
        boolean expandable = areChildrenExpandableY(controls);
        if (valign == VAlign.TOP && !expandable) {
            for (GuiChildControl child : controls) {
                child.setY(0);
                child.setHeight(Math.min(height, child.getPreferredHeight(height)), height);
                child.flowY();
            }
        } else {
            if (valign == VAlign.STRETCH || expandable) {
                for (GuiChildControl child : controls) {
                    if (child.control.isExpandableY() || valign == VAlign.STRETCH)
                        child.setHeight(height, height);
                    else
                        child.setHeight(Math.min(height, child.getPreferredHeight(height)), height);
                    child.setY(0);
                    child.flowY();
                }
            } else if (valign == VAlign.BOTTOM) {
                for (GuiChildControl child : controls) {
                    child.setHeight(Math.min(height, child.getPreferredHeight(height)), height);
                    child.setY(height - child.getHeight());
                    child.flowY();
                }
            } else {
                for (GuiChildControl child : controls) {
                    child.setHeight(Math.min(height, child.getPreferredHeight(height)), height);
                    child.setY((int) Math.ceil(height / 2D - child.getHeight() / 2D));
                    child.flowY();
                }
            }
        }
    }
    
}
