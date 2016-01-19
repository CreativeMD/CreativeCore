package com.n247s.N2ConfigApi.api.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;

import scala.SerialVersionUID;

import com.n247s.N2ConfigApi.api.N2ConfigApi;
import com.n247s.N2ConfigApi.api.core.InitConfigObjectManager.Config;

/**
 * @author N247S
 * An ingame ConfigFile Manager
 */

public class ConfigSectionCollection extends ConfigSection implements Serializable
{
	
	private static final long serialVersionUID = -864529735488611577L;
	
	private static final Logger log = N2ConfigApi.log;
	protected HashMap<String, ConfigSection> AbsoluteSubSectionMap = new HashMap<String, ConfigSection>();
	private HashMap<String, ConfigSection> subSectionMap = new HashMap<String, ConfigSection>();
	private List<ConfigSection> subSectionOrderList = new ArrayList<ConfigSection>();
	private ConfigSectionCollection superConfigSectionCollection;
	private boolean ExtraSpaces;
	private boolean changedSections;
	private int level;

	private ConfigSectionCollection(String sectionName, String[] description, boolean setExtraSpaces, int level)
	{
		super(sectionName, description, null, SectionType.SectionHead, true, false);
		this.ExtraSpaces = setExtraSpaces;
		this.level = level;
	}
	
	/**
	 * @param sectionName
	 * @param description
	 * @param setExtraSpaces - Whether there will be extra spaces printed between subSections or not.
	 */
	public ConfigSectionCollection(String sectionName, String[] description, boolean setExtraSpaces)
	{
		this(sectionName, description, setExtraSpaces, 0);
	}
	
	/**
	 * This will create an new ConfigSectionCollection, and add it to this ConfigSectionCollection.
	 * @param sectionName - The ID of this Section
	 * @param description
	 * @param setExtraSpaces  - Whether there will be extra spaces printed between subSections or not.
	 * @return
	 */
	public final ConfigSection addNewConfigSectionCollection(String sectionName, String[] description, boolean setExtraSpaces)
	{
		ConfigSection configSection = new ConfigSectionCollection(sectionName, description, setExtraSpaces, this.level+1);
		((ConfigSectionCollection)configSection).setSuperConfigSectionCollection(this);
		addNewSection(configSection);
		return configSection;
	}
	
	/**
	 * This will create an new ShortSection, and add it to this ConfigSectionCollection.
	 * @param sectionName - The ID of this Section
	 * @param description
	 * @param defaultValue
	 * @param hideOutLines
	 * @return
	 */
	public final ConfigSection addNewShortSection(String sectionName, String[] description, short defaultValue, boolean hideOutLines)
	{
		ConfigSection configSection = new ConfigSection(sectionName, description, defaultValue, SectionType.Short, hideOutLines);
		addNewSection(configSection);
		return configSection;
	}
	
	/**
	 * This will create an new ShortArraySection, and add it to this ConfigSectionCollection.
	 * @param sectionName - The ID of this Section
	 * @param description
	 * @param defaultValue
	 * @param separateLines
	 * @param hideOutLines
	 * @return
	 */
	public final ConfigSection addNewShortArraySection(String sectionName, String[] description, short[] defaultValue,  boolean separateLines, boolean hideOutLines)
	{
		ConfigSection configSection = new ConfigSection(sectionName, description, defaultValue, SectionType.ShortArray, separateLines, hideOutLines);
		addNewSection(configSection);
		return configSection;
	}
	
	/**
	 * This method will create an IntegerSection, and ad it to this ConfigSectionCollection.
	 * @param sectionName - The ID of this Section
	 * @param description
	 * @param defaultValue
	 * @param hideOutLines
	 * @return
	 */
	public final ConfigSection addNewIntegerSection(String sectionName, String[] description, int defaultValue, boolean hideOutLines)
	{
		ConfigSection configSection = new ConfigSection(sectionName, description, defaultValue, ConfigSection.SectionType.Integer, hideOutLines);
		addNewSection(configSection);
		return configSection;
	}
	
	/**
	 * This method will create an IntegerArraySection, and ad it to this ConfigSectionCollection.
	 * @param sectionName - The ID of this Section
	 * @param description
	 * @param defaultValue
	 * @param separateLines
	 * @param hideOutLines
	 * @return
	 */
	public final ConfigSection addNewIntegerArraySection(String sectionName, String[] description, int[] defaultValue, boolean separateLines, boolean hideOutLines)
	{
		ConfigSection configSection = new ConfigSection(sectionName, description, defaultValue, ConfigSection.SectionType.IntegerArray, hideOutLines);
		addNewSection(configSection);
		return configSection;
	}
	
	/**
	 * This method will create an LongSection, and ad it to this ConfigSectionCollection.
	 * @param sectionName - The ID of this Section
	 * @param description
	 * @param defaultValue
	 * @param hideOutLines
	 * @return
	 */
	public final ConfigSection addNewLongSection(String sectionName, String[] description, long defaultValue, boolean hideOutLines)
	{
		ConfigSection configSection = new ConfigSection(sectionName, description, defaultValue, ConfigSection.SectionType.Long, hideOutLines);
		addNewSection(configSection);
		return configSection;
	}
	
	/**
	 * This method will create an LongArraySection, and ad it to this ConfigSectionCollection.
	 * @param sectionName - The ID of this Section
	 * @param description
	 * @param defaultValue
	 * @param separateLines
	 * @param hideOutLines
	 * @return
	 */
	public final ConfigSection addNewLongArraySection(String sectionName, String[] description, long[] defaultValue, boolean separateLines, boolean hideOutLines)
	{
		ConfigSection configSection = new ConfigSection(sectionName, description, defaultValue, ConfigSection.SectionType.LongArray, hideOutLines);
		addNewSection(configSection);
		return configSection;
	}
	
	/**
	 * This method will create an FloatSection, and ad it to this ConfigSectionCollection.
	 * @param sectionName - The ID of this Section
	 * @param description
	 * @param defaultValue
	 * @param hideOutLines
	 * @return
	 */
	public final ConfigSection addNewFloatSection(String sectionName, String[] description, float defaultValue, boolean hideOutLines)
	{
		ConfigSection configSection = new ConfigSection(sectionName, description, defaultValue, ConfigSection.SectionType.Float, hideOutLines);
		addNewSection(configSection);
		return configSection;
	}
	
