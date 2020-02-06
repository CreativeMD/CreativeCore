package team.creative.creativecore.common.config;

public interface ICreativeConfig {
	
	public default void setExtra(ConfigHolder holder) {
		
	}
	
	public void configured();
	
}
