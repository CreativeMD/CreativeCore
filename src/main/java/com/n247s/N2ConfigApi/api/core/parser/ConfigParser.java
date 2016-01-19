package com.n247s.N2ConfigApi.api.core.parser;

import java.io.File;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;

import com.n247s.N2ConfigApi.api.core.ConfigFile;
import com.n247s.N2ConfigApi.api.core.ConfigSection;
import com.n247s.N2ConfigApi.api.core.ConfigSection.SectionType;
import com.n247s.N2ConfigApi.api.core.ConfigSectionCollection;
import com.n247s.N2ConfigApi.api.core.DefaultConfigFile;
import com.n247s.N2ConfigApi.api.core.InitConfigObjectManager;
import com.n247s.N2ConfigApi.api.core.InitConfigObjectManager.Config;

/**
 * @author N247S
 * An ingame ConfigFile Manager<br>
 * <br>
 * This Class contains all the method regarding the {@link Configuration} parsing.
 */
public class ConfigParser
{
	/**
	 * Parses a {@link Configuration} to a {@link ConfigFile}.<br>
	 * This will return a {@link DefaultConfigFile} which {@link File} location
	 * is pointed at the default ConfigDirectory from Minecraft, with the given
	 * filename.
	 * 
	 * @param configuration
	 *            The {@link Configuration} that should be parsed.
	 * @param newFileName
	 *            The name of the (.cfg) file.
	 * @return A new {@link DefaultConfigFile}.
	 */
	public static ConfigFile parseConfigFile(Configuration configuration, String newFileName)
	{
		ConfigFile configFile = new DefaultConfigFile(newFileName, newFileName);
		
		Config config = InitConfigObjectManager.addConfigFile(newFileName);
		
		for(String currentCategoryName : configuration.getCategoryNames())
		{
			ConfigSectionCollection configSectionCollection = parseConfigCategory(configuration.getCategory(currentCategoryName), config, true, false);
			configFile.addNewSection(configSectionCollection);
		}
		return configFile;
	}
	
	/**
	 * Parses a {@link Configuration} to a {@link ConfigFile}.<br>
	 * This will return The given {@link ConfigFile} with the content of the
	 * {@link Configuration} added to it. This way its possible to parse
	 * multiple {@link Configuration} into one {@link ConfigFile}. Also this
	 * gives you the opportunity to set settings yourself (such as {@link File}
	 * location etc.).
	 * 
	 * @param configuration
	 *            The {@link Configuration} that should be parsed.
	 * @param configFile
	 *            The {@link ConfigFile} where the content {@link Configuration}
	 *            should be added to.
	 * @return The given {@link ConfigFile} with the content of
	 *         {@link Configuration} added to it.
	 */
	public static ConfigFile parseConfigFile(Configuration configuration, ConfigFile configFile)
	{
		Config config = InitConfigObjectManager.addConfigFile(configFile.getFileName());

		for(String currentCategoryName : configuration.getCategoryNames())
		{
			ConfigSectionCollection configSectionCollection = parseConfigCategory(configuration.getCategory(currentCategoryName), config, true, false);
			configFile.addNewSection(configSectionCollection);
		}
		return configFile;
	}
	
	private static ConfigSectionCollection parseConfigCategory(ConfigCategory configCategory, Config config, boolean setExtraSpace, boolean useDefaultValueForConfigValue)
	{
		ConfigSectionCollection sectionCollection = new ConfigSectionCollection(configCategory.getName(), parseComment(configCategory.getComment()), setExtraSpace);
		ConfigSection tempSection;
		for(Property currentProperty : configCategory.getOrderedValues())
		{
			switch (currentProperty.getType()) {
			case BOOLEAN:
				tempSection = parseBooleanProperty(currentProperty, useDefaultValueForConfigValue);
				if (tempSection != null)
				{
					sectionCollection.addNewSection(tempSection);
					if(!useDefaultValueForConfigValue && config != null)
						InitConfigObjectManager.addField(config, currentProperty.getName(), currentProperty.isList() ? currentProperty.getBooleanList() : currentProperty.getBoolean());
				}
				break;
			case INTEGER:
				tempSection = parseIntegerProperty(currentProperty, useDefaultValueForConfigValue);
				if (tempSection != null)
				{
					sectionCollection.addNewSection(tempSection);
					if(!useDefaultValueForConfigValue && config != null)
						InitConfigObjectManager.addField(config, currentProperty.getName(), currentProperty.isList() ? currentProperty.getIntList() : currentProperty.getInt());
				}
				break;
			case DOUBLE:
				tempSection = parseDoubleProperty(currentProperty, useDefaultValueForConfigValue);
				if (tempSection != null)
				{
					sectionCollection.addNewSection(tempSection);
					if(!useDefaultValueForConfigValue && config != null)
						InitConfigObjectManager.addField(config, currentProperty.getName(), currentProperty.isList() ? currentProperty.getDoubleList() : currentProperty.getDouble());
				}
				break;
			case STRING:
				tempSection = parseStringProperty(currentProperty, useDefaultValueForConfigValue);
				if (tempSection != null)
				{
					sectionCollection.addNewSection(tempSection);
					if(!useDefaultValueForConfigValue && config != null)
						InitConfigObjectManager.addField(config, currentProperty.getName(), currentProperty.isList() ? currentProperty.getStringList() : currentProperty.getString());
				}
				break;
			default:
			}
			
		}
		
		for(ConfigCategory currentCategory : configCategory.getChildren())
		{
			ConfigSectionCollection childConfigSectionCollection = parseConfigCategory(currentCategory, useDefaultValueForConfigValue);
			sectionCollection.addNewSection(childConfigSectionCollection);
		}
		
		return sectionCollection;
	}
	
