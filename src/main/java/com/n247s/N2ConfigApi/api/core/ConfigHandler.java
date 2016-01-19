package com.n247s.N2ConfigApi.api.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.minecraft.client.Minecraft;

import org.apache.logging.log4j.Logger;

import com.n247s.N2ConfigApi.api.N2ConfigApi;
import com.n247s.N2ConfigApi.api.networking.N2ConfigApiMessageHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

/**
 * @author N247S
 * An ingame ConfigFile Manager<br>
 * <br>
 * This class contains all the methods regarding the (Config)File handling
 */
public class ConfigHandler
{
	private static final Logger log = N2ConfigApi.log;
	private static final HashMap<ConfigFile, File> fileList = new HashMap<ConfigFile, File>();
	private static final List<ConfigFile> backupList = new ArrayList<ConfigFile>();

	
	/**
	 * This will register a ConfigurationFile. (.cfg) Note, make sure these
	 * configFiles have unique names, otherwise configFiles may be merged, or in
	 * the worse case overwritten by each other!
	 * 
	 * @param configFile
	 * @param targetDirectory
	 *            - This is the Directory where the ConfigFile will go.<br>
	 *            If you want it to go into the default configDir, use
	 *            {@link N2ConfigApi#getConfigDir()}
	 * @return - True if the registration was successful, false otherwise.<Br>
	 *         Note, if a ConfigFile isn't registered, a lot of functions
	 *         probably won't work! Like writing and reading methods.
	 */
	public static boolean registerConfigFile(final ConfigFile configFile, File targetDirectory)
	{
		ProxySide side = configFile.getProxySide();
		if(!side.isEffectiveSide(FMLCommonHandler.instance().getEffectiveSide()))
			return false;
		
		String fileName = configFile.getFileName() + ".cfg";
		File newFile;
		if(targetDirectory != null)
			if(targetDirectory.isDirectory())
				newFile = new File(targetDirectory, fileName);
			else
			{
				log.catching(new IllegalArgumentException("File " + targetDirectory.getPath() + " isn't a ConfigurationDirectory!"));
				return false;
			}
		else newFile = new File(N2ConfigApi.getConfigDir(), fileName);
		
		Iterator<ConfigFile> file = fileList.keySet().iterator();
		while(file.hasNext())
		{
			ConfigFile currentFile = file.next();
			if(currentFile.getFileName() == configFile.getFileName())
			{
				log.catching(new IllegalArgumentException("Filename " + configFile.getFileName() + " already exist!"));
				return false;
			}
		}
		
		if (!fileList.containsKey(configFile))
		{
			if(getConfigFileFromName(configFile.getFileName()) == null)
				fileList.put(configFile, newFile);
		}
		else
		{
			log.warn("ConfigFile is already registered!");
			return false;
		}
		return true;
	}
	
	/**
	 * @param fileName
	 * @return true if the ConfigFile is removed, false otherwise.
	 * (Server Side Only!)
	 */
	public static boolean removeConfigurationFile(String fileName)
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
			return false;
		
