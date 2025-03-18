package team.creative.creativecore.common.gui.control.menu;

import java.util.Map.Entry;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.control.parent.GuiScrollY;
import team.creative.creativecore.common.gui.control.simple.GuiLabel;
import team.creative.creativecore.common.gui.extension.GuiExtensionCreator;
import team.creative.creativecore.common.gui.extension.GuiExtensionCreator.ExtensionDirection;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.type.tree.NamedTree;

public abstract class GuiMenu<T> extends GuiScrollY {
    
    protected final GuiExtensionCreator<GuiMenu, GuiMenu> submenu = new GuiExtensionCreator<>(this) {
        
        public void markKeptFocus() {
            super.markKeptFocus();
            parentCreator().markKeptFocus();
        }
    };
    protected final NamedTree<T> tree;
    
    public GuiMenu(NamedTree<T> tree) {
        super();
        this.tree = tree;
        this.hoveredScroll = true;
        this.align = Align.STRETCH;
        this.spacing = 0;
    }
    
    protected void buildTree() {
        String path = tree.path();
        
        for (String f : tree.folders())
            add(new GuiMenuFolder(path + f, tree.folder(f)));
        for (Entry<String, T> entry : tree.valueEntries())
            add(new GuiMenuValue(path + entry.getKey(), entry.getValue()));
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.PROGRESSBAR;
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
        
        public GuiMenuEntry(String name, Component title) {
            super(name);
            setTitle(title);
        }
        
        @Override
        public ControlFormatting getControlFormatting() {
            return ControlFormatting.CLICKABLE_NO_BORDER;
        }
    }
    
    public class GuiMenuFolder extends GuiMenuEntry {
        
        public NamedTree<T> folder;
        
        public GuiMenuFolder(String name, NamedTree<T> tree) {
            super(name, root().translateFolder(name));
            this.folder = tree;
        }
        
        @Override
        public boolean mouseClicked(double x, double y, int button) {
            if (submenu.hasExtension())
                submenu.close();
            submenu.open(new GuiMenuSub<T>(root(), folder, submenu), ExtensionDirection.RIGHT);
            playSound(SoundEvents.UI_BUTTON_CLICK);
            return true;
        }
    }
    
    public class GuiMenuValue extends GuiMenuEntry {
        
        public final T value;
        
        public GuiMenuValue(String name, T value) {
            super(name, root().translateValue(value));
            this.value = value;
        }
        
        @Override
        public boolean mouseClicked(double x, double y, int button) {
            root().select(value);
            playSound(SoundEvents.UI_BUTTON_CLICK);
            return true;
        }
        
    }
}