	/**
	 * Parses a {@link ConfigCategory} to a {@link ConfigSectionCollection}
	 * 
	 * @param configCategory
	 * @param setExtraSpace
	 * @param useDefaultValueForConfigValue
	 *            Weather the value or the DefaultValue of the {@link Property
	 *            Properties} should be assigned to the defaultValue of the
	 *            {@link ConfigSection ConfigSections}.
	 * @return
	 */
	public static ConfigSectionCollection parseConfigCategory(ConfigCategory configCategory, boolean setExtraSpace, boolean useDefaultValueForConfigValue)
	{
		return parseConfigCategory(configCategory, null, setExtraSpace, useDefaultValueForConfigValue);
	}
	
	
	/**
	 * Parses a {@link ConfigCategory} to a {@link ConfigSectionCollection}
	 * @param configCategory
	 * @param useDefaultValueForConfigValue
	 *            Weather the value or the DefaultValue of the {@link Property
	 *            Properties} should be assigned to the defaultValue of the
	 *            {@link ConfigSection ConfigSections}.
	 * @return
	 */
	public static ConfigSectionCollection parseConfigCategory(ConfigCategory configCategory, boolean useDefaultValueForConfigValue)
	{
		return parseConfigCategory(configCategory, null, true, useDefaultValueForConfigValue);
	}
	
	/**
	 * Parese a {@link ConfigCategory} with the {@link Type}
	 * {@link Type#BOOLEAN BOOLEAN} to a {@link ConfigSection} with
	 * {@link SectionType} {@link SectionType#Boolean Boolean} or
	 * {@link SectionType#BooleanArray BooleanArray}
	 * 
	 * @param booleanProperty
	 * @return
	 */
	public static ConfigSection parseBooleanProperty(Property booleanProperty, boolean useDefaultValueForConfigValue)
	{
		if(!booleanProperty.getType().equals(Type.BOOLEAN))
			return null;
		
		ConfigSection configSection;
		
		if(booleanProperty.isList())
		{
			String[] defaultStringArray = booleanProperty.getDefaults();
			boolean[] defaultBooleanArray = new boolean[defaultStringArray.length];
			
			for(int i = 0; i< defaultBooleanArray.length; i++)
				defaultBooleanArray[i] = Boolean.parseBoolean(defaultStringArray[i]);
			
			configSection = new ConfigSection(booleanProperty.getName(), parseComment(booleanProperty.comment), useDefaultValueForConfigValue ? booleanProperty.getBooleanList() : defaultBooleanArray, SectionType.BooleanArray, true, false);
		}
		else configSection = new ConfigSection(booleanProperty.getName(), parseComment(booleanProperty.comment), useDefaultValueForConfigValue ? booleanProperty.getBoolean() : Boolean.parseBoolean(booleanProperty.getDefault()), SectionType.Boolean, true, false);
		
		return configSection;
	}
	
	/**
	 * Parese a {@link ConfigCategory} with the {@link Type}
	 * {@link Type#INTEGER INTEGER} to a {@link ConfigSection} with
	 * {@link SectionType} {@link SectionType#Integer Integer} or
	 * {@link SectionType#IntegerArray IntegerArray}
	 * 
	 * @param integerProperty
	 * @param useDefaultValueForConfigValue
	 *            Weather the value or the DefaultValue of the {@link Property
	 *            Properties} should be assigned to the defaultValue of the
	 *            {@link ConfigSection ConfigSections}.
	 * @return
	 */
	public static ConfigSection parseIntegerProperty(Property integerProperty, boolean useDefaultValueForConfigValue)
	{
		if(!integerProperty.getType().equals(Type.INTEGER))
			return null;
		
		ConfigSection configSection;
		
		if(integerProperty.isList())
		{
			String[] defaultStringArray = integerProperty.getDefaults();
			int[] defaultIntArray = new int[defaultStringArray.length];
			
			for(int i = 0; i< defaultIntArray.length; i++)
				defaultIntArray[i] = Integer.parseInt(defaultStringArray[i]);
			
			configSection = new ConfigSection(integerProperty.getName(), parseComment(integerProperty.comment), useDefaultValueForConfigValue ? integerProperty.getIntList() : defaultIntArray, SectionType.IntegerArray, true, false);
		}
		else configSection = new ConfigSection(integerProperty.getName(), parseComment(integerProperty.comment), useDefaultValueForConfigValue ? integerProperty.getInt() : Integer.parseInt(integerProperty.getDefault()), SectionType.Integer, true, false);
		
		return configSection;
	}
	
