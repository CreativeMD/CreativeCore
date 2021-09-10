package team.creative.creativecore.common.gui.controls.collection;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.TextComponent;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.controls.parent.GuiScrollY;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;

public class GuiListBoxBase<T extends GuiControl> extends GuiScrollY {
    
    protected List<T> content;
    protected List<GuiChildControl> removeButtons;
    protected int cachedWidth;
    protected int cachedHeight;
    
    public final boolean modifiable;
    
    public GuiListBoxBase(String name, int width, int height, boolean modifiable, List<T> entries) {
        super(name, width, height);
        this.align = Align.CENTER;
        this.content = entries;
        this.modifiable = modifiable;
        if (modifiable)
            removeButtons = new ArrayList<>();
        createItems();
    }
    
    public GuiListBoxBase(String name, boolean modifiable, List<T> entries) {
        super(name);
        this.align = Align.CENTER;
        this.content = entries;
        this.modifiable = modifiable;
        if (modifiable)
            removeButtons = new ArrayList<>();
        createItems();
    }
    
    protected void createItems() {
        for (int i = 0; i < content.size(); i++) {
            super.add(content.get(i));
            if (modifiable)
                removeButtons.add(super.addHover(new GuiButtonRemove(i)));
        }
    }
    
    @Override
    public GuiChildControl add(GuiControl control) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public GuiChildControl addHover(GuiControl control) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void flowX(int width, int preferred) {
        this.cachedWidth = width;
        super.flowX(width, preferred);
    }
    
    @Override
    public void flowY(int height, int preferred) {
        this.cachedHeight = height;
        super.flowY(height, preferred);
        
        for (int i = 0; i < controls.size(); i++) {
            GuiChildControl control = controls.get(i);
            if (modifiable) {
                GuiChildControl button = removeButtons.get(i);
                ((GuiButtonRemove) button.control).index = i;
                button.setY(control.getY() + 3);
            }
        }
    }
    
    public void reflowInternal() {
        flowX(cachedWidth, getPreferredWidth());
        flowY(cachedHeight, getPreferredHeight());
    }
    
    public void removeItem(int index) {
        remove(content.get(index));
        content.remove(index);
        if (modifiable) {
            remove(removeButtons.get(index));
            removeButtons.remove(index);
            for (int i = 0; i < removeButtons.size(); i++)
                ((GuiButtonRemove) removeButtons.get(i).control).index = i;
        }
        reflow();
        
        raiseEvent(new GuiControlChangedEvent(this));
    }
    
    public void clearItems() {
        for (int i = 0; i < content.size(); i++) {
            remove(content.get(i));
            if (modifiable)
                remove(removeButtons.get(i));
        }
        
        content.clear();
        if (modifiable)
            removeButtons.clear();
    }
    
    public void addAllItems(List<T> entries) {
        for (T entry : entries) {
            content.add(entry);
            add(entry);
            if (modifiable)
                removeButtons.add(super.addHover(new GuiButtonRemove(content.size() - 1)));
        }
        
        reflowInternal();
    }
    
    public void addItem(T entry) {
        content.add(entry);
        add(entry);
        if (modifiable)
            removeButtons.add(super.addHover(new GuiButtonRemove(content.size() - 1)));
        
        reflowInternal();
        
        raiseEvent(new GuiControlChangedEvent(this));
    }
    
    @Override
    public boolean isEmpty() {
        return content.isEmpty();
    }
    
    @Override
    public int size() {
        return content.size();
    }
    
    public T get(int index) {
        return content.get(index);
    }
    
    public class GuiButtonRemove extends GuiButton {
        
        public int index;
        
        public GuiButtonRemove(int index) {
            super("x", 12, 12, null);
            setTitle(new TextComponent("x"));
            pressed = (x) -> GuiListBoxBase.this.removeItem(this.index);
            this.index = index;
        }
        
    }
    
}
