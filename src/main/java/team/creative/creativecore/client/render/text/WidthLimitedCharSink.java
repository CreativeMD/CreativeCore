package team.creative.creativecore.client.render.text;

import java.util.Arrays;

import net.minecraft.client.StringSplitter;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSink;
import team.creative.creativecore.mixin.StringSplitterAccessor;

public class WidthLimitedCharSink implements FormattedCharSink {
    
    private final StringSplitter.WidthProvider widthProvider;
    private float maxWidth;
    private int position;
    private final int[] lastPositions = new int[Linebreaker.values().length];
    
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
        Arrays.fill(lastPositions, -1);
    }
    
}