	/**
	 * Parese a {@link ConfigCategory} with the {@link Type}
	 * {@link Type#DOUBLE DOUBLE} to a {@link ConfigSection} with
	 * {@link SectionType} {@link SectionType#Double Double} or
	 * {@link SectionType#DoubleArray DoubleArray}
	 * 
	 * @param doubleProperty
	 * @param useDefaultValueForConfigValue
	 *            Weather the value or the DefaultValue of the {@link Property
	 *            Properties} should be assigned to the defaultValue of the
	 *            {@link ConfigSection ConfigSections}.
	 * @return
	 */
	public static ConfigSection parseDoubleProperty(Property doubleProperty, boolean useDefaultValueForConfigValue)
	{
		if(!doubleProperty.getType().equals(Type.DOUBLE))
			return null;
		
		ConfigSection configSection;
		
		if(doubleProperty.isList())
		{
			String[] defaultStringArray = doubleProperty.getDefaults();
			double[] defaultDoubleArray = new double[defaultStringArray.length];
			
			for(int i = 0; i< defaultDoubleArray.length; i++)
				defaultDoubleArray[i] = Double.parseDouble(defaultStringArray[i]);
			
			configSection = new ConfigSection(doubleProperty.getName(), parseComment(doubleProperty.comment), useDefaultValueForConfigValue ? doubleProperty.getDoubleList() : defaultDoubleArray, SectionType.DoubleArray, true, false);
		}
		else configSection = new ConfigSection(doubleProperty.getName(), parseComment(doubleProperty.comment), useDefaultValueForConfigValue ? doubleProperty.getDouble() : Double.parseDouble(doubleProperty.getDefault()), SectionType.Double, true, false);
		
		return configSection;
	}
	
	/**
	 * Parese a {@link ConfigCategory} with the {@link Type}
	 * {@link Type#STRING STRING} to a {@link ConfigSection} with
	 * {@link SectionType} {@link SectionType#String String} or
	 * {@link SectionType#StringArray StringArray}
	 * 
	 * @param stringProperty
	 * @param useDefaultValueForConfigValue
	 *            Weather the value or the DefaultValue of the {@link Property
	 *            Properties} should be assigned to the defaultValue of the
	 *            {@link ConfigSection ConfigSections}.
	 * @return
	 */
	public static ConfigSection parseStringProperty(Property stringProperty, boolean useDefaultValueForConfigValue)
	{
		if(!stringProperty.getType().equals(Type.STRING))
			return null;
		
		ConfigSection configSection;
		
		if(stringProperty.isList())
			configSection = new ConfigSection(stringProperty.getName(), parseComment(stringProperty.comment), useDefaultValueForConfigValue ? stringProperty.getStringList() : stringProperty.getDefaults(), SectionType.StringArray, true, false);
		else configSection = new ConfigSection(stringProperty.getName(), parseComment(stringProperty.comment), useDefaultValueForConfigValue ? stringProperty.getString() : stringProperty.getDefault(), SectionType.String, true, false);
		
		return configSection;
	}
	
	public static ConfigSection parseProperty(Property property, boolean useDefaultValueForConfigValue)
	{
		ConfigSection configSection;
		
		if(property.isList())
			configSection = new ConfigSection(property.getName(), parseComment(property.comment), useDefaultValueForConfigValue ? property.getStringList() : property.getDefaults(), SectionType.StringArray, true, false);
		else configSection = new ConfigSection(property.getName(), parseComment(property.comment), useDefaultValueForConfigValue ? property.getString() : property.getDefault(), SectionType.String, true, false);
		
		return configSection;
	}
	
	/**
	 * @deprecated - seems to be unused!
	 * 
	 * @return {@code null}
	 */
	public static ConfigSection parseColourProperty()
	{
		return null;
	}
	
	/**
	 * @deprecated - seems to be unused!
	 * 
	 * @return {@code null}
	 */
	public static ConfigSection parseModIDProperty()
	{
		return null;
	}
	
	private static String[] parseComment(String comment)
	{
		if(comment != null)
			if(comment.contains("\n"))
				return comment.split("\n");
			else return new String[]{comment};
		return null;
	}
}