		boolean isSuccessFull = false;
		final ConfigFile configFile = getConfigFileFromName(fileName);
		isSuccessFull = removeConfigFile(configFile);
		if(isSuccessFull)
			N2ConfigApiMessageHandler.syncDeleteConfigFile(new ArrayList<ConfigFile>(){{add(configFile);}}, null);
		return isSuccessFull;
	}
	
	public static void requestConfigFileDeletion(String fileName)
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isServer())
			return;
		
		final ConfigFile configFile = getConfigFileFromName(fileName);
		if(configFile != null);
			N2ConfigApiMessageHandler.syncDeleteConfigFile(new ArrayList<ConfigFile>(){{add(configFile);}}, Minecraft.getMinecraft().thePlayer.getUniqueID());
	}
	
	/** Don't use this method since it could break the game! */
	public static boolean removeConfigFile(ConfigFile configFile)
	{
		if(configFile != null)
			if(FMLCommonHandler.instance().getEffectiveSide().isServer() ? true : configFile.getProxySide().isClient())
				if(InitConfigObjectManager.removeConfigFile(configFile.getFileName()))
				{
					fileList.remove(configFile);
					return true;
				}
		return false;
	}
	
	/**
	 * Sync the value's of a single ConfigFile if the file is a {@link ProxySide#Common} type ConfigFiles.<Br>
	 * (Server Side only!)
	 * @param fileName
	 */
	public static void syncConfigFile(String fileName)
	{
		syncConfigFile(getConfigFileFromName(fileName));
	}
	
	/** 
	 * Sync the value's of a single ConfigFile if the file is a {@link ProxySide#Common} type ConfigFiles.<br>
	 * (Server Side only!)
	 * @param configFile
	 */
	public static void syncConfigFile(final ConfigFile configFile)
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		
		if(configFile.getProxySide().isCommon())
			if(configFile.haveSectionChanged())
				syncFullConfigFile(configFile);
			else N2ConfigApiMessageHandler.syncValues(new ArrayList<ConfigFile>(){{add(configFile);}}, null);
		else log.catching(new IllegalArgumentException("ConfigFile " + configFile.getFileName() + " is not a FileSide.Common type File!"));
	}
	
	/**
	 * Syncs the value's of all the {@link ProxySide#Common} type ConfigFiles inside the List.<Br>
	 * (Server Side only!)
	 * @param configFiles
	 */
	public static void SyncConfigFiles(List<ConfigFile> configFiles)
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		
		List<ConfigFile> configFileList = new ArrayList<ConfigFile>();
		
		for(ConfigFile currentFile : configFiles)
		{
			if(currentFile.getProxySide().isCommon())
				if(currentFile.haveSectionChanged())
					syncFullConfigFile(currentFile);
				else configFileList.add(currentFile);
		}
		N2ConfigApiMessageHandler.syncValues(configFileList, null);
	}
	
	/** Server Side only! */
	private static void syncFullConfigFile(ConfigFile configFile)
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isServer())
			N2ConfigApiMessageHandler.syncFullConfigFile(configFile, null);
	}
	
	/**
	 * Forces a total sync. Don't use this method since it could break the game!<Br>
	 * (Server Side only!)
	 * @param playerID
	 */
	public static void forceCompleteSync(UUID playerID)
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		
		for(ConfigFile currentFile : fileList.keySet())
			if(currentFile.getProxySide().isCommon())
				N2ConfigApiMessageHandler.syncFullConfigFile(currentFile, playerID);
	}
	
	/**
	 * Send a request to update a full ConfigFile (may be denied if no permission is granted!)<Br>
	 * (Client Side Only!)
	 * @param fileName
	 */
	public static void requestFullConfigFileSync(String fileName)
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isServer())
			return;
		
		ConfigFile configFile = getConfigFileFromName(fileName);
		if(configFile != null)
			N2ConfigApiMessageHandler.syncFullConfigFile(configFile, Minecraft.getMinecraft().thePlayer.getUniqueID());
		else log.catching(new NullPointerException("Couldn't find ConfigFile " + fileName));
	}
	
	/**
	 * Send a request to update ConfigFile Value's (may be denied if no permission is granted!)<Br>
	 * (Client Side Only!)
	 * @param fileName
	 */
	public static void requestConfigFileValueSync(String fileName)
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isServer())
			return;
		
		final ConfigFile configFile = getConfigFileFromName(fileName);
		if(configFile != null)
			N2ConfigApiMessageHandler.syncValues(new ArrayList<ConfigFile>(){{add(configFile);}}, Minecraft.getMinecraft().thePlayer.getUniqueID());
		else log.catching(new NullPointerException("Couldn't find ConfigFile " + fileName));
	}
	
	/** Don't use this method since it could break the game! */
	public static void backupConfigFile(String fileName)
	{
		if(backupList.isEmpty())
		{
			ConfigFile namesConfig = new DefaultConfigFile("2N4$7B@cNq", "");
			namesConfig.addNewStringSection("ConfigFileNames", null, "", false);
			backupList.add(namesConfig);
		}
		ConfigFile configFile = getConfigFileFromName(fileName);
		if(configFile != null)
			backupList.add(configFile);
		else
		{
			ConfigSection section = backupList.get(0).getSubSection("ConfigFileNames");
			section.setDefaultValue((String)section.getDefaultValue() + ";" + fileName);
		}
	}
	
	/** Don't use this method since it could break the game! */
	public static void cleanUpTempServerFiles()
	{
		if(backupList.isEmpty())
			return;
		String configNames = (String) backupList.get(0).getSubSection("ConfigFileNames").getDefaultValue();
		String[] nameArray = configNames.split(";");
		for(String currentName : nameArray)
			removeConfigFile(getConfigFileFromName(currentName));
		backupList.remove(0);
		
		for(ConfigFile currentFile : backupList)
		{
			ConfigFile sConfigFile = getConfigFileFromName(currentFile.getFileName());
			File file = fileList.get(sConfigFile);
			fileList.remove(sConfigFile);
			fileList.put(currentFile, file);
			currentFile.readAndSaveConfigValues();
		}
		backupList.clear();
	}
	
	/**
	 * This will create new, or check all existing files.
	 * Warning!, you really shouldn't call this if you don't know what you are doing!
	 * (Inaccessible for now, since there isn't a way to store files per ModInstance,
	 * this would only harm the performance when called.)
	 * @deprecated - This method could break too much mechanisms!
	 */
	private static void loadAndCheckAllConfigFiles()
	{
		try
		{
//			Iterator DirectoryID = N2ConfigApi.DirectoryList.keySet().iterator();
//			while(DirectoryID.hasNext())
//			{
//				File dir = N2ConfigApi.DirectoryList.get(DirectoryID.next());
//				dir.mkdirs();
//			}
			generateAllFiles();
			Iterator<ConfigFile> file = fileList.keySet().iterator();
			while(file.hasNext())
			{
				ConfigFile current = file.next();
				List<String> invalidSections = current.checkConfigFile();
				if(invalidSections != null)
					current.regenerateConfigFile(invalidSections);
				current.readAndSaveConfigValues();
			}
		}
		catch(Exception e)
		{
			log.catching(e);
		}
	}
	
	/**
	 * This will create or check the configFile.
	 * @param configFileName
	 * @return - True if check was successful, false otherwise.<br>
	 */
	public static boolean loadAndCheckConfigFile(String configFileName)
	{
		try
		{
			ConfigFile configFile = getConfigFileFromName(configFileName);
			if(configFile == null)
				return false;
			else
			{
				if(!configFile.getProxySide().isEffectiveSide(FMLCommonHandler.instance().getEffectiveSide()))
					return false;
				
				configFile.generateFile();
				configFile.readAndSaveConfigValues();
				
				generateSingleFile(configFile);
			
				List<String> invalidSections = configFile.checkConfigFile();
				configFile.regenerateConfigFile(invalidSections);
				if(configFile.getProxySide().isCommon() && FMLCommonHandler.instance().getEffectiveSide().isServer())
					syncFullConfigFile(configFile);
				return true;
			}
		}
		catch(Exception e)
		{
			log.catching(e);
		}
		return false;
	}
	
	/**
	 * This will create the configFile.
	 * @param configFileName
	 */
	public static boolean loadConfigFile(String configFileName)
	{
		try
		{
			ConfigFile configFile = getConfigFileFromName(configFileName);
			if(configFile == null)
				return false;
			else
			{
				if(!configFile.getProxySide().isEffectiveSide(FMLCommonHandler.instance().getEffectiveSide()))
					return false;
				
				configFile.generateFile();
				configFile.readAndSaveConfigValues();
				
				generateSingleFile(configFile);
				if(configFile.getProxySide().isCommon() && FMLCommonHandler.instance().getEffectiveSide().isServer())
					syncFullConfigFile(configFile);
				return true;
			}
		}
		catch(Exception e)
		{
			log.catching(e);
		}
		return false;
	}
	
	/**
	 * This will create or check all the configFiles in the StringArray.
	 * @param configFileNames
	 */
	public static boolean loadAndCheckConfigFileList(String[] configFileNames)
	{
		boolean checkSuccessful = true;
		try
		{
			for(int i = 0; i < configFileNames.length; i++)
			{
				if(!loadAndCheckConfigFile(configFileNames[i]))
					checkSuccessful = false;
			}
		}
		catch(Exception e)
		{
			log.catching(e);
		}
		return checkSuccessful;
	}
	
	/**
	 * This will create all the configFiles in the StringArray.
	 * @param configFileNames
	 */
	public static boolean loadConfigFileList(String[] configFileNames)
	{
		boolean checkSuccessful = true;
		for (String configName : configFileNames)
		{
			if(!loadConfigFile(configName))
				checkSuccessful = false;
		}
		return checkSuccessful;
	}
		
	/**
	 * This will create a single File. Note, if an File already exist, it will be overwritten! (except 'Clone' FileTypes)
	 * @param originalFileName
	 * @param fileType
	 * @param originalFile
	 * @param targetDirectory - The Directory where this File will be created
	 * @return - The new created File. Note, {@link FileType#Original} will return an empty File!
	 * @throws IOException
	 */
	public static File generateSingeFile(String originalFileName, FileType fileType, File originalFile, File targetDirectory) throws IOException
	{
		File target = null;
		if(targetDirectory != null)
			if(targetDirectory.isDirectory())
				target = targetDirectory;
			else log.error("File isn't generated. Couldn't resolve targetDirectory!");
		else log.error("File isn't generated. Couldn't resolve targetDirectory!");
		
		File newFile = null;
		
		switch(fileType)
		{
		case Original:
			newFile = originalFile;
			break;
		case BackUp:
			newFile = new File(target, originalFileName.substring(0, originalFileName.lastIndexOf(".") - 1) + "_BackUp.txt");
			break;
		case Temp:
			newFile = new File(target, originalFileName.substring(0, originalFileName.lastIndexOf(".") - 1) + "_Temp.tmp");
			break;
		case Clone:
			int i = 1;
			if(getConfigFileFromName(originalFileName) == null)
				newFile = new File(target, originalFileName);
			else
			{
				while(true)
				{
					newFile = new File(target, originalFileName + "_copy_" + i + originalFile.getName().substring(originalFileName.length(), originalFile.getName().length()));
					if(newFile.exists()) i++;
					else break;
				}
			}
			break;
		}
		if(newFile.exists())
			newFile.delete();
		newFile.createNewFile();
		if(fileType != FileType.Original)
			copyFile(originalFile, newFile);
		return newFile;
	}
	
	/**
	 * This will create a new ConfigFile. Note, it will override existing files (except 'Clone' FileTypes).
	 * @param configFile
	 * @param fileType
	 * @param targetDirectory - This is the Directory where the ConfigurationFile will be created.
	 * @return - The new created File. Note, {@link FileType#Original} will return an empty File!
	 * @throws IOException
	 */
	public static File generateSingleFileFromConfigFile(ConfigFile configFile, FileType fileType, File targetDirectory) throws IOException
	{
		return generateSingleFileFromConfigFile(null, configFile, fileType, targetDirectory);
	}
	
	/**
	 * This is mainly used for Clones with specific names.
	 * @param fileName - this will only be used for {@link FileType#Clone}
	 * @param configFile
	 * @param fileType
	 * @param targetDirectory - This is the Directory where the ConfigurationFile will be created.
	 * @return - The new created File. Note, {@link FileType#Original} will return an empty File!
	 */
	public static File generateSingleFileFromConfigFile(String fileName, ConfigFile configFile, FileType fileType, File targetDirectory)
	{
		if(configFile.getProxySide().isEffectiveSide(FMLCommonHandler.instance().getEffectiveSide()) || (configFile.getProxySide().isClient() && configFile.isConnected()))
			return null;
		
		if(getConfigFileFromName(configFile.getFileName()) == null)
		{
			log.catching(new IllegalArgumentException("ConfigFile " + configFile.getFileName() + " couldn't be found!"));
			return null;
		}
		
		File target = null;
		File originalFile = getFileFromConfigFile(configFile);
		String originalFileName = configFile.getFileName();
		if(targetDirectory == null)
			target = originalFile.getParentFile();
		else target = targetDirectory;
		
		File file;
		
		try
		{
			if(fileType.equals(FileType.Clone))
			{
				ConfigFile newConfigurationFile = (ConfigFile) configFile.CloneConfigurationFileInstance();
				
				if(fileName != null)
				{
					newConfigurationFile.setFileName(fileName);
					registerConfigFile(newConfigurationFile, target);
					file = generateSingeFile(fileName, FileType.Original, getFileFromConfigFile(newConfigurationFile), target);
				}
				else
				{
					file = generateSingeFile(fileName, FileType.Clone, getFileFromConfigFile(configFile), target);
					newConfigurationFile.setFileName(file.getName().substring(0, file.getName().length() - 4));
					registerConfigFile(newConfigurationFile, target);
				}
				newConfigurationFile.readAndSaveConfigValues();
			}
			else file = generateSingeFile(originalFileName, fileType, originalFile, target);
		}
		catch(Exception e)
		{
			log.catching(e);
			return null;
		}
		return file;
	}
	
	/**
	 * Don't, just don't!
	 * @deprecated - This method could break too much mechanisms!
	 * @throws IOException
	 */
	private static void generateAllFiles() throws IOException
	{
		Iterator i = fileList.keySet().iterator();
		
		while(i.hasNext())
		{
			ConfigFile currentConfigFile = (ConfigFile) i.next();
			if(currentConfigFile != null)
				{
				File file = getFileFromConfigFile(currentConfigFile);
				File dir = file.getParentFile();
				File dirtemp = N2ConfigApi.getTempDir(dir);
				
				dir.mkdirs();
				dirtemp.mkdirs();
				if(!file.exists())
					file.createNewFile();

				currentConfigFile.generateFile();

				if(!currentConfigFile.getIsWritten())
					currentConfigFile.writeAllSections();
			} 
		}
	}
	
	private static void generateSingleFile(ConfigFile configFile) throws IOException
	{
		if(!configFile.getProxySide().isEffectiveSide(FMLCommonHandler.instance().getEffectiveSide()) || configFile.isConnected())
			return;
		
		if(getConfigFileFromName(configFile.getFileName()) == null)
		{
			log.catching(new IllegalArgumentException("ConfigFile " + configFile.getFileName() + " Couldn't be found!"));
			return;
		}
		
		if(configFile != null)
		{
			File file = getFileFromConfigFile(configFile);
			File dir = file.getParentFile();
			File dirtemp = N2ConfigApi.getTempDir(dir);
			
			dir.mkdirs();
			dirtemp.mkdirs();
			if(!file.exists())
				file.createNewFile();

			if(!configFile.getIsWritten())
				configFile.writeAllSections();
		}
	}
	
	/**
	 * This will simply copy the content of one File, into the other.
	 * @param oldFile - File which you want to copy from.
	 * @param copyFile - File which it should copy to.
	 */
	public static void copyFile(File oldFile, File copyFile)
	{
		try
		{
			if(oldFile != null && copyFile != null)
			{
				BufferedReader reader = new BufferedReader(new FileReader(oldFile));
				BufferedWriter writer = new BufferedWriter(new FileWriter(copyFile));
				
				String readedLine;
				
				while((readedLine = reader.readLine()) != null)
				{
					writer.append(readedLine);
					writer.newLine();
					writer.flush();
				}
				writer.close();
				reader.close();
			}
		}
		catch(Exception e)
		{
			log.catching(e);
		}
	}
	
	/**
	 * @param configFile
	 * @return The File linked to this ConfigFile, or null if the ConfigFile couldn't be found.
	 */
	public static File getFileFromConfigFile(ConfigFile configFile)
	{
		return fileList.get(configFile);
	}
	
	/**
	 * @param configFileName
	 * @return The ConfigFile with this name, or null if no file exist with this name.
	 */
	public static ConfigFile getConfigFileFromName(String configFileName)
	{
		Iterator<ConfigFile> currentFile = fileList.keySet().iterator();
		
		while(currentFile.hasNext())
		{
			ConfigFile file = currentFile.next();
			if(file.getFileName().equals(configFileName))
				return file;
		}
		return null;
	}
	
	public BufferedWriter getWriter(File file) throws IOException
	{
		return new BufferedWriter(new FileWriter(file));
	}
	
	public BufferedReader getReader(File file) throws IOException
	{
		return new BufferedReader(new FileReader(file));
	}
	
	public void closeWriter(BufferedWriter writer) throws IOException
	{
		writer.flush();
		writer.close();
	}
	
	public void closeReader(BufferedReader reader) throws IOException
	{
		reader.close();
	}
	
	
	public enum FileType
	{
		Original,
		BackUp,
		/** Don't use this until its really an temporary file!, otherwise use backup or clone instead! */
		Temp,
		Clone;
	}
	
	public enum ProxySide
	{
		/** ClientSide only configFile */
		Client,
		/** ServerSide only configFile */
		Server,
		/** usual configFile */
		Common;
		
		public boolean isEffectiveSide(Side proxySide)
		{
			switch(this)
			{
				case Client: return proxySide.isClient();
				case Server: return proxySide.isServer();
				case Common: return true;
			}
			return false;
		}
		
		public boolean isServer()
		{
			return this == Server;
		}
		
		public boolean isClient()
		{
			return this == Client;
		}
		
		public boolean isCommon()
		{
			return this == Common;
		}
	}
}