	/**
	 * This method will create an FloatArraySection, and ad it to this ConfigSectionCollection.
	 * @param sectionName - The ID of this Section
	 * @param description
	 * @param defaultValue
	 * @param separateLines
	 * @param hideOutLines
	 * @return
	 */
	public final ConfigSection addNewFloatArraySection(String sectionName, String[] description, float[] defaultValue, boolean separateLines, boolean hideOutLines)
	{
		ConfigSection configSection = new ConfigSection(sectionName, description, defaultValue, ConfigSection.SectionType.FloatArray, hideOutLines);
		addNewSection(configSection);
		return configSection;
	}
	
	/**
	 * This method will create an DoubleSection, and ad it to this ConfigSectionCollection.
	 * @param sectionName - The ID of this Section
	 * @param description
	 * @param defaultValue
	 * @param hideOutLines
	 * @return
	 */
	public final ConfigSection addNewDoubleSection(String sectionName, String[] description, double defaultValue, boolean hideOutLines)
	{
		ConfigSection configSection = new ConfigSection(sectionName, description, defaultValue, ConfigSection.SectionType.Double, hideOutLines);
		addNewSection(configSection);
		return configSection;
	}
	
	/**
	 * This method will create an DoubleArraySection, and ad it to this ConfigSectionCollection.
	 * @param sectionName - The ID of this Section
	 * @param description
	 * @param defaultValue
	 * @param separateLines
	 * @param hideOutLines
	 * @return
	 */
	public final ConfigSection addNewDoubleArraySection(String sectionName, String[] description, double[] defaultValue, boolean separateLines, boolean hideOutLines)
	{
		ConfigSection configSection = new ConfigSection(sectionName, description, defaultValue, ConfigSection.SectionType.DoubleArray, hideOutLines);
		addNewSection(configSection);
		return configSection;
	}
	
	/**
	 * This method will create an BooleanSection, and ad it to this ConfigSectionCollection.
	 * @param sectionName - The ID of this Section
	 * @param description
	 * @param defaultValue
	 * @param hideOutLines
	 * @return
	 */
	public final ConfigSection addNewBooleanSection(String sectionName, String[] description, boolean defaultValue, boolean hideOutLines)
	{
		ConfigSection configSection = new ConfigSection(sectionName, description, defaultValue, ConfigSection.SectionType.Boolean, hideOutLines);
		addNewSection(configSection);
		return configSection;
	}
	
	/**
	 * This method will create an BooleanArraySection, and ad it to this ConfigSectionCollection.
	 * @param sectionName - The ID of this Section
	 * @param description
	 * @param defaultValue
	 * @param separateLines
	 * @param hideOutLines
	 * @return
	 */
	public final ConfigSection addNewBooleanArraySection(String sectionName, String[] description, boolean[] defaultValue, boolean separateLines, boolean hideOutLines)
	{
		ConfigSection configSection = new ConfigSection(sectionName, description, defaultValue, ConfigSection.SectionType.BooleanArray, hideOutLines);
		addNewSection(configSection);
		return configSection;
	}
	
	/**
	 * This method will create an StringSection, and ad it to this ConfigSectionCollection.
	 * @param sectionName - The ID of this Section
	 * @param description
	 * @param defaultValue
	 * @param hideOutLines
	 * @return
	 */
	public final ConfigSection addNewStringSection(String sectionName, String[] description, String defaultValue, boolean hideOutLines)
	{
		ConfigSection configSection = new ConfigSection(sectionName, description, defaultValue, ConfigSection.SectionType.String, hideOutLines);
		addNewSection(configSection);
		return configSection;
	}
	
	/**
	 * This method will create an StringArraySection, and ad it to this ConfigSectionCollection.
	 * @param sectionName - The ID of this Section
	 * @param description
	 * @param defaultValue
	 * @param separateLines
	 * @param hideOutLines
	 * @return
	 */
	public final ConfigSection addNewStringArraySection(String sectionName, String[] description, String[] defaultValue, boolean separateLines, boolean hideOutLines)
	{
		ConfigSection configSection = new ConfigSection(sectionName, description, defaultValue, ConfigSection.SectionType.StringArray, hideOutLines);
		addNewSection(configSection);
		return configSection;
	}
	
	/**
	 * This method will create an StringSection, and ad it to this ConfigSectionCollection.
	 * @param sectionName - The ID of this Section
	 * @param description - If description is null, a newLine (return) will be written instead of a Text Section
	 * @return
	 */
	public final ConfigSection addNewTextSection(String sectionName, String[] description)
	{
		ConfigSection configSection = new ConfigSection(sectionName, description, ConfigSection.SectionType.Text);
		addNewSection(configSection);
		return configSection;
	}
	
	/**
	 * This will add the ConfigSection to this ConfigSectionCollection.
	 * @param sectionName
	 * @param configSection
	 */
	public final void addNewSection(ConfigSection configSection)
	{
		String sectionName = configSection.getSectionName();
		if(!this.subSectionMap.containsKey(sectionName))
		{
			if(this.superConfigSectionCollection != null)
				if(!this.superConfigSectionCollection.insertConfigSection(configSection, this.superConfigSectionCollection.getConfigSectionAtIndex(this.superConfigSectionCollection.getConfigSectionIndex(this.getSectionName()) + 1)))
					return;
			this.subSectionOrderList.add(this.subSectionOrderList.size(), configSection);
			this.subSectionMap.put(sectionName, configSection);
			this.AbsoluteSubSectionMap.put(sectionName, configSection);
			setChangedSections(true);
		}
		else log.error("Section: " + sectionName + " is already registered!");
	}
	
	/**
	 * This will insert an section into this ConfigSectionCollection.
	 * @param insertConfigSection - The configSection you want to insert.
	 * @param configSectionTarget -  The ConfigSection which the new ConfigSection should be inserted before!
	 * @return True if the insertion was successful, false otherwise.
	 */
	public final boolean insertConfigSection(ConfigSection insertConfigSection, ConfigSection configSectionTarget)
	{
		if(this.AbsoluteSubSectionMap.containsValue(insertConfigSection) || this.AbsoluteSubSectionMap.containsKey(insertConfigSection.getSectionName()))
			return false;
		if(this.superConfigSectionCollection != null)
			return this.superConfigSectionCollection.insertConfigSection(insertConfigSection, configSectionTarget);
		else return superInsertConfigSection(insertConfigSection, configSectionTarget);
	}
	
