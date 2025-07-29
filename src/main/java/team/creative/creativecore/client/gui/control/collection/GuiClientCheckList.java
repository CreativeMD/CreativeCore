package team.creative.creativecore.client.gui.control.collection;

import team.creative.creativecore.client.gui.control.parent.GuiClientScrollY;
import team.creative.creativecore.common.gui.control.collection.GuiCheckList;
import team.creative.creativecore.common.gui.control.collection.GuiCheckList.GuiCheckListDist;

public class GuiClientCheckList<T extends GuiCheckList> extends GuiClientScrollY<T> implements GuiCheckListDist {
    
    protected int cachedWidth;
    protected int cachedHeight;
    
    public GuiClientCheckList(T control) {
        super(control);
    }
    
    @Override
    public void flowX(int width, int preferred) {
        this.cachedWidth = width;
        super.flowX(width, preferred);
    }
    
    @Override
    public void flowY(int width, int height, int preferred) {
        this.cachedHeight = height;
        super.flowY(width, height, preferred);
    }
    
    @Override
    public void reflowInternal() {
        if (control.hasGui()) {
            super.flowX(cachedWidth, preferredWidth(cachedWidth));
            super.flowY(cachedWidth, cachedHeight, preferredHeight(cachedWidth, cachedHeight));
        }
    }
    
}
