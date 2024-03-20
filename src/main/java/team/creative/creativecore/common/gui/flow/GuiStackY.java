package team.creative.creativecore.common.gui.flow;

import java.util.List;

import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.util.type.list.MarkIterator;
import team.creative.creativecore.common.util.type.list.MarkList;

public class GuiStackY extends GuiFlow {
    
    @Override
    public int minWidth(List<GuiChildControl> controls, int spacing) {
        int width = 0;
        for (GuiChildControl child : controls)
            width = Math.max(width, child.getMinWidth());
        return width;
    }
    
    @Override
    public int preferredWidth(List<GuiChildControl> controls, int spacing) {
        int width = 0;
        for (GuiChildControl child : controls)
            width = Math.max(width, child.getPreferredWidth());
        return width;
    }
    
    @Override
    public int minHeight(List<GuiChildControl> controls, int spacing) {
        int height = -spacing;
        boolean has = false;
        for (GuiChildControl child : controls) {
            int min = child.getMinHeight();
            if (min != -1) {
                height += min;
                has = true;
            }
            height += spacing;
        }
        return has ? height : -1;
    }
    
    @Override
    public int preferredHeight(List<GuiChildControl> controls, int spacing) {
        int height = -spacing;
        for (GuiChildControl child : controls)
            height += child.getPreferredHeight() + spacing;
        return height;
    }
    
    @Override
    public void flowX(List<GuiChildControl> controls, int spacing, Align align, int width, int preferred) {
        boolean expandable = areChildrenExpandableX(controls);
        if (align == Align.LEFT && !expandable) {
            for (GuiChildControl child : controls) {
                child.setX(0);
                child.setWidth(Math.min(width, child.getPreferredWidth()));
                child.flowX();
            }
        } else {
            if (align == Align.STRETCH || expandable) {
                for (GuiChildControl child : controls) {
                    if (child.control.isExpandableX() || align == Align.STRETCH)
                        child.setWidth(width);
                    else
                        child.setWidth(Math.min(width, child.getPreferredWidth()));
                    child.setX(0);
                    child.flowX();
                }
            } else if (align == Align.RIGHT) {
                for (GuiChildControl child : controls) {
                    child.setWidth(Math.min(width, child.getPreferredWidth()));
                    child.setX(width - child.getWidth());
                    child.flowX();
                }
            } else {
                for (GuiChildControl child : controls) {
                    child.setWidth(Math.min(width, child.getPreferredWidth()));
                    child.setX((int) Math.ceil(width / 2D - child.getWidth() / 2D));
                    child.flowX();
                }
            }
        }
    }
    
    @Override
    public void flowY(List<GuiChildControl> controls, int spacing, VAlign valign, int height, int preferred) {
        int available = height - spacing * (controls.size() - 1);
        MarkList<GuiChildControl> list = new MarkList<>(controls);
        if (height >= preferred) { // If there is enough space available
            if (valign == VAlign.STRETCH && !areChildrenExpandableY(controls)) { // force expansion
                
                while (available > 0 && !list.isEmpty()) { // add height to remaining controls until there is no space available or everything is at max
                    int average = (int) Math.ceil((double) available / list.remaing());
                    for (MarkIterator<GuiChildControl> itr = list.iterator(); itr.hasNext();) {
                        GuiChildControl child = itr.next();
                        available -= child.addHeight(Math.min(average, available));
                        if (child.isMaxHeight())
                            itr.mark();
                    }
                }
            } else { // let expandable controls take over the empty area
                for (MarkIterator<GuiChildControl> itr = list.iterator(); itr.hasNext();) {
                    GuiChildControl child = itr.next();
                    child.setHeight(child.getPreferredHeight());
                    if (!child.control.isExpandableY())
                        itr.mark();
                    available -= child.getHeight();
                }
                
                if (valign == VAlign.STRETCH || areChildrenExpandableY(controls))
                    while (available > 0 && !list.isEmpty()) { // add height to remaining controls until there is no space available or everything is at max
                        int average = (int) Math.ceil((double) available / list.remaing());
                        for (MarkIterator<GuiChildControl> itr = list.iterator(); itr.hasNext();) {
                            GuiChildControl child = itr.next();
                            available -= child.addHeight(Math.min(average, available));
                            if (child.isMaxHeight())
                                itr.mark();
                        }
                    }
            }
        } else { // If there is not enough space
            for (GuiChildControl child : list) { // Make sure min dimensions are used
                int min = child.getMinHeight();
                if (min != -1) {
                    available -= min;
                    child.setHeight(min);
                } else
                    child.setHeight(0);
            }
            
            while (available > 0 && !list.isEmpty()) { // add height to remaining controls until there is no space available or everything is at max
                int average = (int) Math.ceil((double) available / list.remaing());
                for (MarkIterator<GuiChildControl> itr = list.iterator(); itr.hasNext();) {
                    GuiChildControl child = itr.next();
                    int prefer = child.getPreferredHeight();
                    available -= child.addHeight(Math.min(average, Math.min(prefer - child.getHeight(), available)));
                    if (child.isMaxHeight() || prefer <= child.getHeight())
                        itr.mark();
                }
            }
            
            list.clear();
            
            while (available > 0 && !list.isEmpty()) { // add height to remaining controls until there is no space available or everything is at max
                int average = (int) Math.ceil((double) available / list.remaing());
                for (MarkIterator<GuiChildControl> itr = list.iterator(); itr.hasNext();) {
                    GuiChildControl child = itr.next();
                    available -= child.addHeight(Math.min(average, available));
                    if (child.isMaxHeight())
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
            int contentHeight = height - (available + spacing * (controls.size() - 1));
            int y = height / 2 - contentHeight / 2;
            for (GuiChildControl child : controls) {
                child.setY(y);
                y += child.getHeight() + spacing;
            }
        }
    }
    
}
