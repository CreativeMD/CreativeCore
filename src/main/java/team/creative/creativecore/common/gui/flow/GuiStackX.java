package team.creative.creativecore.common.gui.flow;

import java.util.List;

import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.util.type.MarkIterator;
import team.creative.creativecore.common.util.type.MarkList;

public class GuiStackX extends GuiFlow {
    
    @Override
    public int minWidth(List<GuiChildControl> controls, int spacing) {
        boolean has = false;
        int width = -spacing;
        for (GuiChildControl child : controls) {
            int min = child.getMinWidth();
            if (min != -1) {
                width += min;
                has = true;
            }
            width += spacing;
        }
        return has ? width : -1;
    }
    
    @Override
    public int preferredWidth(List<GuiChildControl> controls, int spacing) {
        int width = -spacing;
        for (GuiChildControl child : controls)
            width += child.getPreferredWidth() + spacing;
        return width;
    }
    
    @Override
    public int minHeight(List<GuiChildControl> controls, int spacing) {
        int height = -1;
        for (GuiChildControl child : controls)
            height = Math.max(height, child.getMinHeight());
        return height;
    }
    
    @Override
    public int preferredHeight(List<GuiChildControl> controls, int spacing) {
        int height = 0;
        for (GuiChildControl child : controls)
            height = Math.max(height, child.getPreferredHeight());
        return height;
    }
    
    @Override
    public void flowX(List<GuiChildControl> controls, int spacing, Align align, int width, int preferred) {
        int available = width - spacing * (controls.size() - 1);
        MarkList<GuiChildControl> list = new MarkList<>(controls);
        if (width >= preferred) { // If there is enough space available
            if (align == Align.STRETCH && !areChildrenExpandableX(controls)) { // force expansion
                
                for (GuiChildControl child : list) { // Make sure min dimensions are used
                    int min = child.getMinWidth();
                    if (min != -1) {
                        available -= min;
                        child.setWidth(min);
                    } else
                        child.setWidth(0);
                }
                
                while (available > 0 && !list.isEmpty()) { // add width to remaining controls until there is no space available or everything is at max
                    int average = (int) Math.ceil((double) available / list.remaing());
                    for (MarkIterator<GuiChildControl> itr = list.iterator(); itr.hasNext();) {
                        GuiChildControl child = itr.next();
                        available -= child.addWidth(Math.min(average, available));
                        if (child.isMaxWidth())
                            itr.mark();
                    }
                }
            } else { // let expandable controls take over the empty area
                for (MarkIterator<GuiChildControl> itr = list.iterator(); itr.hasNext();) {
                    GuiChildControl child = itr.next();
                    child.setWidth(child.getPreferredWidth());
                    if (!child.control.isExpandableX())
                        itr.mark();
                    available -= child.getWidth();
                }
                
                if (align == Align.STRETCH || areChildrenExpandableX(controls))
                    while (available > 0 && !list.isEmpty()) { // add width to remaining controls until there is no space available or everything is at max
                        int average = (int) Math.ceil((double) available / list.remaing());
                        for (MarkIterator<GuiChildControl> itr = list.iterator(); itr.hasNext();) {
                            GuiChildControl child = itr.next();
                            available -= child.addWidth(Math.min(average, available));
                            if (child.isMaxWidth())
                                itr.mark();
                        }
                    }
            }
        } else { // If there is not enough space
            for (GuiChildControl child : list) { // Make sure min dimensions are used
                int min = child.getMinWidth();
                if (min != -1) {
                    available -= min;
                    child.setWidth(min);
                } else
                    child.setWidth(0);
            }
            
            while (available > 0 && !list.isEmpty()) { // add width to remaining controls until there is no space available or everything is at max
                int average = (int) Math.ceil((double) available / list.remaing());
                for (MarkIterator<GuiChildControl> itr = list.iterator(); itr.hasNext();) {
                    GuiChildControl child = itr.next();
                    available -= child.addWidth(Math.min(average, available));
                    if (child.isMaxWidth())
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
    public void flowY(List<GuiChildControl> controls, int spacing, VAlign valign, int height, int preferred) {
        boolean expandable = areChildrenExpandableY(controls);
        if (valign == VAlign.TOP && !expandable) {
            for (GuiChildControl child : controls) {
                child.setY(0);
                child.setHeight(Math.min(height, child.getPreferredHeight()));
                child.flowY();
            }
        } else {
            if (valign == VAlign.STRETCH || expandable) {
                for (GuiChildControl child : controls) {
                    if (child.control.isExpandableY())
                        child.setHeight(height);
                    else
                        child.setHeight(Math.min(height, child.getPreferredHeight()));
                    child.setY(0);
                    child.flowY();
                }
            } else if (valign == VAlign.BOTTOM) {
                for (GuiChildControl child : controls) {
                    child.setHeight(Math.min(height, child.getPreferredHeight()));
                    child.setY(height - child.getHeight());
                    child.flowY();
                }
            } else {
                for (GuiChildControl child : controls) {
                    child.setHeight(Math.min(height, child.getPreferredHeight()));
                    child.setY(height / 2 - child.getHeight() / 2);
                    child.flowY();
                }
            }
        }
    }
    
}
