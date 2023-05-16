package team.creative.creativecore.common.gui.controls.timeline;

public class GuiTimelineChannelDouble extends GuiTimelineChannel<Double> {
    
    public GuiTimelineChannelDouble(GuiTimeline timeline) {
        super(timeline);
    }
    
    @Override
    protected Double getValueAt(int time) {
        return 0D;
    }
    
}
