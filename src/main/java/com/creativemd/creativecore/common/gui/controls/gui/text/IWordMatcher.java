package com.creativemd.creativecore.common.gui.controls.gui.text;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;

public interface IWordMatcher {
    
    public static IWordMatcher DEFAULT_MATCHER = new IWordMatcher() {
        @Override
        public int findNextWordStartBoundary(String text, int from) {
            IWordMatcher.checkBounds(text, from);
            return -1;
        }
        
        @Override
        public int findNextWordEndBoundary(String text, int from) {
            IWordMatcher.checkBounds(text, from);
            return text == null || from == text.length() ? -1 : text.length();
        }
        
        @Override
        public int findPreviousWordStartBoundary(String text, int from) {
            IWordMatcher.checkBounds(text, from);
            return text == null || from == 0 ? -1 : 0;
        }
        
        @Override
        public int findPreviousWordEndBoundary(String text, int from) {
            IWordMatcher.checkBounds(text, from);
            return -1;
        }
    };
    
    public default int findNextWordBoundary(String text, int from) {
        return Math.min(this.findNextWordStartBoundary(text, from), this.findNextWordEndBoundary(text, from));
    }
    
    public int findNextWordStartBoundary(String text, int from);
    
    public int findNextWordEndBoundary(String text, int from);
    
    public default int findPreviousWordBoundary(String text, int from) {
        return Math.max(this.findPreviousWordStartBoundary(text, from), this.findPreviousWordEndBoundary(text, from));
    }
    
    public int findPreviousWordStartBoundary(String text, int from);
    
    public int findPreviousWordEndBoundary(String text, int from);
    
    public default TextareaRange findWordAt(String text, int pos) {
        IWordMatcher.checkBounds(text, pos);
        
        if (this.isWordStartBoundary(text, pos)) {
            int end = this.findNextWordEndBoundary(text, pos);
            if (end < 0)
                return TextareaRange.INVALID_RANGE;
            else
                return new TextareaRange(pos, end);
            
        } else if (this.isWordEndBoundary(text, pos)) {
            int start = this.findPreviousWordStartBoundary(text, pos);
            if (start < 0)
                return TextareaRange.INVALID_RANGE;
            else
                return new TextareaRange(start, pos);
        } else {
            int start = this.findPreviousWordStartBoundary(text, pos);
            int end = this.findNextWordEndBoundary(text, pos);
            if (start < 0 || end < 0)
                return TextareaRange.INVALID_RANGE;
            else
                return new TextareaRange(start, end);
        }
    }
    
    public default boolean isWordAt(String text, int pos) {
        return this.isWordBoundary(text, pos) || (this.findPreviousWordEndBoundary(text, pos) < this.findPreviousWordStartBoundary(text, pos));
    }
    
    public default boolean isWordBoundary(String text, int pos) {
        return this.isWordStartBoundary(text, pos) || this.isWordEndBoundary(text, pos);
    }
    
    public default boolean isWordStartBoundary(String text, int pos) {
        IWordMatcher.checkBounds(text, pos);
        
        if (text == null || text.isEmpty())
            return false;
        else {
            if (pos == 0)
                return this.findPreviousWordStartBoundary(text, pos + 1) == pos;
            else
                return this.findNextWordStartBoundary(text, pos - 1) == pos;
        }
    }
    
    public default boolean isWordEndBoundary(String text, int pos) {
        IWordMatcher.checkBounds(text, pos);
        
        if (text == null || text.isEmpty())
            return false;
        else {
            if (pos == text.length())
                return this.findNextWordEndBoundary(text, pos - 1) == pos;
            else
                return this.findPreviousWordEndBoundary(text, pos + 1) == pos;
        }
    }
    
    public default boolean isLineBreakCanditate(String text, int pos) {
        return this.isWordStartBoundary(text, pos);
    }
    
    public default int findPreviousLineBreakCanditate(String text, int pos) {
        return this.findPreviousWordStartBoundary(text, pos);
    }
    
    public default int findNextLineBreakCanditate(String text, int pos) {
        return this.findNextWordStartBoundary(text, pos);
    }
    
    public default String trimToWidth(String text, int width) {
        String r = GuiRenderHelper.instance.font.trimStringToWidth(text, width);
        if (r == null || r.length() == 0)
            return text;
        
        if (!this.isLineBreakCanditate(text, r.length())) {
            int pe = this.findPreviousLineBreakCanditate(text, r.length());
            if (pe > 0 && pe < r.length())
                return text.substring(0, pe);
        }
        return r;
    }
    
    static void checkBounds(String text, int pos) {
        if (pos < 0 || pos > text.length())
            throw new IndexOutOfBoundsException();
    }
}
