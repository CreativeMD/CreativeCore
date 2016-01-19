package com.n247s.N2ConfigApi.api.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;

import org.apache.logging.log4j.Logger;

import com.n247s.N2ConfigApi.api.N2ConfigApi;
import com.n247s.N2ConfigApi.api.core.ConfigHandler.FileType;
import com.n247s.N2ConfigApi.api.core.ConfigHandler.ProxySide;
import com.n247s.N2ConfigApi.api.core.InitConfigObjectManager.Config;
import com.n247s.N2ConfigApi.api.networking.N2ConfigApiMessageHandler;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * @author N247S
 * An ingame ConfigFile Manager
 */

public abstract class ConfigFile extends ConfigSectionCollection implements Cloneable, Serializable
{
	private static final long serialVersionUID = -864529735488611537L;
	
	private static final Logger log = N2ConfigApi.log;
	private String fileName;
	private ProxySide proxySide;
	
	private ConfigFile(String sectionName, String[] description, String fileName, ProxySide proxySide, boolean setExtraSpaces, boolean isVirtual)
	{
		super(sectionName, description, setExtraSpaces);
		this.fileName = fileName;
		this.proxySide = proxySide;
	}
	
	/**
	 * This will automatically register a new ConfigFile.
	 * @param fileName - the name which the configFile will get (fileName.cfg)
	 * @param proxySide - The ProxySide of this ConfigFile. Note, This can't be changed after Construction!
	 */
	public ConfigFile(String fileName, ProxySide proxySide)
	{
		this("defaultSection", null, fileName, proxySide, true, false);
	}
	
	protected final void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
	
	/** This method will be called when you generate a ConfigFile */
	public abstract void generateFile();
	
	/** In this method you can check anything you want regarding configFile changing etc. (serverSide!) */
	public abstract boolean getPermission(EntityPlayerMP player);
	
	/**
	 * This will write All sections that are registered to this ConfigFile
	 */
	public final void writeAllSections()
	{
		if(isConnected())
			return;
		try
		{
			BufferedWriter writer = getNewWriter();
			super.writeAllSections(writer);
			closeWriter(writer);
		}
		catch(Exception e)
		{
			log.catching(e);
		}
	}
	
	/**
	 * This will write the section. Note, it will be written behind the existing text inside the File!
	 * @param sectionName
	 */
	public final void writeSection(String sectionName)
	{
		if(isConnected())
			return;
		try
		{
			BufferedWriter writer = getNewWriter();
			super.writeSubSection(writer, sectionName);
			closeWriter(writer);
		}
		catch(Exception e)
		{
			log.catching(e);
		}
	}
	
	/**
	 * This method will regenerate a configFile. Note, it will regenerate only invalidSections!
	 * If something goes wrong during the regeneration, it will backup the file, and regenerate the whole File!
	 * @param invalidSectionNames - List of IDNames of all the invalid Sections.
	 */
	public final void regenerateConfigFile(List<String> invalidSectionNames)
	{
		if(isConnected())
			return;
		if(invalidSectionNames != null && !invalidSectionNames.isEmpty())
		{
			try
			{
				File tempFile = ConfigHandler.generateSingleFileFromConfigFile(this, FileType.Temp, N2ConfigApi.getTempDir(ConfigHandler.getFileFromConfigFile(this).getParentFile()));
				File configFile = ConfigHandler.generateSingleFileFromConfigFile(this, FileType.Original, ConfigHandler.getFileFromConfigFile(this).getParentFile());
				
				if(tempFile == null || configFile == null)
				{
					log.error("Couldnt regenerate ConfigFile: " + invalidSectionNames);
					return;
				}
				BufferedWriter writer = getNewWriter();
				BufferedReader reader = new BufferedReader(new FileReader(tempFile));
				
				int sectionCount = 0;
				
				sectionCount = super.regenConfigChapter(writer, reader, invalidSectionNames);
				
				if(sectionCount != this.AbsoluteSubSectionMap.size())
				{
					log.error("Couldn't resolve the sections in: " + configFile.getPath());
					log.error("recreating this config file, all data will be restored to its default Value");
					File backUpFile = ConfigHandler.generateSingleFileFromConfigFile(this, ConfigHandler.FileType.BackUp, N2ConfigApi.getTempDir(ConfigHandler.getFileFromConfigFile(this).getParentFile()));
					ConfigHandler.copyFile(tempFile, backUpFile);
					ConfigHandler.generateSingleFileFromConfigFile(this, ConfigHandler.FileType.Original, ConfigHandler.getFileFromConfigFile(this).getParentFile());
				}
				closeWriter(writer);
				tempFile.delete();
				closeReader(reader);
			}
			catch(Exception e)
			{
				log.catching(e);
			}
		}
	}
	
