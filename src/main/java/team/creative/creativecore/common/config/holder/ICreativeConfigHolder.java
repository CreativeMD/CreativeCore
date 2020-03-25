package team.creative.creativecore.common.config.holder;

import java.util.Collection;

import com.google.gson.JsonObject;

import net.minecraftforge.api.distmarker.Dist;
import team.creative.creativecore.common.config.sync.ConfigSynchronization;

public interface ICreativeConfigHolder {
	
	public ICreativeConfigHolder parent();
	
	public default String getName() {
		return path()[path().length - 1];
	}
	
	public String[] path();
	
	public Collection<? extends ConfigKey> fields();
	
	public Collection<String> names();
	
	public Object get(String key);
	
	public ConfigKey getField(String key);
	
	public boolean isDefault(Dist side);
	
	public void restoreDefault(Dist side);
	
	public void load(boolean loadDefault, JsonObject json, Dist side);
	
	public JsonObject save(boolean saveDefault, Dist side);
	
	public boolean isEmpty(Dist side);
	
	public boolean isEmptyWithoutForce(Dist side);
	
	public ConfigSynchronization synchronization();
	
}
