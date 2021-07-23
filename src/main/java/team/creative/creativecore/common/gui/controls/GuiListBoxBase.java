package team.creative.creativecore.common.gui.controls;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.TextComponent;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;

public class GuiListBoxBase<T extends GuiControl> extends GuiScrollBox {
    
    protected List<T> content;
    protected List<GuiButtonRemove> removeButtons;
    
    public final boolean modifiable;
    public int space = 2;
    
    public GuiListBoxBase(String name, int x, int y, int width, int height, boolean modifiable, List<T> entries) {
        super(name, x, y, width, height);
        this.content = entries;
        this.modifiable = modifiable;
        if (modifiable)
            removeButtons = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            add(entries.get(i));
            if (modifiable) {
                GuiButtonRemove button = new GuiButtonRemove(i);
                add(button);
                removeButtons.add(button);
            }
        }
        reloadPositions();
    }
    
    public void reloadPositions() {
        int y = space / 2;
        for (int i = 0; i < content.size(); i++) {
            GuiControl control = content.get(i);
            if (modifiable) {
                GuiButtonRemove button = removeButtons.get(i);
                button.index = i;
                button.setY(y + 3);
            }
            
            control.setY(y);
            y += control.getHeight() + space;
        }
    }
    
    public void removeItem(int index) {
        remove(content.get(index));
        content.remove(index);
        if (modifiable) {
            remove(removeButtons.get(index));
            removeButtons.remove(index);
            for (int i = 0; i < removeButtons.size(); i++)
                removeButtons.get(i).index = i;
        }
        reloadPositions();
        
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
            if (modifiable) {
                GuiButtonRemove button = new GuiButtonRemove(content.size() - 1);
                add(button);
                removeButtons.add(button);
            }
        }
        
        reloadPositions();
    }
    
    public void addItem(T entry) {
        content.add(entry);
        add(entry);
        if (modifiable) {
            GuiButtonRemove button = new GuiButtonRemove(content.size() - 1);
            add(button);
            removeButtons.add(button);
        }
        
        if (content.size() == 1)
            reloadPositions();
        else {
            GuiControl before = content.get(content.size() - 2);
            entry.setY(before.getY() + before.getHeight() + space);
            
            if (modifiable)
                removeButtons.get(removeButtons.size() - 1).setY(before.getY() + before.getHeight() + space + 3);
        }
        
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
    
    public class GuiButtonRemove extends GuiButtonFixed {
        
        public int index;
        
        public GuiButtonRemove(int index) {
            super("x", GuiListBoxBase.this.getWidth() - 25, 0, 12, 12, null);
            setTitle(new TextComponent("x"));
            pressed = (x) -> GuiListBoxBase.this.removeItem(this.index);
            this.index = index;
        }
        
    }
    
}