	/**
	 * This will insert an section into this ConfigSectionCollection.
	 * @param insertConfigSection - The configSection you want to insert.
	 * @param configSectionTarget -  The ConfigSection which the new ConfigSection should be inserted before!
	 * @return True if the insertion was successful, false otherwise.
	 */
	private boolean superInsertConfigSection(ConfigSection insertConfigSection, ConfigSection configSectionTarget)
	{
		if(!this.AbsoluteSubSectionMap.containsValue(insertConfigSection) || this.AbsoluteSubSectionMap.containsKey(insertConfigSection.getSectionName()))
			return false;
		
		int index = 0;
		if(this.subSectionMap.containsValue(configSectionTarget))
		{
			for(int i = 0; i < this.subSectionOrderList.size(); i++)
			{
				ConfigSection configSection = this.subSectionOrderList.get(i);
				if(configSection.equals(configSectionTarget))
				{
					index = i;
					break;
				}
			}
			this.subSectionOrderList.add(index, insertConfigSection);
			this.subSectionMap.put(insertConfigSection.getSectionName(), insertConfigSection);
			this.AbsoluteSubSectionMap.put(insertConfigSection.getSectionName(), insertConfigSection);
			
			if(insertConfigSection instanceof ConfigSectionCollection)
			{
				List<String> configSectionNames = new ArrayList<String>();
				int subSectionCount = ((ConfigSectionCollection) insertConfigSection).getAbsoluteSubSectionCount();
				
				for(int ii = 0; ii < subSectionCount; ii++)
				{
					if(!superInsertConfigSection(((ConfigSectionCollection)insertConfigSection).getConfigSectionAtIndex(ii), configSectionTarget))
					{
						for(int ij = 0; ij < configSectionNames.size(); ij++)
						{
							removeSubConfigSection(configSectionNames.get(ij));
						}
						return false;
					}
					configSectionNames.add(((ConfigSectionCollection)insertConfigSection).getConfigSectionAtIndex(ii).getSectionName());
				}
			}
			setChangedSections(true);
			return true;
		}
		
		for(int ip = 0; ip < this.subSectionOrderList.size(); ip++)
		{
			ConfigSection configSection	= this.subSectionOrderList.get(ip);
			if(configSection instanceof ConfigSectionCollection)
			{
				if(((ConfigSectionCollection)configSection).superInsertConfigSection(insertConfigSection, configSectionTarget))
				{
					this.AbsoluteSubSectionMap.put(insertConfigSection.getSectionName(), insertConfigSection);
					setChangedDefaultValue(true);
					return true;
				}
			}
		}
		log.error("Something Strange happens during adding ConfigSection: " + insertConfigSection.getSectionName());
		log.info("Its recommended to regenerate this ConfigFile");
		return false;
	}
	
	/**
	 * @param configSectionName
	 * @return - true if subSection was successfully removed, false otherwise.
	 */
	public final boolean removeSubConfigSection(String configSectionName)
	{
		if(!this.AbsoluteSubSectionMap.containsKey(configSectionName))
			return false;
		if(this.superConfigSectionCollection != null)
			return (this.superConfigSectionCollection).superRemoveSubConfigSection(configSectionName);
		else return superRemoveSubConfigSection(configSectionName);
	}
		
	/**
	 * @param configSectionName
	 * @return - true if subSection was successfully removed, false otherwise.
	 */
	private boolean superRemoveSubConfigSection(String configSectionName)
	{
		if(this.AbsoluteSubSectionMap.containsKey(configSectionName))
		{
			if(this.subSectionOrderList.contains(getConfigSectionAtIndex(getConfigSectionIndex(configSectionName))))
			{
				this.subSectionOrderList.remove(getConfigSectionAtIndex(getConfigSectionIndex(configSectionName)));
				this.subSectionMap.remove(configSectionName);
				this.AbsoluteSubSectionMap.remove(configSectionName);
				setChangedSections(true);
				return true;
			}
			else
			{
				for(int i = 0; i < this.subSectionMap.size(); i++)
				{
					if(this.subSectionOrderList.get(i) instanceof ConfigSectionCollection)
						if(((ConfigSectionCollection) this.subSectionOrderList.get(i)).superRemoveSubConfigSection(configSectionName))
						{
							this.AbsoluteSubSectionMap.remove(configSectionName);
							setChangedDefaultValue(true);
							return true;
						}
				}
				log.error("Something went horibly wrong, Did you touch the 'AbsoluteSubSectionMap'?");
				return false;
			}
		}
		else return false;
	}
	
	/**
	 * This will remove all ConfigSections which are stored in THIS ConfigSectionCollection.
	 */
	public final void clearAllConfigSections()
	{
		this.AbsoluteSubSectionMap = new HashMap<String, ConfigSection>();
		this.subSectionMap = new HashMap<String, ConfigSection>();
		this.subSectionOrderList = new ArrayList<ConfigSection>();
		setChangedSections(true);
	}
	
	/** Weather this ConfigSectionCollection should have extra lines between ConfigSections */
	public void setExtraSpaces(boolean extraSpaces)
	{
		this.ExtraSpaces = extraSpaces;
	}
	
	/**
	 * @param sectionName
	 * @return - A ConfigSection inside this ConfigSectionCollection or 'nested' ConfigSectionCollections.
	 */
	public final ConfigSection getSubSection(String sectionName)
	{
		if(this.subSectionMap.containsKey(sectionName))
			return this.subSectionMap.get(sectionName);
		else
		{
			Iterator<String> CurrentSectionName = this.subSectionMap.keySet().iterator();
			
			while(CurrentSectionName.hasNext())
			{
				ConfigSection subSection = null;
				ConfigSection section = this.subSectionMap.get(CurrentSectionName.next());
				if(section instanceof ConfigSectionCollection)
					if((subSection = ((ConfigSectionCollection) section).getSubSection(sectionName)) != null)
						return subSection;
			}
		}
		return null;
	}
	
	/**
	 * @return - The number of ConfigSection which are stored in this ConfigSectionCollection.
	 */
	public final int getSubSectionCount()
	{
		return this.subSectionMap.size();
	}
	
	/**
	 * @return - The Number of ConfigSections which are stored in this ConfigSectionCollection, including the 'nested' ConfigSections.
	 */
	public final int getAbsoluteSubSectionCount()
	{
		int finalAmount = 0;
		finalAmount += this.getSubSectionCount();
		
		Iterator<String> currentSectionName = this.subSectionMap.keySet().iterator();
		
		while(currentSectionName.hasNext())
		{
			if(this.subSectionMap.get(currentSectionName.next()) instanceof ConfigSectionCollection)
				finalAmount += ((ConfigSectionCollection)this.subSectionMap.get(currentSectionName.next())).getAbsoluteSubSectionCount();
		}		
		return finalAmount + 1;		
	}
	
