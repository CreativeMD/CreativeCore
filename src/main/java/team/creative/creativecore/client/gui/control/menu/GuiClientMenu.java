package team.creative.creativecore.client.gui.control.menu;

import java.util.Map.Entry;

import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.client.gui.GuiClientControl;
import team.creative.creativecore.client.gui.control.parent.GuiClientScrollY;
import team.creative.creativecore.client.gui.control.simple.GuiClientLabel;
import team.creative.creativecore.client.gui.extension.GuiExtensionCreator;
import team.creative.creativecore.client.gui.extension.GuiExtensionCreator.ExtensionDirection;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.control.menu.GuiMenu;
import team.creative.creativecore.common.gui.control.menu.GuiMenu.GuiMenuDist;
import team.creative.creativecore.common.gui.control.menu.GuiMenu.GuiMenuEntry;
import team.creative.creativecore.common.gui.control.menu.GuiMenu.GuiMenuEntryDist;
import team.creative.creativecore.common.gui.control.menu.GuiMenuRoot;
import team.creative.creativecore.common.gui.control.menu.GuiMenuSub;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.gui.style.display.DisplayColor;
import team.creative.creativecore.common.util.type.tree.NamedTree;

public abstract class GuiClientMenu<K, T extends GuiMenu<K>> extends GuiClientScrollY<T> implements GuiMenuDist<K> {
    
    public final GuiExtensionCreator<GuiClientMenu, GuiMenu> submenu = new GuiExtensionCreator<>(this) {
        
        public void markKeptFocus() {
            super.markKeptFocus();
            parentCreator().markKeptFocus();
        }
    };
    protected NamedTree<K> tree;
    private String opened;
    
    public GuiClientMenu(T control) {
        super(control);
    }
    
    @Override
    public void populateEntryTree(NamedTree<GuiMenuEntry> entryTree) {
        for (Entry<String, NamedTree<K>> entry : tree.entries())
            entryTree.add(entry.getKey(), control.get(entry.getKey(), GuiMenuEntry.class));
        if (submenu.hasExtension())
            submenu.get().populateEntryTree(entryTree.folder(opened));
    }
    
    @Override
    public void init(NamedTree tree) {
        this.tree = tree;
        this.hoveredScroll = true;
        this.align = Align.STRETCH;
        this.spacing = -1;
    }
    
    @Override
    protected ControlFormatting defaultFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
    
    @Override
    public boolean mouseClicked(double x, double y, MouseButtonInfo info) {
        if (super.mouseClicked(x, y, info)) {
            parentCreator().markKeptFocus();
            return true;
        }
        return false;
    }
    
    @Override
    public abstract boolean isRoot();
    
    @Override
    public abstract GuiMenuRoot<K> root();
    
    public abstract GuiExtensionCreator<? extends GuiClientControl, ? extends GuiMenu> parentCreator();
    
    @Override
    public void buildTree() {
        String path = tree.path();
        if (!path.isBlank())
            path += ".";
        for (Entry<String, NamedTree<K>> entry : tree.entries())
            control.add(new GuiMenuEntry(control, entry.getKey(), path + entry.getKey(), entry.getValue()));
    }
    
    @Override
    public void closed() {
        if (submenu.hasExtension())
            submenu.close();
    }
    
    public static class GuiClientMenuEntry extends GuiClientLabel<GuiMenuEntry> implements GuiMenuEntryDist {
        
        private static final GuiStyle HOVERED = new GuiStyle();
        
        static {
            HOVERED.clickable = new DisplayColor(0.7F, 0.7F, 0.7F, 1);
        }
        
        private boolean highlighted;
        
        public GuiClientMenuEntry(GuiMenuEntry control) {
            super(control);
        }
        
        public GuiClientMenu getMenu() {
            return (GuiClientMenu) control.menu().dist();
        }
        
        @Override
        public void setHighlighted(boolean value) {
            this.highlighted = value;
        }
        
        @Override
        public void close() {
            var menu = getMenu();
            if (menu.submenu.hasExtension())
                menu.submenu.close();
        }
        
        @Override
        public void open() {
            var menu = getMenu();
            if (menu.submenu.hasExtension())
                menu.submenu.close();
            var sub = new GuiMenuSub(menu.root(), control.folder);
            ((GuiClientMenuSub) sub.dist()).parent = menu.submenu;
            menu.submenu.open(sub, this, ExtensionDirection.RIGHT);
            menu.opened = control.name;
        }
        
        @Override
        public void mouseMoved(double x, double y) {
            if (rect.insideLocalPos(x, y) && control.folder.hasChildren()) {
                var dist = ((GuiClientMenu<?, ?>) control.menu().dist());
                if (dist.submenu.hasExtension())
                    dist.submenu.close();
                dist.submenu.open(new GuiMenuSub<>(control.menu().root(), control.folder), this, ExtensionDirection.RIGHT);
                
            }
        }
        
        @Override
        public boolean mouseClicked(double x, double y, MouseButtonInfo info) {
            if (control.folder.value != null) {
                control.menu().root().select(control.folder.path(), control.folder.value);
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
        protected ControlFormatting defaultFormatting() {
            return ControlFormatting.CLICKABLE;
        }
        
    }
}
