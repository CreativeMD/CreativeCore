package team.creative.creativecore.client.gui.control.tree;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix3x2fStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonInfo;
import team.creative.creativecore.client.gui.GuiClientControl;
import team.creative.creativecore.client.gui.control.parent.GuiClientScrollXY;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.control.tree.GuiTree;
import team.creative.creativecore.common.gui.control.tree.GuiTree.GuiTreeDist;
import team.creative.creativecore.common.gui.control.tree.GuiTreeDragPosition;
import team.creative.creativecore.common.gui.control.tree.GuiTreeDragPosition.ItemPosition;
import team.creative.creativecore.common.gui.control.tree.GuiTreeItem;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.display.DisplayColor;
import team.creative.creativecore.common.gui.style.display.StyleDisplay;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class GuiClientTree<T extends GuiTree> extends GuiClientScrollXY<T> implements GuiTreeDist {
    
    private static final int LEVEL_SPACING = 10;
    
    protected StyleDisplay line = new DisplayColor(ColorUtils.WHITE);
    protected StyleDisplay dragLine = new DisplayColor(ColorUtils.BLACK);
    protected StyleDisplay dragHover = new DisplayColor(ColorUtils.rgb(200, 150, 0));
    
    private int lastWidth;
    private int lastHeight;
    
    private int lineThickness;
    private int halfLineThickness;
    
    private GuiTreeItem dragged = null;
    private GuiTreeDragPosition lastDragPosition = null;
    
    public GuiClientTree(T control) {
        super(control);
    }
    
    @Override
    public void reflowTree() {
        if (lastWidth != 0) {
            flowX(lastWidth, preferredWidth(lastWidth));
            flowY(lastWidth, lastHeight, preferredHeight(lastWidth, lastHeight));
        }
    }
    
    @Override
    public void setLineThickness(int thickness) {
        lineThickness = thickness;
        halfLineThickness = thickness / 2;
    }
    
    protected int offsetByLevel(int level) {
        if (control.isRootVisible())
            return LEVEL_SPACING * level;
        return LEVEL_SPACING * (level - 1);
    }
    
    @Override
    protected int preferredWidth(int availableWidth) {
        int width = 0;
        for (GuiClientControl control : controls())
            if (control.control instanceof GuiTreeItem item)
                width = Math.max(width, offsetByLevel(item.getLevel()) + 1 + control.rect.getPreferredWidth(availableWidth));
            else
                width = Math.max(width, control.rect.getPreferredWidth(availableWidth));
        return width;
    }
    
    @Override
    public void flowX(int width, int preferred) {
        super.flowX(width, preferred);
        for (GuiClientControl control : controls())
            if (control.control instanceof GuiTreeItem item)
                control.rect.setX(offsetByLevel(item.getLevel()) + 1);
        updateWidth();
        lastWidth = width;
    }
    
    @Override
    public void flowY(int width, int height, int preferred) {
        super.flowY(width, height, preferred);
        lastHeight = height;
    }
    
    @Override
    protected void renderContent(GuiGraphics graphics, ControlFormatting formatting, int borderWidth, Rect controlRect, Rect realRect, double scale, int mouseX, int mouseY) {
        Matrix3x2fStack pose = graphics.pose();
        if (control.isDragged()) {
            pose.pushMatrix();
            pose.translate((float) (getContentOffset() + getOffsetX()), (float) (getContentOffset() + getOffsetY()));
            lastDragPosition = calculatePosition((int) (mouseX - realRect.minX - getContentOffset()), (int) (mouseY - realRect.minY - getContentOffset()));
            if (lastDragPosition != null) {
                var rect = ((GuiClientControl) lastDragPosition.item().dist()).rect;
                if (lastDragPosition.position() == ItemPosition.IN)
                    dragHover.render(graphics, rect);
                else {
                    int thickness = 1;
                    int minY;
                    int width = rect.getWidth();
                    if (lastDragPosition.position() == ItemPosition.ABOVE) {
                        minY = rect.getY() - thickness - 1;
                        if (lastDragPosition.above() != null)
                            width = Math.max(width, rect.getWidth());
                    } else
                        minY = rect.getBottom() + 1;
                    
                    if (lastDragPosition.above() != null) {
                        dragLine.render(graphics, rect.getX() - thickness, minY - thickness, thickness, thickness * 3);
                        dragLine.render(graphics, rect.getX() + width, minY - thickness, thickness, thickness * 3);
                    } else if (lastDragPosition.position() == ItemPosition.ABOVE) {
                        dragLine.render(graphics, rect.getX() - thickness, minY, thickness, thickness * 2);
                        dragLine.render(graphics, rect.getX() + width, minY, thickness, thickness * 2);
                    } else {
                        dragLine.render(graphics, rect.getX() - thickness, minY - thickness, thickness, thickness * 2);
                        dragLine.render(graphics, rect.getX() + width, minY - thickness, thickness, thickness * 2);
                    }
                    
                    dragLine.render(graphics, rect.getX(), minY, width, thickness);
                }
            }
            pose.popMatrix();
        } else
            lastDragPosition = null;
        
        super.renderContent(graphics, formatting, borderWidth, controlRect, realRect, scale, mouseX, mouseY);
        
        pose.pushMatrix();
        pose.translate((float) getOffsetX(), (float) (getContentOffset() + getOffsetY()));
        List<GuiTreeLine> lines = new ArrayList<>();
        int size = -1;
        for (GuiClientControl control : controls()) {
            if (!(control.control instanceof GuiTreeItem item))
                continue;
            int lineY = (control.rect.getY() + control.rect.getBottom()) / 2 + halfLineThickness;
            int level = item.getLevel() - (this.control.isRootVisible() ? 1 : 2);
            
            if (level <= size) {
                if (level >= 0)
                    lines.get(level).y2 = lineY;
                for (int i = size; i > level; i--) {
                    if (lines.get(i).invalid)
                        continue;
                    
                    lines.get(i).render(graphics);
                    lines.get(i).invalid = false;
                }
                size = level;
            } else {
                if (level < 0)
                    continue;
                
                while (level > size) {
                    if (lines.size() > size + 1)
                        lines.get(level).set(control.rect.getY() - 2, lineY);
                    else
                        lines.add(new GuiTreeLine(size + 1, control.rect.getY() - 2, lineY));
                    size++;
                }
            }
            
            if (level >= 0)
                line.render(graphics, lines.get(level).x + lineThickness, lineY - lineThickness, LEVEL_SPACING / 2f, lineThickness);
        }
        
        if (size >= 0) {
            for (int i = size; i > -1; i--) {
                if (lines.get(i).invalid)
                    continue;
                
                lines.get(i).render(graphics);
                lines.get(i).invalid = false;
            }
        }
        
        pose.popMatrix();
    }
    
    private GuiTreeDragPosition createPosition(ItemPosition position, GuiTreeItem item, GuiControl before) {
        if (item == control.root() && position != ItemPosition.IN)
            return null;
        return new GuiTreeDragPosition(position, item, before != null && before instanceof GuiTreeItem item2 && item2.getLevel() == item.getLevel() ? before : null);
    }
    
    protected GuiTreeDragPosition calculatePosition(int mouseX, int mouseY) {
        GuiControl last = null;
        GuiControl before = null;
        for (GuiClientControl control : controls()) {
            if (control.control == dragged)
                continue;
            if (control.control instanceof GuiTreeItem item)
                if (control.rect.getY() > mouseY)
                    return createPosition(ItemPosition.ABOVE, item, before);
                else if (control.rect.inside(mouseX, mouseY))
                    return createPosition(ItemPosition.IN, item, before);
                else
                    last = control.control;
            before = control.control;
        }
        if (last != null)
            return createPosition(ItemPosition.BELOW, (GuiTreeItem) last, null);
        return null;
    }
    
    @Override
    public boolean mouseClicked(double x, double y, MouseButtonInfo info) {
        if (super.mouseClicked(x, y, info))
            return true;
        if (control.canDeselect())
            control.select(null);
        return true;
    }
    
    @Override
    public boolean isDragged() {
        return dragged != null;
    }
    
    @Override
    public void startDrag(GuiTreeItem item) {
        dragged = item;
    }
    
    @Override
    public boolean endDrag() {
        control.performModication(dragged, lastDragPosition);
        lastDragPosition = null;
        dragged = null;
        return true;
    }
    
    private class GuiTreeLine {
        
        public final int x;
        public int y;
        public int y2;
        public boolean invalid;
        
        public GuiTreeLine(int level, int minY, int maxY) {
            this.x = offsetByLevel(level + (control.isRootVisible() ? 1 : 2)) - LEVEL_SPACING / 2;
            set(minY, maxY);
        }
        
        public void set(int minY, int maxY) {
            this.y = minY;
            this.y2 = maxY;
            invalid = false;
        }
        
        public void render(GuiGraphics graphics) {
            line.render(graphics, x, y, lineThickness, y2 - y);
        }
    }
    
}