	/**
	 * @param index
	 * @return - The configSection inside this ConfigSectionCollection at index (including 'nested' ConfigSections).
	 * 	if index is 0 or lower it will return this ConfigSectionCollection.
	 */
	public final ConfigSection getConfigSectionAtAbsoluteIndex(int index)
	{
		int corrector = 0;
		ConfigSection finalsection = null;
		
		if(index > this.subSectionOrderList.size())
			return null;
		if(index <= 0)
			return this;
		
		for(int i = 0; i <= (index - 1 - corrector); i++)
		{
			ConfigSection section = this.subSectionOrderList.get(i + corrector);
			if(section instanceof ConfigSectionCollection)
			{
				for(int il = 0; il <= (index - i); il++)
				{
					finalsection = ((ConfigSectionCollection) section).getConfigSectionAtIndex(il - 1);
					if(finalsection == null)
						corrector += ((ConfigSectionCollection) section).getSubSectionCount();
				}
			}
			else
			{
				finalsection = this.subSectionOrderList.get(i + corrector);
			}
		}
		return finalsection;
	}
	
	/**
	 * @param index
	 * @return The ConfigSection inside this ConfigSectionCollection at index (excluding 'nested' ConfigSections).
	 * 	if index is 0 or lower it will return this ConfigSectionCollection.
	 */
	public final ConfigSection getConfigSectionAtIndex(int index)
	{
		ConfigSection configSection = this.subSectionOrderList.get(index - 1);
		if(index > 0)
			return configSection;
		else return this;
	}
	
	/**
	 * 
	 * @param ConfigSectionName
	 * @return The index of this ConfigSectionCollection, or -1 if it can't be found. (including 'nested' ConfigSections)
	 */
	public final int getAbsoluteConfigSectionIndex(String ConfigSectionName)
	{
		ConfigSection target = this.AbsoluteSubSectionMap.get(ConfigSectionName);
		int corrector = 0;
		ConfigSection finalsection = null;
		
		if(target != null)
		{
			
			for(int i = 0; i < this.subSectionOrderList.size(); i++)
			{
				ConfigSection section = this.subSectionOrderList.get(i);
				if(section.equals(target))
					return i + 1;
				else if(section instanceof ConfigSectionCollection)
					corrector += ((ConfigSectionCollection)section).getAbsoluteConfigSectionIndex(ConfigSectionName);
			}
			return corrector;
		}
		return -1;
	}
	
	/**
	 * @param ConfigSectionName
	 * @return The index of this ConfigSectionCollection, or -1 if it can't be found. (excluding 'nested' ConfigSections)
	 */
	public final int getConfigSectionIndex(String ConfigSectionName)
	{
		ConfigSection target = this.AbsoluteSubSectionMap.get(ConfigSectionName);
		if(target != null)
		{
			for(int i = 0; i < this.subSectionOrderList.size(); i++)
			{
				if(this.subSectionOrderList.get(i).equals(target))
					return i+1;
			}
		}
		return -1;
	}
	
	protected final void setChangedSections(boolean changedSections)
	{
		this.changedSections = changedSections;
		if(this.superConfigSectionCollection != null)
			this.superConfigSectionCollection.setChangedSections(changedSections);
	}
	
	private void setSuperConfigSectionCollection(ConfigSectionCollection configSectionCollection)
	{
		this.superConfigSectionCollection = configSectionCollection;
		this.level = this.superConfigSectionCollection.getLevel();
	}
	
	private int getLevel()
	{
		return this.level;
	}
	
	public final boolean haveSectionChanged()
	{
		return this.changedSections;
	}
	
	/**
	 * @return - The ConfigSectionCollection where this ConfigSectionCollection is 'nested' in.
	 * 	If non exist, it will return null.
	 */
	public final ConfigSectionCollection getSuperConfigSectionChapter()
	{
		return this.superConfigSectionCollection;
	}
	
