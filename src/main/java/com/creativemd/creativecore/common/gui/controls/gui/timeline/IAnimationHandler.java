package com.creativemd.creativecore.common.gui.controls.gui.timeline;

public interface IAnimationHandler {
	
	public void loop(boolean loop);
	
	public void play();
	
	public void pause();
	
	public void stop();
	
	public void set(int tick);
	
	public int get();
	
}
