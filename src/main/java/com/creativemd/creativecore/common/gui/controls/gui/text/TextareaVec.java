package com.creativemd.creativecore.common.gui.controls.gui.text;

public class TextareaVec {
    
    public static final TextareaVec INVALID_VEC = new TextareaVec(-1, -1);
    
    public final int column;
    public final int row;
    
    public TextareaVec(int column, int row) {
        this.column = column;
        this.row = row;
    }
    
}
