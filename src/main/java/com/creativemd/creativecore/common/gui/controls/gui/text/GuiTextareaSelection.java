package com.creativemd.creativecore.common.gui.controls.gui.text;

import com.creativemd.creativecore.common.utils.type.Pair;

public class GuiTextareaSelection {
    protected GuiTextarea textarea;
    protected int cursorOffset = 0;
    protected int cursorRowPosition = 0;
    protected int cursorColumnPosition = 0;
    protected int anchorOffset = 0;
    protected int anchorRowPosition = 0;
    protected int anchorColumnPosition = 0;
    
    protected String selectedString = null;
    
    protected GuiTextareaSelection(GuiTextarea textarea) {
        if (textarea == null)
            throw new NullPointerException();
        this.textarea = textarea;
    }
    
    // getters
    
    public boolean hasSelectedText() {
        return this.anchorOffset >= 0 && this.anchorOffset != this.cursorOffset;
    }
    
    public String getSelectedText() {
        if (this.hasSelectedText()) {
            if (this.selectedString == null)
                return this.selectedString = this.textarea.getText().substring(Math.min(this.anchorOffset, this.cursorOffset), Math.max(this.anchorOffset, this.cursorOffset));
            else
                return this.selectedString;
        }
        return "";
    }
    
    public int getCursorOffset() {
        return this.cursorOffset;
    }
    
    public TextareaVec getCursorPosition() {
        return new TextareaVec(this.cursorColumnPosition, this.cursorRowPosition);
    }
    
    public int getAnchorOffset() {
        return this.anchorOffset;
    }
    
    public TextareaVec getAnchorPosition() {
        return new TextareaVec(this.anchorColumnPosition, this.anchorRowPosition);
    }
    
    public TextareaRange getSelectionOffsets() {
        return new TextareaRange(this.anchorOffset, this.cursorOffset);
    }
    
    public TextareaRange getSelectionRangeOffsets() {
        return new TextareaRange(Math.min(this.anchorOffset, this.cursorOffset), Math.max(this.anchorOffset, this.cursorOffset));
    }
    
    public Pair<TextareaVec, TextareaVec> getSelectionPositions() {
        return new Pair<>(new TextareaVec(this.anchorColumnPosition, this.anchorRowPosition), new TextareaVec(this.cursorColumnPosition, this.cursorRowPosition));
    }
    
    public Pair<TextareaVec, TextareaVec> getSelectionRangePositions() {
        boolean anchorFirst = this.anchorRowPosition < this.cursorRowPosition || (this.anchorRowPosition == this.cursorRowPosition && this.anchorColumnPosition <= this.cursorColumnPosition);
        if (anchorFirst)
            return new Pair<>(new TextareaVec(this.anchorColumnPosition, this.anchorRowPosition), new TextareaVec(this.cursorColumnPosition, this.cursorRowPosition));
        return new Pair<>(new TextareaVec(this.cursorColumnPosition, this.cursorRowPosition), new TextareaVec(this.anchorColumnPosition, this.anchorRowPosition));
    }
    
    // cursor manipulations
    
    public void setCursorOffset(int offset) {
        this.textarea.onContentChange(new TextareaChange(this.textarea, offset, offset));
    }
    
    public void setCursorPosition(int row, int column) {
        this.textarea.onContentChange(new TextareaChange(this.textarea, row, column, row, column));
    }
    
    public void moveCursorRight() {
        this.setCursorOffset(Math.min(this.cursorOffset + 1, this.textarea.getText().length()));
    }
    
    public void moveCursorLeft() {
        this.setCursorOffset(Math.max(this.cursorOffset - 1, 0));
    }
    
    public void moveCursorUp() {
        if (this.cursorRowPosition > 0)
            this.setCursorPosition(this.cursorRowPosition - 1, this.textarea
                .rayTraceColumn(this.cursorRowPosition - 1, this.textarea.columnToXpos(this.cursorRowPosition, this.cursorColumnPosition)));
    }
    
    public void moveCursorDown() {
        if (this.cursorRowPosition < this.textarea.getContent().getLineCount() - 1)
            this.setCursorPosition(this.cursorRowPosition + 1, this.textarea
                .rayTraceColumn(this.cursorRowPosition + 1, this.textarea.columnToXpos(this.cursorRowPosition, this.cursorColumnPosition)));
    }
    
    public void moveCursorToStart() {
        this.setCursorOffset(0);
    }
    
    public void moveCursorToEnd() {
        this.setCursorOffset(this.textarea.getText().length());
    }
    
    public void moveCursorToSelectionStart() {
        this.setCursorOffset(this.getSelectionRangeOffsets().start);
    }
    
    public void moveCursorToSelectionEnd() {
        this.setCursorOffset(this.getSelectionRangeOffsets().end);
    }
    
    public void moveCursorToLineStart() {
        this.setCursorPosition(this.cursorRowPosition, 0);
    }
    
