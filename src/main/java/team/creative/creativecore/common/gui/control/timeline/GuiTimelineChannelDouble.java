package team.creative.creativecore.common.gui.control.timeline;

public class GuiTimelineChannelDouble extends GuiTimelineChannel<Double> {
    
    public GuiTimelineChannelDouble(GuiTimeline timeline) {
        super(timeline);
    }
    
    @Override
    public Double getValueAt(int time) {
        return 0D;
    }
    
}