	/**
	 * This will regenerate this ConfigSectionChapter.
	 * @param writer
	 * @param reader
	 * @param invalidSections - Note, SectionType Text will always be reWritten.
	 * @return - The number of ConfigSections it was able to recover.
	 * @throws IOException
	 */
	public final int regenConfigChapter(BufferedWriter writer, BufferedReader reader, List invalidSections) throws IOException
	{
		ConfigSectionCollection superSectionCollection = this.superConfigSectionCollection;
		while(superSectionCollection.superConfigSectionCollection != null)
			superSectionCollection = superSectionCollection.superConfigSectionCollection;
		if(superSectionCollection instanceof ConfigFile)
			if(((ConfigFile) superSectionCollection).isConnected())
				return -1;
		
		String readedLine = reader.readLine();
		int sectionCount = 0;
		int totalSectionsChecked = 0;
		HashMap<String, String[]> sectionValues = new HashMap<String, String[]>();
		while(readedLine != null)
		{
			if(readedLine.isEmpty() || readedLine.trim().startsWith("#"))
			{
				readedLine = reader.readLine();
				continue;
			}
			if(readedLine.contains(";"))
			{
				for(int i = 0; i < this.subSectionOrderList.size(); i++)
				{
					ConfigSection section = this.subSectionOrderList.get(i);
					
					try
					{
						if(section.getSectionName().equals(readedLine.trim().substring(0, readedLine.trim().indexOf(";"))))
						{
							if(section.getSectionType() == SectionType.Text || section.getSectionType() == SectionType.SectionHead)
								continue;
							else
							{
								if(!invalidSections.contains(section.getSectionName()))
								{
									List<String> value = new ArrayList<String>();
									if(section.getSectionType().isArray())
									{
										StringBuilder SB1 =  new StringBuilder();
										Pattern p1 = Pattern.compile(";\\{");
										String[] s1 = p1.split(readedLine);
										for(String s2: s1)
										{
											if(s2.length() > 0 && !s2.contains(section.getSectionName()))
											{
												while(readedLine != null)
												{
													if(readedLine.contains(";{") && readedLine.contains("};"))
													{
														String s3 = readedLine.substring(readedLine.indexOf("{") + 1, readedLine.indexOf("}"));
														SB1.append(s3);
														break;
													}
													else if(readedLine.contains("};"))
													{
														String s3 = readedLine.substring(0, readedLine.indexOf("}"));
														SB1.append(s3);
														break;
													}
													else
													{
														SB1.append(readedLine);
													}
													readedLine = reader.readLine();
												}
											}
										}
										Pattern p2 = Pattern.compile(",");
										String[] s4 = p2.split(SB1.toString());
										for(String s5: s4)
										{
											if(s5.length() > 0)
												value.add(value.size(), s5);
										}
									}
									else
									{
										Pattern p1 = Pattern.compile(";");
										String[] s1 = p1.split(readedLine);
										for(String s2: s1)
										{
											if(s2.length() > 0 && !s2.contains(section.getSectionName()))
											{
												value.add(value.size(), s2);
											}
										}
									}
									sectionValues.put(section.getSectionName(), (String[]) value.toArray());
								}
							}
						}
					}
					catch(Exception e){}
				}
			}
			readedLine = reader.readLine();
		}
		
		if(this.getStarter()!= null && !this.getHideOutLines())
		{
			appendLineWithOffset(writer, this.getStarter(), this.level);
			writer.newLine();
		}
		
		if(this.getDescription() != null)
		{
			for(int ik = 0; ik < this.getDescription().length; ik++)
			{
				appendLineWithOffset(writer, (" # " + this.getDescription()[ik]), this.level);
				writer.newLine();
			}
			
			if(this.getHeadEnder() != null && ! this.getHideOutLines())
			{
				appendLineWithOffset(writer, this.getHeadEnder(), this.level);
				writer.newLine();
			}
		}
		
		writer.newLine();
		writer.flush();
	
		for(int il = 0; il < this.subSectionOrderList.size(); il++)
		{
			ConfigSection currentSection = this.subSectionOrderList.get(il);
			
			if(currentSection instanceof ConfigSectionCollection)
				totalSectionsChecked += ((ConfigSectionCollection) currentSection).regenConfigChapter(writer, reader, invalidSections) + 1;
			else
			{
				if(sectionValues.containsKey(currentSection.getSectionName()))
				{

					if(currentSection.getStarter() != null && !currentSection.getHideOutLines())
					{
						appendLineWithOffset(writer, currentSection.getStarter(), this.level + 1);
						writer.newLine();	
					}
					
					if(currentSection.getDescription() != null)
					{
						for(int ip = 0; ip < currentSection.getDescription().length; ip++)
						{
							appendLineWithOffset(writer, " # " + currentSection.getDescription()[ip], this.level + 1);
							writer.newLine();
						}
						
						if(currentSection.getHeadEnder() != null && !currentSection.getHideOutLines())
						{
							appendLineWithOffset(writer, currentSection.getHeadEnder(), this.level + 1);
							writer.newLine();
						}
					}
					
					writer.flush();
					
						String[] values = sectionValues.get(currentSection.getSectionName());
						if(currentSection.getSectionType().isArray())
						{
							appendLineWithOffset(writer, currentSection.getSectionName() + ";{ ", this.level + 1);
							for(int iu = 0; iu < values.length; iu++)
							{
								if(currentSection.getSeparteLines())
									writer.newLine();
								appendLineWithOffset(writer, values[iu] + ", ", this.level + 1);
							}
							if(currentSection.getSeparteLines())
								writer.newLine();
							appendLineWithOffset(writer, "};", this.level + 1);
						}
						else
						{
							appendLineWithOffset(writer, currentSection.getSectionName() + "; " + values[0], this.level + 1);
						}
						writer.newLine();
					
					if(currentSection.getEnder() != null && !currentSection.getHideOutLines())
					{
						appendLineWithOffset(writer, currentSection.getEnder(), this.level + 1);
						writer.newLine();
					}
					sectionCount++;
				}
				else
				{
					this.writeSubSection(writer, currentSection);
					sectionCount++;
				}
			}
			if(this.ExtraSpaces)
				writer.newLine();
		}
		
		if(!this.ExtraSpaces)
			writer.newLine();
		
		if(this.getEnder() != null && !this.getHideOutLines())
		{
			appendLineWithOffset(writer, this.getEnder(), this.level);
			writer.newLine();
		}
		return totalSectionsChecked + sectionCount;
	}
	
	private void appendLineWithOffset(BufferedWriter writer, String line, int offset) throws IOException
	{
		for(int i = 0; i < offset; i++)
		{
			writer.append("  ");
		}
		writer.append(line);
	}
	
	public final void writeAllSections(BufferedWriter writer) throws IOException
	{
		ConfigSectionCollection superSectionCollection = this;
		while(superSectionCollection.superConfigSectionCollection != null)
			superSectionCollection = superSectionCollection.superConfigSectionCollection;
		if(superSectionCollection instanceof ConfigFile)
			if(((ConfigFile) superSectionCollection).isConnected())
				return;
		
		writeSectionChapter(writer, this);
		writer.flush();
	}
	
	public final void writeSubSection(BufferedWriter writer, String sectionName) throws IOException
	{
		ConfigSectionCollection superSectionCollection = this;
		while(superSectionCollection.superConfigSectionCollection != null)
			superSectionCollection = superSectionCollection.superConfigSectionCollection;
		if(superSectionCollection instanceof ConfigFile)
			if(((ConfigFile) superSectionCollection).isConnected())
				return;
		
		ConfigSection section = subSectionMap.get(sectionName);
		if(section instanceof ConfigSectionCollection)
			((ConfigSectionCollection)section).writeSectionChapter(writer, (ConfigSectionCollection) section);
		else writeSubSection(writer, section);
		writer.flush();
	}
	
	private void writeSectionChapter(BufferedWriter writer, ConfigSectionCollection section) throws IOException
	{
		if(!section.getHideOutLines())
		{
			if(this.getStarter() != null)
			{
				appendLineWithOffset(writer, section.getStarter(), this.level);
				writer.newLine();
			}
			
			if(section.getDescription() != null)
			{
				for(int i = 0; i < section.getDescription().length; i++)
				{
					appendLineWithOffset(writer, " # " + section.getDescription()[i], this.level);
					writer.newLine();
				}
				if(this.getHeadEnder() != null)
				{
					appendLineWithOffset(writer, section.getHeadEnder(), this.level);
					writer.newLine();
				}
			}
			writer.newLine();
		}
		
		for (int i = 0; i < this.subSectionOrderList.size(); i++)
		{
			ConfigSection subSection = this.subSectionOrderList.get(i);
			if (subSection != null)
				if (subSection instanceof ConfigSectionCollection)
					((ConfigSectionCollection) subSection).writeAllSections(writer);
				else writeSubSection(writer, subSection);
			
			if(section.ExtraSpaces)
				writer.newLine();
		}
		
		writer.newLine();
		if(this.getEnder() != null && !section.getHideOutLines())
			appendLineWithOffset(writer, section.getEnder(), this.level);
		writer.flush();
	}
	