    public void moveCursorToLineEnd() {
        this.setCursorPosition(this.cursorRowPosition, this.textarea.getContent().getLine(this.cursorRowPosition)
            .length() - (this.textarea.replaceModeActive && this.cursorRowPosition != this.textarea.getContent().getLineCount() - 1 ? 1 : 0));
    }
    
    public void moveCursorToNextWordStart() {
        IWordMatcher wm = this.textarea.getWordMatcher();
        int nw = wm.findNextWordStartBoundary(this.textarea.getText(), this.cursorOffset);
        if (nw < 0)
            this.moveCursorToEnd();
        else
            this.setCursorOffset(nw);
    }
    
    public void moveCursorToNextWordEnd() {
        IWordMatcher wm = this.textarea.getWordMatcher();
        int nw = wm.findNextWordEndBoundary(this.textarea.getText(), this.cursorOffset);
        if (nw < 0)
            this.moveCursorToEnd();
        else
            this.setCursorOffset(nw);
    }
    
    public void moveCursorToNextWordBoundary() {
        IWordMatcher wm = this.textarea.getWordMatcher();
        int nw = wm.findNextWordBoundary(this.textarea.getText(), this.cursorOffset);
        if (nw < 0)
            this.moveCursorToEnd();
        else
            this.setCursorOffset(nw);
    }
    
    public void moveCursorToPreviousWordStart() {
        IWordMatcher wm = this.textarea.getWordMatcher();
        int nw = wm.findPreviousWordStartBoundary(this.textarea.getText(), this.cursorOffset);
        if (nw < 0)
            this.moveCursorToStart();
        else
            this.setCursorOffset(nw);
    }
    
    public void moveCursorToPreviousWordEnd() {
        IWordMatcher wm = this.textarea.getWordMatcher();
        int nw = wm.findPreviousWordEndBoundary(this.textarea.getText(), this.cursorOffset);
        if (nw < 0)
            this.moveCursorToStart();
        else
            this.setCursorOffset(nw);
    }
    
    public void moveCursorToPreviousWordBoundary() {
        IWordMatcher wm = this.textarea.getWordMatcher();
        int nw = wm.findPreviousWordBoundary(this.textarea.getText(), this.cursorOffset);
        if (nw < 0)
            this.moveCursorToStart();
        else
            this.setCursorOffset(nw);
    }
    
    public void setSelection(int from, int to) {
        this.textarea.onContentChange(new TextareaChange(this.textarea, to, from));
    }
    
    public void setSelection(int fromRow, int fromColumn, int toRow, int toColumn) {
        this.textarea.onContentChange(new TextareaChange(this.textarea, toRow, toColumn, fromRow, fromColumn));
    }
    
    public void selectTo(int offset) {
        this.setSelection(this.anchorOffset, offset);
    }
    
    public void selectTo(int row, int column) {
        this.selectTo(this.toOffset(row, column));
    }
    
    public void selectAll() {
        this.setSelection(0, this.textarea.getText().length());
    }
    
    public void selectRight() {
        this.selectTo(Math.min(this.cursorOffset + 1, this.textarea.getText().length()));
    }
    
    public void selectLeft() {
        this.selectTo(Math.max(this.cursorOffset - 1, 0));
    }
    
    public void selectUp() {
        if (this.cursorRowPosition > 0)
            this.selectTo(this.cursorRowPosition - 1, this.textarea
                .rayTraceColumn(this.cursorRowPosition - 1, this.textarea.columnToXpos(this.cursorRowPosition, this.cursorColumnPosition)));
    }
    
    public void selectDown() {
        if (this.cursorRowPosition < this.textarea.getContent().getLineCount() - 1)
            this.selectTo(this.cursorRowPosition + 1, this.textarea
                .rayTraceColumn(this.cursorRowPosition + 1, this.textarea.columnToXpos(this.cursorRowPosition, this.cursorColumnPosition)));
    }
    
    public void selectToStart() {
        this.selectTo(0);
    }
    
    public void selectToEnd() {
        this.selectTo(this.textarea.getText().length());
    }
    
    public void selectToLineStart() {
        this.selectTo(this.cursorRowPosition, 0);
    }
    
    public void selectToLineEnd() {
        this.selectTo(this.cursorRowPosition, this.textarea.getContent().getLine(this.cursorRowPosition)
            .length() - (this.textarea.replaceModeActive && this.cursorRowPosition != this.textarea.getContent().getLineCount() - 1 ? 1 : 0));
    }
    
    public void selectToNextWord() {
        if (this.anchorOffset > this.cursorOffset) {
            String text = this.textarea.getText();
            IWordMatcher wm = this.textarea.getWordMatcher();
            
            int nw = wm.findNextWordStartBoundary(text, this.cursorOffset);
            if (nw > this.anchorOffset) {
                if (wm.isWordBoundary(text, this.anchorOffset))
                    nw = this.anchorOffset;
                else if (wm.isWordAt(text, this.anchorOffset))
                    nw = wm.findNextWordEndBoundary(text, this.anchorOffset);
                else
                    nw = wm.findPreviousWordEndBoundary(text, this.anchorOffset);
            }
            this.selectTo(nw);
        } else
            this.selectToNextWordEnd();
    }
    
