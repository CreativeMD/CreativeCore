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
        this.spacing = -1;
    }
    
    protected void buildTree() {
        String path = tree.path();
        if (!path.isBlank())
            path += ".";
        for (Entry<String, NamedTree<T>> entry : tree.entries())
            add(new GuiMenuEntry(path + entry.getKey(), entry.getValue()));
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
        
        public NamedTree<T> folder;
        
        public GuiMenuEntry(String name, NamedTree<T> folder) {
            super(name);
            this.folder = folder;
            setTitle(root().translate(name, folder.value != null));
        }
        
        @Override
        public void mouseMoved(double x, double y) {
            if (rect.insideLocalPos(x, y) && folder.hasChildren()) {
                if (submenu.hasExtension())
                    submenu.close();
                submenu.open(new GuiMenuSub<T>(root(), folder, submenu), ExtensionDirection.RIGHT);
                
            }
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
        public ControlFormatting getControlFormatting() {
            return ControlFormatting.CLICKABLE;
        }
    }
}