	private void writeSubSection(BufferedWriter writer, ConfigSection section) throws IOException
	{
		SectionType sectionType = section.getSectionType();
		
		if(sectionType == SectionType.Text && section.getDescription() == null)
		{
			writer.newLine();
		}
		else if(!section.getHideOutLines())
		{
			if(this.getStarter() != null)
			{
				appendLineWithOffset(writer, section.getStarter(), (1+this.level));
				writer.newLine();
			}
			
			if(section.getDescription() != null)
			{
				for(int i = 0; i < section.getDescription().length; i++)
				{
					appendLineWithOffset(writer, " # " + section.getDescription()[i], (1+this.level));
					writer.newLine();
				}
				if(sectionType != SectionType.Text && this.getHeadEnder() != null)
				{
					appendLineWithOffset(writer, section.getHeadEnder(), (1+this.level));
					writer.newLine();
				}
				else if(this.getEnder() != null)
				{
					appendLineWithOffset(writer, section.getEnder(), (1+this.level));
					writer.newLine();
				}
			}
			writer.newLine();
			writer.flush();
		}
		
		if(sectionType != SectionType.Text)
		{
			if(sectionType.isArray())
			{
				if(section.getDefaultValue() != null)
				{
					appendLineWithOffset(writer, section.getSectionName() + ";{ " , (1+this.level));
					if(section.getSeparteLines())
						writer.newLine();
					
					switch (sectionType)
					{
					case ShortArray:
						short[] shortValues = (short[]) section.getDefaultValue();
						for (int i = 0; i < shortValues.length; i++)
						{
							if(!section.getSeparteLines())
								writer.append(Short.toString(shortValues[i]) + ", " );
							else
							{
								appendLineWithOffset(writer, Short.toString(shortValues[i]), (1+this.level));
								writer.newLine();
							}
							writer.flush();
						}
						break;
					case IntegerArray:
						int[] intValues = (int[]) section.getDefaultValue();
						for (int i = 0; i < intValues.length; i++)
						{
							if(!section.getSeparteLines())
								writer.append(Integer.toString(intValues[i]) + ", ");
							else
							{
								appendLineWithOffset(writer, Integer.toString(intValues[i]), (1+this.level));
								writer.newLine();
							}
							writer.flush();
						}
						break;
					case LongArray:
						long[] longValues = (long[]) section.getDefaultValue();
						for (int i = 0; i < longValues.length; i++)
						{
							if(!section.getSeparteLines())
								writer.append(Long.toString(longValues[i]) + ", ");
							else
							{
								appendLineWithOffset(writer, Long.toString(longValues[i]), (1+this.level));
								writer.newLine();
							}
							writer.flush();
						}
						break;
					case FloatArray:
						float[] floatValues = (float[]) section.getDefaultValue();
						for (int i = 0; i < floatValues.length; i++)
						{
							if(!section.getSeparteLines())
								writer.append(Float.toString(floatValues[i]) + ", ");
							else
							{
								appendLineWithOffset(writer, Float.toString(floatValues[i]), (1+this.level));
								writer.newLine();
							}
							writer.flush();
						}
						break;
					case DoubleArray:
						double[] doubleValues = (double[]) section.getDefaultValue();
						for (int i = 0; i < doubleValues.length; i++)
						{
							if(!section.getSeparteLines())
								writer.append(Double.toString(doubleValues[i]) + ", ");
							else
							{
								appendLineWithOffset(writer, Double.toString(doubleValues[i]), (1+this.level));
								writer.newLine();
							}
							writer.flush();
						}
						break;
					case BooleanArray:
						boolean[] booleanValues = (boolean[]) section.getDefaultValue();
						for (int i = 0; i < booleanValues.length; i++)
						{
							if(!section.getSeparteLines())
								writer.append(Boolean.toString(booleanValues[i]) + ", ");
							else
							{
								appendLineWithOffset(writer, Boolean.toString(booleanValues[i]), (1+this.level));
								writer.newLine();
							}
							writer.flush();
						}
						break;
					case StringArray:
						String[] values = (String[]) section.getDefaultValue();
						for (int i = 0; i < values.length; i++)
						{
							if(!section.getSeparteLines())
								writer.append(values[i] + ", ");
							else
							{
								appendLineWithOffset(writer, values[i], (1+this.level));
								writer.newLine();
							}
							writer.flush();
						}
						break;
					default:
						log.error("Did something go wrong? Tried writing an Array while input wasn't an ArrayType");
						break;
					}
					if(section.getSeparteLines())
						appendLineWithOffset(writer, "};", (1+this.level));
					else writer.append("};");
					
					writer.newLine();
					writer.flush();
				}
				else
				{
					appendLineWithOffset(writer, section.getSectionName() + ";{ ", (1+this.level));
					if(section.getSeparteLines())
					{
						writer.newLine();
						appendLineWithOffset(writer, "};", this.level + 1);
					}
					else writer.append("};");
				}
				writer.newLine();
				writer.flush();
			}
			else
			{
				appendLineWithOffset(writer, section.getSectionName() + "; ", (1+this.level));
				if(section.getDefaultValue() != null)
				{
					switch(sectionType)
					{
					case Short:
						writer.append(Short.toString((Short)section.getDefaultValue()));
						break;
					case Integer:
						writer.append(Integer.toString((Integer)section.getDefaultValue()));
						break;
					case Long:
						writer.append(Long.toString((Long)section.getDefaultValue()));
						break;
					case Float:
						writer.append(Float.toString((Float)section.getDefaultValue()));
						break;
					case Double:
						writer.append(Double.toString((Double)section.getDefaultValue()));
						break;
					case Boolean:
						writer.append(Boolean.toString((Boolean)section.getDefaultValue()));
						break;
					case String:
						writer.append((String)section.getDefaultValue());
						break;
					default:
						log.error("Did something go wrong? tried wrinting an primitive dataType while input wasn't an primitive dataType");
						break;
					}
					writer.newLine();
					writer.flush();
				}
				writer.newLine();
				writer.flush();
			}
			if(this.getEnder() != null && !section.getHideOutLines())
			{
				appendLineWithOffset(writer, section.getEnder(), (1+this.level));
				writer.newLine();
			}
		}
		writer.flush();
	}
	