    public void selectToNextWordStart() {
        IWordMatcher wm = this.textarea.getWordMatcher();
        int nw = wm.findNextWordStartBoundary(this.textarea.getText(), this.cursorOffset);
        if (nw < 0)
            this.selectToEnd();
        else
            this.selectTo(nw);
    }
    
    public void selectToNextWordEnd() {
        IWordMatcher wm = this.textarea.getWordMatcher();
        int nw = wm.findNextWordEndBoundary(this.textarea.getText(), this.cursorOffset);
        if (nw < 0)
            this.selectToEnd();
        else
            this.selectTo(nw);
    }
    
    public void selectToNextWordBoundary() {
        IWordMatcher wm = this.textarea.getWordMatcher();
        int nw = wm.findNextWordBoundary(this.textarea.getText(), this.cursorOffset);
        if (nw < 0)
            this.selectToEnd();
        else
            this.selectTo(nw);
    }
    
    public void selectToPreviousWord() {
        if (this.anchorOffset < this.cursorOffset) {
            String text = this.textarea.getText();
            IWordMatcher wm = this.textarea.getWordMatcher();
            
            int pw = wm.findPreviousWordEndBoundary(text, this.cursorOffset);
            if (pw < this.anchorOffset) {
                if (wm.isWordBoundary(text, this.anchorOffset))
                    pw = this.anchorOffset;
                else if (wm.isWordAt(text, this.anchorOffset))
                    pw = wm.findPreviousWordStartBoundary(text, this.anchorOffset);
                else
                    pw = wm.findPreviousWordEndBoundary(text, this.anchorOffset);
            }
            this.selectTo(pw);
        } else
            this.selectToPreviousWordStart();
    }
    
    public void selectToPreviousWordStart() {
        IWordMatcher wm = this.textarea.getWordMatcher();
        int nw = wm.findPreviousWordStartBoundary(this.textarea.getText(), this.cursorOffset);
        if (nw < 0)
            this.selectToStart();
        else
            this.selectTo(nw);
    }
    
    public void selectToPreviousWordEnd() {
        IWordMatcher wm = this.textarea.getWordMatcher();
        int nw = wm.findPreviousWordEndBoundary(this.textarea.getText(), this.cursorOffset);
        if (nw < 0)
            this.selectToStart();
        else
            this.selectTo(nw);
    }
    
    public void selectToPreviousWordBoundary() {
        IWordMatcher wm = this.textarea.getWordMatcher();
        int nw = wm.findPreviousWordBoundary(this.textarea.getText(), this.cursorOffset);
        if (nw < 0)
            this.selectToStart();
        else
            this.selectTo(nw);
    }
    
    // Internal utility
    
    public int toOffset(int row, int column) {
        int r = Math.max(0, Math.min(row, this.textarea.getContent().getLineCount()));
        int c = Math
            .max(0, Math.min(column, this.textarea.getContent().getLine(r).length() - (this.textarea.replaceModeActive && r != this.textarea.getContent().getLineCount() - 1 ? 1 : 0)));
        return this.textarea.getContent().getLineOffset(r) + c;
    }
    
    public TextareaVec toCoordinates(int offset) {
        int o = Math.max(0, Math.min(offset, this.textarea.getText().length()));
        
        for (int i = this.textarea.getContent().getLineCount(); i-- > 0;)
            if (this.textarea.getContent().getLineOffset(i) < o)
                return new TextareaVec(o - this.textarea.getContent().getLineOffset(i), i);
        return TextareaVec.INVALID_VEC;
    }
    
    protected void doSelectionChanged(TextareaChange change) {
        if (change.cursorOffset != null) {
            this.cursorOffset = change.cursorOffset;
            TextareaVec cords = this.toCoordinates(this.cursorOffset);
            this.cursorRowPosition = cords.row;
            this.cursorColumnPosition = cords.column;
        } else if (change.cursorRowPosition != null && change.cursorColumnPosition != null) {
            this.cursorRowPosition = change.cursorRowPosition;
            this.cursorColumnPosition = change.cursorColumnPosition;
            this.cursorOffset = this.toOffset(this.cursorRowPosition, this.cursorColumnPosition);
        }
        
        if (change.anchorOffset != null) {
            this.anchorOffset = change.anchorOffset;
            TextareaVec cords = this.toCoordinates(this.anchorOffset);
            this.anchorRowPosition = cords.row;
            this.anchorColumnPosition = cords.column;
        } else if (change.anchorRowPosition != null && change.anchorColumnPosition != null) {
            this.anchorRowPosition = change.anchorRowPosition;
            this.anchorColumnPosition = change.anchorColumnPosition;
            this.anchorOffset = this.toOffset(this.anchorRowPosition, this.anchorColumnPosition);
        }
        
        this.selectedString = (this.cursorOffset == this.anchorOffset) ? "" : null;
    }
}