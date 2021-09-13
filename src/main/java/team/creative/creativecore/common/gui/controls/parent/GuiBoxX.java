package team.creative.creativecore.common.gui.controls.parent;

import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.type.MarkIterator;
import team.creative.creativecore.common.util.type.MarkList;

public class GuiBoxX extends GuiParent {
    
    public GuiBoxX(String name, Align align, VAlign valign) {
        super(name);
        this.align = align;
        this.valign = valign;
    }
    
    public GuiBoxX(String name, int width, int height, Align align, VAlign valign) {
        super(name, width, height);
        this.align = align;
        this.valign = valign;
    }
    
    public GuiBoxX(String name, int width, int height, VAlign valign) {
        this(name, width, height, Align.LEFT, valign);
    }
    
    public GuiBoxX(String name, int width, int height) {
        this(name, width, height, VAlign.TOP);
    }
    
    public GuiBoxX(String name) {
        this(name, Align.LEFT, VAlign.TOP);
    }
    
    public GuiBoxX() {
        this("");
    }
    
    @Override
    public int getMinWidth() {
        int width = -spacing;
        for (GuiChildControl child : controls)
            width += child.control.getMinWidth() + spacing;
        return width;
    }
    
    @Override
    public int preferredWidth() {
        int width = -spacing;
        for (GuiChildControl child : controls)
            width += child.control.getPreferredWidth() + spacing;
        return width;
    }
    
    @Override
    public int getMinHeight() {
        int height = 0;
        for (GuiChildControl child : controls)
            height = Math.max(height, child.control.getMinHeight());
        return height;
    }
    
    @Override
    public int preferredHeight() {
        int height = 0;
        for (GuiChildControl child : controls)
            height = Math.max(height, child.control.getPreferredHeight());
        return height;
    }
    
    @Override
    public void flowX(int width, int preferred) {
        int available = width - spacing * (controls.size() - 1);
        MarkList<GuiChildControl> list = new MarkList<>(controls);
        if (width >= preferred) { // If there is enough space available
            if (align == Align.STRETCH && !areChildrenExpandableX()) { // force expansion
                
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
                    child.setWidth(child.control.getPreferredWidth());
                    if (!child.control.isExpandableX())
                        itr.mark();
                    available -= child.getWidth();
                }
                
                if (align == Align.STRETCH)
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
                int min = child.control.getMinWidth();
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
            int x = contentWidth / 2 - width / 2;
            for (GuiChildControl child : controls) {
                child.setX(x);
                x += child.getWidth() + spacing;
            }
        }
    }
    
    @Override
    public void flowY(int height, int preferred) {
        if (valign == VAlign.TOP) {
            for (GuiChildControl child : controls) {
                child.setY(0);
                child.setHeight(Math.min(preferred, child.control.getPreferredHeight()));
                child.flowY();
            }
        } else {
            if (valign == VAlign.STRETCH) {
                for (GuiChildControl child : controls) {
                    child.setHeight(height);
                    child.setY(0);
                    child.flowY();
                }
            } else if (valign == VAlign.BOTTOM) {
                for (GuiChildControl child : controls) {
                    child.setHeight(Math.min(preferred, child.control.getPreferredHeight()));
                    child.setY(preferred - child.getHeight());
                    child.flowY();
                }
            } else {
                for (GuiChildControl child : controls) {
                    child.setHeight(Math.min(preferred, child.control.getPreferredHeight()));
                    child.setY(preferred / 2 - child.getHeight() / 2);
                    child.flowY();
                }
            }
        }
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
    
}
