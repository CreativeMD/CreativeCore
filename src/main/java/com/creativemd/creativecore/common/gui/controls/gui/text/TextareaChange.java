package com.creativemd.creativecore.common.gui.controls.gui.text;

import java.util.ArrayList;
import java.util.List;

import com.creativemd.creativecore.common.utils.type.Pair;

public class TextareaChange {
    protected GuiTextarea textarea;
    protected String text;
    protected List<String> lines = new ArrayList<>();
    protected List<Integer> offsets = new ArrayList<>();
    
    protected String replacementText = null;
    protected Integer replaceFrom = null;
    protected Integer replaceTo = null;
    protected Integer cursorOffset = null;
    protected Integer cursorRowPosition = null;
    protected Integer cursorColumnPosition = null;
    protected Integer anchorOffset = null;
    protected Integer anchorRowPosition = null;
    protected Integer anchorColumnPosition = null;
    
    public TextareaChange(GuiTextarea textarea, String replacementText, int replaceFrom, int replaceTo, int cursorOffset, int anchorOffset) {
        this.textarea = textarea;
        this.replacementText = replacementText;
        this.replaceFrom = Math.min(replaceFrom, replaceTo);
        this.replaceTo = Math.max(replaceFrom, replaceTo);
        this.cursorOffset = cursorOffset;
        this.anchorOffset = anchorOffset;
        this.checkReplacementRange(replaceFrom, replaceTo);
    }
    
    public TextareaChange(GuiTextarea textarea, String replacementText, int replaceFrom, int replaceTo, int cursorRowPosition, int cursorColumnPosition, int anchorRowPosition, int anchorColumnPosition) {
        this.textarea = textarea;
        this.replacementText = replacementText;
        this.replaceFrom = Math.min(replaceFrom, replaceTo);
        this.replaceTo = Math.max(replaceFrom, replaceTo);
        this.cursorRowPosition = cursorRowPosition;
        this.cursorColumnPosition = cursorColumnPosition;
        this.anchorRowPosition = anchorRowPosition;
        this.anchorColumnPosition = anchorColumnPosition;
        this.checkReplacementRange(replaceFrom, replaceTo);
    }
    
    public TextareaChange(GuiTextarea textarea, int cursorOffset, int anchorOffset) {
        this.textarea = textarea;
        this.cursorOffset = cursorOffset;
        this.anchorOffset = anchorOffset;
    }
    
    public TextareaChange(GuiTextarea textarea, int cursorRowPosition, int cursorColumnPosition, int anchorRowPosition, int anchorColumnPosition) {
        this.textarea = textarea;
        this.cursorRowPosition = cursorRowPosition;
        this.cursorColumnPosition = cursorColumnPosition;
        this.anchorRowPosition = anchorRowPosition;
        this.anchorColumnPosition = anchorColumnPosition;
    }
    
    // States
    public boolean isAdded() {
        return this.replacementText != null && !this.replacementText.isEmpty();
    }
    
    public boolean isRemoved() {
        return this.replacementText != null && this.replaceFrom != null && this.replaceFrom != this.replaceTo;
    }
    
    public boolean isReplaced() {
        return this.isAdded() && this.isRemoved();
    }
    
    // current Getters
    
    public String getCurrentText() {
        return this.textarea.getText();
    }
    
    public String getCurrentSelectedText() {
        return this.textarea.getSelectedText();
    }
    
    public int getCurrentCursorOffset() {
        return this.textarea.getSelection().getCursorOffset();
    }
    
    public int getCurrentAnchorOffset() {
        return this.textarea.getSelection().getAnchorOffset();
    }
    
    public TextareaVec getCurrentCursorPosition() {
        return this.textarea.getSelection().getCursorPosition();
    }
    
    public TextareaVec getCurrentAnchorPosition() {
        return this.textarea.getSelection().getAnchorPosition();
    }
    
    public TextareaRange getCurrentSelectionOffsets() {
        return this.textarea.getSelection().getSelectionOffsets();
    }
    
    public TextareaRange getCurrentSelectionRangeOffsets() {
        return this.textarea.getSelection().getSelectionRangeOffsets();
    }
    
    public Pair<TextareaVec, TextareaVec> getCurrentSelectionPositions() {
        return this.textarea.getSelection().getSelectionPositions();
    }
    
    public Pair<TextareaVec, TextareaVec> getCurrentSelectionRangePositions() {
        return this.textarea.getSelection().getSelectionRangePositions();
    }
    
    // new Getters
    
