package team.creative.creativecore.client.render.text;

import net.minecraft.client.StringSplitter;
import net.minecraft.network.chat.Style;
import team.creative.creativecore.common.util.text.content.AdvancedContent;
import team.creative.creativecore.common.util.text.content.FormattedSingleSink;
import team.creative.creativecore.mixin.StringSplitterAccessor;

public class WidthLimitedCharSink implements FormattedSingleSink {
    
    private final StringSplitter.WidthProvider widthProvider;
    private float maxWidth;
    private int position;
    private int[] lastPositions = new int[Linebreaker.values().length];
    
    public WidthLimitedCharSink(float maxWidth, StringSplitter splitter) {
        this.maxWidth = maxWidth;
        this.widthProvider = ((StringSplitterAccessor) splitter).getWidthProvider();
        resetPosition();
    }
    
    @Override
    public boolean accept(int pos, Style style, int character) {
        this.maxWidth -= widthProvider.getWidth(character, style);
        for (int i = 0; i < lastPositions.length; i++)
            if (Linebreaker.values()[i].predicate.test((char) character))
                lastPositions[i] = position;
        if (this.maxWidth >= 0.0F) {
            this.position = pos + Character.charCount(character);
            return true;
        } else
            return false;
    }
    
    @Override
    public boolean accept(Style style, AdvancedContent content) {
        this.maxWidth -= content.width(widthProvider, style);
        return this.maxWidth >= 0.0F;
    }
    
    public int getPosition() {
        return this.position;
    }
    
    public Linebreaker lastBreaker() {
        for (int i = 0; i < lastPositions.length; i++)
            if (lastPositions[i] != -1)
                return Linebreaker.values()[i];
        return null;
    }
    
    public int lastBreakerPos() {
        for (int i = 0; i < lastPositions.length; i++)
            if (lastPositions[i] != -1)
                return lastPositions[i];
        return position;
    }
    
    public void resetPosition() {
        this.position = 0;
        for (int i = 0; i < lastPositions.length; i++)
            lastPositions[i] = -1;
    }
    
}
