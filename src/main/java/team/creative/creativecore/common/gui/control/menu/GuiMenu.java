package team.creative.creativecore.common.gui.control.menu;

import java.util.Map.Entry;

import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.control.parent.GuiScrollY;
import team.creative.creativecore.common.gui.control.simple.GuiLabel;
import team.creative.creativecore.common.gui.extension.GuiExtensionCreator;
import team.creative.creativecore.common.gui.extension.GuiExtensionCreator.ExtensionDirection;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.gui.style.display.DisplayColor;
import team.creative.creativecore.common.util.type.tree.NamedTree;

public abstract class GuiMenu<T> extends GuiScrollY {
    
    protected final GuiExtensionCreator<GuiMenu, GuiMenu> submenu = new GuiExtensionCreator<>(this) {
        
        public void markKeptFocus() {
            super.markKeptFocus();
            parentCreator().markKeptFocus();
        }
    };
    protected final NamedTree<T> tree;
    private String opened;
    
    public GuiMenu(NamedTree<T> tree) {
        super();
        this.tree = tree;
        this.hoveredScroll = true;
        this.align = Align.STRETCH;
        this.spacing = -1;
    }
    
    protected void buildTree() {
        String path = tree.path();
        if (!path.isBlank())
            path += ".";
        for (Entry<String, NamedTree<T>> entry : tree.entries())
            add(new GuiMenuEntry(entry.getKey(), path + entry.getKey(), entry.getValue()));
    }
    
    protected void populateEntryTree(NamedTree<GuiMenuEntry> entryTree) {
        for (Entry<String, NamedTree<T>> entry : tree.entries())
            entryTree.add(entry.getKey(), get(entry.getKey(), GuiMenuEntry.class));
        if (submenu.hasExtension())
            submenu.get().populateEntryTree(entryTree.folder(opened));
    }
    
    public GuiMenuEntry getEntry(String path) {
        String name = path.split("\\.")[0];
        var entry = get(name, GuiMenuEntry.class);
        if (entry == null)
            return null;
        if (path.contains(".")) {
            if (entry.name.equals(opened))
                return submenu.get().getEntry(path.substring(name.length()));
            return null;
        }
        return entry;
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (super.mouseClicked(x, y, button)) {
            parentCreator().markKeptFocus();
            return true;
        }
        return false;
    }
    
    public abstract boolean isRoot();
    
    public abstract GuiMenuRoot<T> root();
    
    public abstract GuiExtensionCreator<? extends GuiControl, ? extends GuiMenu> parentCreator();
    
    @Override
    public void closed() {
        if (submenu.hasExtension())
            submenu.close();
    }
    
    public class GuiMenuEntry extends GuiLabel {
        
        private static final GuiStyle HOVERED = new GuiStyle();
        
        static {
            HOVERED.clickable = new DisplayColor(0.7F, 0.7F, 0.7F, 1);
        }
        
        private boolean highlighted;
        
        public NamedTree<T> folder;
        
        public GuiMenuEntry(String name, String title, NamedTree<T> folder) {
            super(name);
            this.folder = folder;
            setTitle(root().translate(title, folder.value != null));
        }
        
        public void setHighlighted(boolean value) {
            this.highlighted = value;
        }
        
        public void close() {
            if (submenu.hasExtension())
                submenu.close();
        }
        
        public void open() {
            if (submenu.hasExtension())
                submenu.close();
            submenu.open(new GuiMenuSub<T>(root(), folder, submenu), this, ExtensionDirection.RIGHT);
            opened = name;
        }
        
        @Override
        public void mouseMoved(double x, double y) {
            if (rect.insideLocalPos(x, y) && folder.hasChildren())
                open();
        }
        
        @Override
        public boolean mouseClicked(double x, double y, int button) {
            if (folder.value != null) {
                root().select(folder.path(), folder.value);
                playSound(SoundEvents.UI_BUTTON_CLICK);
            }
            return true;
        }
        
        @Override
        public GuiStyle getStyle() {
            if (highlighted)
                return HOVERED;
            return super.getStyle();
        }
        
        @Override
        public ControlFormatting getControlFormatting() {
            return ControlFormatting.CLICKABLE;
        }
    }
}