	/**
	 * This will save the Value's inside a ConfigInitObject. This is also called with the saveConfigValues() method from the ConfigFile.Class.
	 * @param fileName
	 */
	protected final boolean readAndSaveValues(String fileName) throws IOException
	{
		ConfigSectionCollection superSectionCollection = this.superConfigSectionCollection;
		if(superSectionCollection != null)
			while(superSectionCollection.superConfigSectionCollection != null)
				superSectionCollection = superSectionCollection.superConfigSectionCollection;
		else superSectionCollection = this;
		if(superSectionCollection instanceof ConfigFile)
			if(((ConfigFile) superSectionCollection).isConnected())
				return false;
		
		if(this.superConfigSectionCollection != null)
		{
			log.error("Can't save Value's if ConfigSectionCollection is not an ConfigFile!");
			return false;
		}
		boolean hasChangedConfigSections = false;
		if(!InitConfigObjectManager.ConfigList.containsKey(fileName))
			InitConfigObjectManager.addConfigFile(fileName);
		Config config = InitConfigObjectManager.getConfigByName(fileName);
		
		ConfigFile configFile =  ConfigHandler.getConfigFileFromName(fileName);
		boolean exist = ConfigHandler.getFileFromConfigFile(configFile).exists();
		
		Iterator section = this.AbsoluteSubSectionMap.keySet().iterator();
		while(section.hasNext())
		{
			ConfigSection currentSection = this.AbsoluteSubSectionMap.get(section.next());
			if(exist)
			{
				BufferedReader reader = ConfigHandler.getConfigFileFromName(fileName).getNewReader();
				InitConfigObjectManager.addField(config, currentSection.getSectionName(), getValue(currentSection.getSectionName(), reader));
				ConfigHandler.getConfigFileFromName(fileName).closeReader(reader);
			}
			else InitConfigObjectManager.addField(config, currentSection.getSectionName(), getValue(currentSection.getSectionName(), null));
			if(currentSection.getChangedDefaultValue())
				hasChangedConfigSections = true;
		}
		return hasChangedConfigSections;
	}
	
