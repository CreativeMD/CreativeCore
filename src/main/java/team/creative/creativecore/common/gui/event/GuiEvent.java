package team.creative.creativecore.common.gui.event;

public abstract class GuiEvent {
	
	private boolean canceled = false;
	
	public boolean isCanceled() {
		return canceled;
	}
	
	public void cancel() {
		if (cancelable())
			canceled = false;
		throw new UnsupportedOperationException("Event cannot be canceled " + getClass());
	}
	
	public abstract boolean cancelable();
	
}
