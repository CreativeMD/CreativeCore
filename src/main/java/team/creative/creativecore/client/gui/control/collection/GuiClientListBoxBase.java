package team.creative.creativecore.client.gui.control.collection;

import team.creative.creativecore.client.gui.control.parent.GuiClientScrollY;
import team.creative.creativecore.common.gui.control.collection.GuiListBoxBase;
import team.creative.creativecore.common.gui.control.collection.GuiListBoxBase.GuiListBoxBaseDist;

public class GuiClientListBoxBase<T extends GuiListBoxBase> extends GuiClientScrollY<T> implements GuiListBoxBaseDist {
    
    public GuiClientListBoxBase(T control) {
        super(control);
    }
    
    @Override
    public void flowY(int width, int height, int preferred) {
        this.cachedHeight = height;
        super.flowY(width, height, preferred);
    }
    
    @Override
    public void reflowInternal() {
        if (control.hasGui()) {
            super.flowX(rect.getContentWidth(), preferredWidth(rect.getContentWidth()));
            super.flowY(rect.getContentWidth(), cachedHeight, preferredHeight(rect.getContentWidth(), cachedHeight));
        }
    }
    
}