	/**
	 * Note, it isn't recommended to Call this if you access this method through the ConfigFile.Class!
	 * Use "getValue(String)" instead.
	 * @param sectionName
	 * @param reader
	 * @return - The value from the ConfigFile, or the Default Value if an error occurred.
	 * @throws IOException
	 */
	private final Object getValue(String sectionName, BufferedReader reader) throws IOException
	{
		String readedLine;
		ConfigSection configSection = this.getSubSection(sectionName);
		SectionType sectiontype = configSection.getSectionType();
		StringBuilder finalString = new StringBuilder();
		
		if(reader == null)
			return configSection.getDefaultValue();
		
		getValueLoop:
		while((readedLine = reader.readLine()) != null)
		{
			if(readedLine.contains(";") ? readedLine.trim().substring(0, readedLine.trim().indexOf(";")).equals(sectionName) : false)
			{
				
				Pattern p1;
				if(sectiontype.isArray())
					p1 = Pattern.compile(";\\{");
				else p1 = Pattern.compile(";");
				
				String[] value = p1.split(readedLine);
				for(String s1 : value)
				{
					if(s1.length() > 0 && !s1.trim().startsWith(sectionName))
					{
						StringBuilder SB1 = new StringBuilder();
						boolean hasEnder = false;
						Pattern p2 = Pattern.compile("\\s");
						String[] s2 = p2.split(s1);
						for(String s3 : s2)
						{
							if(sectiontype.isArray())
							{
								if(s3.length() > 2 && s3.endsWith("};"))
								{
									SB1.append(s3.substring(0, s3.length()-2));
									hasEnder = true;
								}
								else if (s3.endsWith("};"))
									hasEnder = true;
								else SB1.append(s3);

							}
							else SB1.append(s3);
							String values = SB1.toString();
							
							if(sectiontype.isArray() && !hasEnder)
							{
								StringBuilder SB2 = new StringBuilder();
								
								addArrayValues:
								while((readedLine = reader.readLine()) != null)
								{
									Pattern p3 = Pattern.compile("\\s");
									String[] s5 = p3.split(readedLine);
									for(String s6 : s5)
									{
										if(s6.length() > 0)
										{
											if(s6.endsWith("};"))
											{
												SB2.append(s6.substring(0, readedLine.length()-2));
												break addArrayValues;
											}
											else
											{
												SB2.append(s6);
											}
										}
									}
								}
								values = SB2.toString();
							}
							if (values.length() > 0)
							{
								switch (sectiontype)
								{
								case Short:
									short finalShortValue;
									if (configSection.getDefaultValue() != null)
										finalShortValue = (Short) configSection.getDefaultValue();
									else
										finalShortValue = -1;

									try
									{
										finalShortValue = Short.parseShort(values);
									}
									catch (Exception e)
									{
										log.catching(e);
									}
									return finalShortValue;

								case Integer:
									int finalIntegerValue;
									if (configSection.getDefaultValue() != null)
										finalIntegerValue = (Integer) configSection.getDefaultValue();
									else
										finalIntegerValue = -1;

									try
									{
										finalIntegerValue = Integer.parseInt(values);
									}
									catch (Exception e)
									{
										log.catching(e);
									}
									return finalIntegerValue;

								case Long:
									long finalLongValue;
									if (configSection.getDefaultValue() != null)
										finalLongValue = (Long) configSection.getDefaultValue();
									else
										finalLongValue = -1;

									try
									{
										finalLongValue = Long.parseLong(values);
									}
									catch (Exception e)
									{
										log.catching(e);
									}
									return finalLongValue;

								case Float:
									float finalFloatValue;
									if (configSection.getDefaultValue() != null)
										finalFloatValue = (Float) configSection.getDefaultValue();
									else
										finalFloatValue = -1;

									try
									{
										finalFloatValue = Float.parseFloat(values);
									}
									catch (Exception e)
									{
										log.catching(e);
									}
									return finalFloatValue;

								case Double:
									double finalDoubleValue;
									if (configSection.getDefaultValue() != null)
										finalDoubleValue = (Double) configSection.getDefaultValue();
									else
										finalDoubleValue = -1;

									try
									{
										finalDoubleValue = Double.parseDouble(values);
									}
									catch (Exception e)
									{
										log.catching(e);
									}
									return finalDoubleValue;

								case Boolean:
									boolean finalBooleanValue;
									if (configSection.getDefaultValue() != null)
										finalBooleanValue = (Boolean) configSection.getDefaultValue();
									else
										finalBooleanValue = false;

									try
									{
										finalBooleanValue = Boolean.parseBoolean(values);
									}
									catch (Exception e)
									{
										log.catching(e);
									}
									return finalBooleanValue;

								case String:
									String finalStringValue;
									if (configSection.getDefaultValue() != null)
										finalStringValue = (String) configSection.getDefaultValue();
									else
										finalStringValue = null;

									try
									{
										finalStringValue = values;
									}
									catch (Exception e)
									{
										log.catching(e);
									}
									return finalStringValue;

								case ShortArray:
									HashMap<Integer, Short> finalShortArrayValueMap = new HashMap<Integer, Short>();
									Pattern ShortP1 = Pattern.compile(",");
									String[] shortS1 = ShortP1.split(values);
									for (String shortS2 : shortS1)
									{
										if (shortS2.length() > 0)
										{
											try
											{
												finalShortArrayValueMap.put(finalShortArrayValueMap.size(), Short.parseShort(shortS2));
											}
											catch (Exception e)
											{
												log.catching(e);
											}
										}
										
										if (finalShortArrayValueMap.isEmpty())
											return null;
										else
										{
											short[] finalShortArrayValue = new short[finalShortArrayValueMap.size()];

											for (int i = 0; i < finalShortArrayValue.length; i++)
											{
												finalShortArrayValue[i] = finalShortArrayValueMap.get(i);
											}
											return finalShortArrayValue;
										}
									}

								case IntegerArray:
									HashMap<Integer, Integer> finalIntArrayValueMap = new HashMap<Integer, Integer>();
									Pattern intP1 = Pattern.compile(",");
									String[] intS1 = intP1.split(values);
									for (String intS2 : intS1)
									{
										if (intS2.length() > 0)
										{
											try
											{
												finalIntArrayValueMap
														.put(finalIntArrayValueMap
																.size(),
																Integer.parseInt(intS2));
											}
											catch (Exception e)
											{
												log.catching(e);
											}
										}
										
										if (finalIntArrayValueMap.isEmpty())
											return null;
										else
										{
											int[] finalIntArrayValue = new int[finalIntArrayValueMap.size()];

											for (int i = 0; i < finalIntArrayValue.length; i++)
											{
												finalIntArrayValue[i] = finalIntArrayValueMap.get(i);
											}
											return finalIntArrayValue;
										}
									}

								case LongArray:
									HashMap<Integer, Long> finalLongArrayValueMap = new HashMap<Integer, Long>();
									Pattern longP1 = Pattern.compile(",");
									String[] longS1 = longP1.split(values);
									for (String longS2 : longS1)
									{
										if (longS2.length() > 0)
										{
											try
											{
												finalLongArrayValueMap.put(finalLongArrayValueMap.size(), Long.parseLong(longS2));
											}
											catch (Exception e)
											{
												log.catching(e);
											}
										}
										
										if (finalLongArrayValueMap.isEmpty())
											return null;
										else
										{
											long[] finalLongArrayValue = new long[finalLongArrayValueMap.size()];

											for (int i = 0; i < finalLongArrayValue.length; i++)
											{
												finalLongArrayValue[i] = finalLongArrayValueMap.get(i);
											}
											return finalLongArrayValue;
										}
									}

								case FloatArray:
									HashMap<Integer, Float> finalFloatArrayValueMap = new HashMap<Integer, Float>();
									Pattern floatP1 = Pattern.compile(",");
									String[] floatS1 = floatP1.split(values);
									for (String floatS2 : floatS1)
									{
										if (floatS2.length() > 0)
										{
											try
											{
												finalFloatArrayValueMap.put(finalFloatArrayValueMap.size(), Float.parseFloat(floatS2));
											}
											catch (Exception e)
											{
												log.catching(e);
											}
										}
										
										if (finalFloatArrayValueMap.isEmpty())
											return null;
										else
										{
											float[] finalFloatArrayValue = new float[finalFloatArrayValueMap.size()];

											for (int i = 0; i < finalFloatArrayValue.length; i++)
											{
												finalFloatArrayValue[i] = finalFloatArrayValueMap.get(i);
											}
											return finalFloatArrayValue;
										}
									}

								case DoubleArray:
									HashMap<Integer, Double> finalDoubleArrayValueMap = new HashMap<Integer, Double>();
									Pattern doubleP1 = Pattern.compile(",");
									String[] doubleS1 = doubleP1.split(values);
									for (String doubleS2 : doubleS1)
									{
										if (doubleS2.length() > 0)
										{
											try
											{
												finalDoubleArrayValueMap.put(finalDoubleArrayValueMap.size(), Double.parseDouble(doubleS2));
											}
											catch (Exception e)
											{
												log.catching(e);
											}
										}
										
										if (finalDoubleArrayValueMap.isEmpty())
											return null;
										else
										{
											double[] finalDoubleArrayValue = new double[finalDoubleArrayValueMap.size()];

											for (int i = 0; i < finalDoubleArrayValue.length; i++) 
											{
												finalDoubleArrayValue[i] = finalDoubleArrayValueMap.get(i);
											}
											return finalDoubleArrayValue;
										}
									}

								case BooleanArray:
									HashMap<Integer, Boolean> finalBooleanArrayValueMap = new HashMap<Integer, Boolean>();
									Pattern booleanP1 = Pattern.compile(",");
									String[] booleanS1 = booleanP1.split(values);
									for (String booleanS2 : booleanS1)
									{
										if (booleanS2.length() > 0)
										{
											try
											{
												finalBooleanArrayValueMap.put(finalBooleanArrayValueMap.size(), Boolean.parseBoolean(booleanS2));
											}
											catch (Exception e)
											{
												log.catching(e);
											}
										}
										
										if (finalBooleanArrayValueMap.isEmpty())
											return null;
										else
										{
											boolean[] finalDoubleArrayValue = new boolean[finalBooleanArrayValueMap.size()];

											for (int i = 0; i < finalDoubleArrayValue.length; i++)
											{
												finalDoubleArrayValue[i] = finalBooleanArrayValueMap.get(i);
											}
											return finalDoubleArrayValue;
										}
									}

								case StringArray:
									HashMap<Integer, String> finalStringArrayValueMap = new HashMap<Integer, String>();
									Pattern stringP1 = Pattern.compile(",");
									String[] stringS1 = stringP1.split(values);
									for (String stringS2 : stringS1)
									{
										if (stringS2.length() > 0)
										{
											try
											{
												finalStringArrayValueMap.put(finalStringArrayValueMap.size(), stringS2);
											}
											catch (Exception e)
											{
												log.catching(e);
											}
										}
										
										if (finalStringArrayValueMap.isEmpty())
											return null;
										else
										{
											String[] finalStringArrayValue = new String[finalStringArrayValueMap.size()];

											for (int i = 0; i < finalStringArrayValue.length; i++)
											{
												finalStringArrayValue[i] = finalStringArrayValueMap
														.get(i);
											}
											return finalStringArrayValue;
										}
									}

								default:
									log.error("Did something go wrong? An Error ocured while trying to retrieve Value from: " + sectionName + "!");
									log.info("Its recommended to check/regenerate the configFile while this might be caused by a faulty input.");
									break getValueLoop;
								}
							}
						}
					}
				}
			}	
		}
		log.error("Couldn't find ConfigSection " + sectionName + " in the configFile!, returning Default value instead.");
		log.info("Its recommended to check/regenerate the configFile while this might be caused by a faulty input.");
		return configSection.getDefaultValue();
	}
}