    public TextareaRange getReplacementRange() {
        if (this.replaceFrom != null && this.replaceTo != null)
            return new TextareaRange(this.replaceFrom, this.replaceTo);
        return TextareaRange.INVALID_RANGE;
    }
    
    public String getNewText() {
        if (this.text == null)
            this.recalcText();
        return this.text;
    }
    
    public String getNewSelectedText() {
        TextareaRange sel = this.getNewSelectionRangeOffsets();
        return this.getNewText().substring(sel.start, sel.end);
    }
    
    public int getNewCursorOffset() {
        if (this.cursorOffset == null)
            this.cursorOffset = this.toOffset(this.cursorRowPosition, this.cursorColumnPosition);
        return this.cursorOffset;
    }
    
    public int getNewAnchorOffset() {
        if (this.anchorOffset == null)
            this.anchorOffset = this.toOffset(this.anchorRowPosition, this.anchorColumnPosition);
        return this.anchorOffset;
    }
    
    public TextareaVec getNewCursorPosition() {
        if (this.cursorRowPosition == null || this.cursorColumnPosition == null) {
            TextareaVec cur = this.toCoordinates(this.cursorOffset);
            this.cursorRowPosition = cur.row;
            this.cursorColumnPosition = cur.column;
            return cur;
        } else
            return new TextareaVec(this.cursorColumnPosition, this.cursorRowPosition);
    }
    
    public TextareaVec getNewAnchorPosition() {
        if (this.anchorRowPosition == null || this.anchorColumnPosition == null) {
            TextareaVec anch = this.toCoordinates(this.anchorOffset);
            this.anchorRowPosition = anch.row;
            this.anchorColumnPosition = anch.column;
            return anch;
        }
        
        return new TextareaVec(this.anchorColumnPosition, this.anchorOffset);
    }
    
    public TextareaRange getNewSelectionOffsets() {
        return new TextareaRange(this.getNewAnchorOffset(), this.getNewCursorOffset());
    }
    
    public TextareaRange getNewSelectionRangeOffsets() {
        int anch = this.getNewAnchorOffset();
        int cur = this.getNewCursorOffset();
        return new TextareaRange(Math.min(anch, cur), Math.max(anch, cur));
    }
    
    public Pair<TextareaVec, TextareaVec> getNewSelectionPositions() {
        return new Pair<TextareaVec, TextareaVec>(this.getNewAnchorPosition(), this.getNewCursorPosition());
    }
    
    public Pair<TextareaVec, TextareaVec> getNewSelectionRangePositions() {
        TextareaVec cur = this.getNewCursorPosition();
        TextareaVec anch = this.getNewAnchorPosition();
        
        boolean anchorFirst = anch.row < cur.row || (anch.row == cur.row && anch.column <= cur.column);
        if (anchorFirst)
            return new Pair<TextareaVec, TextareaVec>(anch, cur);
        return new Pair<TextareaVec, TextareaVec>(cur, anch);
    }
    
    // Setters
    
    public void setReplacementText(String replacement) {
        this.replacementText = replacement;
        this.text = null;
        this.lines.clear();
        this.offsets.clear();
    }
    
    public void setReplacementRange(int from, int to) {
        this.checkReplacementRange(from, to);
        
        this.replaceFrom = Math.min(from, to);
        this.replaceTo = Math.max(from, to);
        this.text = null;
        this.lines.clear();
        this.offsets.clear();
    }
    
    public void setReplacement(int from, int to, String replacement) {
        this.setReplacementText(replacement);
        this.setReplacementRange(from, to);
    }
    
    public void setCursorOffset(int offset) {
        this.cursorOffset = offset;
        this.cursorRowPosition = null;
        this.cursorColumnPosition = null;
    }
    
    public void setCursorPosition(int row, int column) {
        this.cursorOffset = null;
        this.cursorRowPosition = row;
        this.cursorColumnPosition = column;
    }
    
    public void setSelection(int from, int to) {
        this.cursorOffset = to;
        this.cursorRowPosition = null;
        this.cursorColumnPosition = null;
        this.anchorOffset = from;
        this.anchorRowPosition = null;
        this.anchorColumnPosition = null;
    }
    
    public void setSelection(int fromRow, int fromColumn, int toRow, int toColumn) {
        this.cursorOffset = null;
        this.cursorRowPosition = toRow;
        this.cursorColumnPosition = toColumn;
        this.anchorOffset = null;
        this.anchorRowPosition = fromRow;
        this.anchorColumnPosition = fromColumn;
    }
    
