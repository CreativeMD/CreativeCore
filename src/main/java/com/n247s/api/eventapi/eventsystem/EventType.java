package com.n247s.api.eventapi.eventsystem;

public abstract class EventType {
    private boolean isCancelable = isCancelable();
    private boolean isCanceled = false;
    
    public EventType() {}
    
    public abstract boolean isCancelable();
    
    public final boolean getIsCancelable() {
        return this.isCancelable;
    }
    
    public final void CancelEvent() {
        if (isCancelable)
            this.isCanceled = true;
    }
    
    public final boolean isCanceled() {
        return this.isCanceled;
    }
    
    public final void unCancelEvent() {
        this.isCanceled = false;
    }
}
