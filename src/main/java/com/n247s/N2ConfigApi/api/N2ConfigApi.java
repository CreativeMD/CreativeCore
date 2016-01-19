package com.n247s.N2ConfigApi.api;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraftforge.common.DimensionManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.n247s.N2ConfigApi.api.core.events.ConfigApiEvents;
import com.n247s.N2ConfigApi.api.networking.N2ConfigApiMessageHandler;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.FMLInjectionData;

/**
 * @author N247S
 * An ingame ConfigFile Manager<br>
 * <br>
 * This Class contains all the method regarding the Directory handling.
 */
//@SubApi(APIID="N2ConfigApi", APIVersion="1.0.8")
public final class N2ConfigApi
{
	private static final HashMap<String, File> DirectoryList = new HashMap<String, File>();
	private static final HashMap<File, File> TempDirectoryList = new HashMap<File, File>();
	public static final Logger log = LogManager.getLogger("N2ConfigAPI");
	
//	@Instance
	public static N2ConfigApi instance;
	
//	@FMLEventMethod(EventType=FMLEventTypes.FMLPreInit)
	public static void preInit(FMLPreInitializationEvent event)
	{
		N2ConfigApiMessageHandler.preInitialize();
		ConfigApiEvents.preInitialize();
		createDefaultDir(event.getModConfigurationDirectory());
	}

	/**
	 * This will register a Directory where you can store your (Config)Files.
	 * 
	 * @param directoryPath - This is the path inside the Minecraft default ConfigDirectory.
	 * 	(Illegal DirectoryNames: '&#042&#042/temp')
	 * @param directoryID - With this ID you can retrieve the Instance of the Directory. 
	 * @return - File(Directory)
	 */
	public static File registerCustomConfigDirectory(String directoryPath, String directoryID)
	{
		File newDir = new File(getConfigDir(), directoryPath);
		File newTempDir = new File(getConfigDir(), (directoryPath + "/temp"));
		
		Iterator directory = DirectoryList.keySet().iterator();
		while(directory.hasNext())
		{
			File currentDirectory = (File) (DirectoryList.get(directory.next()));
			if(currentDirectory.getPath().equals(directoryPath))
				log.catching(new IllegalArgumentException("DirectoryPath " + directoryPath + " already exist!"));
		}
		
		if(directoryID != null)
			if(DirectoryList.containsKey(directoryID))
				log.error("ConfigFile " + directoryID + " is Already Registered!");
			else DirectoryList.put(directoryID, newDir);
		else log.catching(new NullPointerException());
		
		TempDirectoryList.put(newDir, newTempDir);
		newTempDir.mkdirs();
		return newDir;
	}
	
	/**
	 * This will register a Directory where you can store your (Configuration)Files.
	 * 
	 * @param ParentDirectory - Where this Directory will be created.
	 * Note, the parentDirectory MUST exist for the auto generate funcions to work!
	 * If you want to create multiple 'nested' Directory's, add them to the directoryPath. ("folder1/folder2/finalfolder")
	 * @param directoryPath - This is the path inside the ParentDirectory.
	 * 	(Illegal DirectoryNames: "/temp")
	 * @param directoryID - With this ID you can retrieve the Instance of the Directory.
	 * @return - File(Directory)
	 */
	public static File registerCustomDirectory(File ParentDirectory, String directoryPath, String directoryID)
	{
		File newDir = new File(ParentDirectory, directoryPath);
		File newTempDir = new File(ParentDirectory, (directoryPath + "/temp"));
		
		Iterator directory = DirectoryList.keySet().iterator();
		while(directory.hasNext())
		{
			File currentDirectory = (File) (DirectoryList.get(directory.next()));
			if(currentDirectory.getPath().equals(directoryPath))
				log.catching(new IllegalArgumentException("DirectoryPath " + directoryPath + " already exist!"));
		}
		
		if(directoryID != null)
			if(DirectoryList.containsKey(directoryID))
				log.error("ConfigFile " + directoryID + " is Already Registered!");
			else DirectoryList.put(directoryID, newDir);
		else log.catching(new NullPointerException());
		
		TempDirectoryList.put(newDir, newTempDir);
		return newDir;
	}
	
	/**
	 * @return - The Minecraft MainFileDirectory (don't use this for "registerCustomDirectory()", it will be added Automatically)
	 */
	public static File getMCMainDir()
	{
		return (File)FMLInjectionData.data()[6];
	}
	
	/**
	 * @return - The Minecraft configDirectory (".../config")
	 */
	public static File getConfigDir()
	{
		return getFileDirFromID("Default");
	}
	
	/**
	 * @return - The MainDirectory of the WorldFile.
	 * Warning, calling this when a world isn't loaded will return null, or may even cause a crash!
	 * Also be very careful when you're modding for multiWorld servers!
	 */
	public static File getWorldDir()
	{
		return DimensionManager.getCurrentSaveRootDirectory();
	}
	
	/**
	 * @param directory
	 * @return - The names of all the Files with the extension '.cfg' inside this Directory.
	 */ 
	public static String[] getDirContentConfigNames(File directory)
	{
		String[] totalList = getDirContentNames(directory);
		List<String> finalList = new ArrayList<String>();
		for(int i = 0; i < totalList.length; i++)
		{
			String currentName = totalList[i];
			if(currentName.endsWith(".cfg"))
				finalList.add(currentName.substring(0, currentName.length()-4));
		}
		
		return (String[]) finalList.toArray(new String[finalList.size()]);
	}
	
	/**
	 * @param directoryID
	 * @return - The names of all the Files with the extension '.cfg' inside this Directory.
	 */ 
	public static String[] getDirContentConfigNames(String directoryID)
	{
		return getDirContentConfigNames(getFileDirFromID(directoryID));
	}
	
	/**
	 * @param directory
	 * @return - The names of all the Files(and Directory's) inside this Directory
	 * Note, this includes extensions!
	 */
	public static String[] getDirContentNames(File directory)
	{
		return directory.list();
	}
	
	/**
	 * @param directoryID
	 * @return - The names of all the Files(and Directory's) inside this Directory
	 * Note, this includes extensions!
	 */
	public static String[] getDirContentNames(String directoryID)
	{
		return getFileDirFromID(directoryID).list();
	}
	
	/**
	 * @param directoryID
	 * @return - File(Directory)
	 */
	public static File getFileDirFromID(String directoryID)
	{
		return DirectoryList.get(directoryID);
	}
	
	/**
	 * @param FileDirectory
	 * @return - The IDName of the FileDirectory
	 */
	public static String getFileIDFromFileDir(File FileDirectory)
	{
		Iterator<String> fileID = DirectoryList.keySet().iterator();
		
		while(fileID.hasNext())
		{
			String currentString = fileID.next();
			if(DirectoryList.get(currentString).equals(FileDirectory))
				return currentString;
		}
		return null;
	}
	
	/**
	 * Retrieve the 'temp' Directory which is registered with the Directory
	 * @param directory
	 * @return - File(Directory)
	 */
	public static File getTempDir(File directory)
	{
		return TempDirectoryList.get(directory);
	}
	
	private static void createDefaultDir(File configDirectory)
	{
		N2ConfigApi.DirectoryList.put("Default", configDirectory);
		N2ConfigApi.TempDirectoryList.put(configDirectory, (new File(configDirectory, "temp")));
	}
}
