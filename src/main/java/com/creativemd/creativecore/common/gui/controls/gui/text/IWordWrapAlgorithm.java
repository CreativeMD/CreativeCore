package com.creativemd.creativecore.common.gui.controls.gui.text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;

@FunctionalInterface
public interface IWordWrapAlgorithm {
    
    public static final IWordWrapAlgorithm DEFAULT_WORDWRAP_ALGORITHM = (text, width, matcher) -> {
        if (width <= 0 || text == null || text.isEmpty())
            return Stream.of(text);
        
        List<String> res = new ArrayList<>();
        String lot = text;
        do {
            String p;
            if (matcher != null)
                p = matcher.trimToWidth(text, width);
            else {
                p = GuiRenderHelper.instance.font.trimStringToWidth(lot, width);
                if (p == null || p.length() == 0)
                    return Stream.of(lot);
            }
            res.add(p);
            lot = lot.substring(p.length());
        } while (!lot.isEmpty());
        return res.stream();
    };
    
    public static final IWordWrapAlgorithm DEFAULT_NEWLINE_WORDWRAP_ALGORITHM = (text, width, matcher) -> {
        String[] lines = text.toString().split("\\R");
        if (width <= 0)
            return Stream.of(lines);
        else
            return Stream.of(lines).flatMap(L -> IWordWrapAlgorithm.DEFAULT_WORDWRAP_ALGORITHM.wrap(L, width, matcher));
    };
    
    public Stream<String> wrap(String text, int width, IWordMatcher wordMatcher);
    
    public default Stream<String> wrap(String text, int width) {
        return this.wrap(text, width, IWordMatcher.DEFAULT_MATCHER);
    }
    
    public default Stream<String> wrap(String text) {
        return this.wrap(text, -1, IWordMatcher.DEFAULT_MATCHER);
    }
}