	/**
	 * This will check if the existing configuration file contains all registered sections.
	 * If this isn't the case it will return an List of String representing all the sections which are considered invalid,
	 * otherwise it will return null. (an section is considered invalid when it doesn't contain its sectionName)
	 * Note, SectionTypes Text and SectionHead will not be checked since they don't contain value's!
	 * @return List(section names) || null - see description.
	 */
	public final List checkConfigFile()
	{
		if(isConnected())
			return null;
		String readedLine;
		List<String> invalidSectionList = new ArrayList<String>();
		
		Iterator<String> section = this.AbsoluteSubSectionMap.keySet().iterator();
		
		while(section.hasNext())
		{
			invalidSectionList.add(section.next());
		}

		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(ConfigHandler.getFileFromConfigFile(this)));
			while((readedLine = reader.readLine()) != null)
			{
				if(readedLine.startsWith("#") || readedLine.isEmpty())
					continue;

				if(readedLine.contains(";"))
				{
					for(int i = 0; i < invalidSectionList.size(); i++)
					{
						String subString = readedLine.replaceAll("[\\s.*]", "");
						char firstLetter = subString.charAt(0);
						try
						{
							if (invalidSectionList.contains(readedLine.substring(readedLine.indexOf(firstLetter), readedLine.indexOf(";"))))
							{
								invalidSectionList.remove(readedLine.substring(readedLine.indexOf(firstLetter), readedLine.indexOf(";")));
							}
						}
						catch(Exception e){}
					}
				}
			}
			reader.close();
		}
		catch (Exception e)
		{
			log.catching(e);
		}	
		if(!invalidSectionList.isEmpty())
		{
			for(int i = 0; i < invalidSectionList.size(); i++)
			{
				if(!(this.AbsoluteSubSectionMap.get(invalidSectionList.get(i)).getSectionType() == SectionType.Text) || !(this.AbsoluteSubSectionMap.get(invalidSectionList.get(i)).getSectionType() == SectionType.SectionHead))
					log.error("Missing Section: " + invalidSectionList.get(i) + " in " + ConfigHandler.getFileFromConfigFile(this).getName());
			}
			return invalidSectionList;
		}
		return null;
	}
	
	/**
	 * This will go through every ConfigSection, and see if the DefaultValue has Changed.
	 * @return - A list of all Changed ConfigSections, or null if there aren't any.
	 */
	public final List<String> getChangedSections()
	{
		List<String> changedSections = new ArrayList<String>();
		Iterator<String> section = this.AbsoluteSubSectionMap.keySet().iterator();
		
		while(section.hasNext())
		{
			ConfigSection currentSection = this.getSubSection(section.next());
			if(currentSection.getChangedDefaultValue())
				changedSections.add(section.next());
		}
		if(changedSections.isEmpty())
			return null;
		return changedSections;
	}
	
	/**
	 * This will save the values of this configFile into an active Object so you can retrieve the Values ingame without affecting the performance.
	 */
	public final void readAndSaveConfigValues()
	{
		if(isConnected())
			return;
		
		if(ConfigHandler.getConfigFileFromName(this.fileName) == null)
			return;
		
		if(!this.proxySide.isEffectiveSide(FMLCommonHandler.instance().getEffectiveSide()))
			return;
		try
		{
			if(super.readAndSaveValues(this.fileName))
				if(FMLCommonHandler.instance().getEffectiveSide().isServer() && this.proxySide.isCommon())
					ConfigHandler.syncConfigFile(this);
		}
		catch(Exception e)
		{
			log.catching(e);
		}
	}
	
	/**
	 * @param sectionName
	 * @return - The value of this section, the Default value if the value can't be resolved, or null if the section does not exist.
	 */
	public final Object getValue(String sectionName)
	{
		if(InitConfigObjectManager.getConfigByName(this.fileName) != null)
			if(this.getSubSection(sectionName) != null)
				return InitConfigObjectManager.getField(InitConfigObjectManager.getConfigByName(this.fileName), sectionName);
			else
			{
				log.error("Couldn't find " + sectionName + " in " + this.fileName);
				return null;
			}
		else return this.getSubSection(sectionName).getDefaultValue();
	}
	
	public final void setValue(String sectionName, Object newValue)
	{
		if (InitConfigObjectManager.getConfigByName(this.fileName) == null)
			InitConfigObjectManager.addConfigFile(this.fileName);
		if (this.getSubSection(sectionName) != null)
			InitConfigObjectManager.changeFieldValue( InitConfigObjectManager.getConfigByName(this.fileName), sectionName, newValue);
		else log.error("Couldn't find " + sectionName + " in " + this.fileName);
	}
	
	/**
	 * Use only if you really know what you are doing! If you want to Clone a configFile,
	 * use {@link ConfigHandler#generateSingleFileFromConfigFile()} instead with {@link FileType#Clone} as second parameter.
	 * @return - new Instance of this ConfigFile.
	 * @throws CloneNotSupportedException
	 */
	protected final ConfigFile CloneConfigurationFileInstance() throws CloneNotSupportedException
	{
		return (ConfigFile) super.clone();
	}
		
	public final String getFileName()
	{
		return this.fileName;
	}
	
	public final boolean getIsWritten()
	{
		if(isConnected())
			return false;
		try
		{
			BufferedReader reader = getNewReader();
			String readedLine;
			if((readedLine = reader.readLine()) != null)
			{
				closeReader(reader);
				return true;
			}
			closeReader(reader);
		}
		catch(Exception e)
		{
			log.catching(e);
		}
		return false;
	}
	
	public final ProxySide getProxySide()
	{
		return this.proxySide;
	}
	
	/**
	 * @return - A new BufferedWriter. Don't forget to close it after using!
	 * @throws IOException
	 */
	public final BufferedWriter getNewWriter() throws IOException
	{
		if(isConnected())
			return null;
		return new BufferedWriter(new FileWriter(ConfigHandler.getFileFromConfigFile(this)));
	}
	
	public final void closeWriter(BufferedWriter writer) throws IOException
	{
		writer.flush();
		writer.close();
	}
	
	/**
	 * @return A new BufferedReader. Don't forget to close it after using!
	 * @throws IOException
	 */
	public final BufferedReader getNewReader() throws IOException
	{
		if(isConnected())
			return null;
		return new BufferedReader(new FileReader(ConfigHandler.getFileFromConfigFile(this)));
	}
	
	public final void closeReader(BufferedReader reader) throws IOException
	{
		reader.close();
	}
	
	public final List<ByteBuf> writeFullConfigFileToBuff(ByteBufOutputStream stream) throws IOException
	{
		List<ByteBuf> byteBufList = new ArrayList<ByteBuf>();
		File parentDirectory = ConfigHandler.getFileFromConfigFile(this).getParentFile();
		stream.writeUTF(N2ConfigApi.getFileIDFromFileDir(parentDirectory));
		stream.writeUTF(parentDirectory.toURI().getPath().substring(N2ConfigApi.getMCMainDir().toURI().getPath().length(), parentDirectory.toURI().getPath().length()));
		stream.writeUTF(this.fileName);
		int subSectionCount = getAbsoluteSubSectionCount();
		stream.writeInt(0);
		
		writeFullSectionToByteBuf(stream, null);
		
		for(int i = 0; i < subSectionCount; i++)
		{
			ConfigSection currentSection = getConfigSectionAtAbsoluteIndex(i);
			currentSection.writeFullSectionToByteBuf(stream, getValue(currentSection.getSectionName()));
			stream.flush();
			
			if(stream.buffer().capacity() > N2ConfigApiMessageHandler.maxByteBuffSize)
			{
				stream.writeUTF(N2ConfigApiMessageHandler.getSignature(2));
				stream.flush();
				byteBufList.add(stream.buffer().copy());
				stream.buffer().clear();
				stream.writeUTF(N2ConfigApiMessageHandler.getSignature(5));
				stream.writeUTF(this.fileName);
				stream.writeInt(i + 1);
				stream.flush();
			}
		}
		stream.writeUTF(N2ConfigApiMessageHandler.getSignature(1));
		stream.writeInt(subSectionCount);
		stream.flush();
		byteBufList.add(stream.buffer());
		
		return byteBufList;
	}
	
	public final List<ByteBuf> writeValuesToBuff(ByteBufOutputStream stream) throws IOException
	{
		List<ByteBuf> byteBufList = new ArrayList<ByteBuf>();
		stream.writeUTF(N2ConfigApiMessageHandler.getSignature(6));
		stream.writeUTF(this.fileName);
		int subSectionCount = getAbsoluteSubSectionCount();
		
		for(int i = 0; i < subSectionCount; i++)
		{
			ConfigSection section = getConfigSectionAtAbsoluteIndex(i);
			section.WriteSectionValuesToByteBuf(stream, getValue(section.getSectionName()));
			stream.flush();
			
			if(stream.buffer().capacity() > N2ConfigApiMessageHandler.maxByteBuffSize && i < subSectionCount - 1)
			{
				stream.writeUTF(N2ConfigApiMessageHandler.getSignature(2));
				stream.flush();
				byteBufList.add(stream.buffer().copy());
				stream.buffer().clear();
				stream.writeUTF(N2ConfigApiMessageHandler.getSignature(3));
				stream.writeUTF(N2ConfigApiMessageHandler.getSignature(6));
				stream.writeUTF(this.fileName);
				stream.flush();
			}
		}
		return byteBufList;
	}
	
	public final boolean createFullConfigFileFromBuff(ByteBufInputStream stream, Config config) throws IOException
	{
		int subSectionStartIndex = stream.readInt();
		int i;
		ConfigSectionCollection sectionCollection = this;
		
		for(i = subSectionStartIndex; i > 0; --i)
		{
			ConfigSection section = getConfigSectionAtAbsoluteIndex(i);
			if(section.getSectionType() == SectionType.SectionHead)
				if((i - getAbsoluteConfigSectionIndex(section.getSectionName())) <= (int)section.getDefaultValue())
					sectionCollection = (ConfigSectionCollection) section;
		}
		
		while(true)
		{
			String currentString = stream.readUTF();
			if(currentString.equals(N2ConfigApiMessageHandler.getSignature(2)))
				return true;
			if(currentString.equals(N2ConfigApiMessageHandler.getSignature(1)))
				if(getAbsoluteSubSectionCount() != stream.readInt())
				{
					log.catching(new Exception("Found end signature while the total SectionCount isn't equal to the sended SectionCount!"));
					return false;
				}
				else return true;
			
			if(!createSection(stream, config, currentString, sectionCollection, subSectionStartIndex - i))
				return false;
			setChangedSections(false);
		}
	}
	
	public final int createValuesFromByteBuf(ByteBufInputStream stream) throws IOException
	{
		if(!InitConfigObjectManager.ConfigList.containsKey(this.fileName))
			InitConfigObjectManager.addConfigFile(this.fileName);
		Config config = InitConfigObjectManager.getConfigByName(this.fileName);
		
		String currentString;
		ConfigSection section;
		
		while(true)
		{
			currentString  = stream.readUTF();
			if(currentString.equals(N2ConfigApiMessageHandler.getSignature(6)))
				return 0;
			if(currentString.equals(N2ConfigApiMessageHandler.getSignature(1)))
				return 1;
			if(currentString.equals(N2ConfigApiMessageHandler.getSignature(2)))
				return 2;
			section = getSubSection(currentString);
			if(section != null)
				section.createSectionValuesFromBuf(stream, config);
			else
			{
				log.catching(new Exception("Couldn't find ConfigSection " + currentString + " in ConfigFile " + this.fileName));
//				return false;
			}
		}
	}
	
	private boolean createSection(ByteBufInputStream stream, Config config, String currentString, ConfigSectionCollection superSectionCollection, int subSectionIndex) throws IOException
	{
		SectionType sectionType = SectionType.values()[stream.readInt()];
		ConfigSection section = new ConfigSection(currentString, null, null, sectionType);
		section.createFullSectionFromBuf(stream, config);
		
		if(section.getSectionType() == SectionType.SectionHead)
		{
			int collectionSize = (int)section.getDefaultValue();
			ConfigSectionCollection sectionCollection = new ConfigSectionCollection(section.getSectionName(), null, false);
			sectionCollection.setDefaultValue(collectionSize);
			
			for(int i = 0; i < collectionSize - subSectionIndex + 1; i++)
			{
				currentString = stream.readUTF();
				if(!createSection(stream, config, currentString, sectionCollection, i))
				{
					superSectionCollection.addNewSection(sectionCollection);
					return false;
				}
			}
			sectionCollection.setChangedSections(false);
			superSectionCollection.addNewSection(sectionCollection);
			return true;
		}
		else
		{
			superSectionCollection.addNewSection(section);
			return true;
		}
	}
	
	public boolean isConnected()
	{
		return FMLCommonHandler.instance().getEffectiveSide().isClient() && !Minecraft.getMinecraft().isSingleplayer();
	}
}
