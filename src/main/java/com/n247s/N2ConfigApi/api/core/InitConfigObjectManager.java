package com.n247s.N2ConfigApi.api.core;

import java.util.HashMap;

import org.apache.logging.log4j.Logger;

import com.n247s.N2ConfigApi.api.N2ConfigApi;

/**
 * @author N247S<br>
 * An ingame ConfigFile Manager<br>
 * <Br>
 * This is a Class to handle data internally. There is no need of using any method or field inside this Class.
 */
public class InitConfigObjectManager
{
	private static final Logger log = N2ConfigApi.log;
	private static final InitConfigObjectManager instance = new InitConfigObjectManager();
	protected static HashMap<String, Config> ConfigList = new HashMap<String, Config>();
	
	/** An subClass representing an ConfigFile, with initial space for data. */
	public class Config
	{
		public HashMap<String, Object> ConfigSections = new HashMap<String, Object>();
		public String name;
		
		public Config(String name)
		{
			this.name = name;
		}
	}
	
	public static Config addConfigFile(String FileName)
	{
		if(ConfigList.containsKey(FileName))
			log.error("The name " + FileName + " is already Used!");
		else
		{
			Config newConfiguration = instance.new Config(FileName);
			ConfigList.put(FileName, newConfiguration);
			return newConfiguration;
		}
		return null;
	}
	
	public static Config getConfigByName(String name)
	{
		if(ConfigList.containsKey(name))
			return ConfigList.get(name);
		else return null;
	}
	
	public static boolean removeConfigFile(String FileName)
	{
		if(!ConfigList.containsKey(FileName))
			log.error("Couldn't find Config " + FileName);
		else
		{
			ConfigList.remove(FileName);
			return true;
		}
		return false;
	}
	
	public static void addField(Config config, String sectionName, Object Value)
	{
		if(config.ConfigSections.containsKey(sectionName))
			log.error("section " + sectionName + " is already initialized!");
		else config.ConfigSections.put(sectionName, Value);
	}
	
	public static Object getField(Config config, String sectionName)
	{
		if(config.ConfigSections.containsKey(sectionName))
			return config.ConfigSections.get(sectionName);
		else return null;
	}
	
	public static boolean changeFieldValue(Config config, String sectionName, Object newValue)
	{
		if(!config.ConfigSections.containsKey(sectionName))
			InitConfigObjectManager.addField(config, sectionName, newValue);
		else
		{
			config.ConfigSections.remove(sectionName);
			config.ConfigSections.put(sectionName, newValue);
			return true;
		}
		return false;
	}
	
	public static boolean removeField(Config config, String sectionName)
	{
		if(config.ConfigSections.containsKey(sectionName))
		{
			config.ConfigSections.remove(sectionName);
			return true;
		}
		else return false;
	}
}