    public void selectTo(int offset) {
        this.setSelection(this.getNewAnchorOffset(), offset);
    }
    
    public void selectTo(int row, int column) {
        this.selectTo(this.toOffset(row, column));
    }
    
    // Internal utility
    
    protected int toOffset(int row, int column) {
        if (this.lines.isEmpty() || this.offsets.isEmpty())
            this.reflowText();
        
        int r = Math.max(0, Math.min(row, this.lines.size()));
        int c = Math.max(0, Math.min(column, this.lines.get(r).length() - (this.textarea.replaceModeActive && row != this.lines.size() - 1 ? 1 : 0)));
        return this.offsets.get(r) + c;
    }
    
    protected TextareaVec toCoordinates(int offset) {
        if (this.offsets.isEmpty())
            this.reflowText();
        
        int o = Math.max(0, Math.min(offset, this.text.length()));
        
        for (int i = this.offsets.size(); i-- > 0;)
            if (this.offsets.get(i) < o)
                return new TextareaVec(o - this.offsets.get(i), i);
            
        return TextareaVec.INVALID_VEC;
    }
    
    protected void recalcText() {
        if (this.replacementText != null && this.replaceFrom != null && this.replaceTo != null) {
            StringBuilder sb = new StringBuilder(this.getCurrentText());
            this.text = sb.replace(this.replaceFrom, this.replaceTo, this.replacementText).toString();
        } else
            this.text = this.getCurrentText();
    }
    
    protected void reflowText() {
        if (this.text == null)
            this.recalcText();
        this.lines.clear();
        this.offsets.clear();
        IWordWrapAlgorithm wwa = (this.textarea.wordWrapAlgorithm != null ? this.textarea.wordWrapAlgorithm : IWordWrapAlgorithm.DEFAULT_NEWLINE_WORDWRAP_ALGORITHM);
        
        wwa.wrap(this.text, this.textarea.width - (this.textarea.getContentOffset() * 2), this.textarea.wordMatcher).sequential().reduce(0, (offset, line) -> {
            this.lines.add(line);
            this.offsets.add(offset);
            return offset + line.length();
        }, (l, r) -> l + r);
    }
    
    protected void checkReplacementRange(Integer from, Integer to) {
        if (from != null && to != null && (from < 0 || to < 0 || from > this.getCurrentText().length() || to > this.getCurrentText().length()))
            throw new IndexOutOfBoundsException("Cannot replace outside range of current string-size");
    }
    
    protected void validate() {
        if (this.lines.isEmpty())
            this.reflowText();
        
        this.checkReplacementRange(this.replaceFrom, this.replaceTo);
        
        String t = this.getNewText();
        if (this.cursorOffset != null)
            if (this.cursorOffset < 0 || this.cursorOffset > t.length())
                throw new IllegalArgumentException("Invalid cursor position");
        if (this.cursorRowPosition != null)
            if (this.cursorRowPosition < 0 || this.cursorRowPosition >= this.lines.size())
                throw new IllegalArgumentException("Invalid cursor row-position");
        if (this.cursorColumnPosition != null) {
            if (this.cursorRowPosition == null)
                throw new IllegalArgumentException("Cursor row-position unavailable for column-check");
            if (this.cursorColumnPosition < 0 || this.cursorColumnPosition > this.lines.get(this.cursorRowPosition)
                .length() - ((this.textarea.replaceModeActive && this.cursorRowPosition != this.lines.size() - 1) ? 1 : 0))
                throw new IllegalArgumentException("Invalid cursor column-position");
        }
        
        if (this.anchorOffset != null)
            if (this.anchorOffset < 0 || this.anchorOffset > t.length())
                throw new IllegalArgumentException("Invalid anchor position");
        if (this.anchorRowPosition != null)
            if (this.anchorRowPosition < 0 || this.anchorRowPosition >= this.lines.size())
                throw new IllegalArgumentException("Invalid anchor row-position");
        if (this.anchorColumnPosition != null) {
            if (this.anchorRowPosition == null)
                throw new IllegalArgumentException("Anchor row-position unavailable for column-check");
            if (this.anchorColumnPosition < 0 || this.anchorColumnPosition > this.lines.get(this.anchorRowPosition)
                .length() - ((this.textarea.replaceModeActive && this.anchorRowPosition != this.lines.size() - 1) ? 1 : 0))
                throw new IllegalArgumentException("Invalid anchor column-position");
        }
    }
}
