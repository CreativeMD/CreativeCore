package com.creativemd.creativecore.common.gui.controls.gui.text;

public class TextareaRange {
    
    public static final TextareaRange INVALID_RANGE = new TextareaRange(-1, -1);
    
    public final int start;
    public final int end;
    
    public TextareaRange(int start, int end) {
        this.start = start;
        this.end = end;
    }
    
}
