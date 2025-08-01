package team.creative.creativecore.common.gui.control.timeline;

import team.creative.creativecore.common.gui.control.parent.GuiColumn.GuiColumnHeader;

public class GuiTimelineHeader extends GuiColumnHeader {
    
    public final GuiTimeline timeline;
    
    public GuiTimelineHeader(GuiTimeline timeline) {
        super(timeline.getParent());
        this.timeline = timeline;
        setExpandableX();
    }
    
}
