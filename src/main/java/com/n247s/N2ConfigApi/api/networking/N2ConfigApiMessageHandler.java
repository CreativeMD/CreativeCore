package com.n247s.N2ConfigApi.api.networking;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Logger;

import scala.actors.threadpool.Arrays;

import com.n247s.N2ConfigApi.api.N2ConfigApi;
import com.n247s.N2ConfigApi.api.core.ConfigFile;
import com.n247s.N2ConfigApi.api.core.ConfigHandler;
import com.n247s.N2ConfigApi.api.core.events.ConfigApiEvents;
import com.n247s.api.eventapi.EventApi;
import com.n247s.n2core.networking.N2MessageHandler;
import com.n247s.n2core.networking.N2NetworkEventHandler;
import com.n247s.n2core.networking.N2Packet;
import com.n247s.n2core.networking.N2PacketHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.relauncher.Side;

/**
 * @author N247S
 * An ingame ConfigFile Manager<br>
 * <br>
 * This is a Class to handle synchronization internally. There is no need of using any method or field inside this Class.
 */
public class N2ConfigApiMessageHandler
{
	private static final N2ConfigApiMessageHandler instance = new N2ConfigApiMessageHandler();//TODO remove?
	private static final Logger log = N2ConfigApi.log;
	public static final int maxByteBuffSize = 25 * 1024;
	private static HashMap<String, List<Byte>> ConfigFileClassMap = new HashMap<String, List<Byte>>();
	private static HashMap<String, List<Byte>> ConfigFileMap = new HashMap<String, List<Byte>>();

	public static void preInitialize()
	{
		FMLCommonHandler.instance().bus().register(instance);
		MinecraftForge.EVENT_BUS.register(instance);
		
		N2NetworkEventHandler.registerMessageHandler(N2PacketHandler.class, N2MessageHandler.class, Side.CLIENT);
		N2NetworkEventHandler.registerMessageHandler(N2PacketHandler.class, N2MessageHandler.class, Side.SERVER);
		
		N2PacketHandler.registerPacket("N2ConfigApi", N2ConfigApiConfigPacket.class);
	}
	
	public static String getSignature(int i)
	{
		final String[] PacketSignatures = new String[]
				{
					"2n4s7t@rD",	//FirstPackage
					"2n4s7F!ni$H",	//LastPackage
					"2n4s7&Re@k",	//BreakPackage
					"2n4$7Y!zSc",	//UpdatePackage
					"2n4$7Z@me$",	//NamesPackage
					"2n4$7c$cq@e",	//CreatePackage
					"2n4s7C0f!q",	//NConfigPackage
					"2n4$7D6Ite",	//DeletePackage
				};
		return PacketSignatures[i];
	}
	
	@SubscribeEvent
	public void onPlayerJoin(PlayerLoggedInEvent event)
	{
		EventApi.defaultEventBusInstance.raiseEvent(ConfigApiEvents.instance.new onPreServerJoinConfigRecieve(event.player));
		if(FMLCommonHandler.instance().getEffectiveSide().isServer() && event.player instanceof EntityPlayerMP)
			ConfigHandler.forceCompleteSync(event.player.getUniqueID());
		EventApi.defaultEventBusInstance.raiseEvent(ConfigApiEvents.instance.new onPostServerJoinConfigRecieve(event.player));
	}
	
	@SubscribeEvent
	public void onPlayerLeave(PlayerLoggedOutEvent event)
	{
		EventApi.defaultEventBusInstance.raiseEvent(ConfigApiEvents.instance.new onPreServerLeaveConfigRemove(event.player));
		if(FMLCommonHandler.instance().getEffectiveSide().isClient() && event.player instanceof EntityPlayerSP)
			ConfigHandler.cleanUpTempServerFiles();
		EventApi.defaultEventBusInstance.raiseEvent(ConfigApiEvents.instance.new onPostServerLeaveConfigRemove(event.player));
	}
	
	public static void syncValues(List<ConfigFile> configFileList, UUID playerID)
	{
		try
		{
			List<N2Packet> packetList = new ArrayList<N2Packet>();
			ByteBuf buff = Unpooled.directBuffer();
			ByteBufOutputStream stream = new ByteBufOutputStream(buff);
			
			stream.writeUTF(getSignature(3));
			stream.writeUTF(getSignature(0));
						
			for(ConfigFile currentConfigFile : configFileList)
			{
				List<ByteBuf> configBuf = currentConfigFile.writeValuesToBuff(stream);
				
				for(ByteBuf currentBuf : configBuf)
					packetList.add(new N2ConfigApiConfigPacket(currentBuf));
				
				if(configFileList.indexOf(currentConfigFile) == configFileList.size() - 1)
				{
					stream.writeUTF(getSignature(1));
					stream.flush();
					packetList.add(new N2ConfigApiConfigPacket(buff.copy()));
				}
				stream.flush();
			}
			stream.close();
			buff.release();
			
			if(FMLCommonHandler.instance().getEffectiveSide().isServer())
				if(playerID != null)
				{
					List<EntityPlayerMP> playerList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
					for(EntityPlayerMP player : playerList)
						if(player.getUniqueID().equals(playerID))
							N2NetworkEventHandler.sendPacketsToPlayer((ArrayList<N2Packet>)packetList, player);
				}
				else N2NetworkEventHandler.sendPacketsToAllPlayers((ArrayList<N2Packet>) packetList);
			else N2NetworkEventHandler.sendPacketsToServer((ArrayList<N2Packet>) packetList);
		}
		catch(Exception e)
		{
			log.catching(e);
		}
	}
	
	public static void syncFullConfigFile(ConfigFile configFile, UUID playerID)
	{
		try
		{
			List<N2Packet> packetList = new ArrayList<N2Packet>();
			ByteBuf buff = Unpooled.directBuffer();
			ByteBufOutputStream stream = new ByteBufOutputStream(buff.copy());
			ObjectOutputStream objectStream = new ObjectOutputStream(new ByteBufOutputStream(buff));
			
			File file = ConfigHandler.getFileFromConfigFile(configFile).getParentFile();
			String filePath = file.getAbsolutePath().substring(N2ConfigApi.getMCMainDir().getAbsolutePath().length(), file.getAbsolutePath().length());
			String fileID;
			
			if(filePath.equals(N2ConfigApi.getConfigDir().getAbsolutePath().toString()))
				fileID = "2N4$7Det@nT";
			else fileID = N2ConfigApi.getFileIDFromFileDir(file);
			
			stream.writeUTF(getSignature(5));
			stream.writeUTF(getSignature(0));
			stream.writeUTF(fileID);
			stream.writeUTF(filePath);
			stream.writeUTF(configFile.getFileName());
			
			objectStream.writeObject(configFile);
			objectStream.flush();
			
			int packetsCount = (int) Math.ceil((float)buff.writerIndex() / (float) maxByteBuffSize);
			for(int i = 0; i < packetsCount; i++)
			{
				ByteBuf bufff = (buff.copy(i * maxByteBuffSize, ((i + 1) * maxByteBuffSize) > buff.writerIndex() ? buff.writerIndex() - (i * maxByteBuffSize) : maxByteBuffSize));
				
				stream.writeInt(bufff.writerIndex());
				stream.buffer().capacity(stream.buffer().capacity() + bufff.writerIndex());
				stream.buffer().setBytes(stream.buffer().writerIndex(), bufff);
				stream.buffer().writerIndex(stream.buffer().writerIndex() + bufff.capacity());
				stream.writeUTF(i - 1 == packetsCount ? getSignature(1) : getSignature(2));
				stream.flush();
				
				packetList.add(new N2ConfigApiConfigPacket(stream.buffer().copy()));
				stream.buffer().clear();
				stream.writeUTF(getSignature(5));
				stream.writeUTF(configFile.getFileName());
				stream.flush();
			}

//			for(ByteBuf currentbuff : configFile.writeFullConfigFileToBuff(stream))
//				packetList.add(new N2ConfigApiConfigPacket(currentbuff));
			
			objectStream.flush();
			objectStream.close();
			
			stream.flush();
			stream.close();
			buff.release();
			
			if(FMLCommonHandler.instance().getEffectiveSide().isServer())
				if(playerID != null)
				{
					List<EntityPlayerMP> playerList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
					for(EntityPlayerMP player : playerList)
						if(player.getUniqueID().equals(playerID))
							N2NetworkEventHandler.sendPacketsToPlayer((ArrayList<N2Packet>)packetList, player);
				}
				else N2NetworkEventHandler.sendPacketsToAllPlayers(packetList);
			else N2NetworkEventHandler.sendPacketsToServer(packetList);
		}
		catch(Exception e)
		{
			log.catching(e);
		}
	}
	
	public static void syncDeleteConfigFile(List<ConfigFile> configFileList, UUID playerID)
	{
		try
		{
			List<N2Packet> packetList = new ArrayList<N2Packet>();
			ByteBuf buff = Unpooled.directBuffer();
			ByteBufOutputStream stream = new ByteBufOutputStream(buff);
			
			stream.writeUTF(getSignature(7));
			stream.writeUTF(getSignature(0));
			
			for(ConfigFile currentConfig : configFileList)
			{
				stream.writeUTF(currentConfig.getFileName());
				if(stream.buffer().capacity() > maxByteBuffSize && configFileList.indexOf(currentConfig) < configFileList.size() - 3)
				{
					stream.writeUTF(getSignature(2));
					stream.flush();
					packetList.add(new N2ConfigApiConfigPacket(stream.buffer().copy()));
					stream.buffer().clear();
					stream.writeUTF(getSignature(7));
				}
				stream.flush();
			}
			
			stream.writeUTF(getSignature(1));
			stream.flush();
			packetList.add(new N2ConfigApiConfigPacket(stream.buffer().copy()));
			stream.close();
			buff.release();
			
			if(FMLCommonHandler.instance().getEffectiveSide().isServer())
			{
				if(playerID != null)
				{
					List<EntityPlayerMP> playerList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
					for(EntityPlayerMP player : playerList)
						if(player.getUniqueID().equals(playerID))
							N2NetworkEventHandler.sendPacketsToPlayer((ArrayList<N2Packet>)packetList, player);
				}
				else N2NetworkEventHandler.sendPacketsToAllPlayers(packetList);
			}
			else N2NetworkEventHandler.sendPacketsToServer(packetList);
		}
		catch(Exception e)
		{
			log.catching(e);
		}
	}
	
	public static void processPackage(ByteBuf buf, EntityPlayerMP player)
	{
		try
		{
//			buf.readerIndex(1);
			ByteBufInputStream stream = new ByteBufInputStream(buf);
			String ID = stream.readUTF();
			switch(ID)
			{
			case "2n4$7Y!zSc": updateConfigValues(stream, player);
				break;
			case "2n4$7c$cq@e": updateFullConfig(stream, player);
				break;
			case "2n4$7D6Ite": updateDeleteConfig(stream, player);
			}
			stream.close();
		}
		catch(Exception e)
		{
			log.catching(e);
		}
	}
	
	private static void updateConfigValues(ByteBufInputStream stream, EntityPlayerMP player) throws Exception
	{
		ConfigFile configFile = null;
		String currentString = stream.readUTF();

		if (currentString.equals(getSignature(0)))
		{
			currentString = stream.readUTF();
			currentString = stream.readUTF();
		}
		
		ConfigFileLoop:
		while (currentString != null)
		{
			configFile = ConfigHandler.getConfigFileFromName(currentString);
			
			if(configFile == null)
			{
				log.catching(new Exception("Tried to update ConfigFile " + currentString + " which Couldn't be found!, continuing without any progress."));
				return;
			}
			
			if(FMLCommonHandler.instance().getEffectiveSide().isServer() && player != null && !configFile.getPermission(player))
			{
				log.warn("Player " + player.getDisplayName() + " requested ConfigFile " + configFile.getFileName() + " to change with no permission!");
				continue;
			}
			
			EventApi.defaultEventBusInstance.raiseEvent(ConfigApiEvents.instance.new OnPreConfigValuesChanged(configFile));
			
			switch(configFile.createValuesFromByteBuf(stream))
			{
			case 0:
				ConfigHandler.loadAndCheckConfigFile(configFile.getFileName());
			case 1:
				ConfigHandler.loadAndCheckConfigFile(configFile.getFileName());
				break ConfigFileLoop;
			case 2:
				break ConfigFileLoop;
			}
			
			EventApi.defaultEventBusInstance.raiseEvent(ConfigApiEvents.instance.new OnPostConfigValuesChanged(configFile));
			
			currentString = stream.readUTF();
		}
		
	}
	
	private static void updateFullConfig(ByteBufInputStream stream, EntityPlayerMP player) throws IOException, ClassNotFoundException
	{
		String ID = null;
		try
		{
		String currentString = stream.readUTF();
		File parentDirectory = null;
		
		if(currentString.equals(getSignature(0)))
		{
			String fileID = stream.readUTF();
			if(fileID.equals("2N4$7Det@nT"))
				parentDirectory = N2ConfigApi.getConfigDir();
			else
			{
				if((parentDirectory = N2ConfigApi.getFileDirFromID(fileID)) != null)
				{
					if(!(parentDirectory.getAbsolutePath().endsWith(stream.readUTF())))
					{
						log.catching(new Exception("Directory with ID " + fileID + " Doesn't share the same DirectoryPath as the File found with this same ID"));
						return;
					}
				}
				else parentDirectory = N2ConfigApi.registerCustomConfigDirectory(stream.readUTF(), fileID);
			}
			
			
			currentString = ID = stream.readUTF();
			if(player != null)
				if(!ConfigFileMap.containsKey(currentString + player.getUniqueID()))
					ConfigFileMap.put(currentString + player.getUniqueID(), new ArrayList<Byte>());
				else log.warn("Found a second packetsStreak while the first isn't processed yet!");
			else if(!ConfigFileMap.containsKey(currentString))
				ConfigFileMap.put(currentString, new ArrayList<Byte>());
			else log.warn("Found a second packetsStreak while the first isn't processed yet!");
		}
		
		int byteLength = stream.readInt();
		byte[] byteArray = new byte[byteLength];
		List<Byte> byteList = new ArrayList<Byte>();
		stream.read(byteArray, 0, byteLength);
		
		for(byte b : byteArray)
			byteList.add(b);
		
		ConfigFileMap.get(currentString).addAll(byteList);
		byteList = ConfigFileMap.get(currentString);
		currentString = stream.readUTF();
		if(currentString.equals(getSignature(1)))
		{
			ByteBuf buff = Unpooled.directBuffer();
			buff.capacity(byteList.size());
			
			byteArray = new byte[byteList.size()];
			int i = 0;
			for(Byte b : byteList)
				byteArray[i++] = b;
			
			buff.setBytes(0, byteArray);
			ObjectInputStream objectStream = new ObjectInputStream(new ByteBufInputStream(buff));
			
			Object obj = objectStream.readObject();
			ConfigFile cfg = null;
			if(!(obj instanceof ConfigFile))
			{
				log.catching(new Exception("Sended Object wasn't a ConfigFile!"));
				objectStream.close();
				return;
			}
			else
			{
				cfg = (ConfigFile)obj;
				if(player != null)
					if(!cfg.getPermission(player))
					{
						objectStream.close();
						return;
					}
				
				boolean isNewConfig = ConfigHandler.getConfigFileFromName(cfg.getFileName()) == null;
				
				if(isNewConfig)
					EventApi.defaultEventBusInstance.raiseEvent(ConfigApiEvents.instance.new OnPreConfigAdded(cfg));
				
				if(FMLCommonHandler.instance().getEffectiveSide().isClient())
					ConfigHandler.backupConfigFile(cfg.getFileName());
				
				if(ConfigHandler.getConfigFileFromName(cfg.getFileName()) != null)
					ConfigHandler.removeConfigFile(ConfigHandler.getConfigFileFromName(cfg.getFileName()));
				
				if(ConfigHandler.registerConfigFile(cfg, parentDirectory))
					ConfigHandler.loadAndCheckConfigFile(cfg.getFileName());
				else log.catching(new Exception(" Sync Fialed! Couldn't register ConfigFile " + cfg.getFileName()));
				
				if(isNewConfig)
					EventApi.defaultEventBusInstance.raiseEvent(ConfigApiEvents.instance.new OnPostConfigAdded(cfg));
			}
			objectStream.close();
		}
		else return;
		
		//Left here for test purposes. (alternative Object sending!)
//		String currentString = stream.readUTF();
//		File parentDirectory;
//		ConfigFile configFile = null;
//		Config config = null;
//
//		if (currentString == getSignature(0))
//		{
//			String parentDirectoryID = stream.readUTF();
//			parentDirectory = N2ConfigApi.getFileDirFromID(parentDirectoryID);
//			if (parentDirectory == null)
//				parentDirectory = N2ConfigApi.registerCustomConfigDirectory(N2ConfigApi.getMCMainDir() + "/" + stream.readUTF(), parentDirectoryID);
//			else if (!parentDirectory.toURI().getPath().endsWith(N2ConfigApi.getMCMainDir() + "/" + stream.readUTF()))
//			{
//				log.catching(new Exception("The parentDirectoryID is already occupied, while the Directory doesn't point to the same place!"));
//				return;
//			}
//
//			currentString = stream.readUTF();
//			configFile = ConfigHandler.getConfigFileFromName(currentString);
//
//			if (configFile == null)
//			{
//				if (!Minecraft.getMinecraft().isSingleplayer() && !(MinecraftServer.getServer() != null && MinecraftServer.getServer().isServerRunning()))
//				{
//					config = InitConfigObject.getConfigByName(currentString);
//					if (config == null)
//						config = InitConfigObject.addConfigFile(currentString);
//				}
//				configFile = new DefaultConfigFile(currentString, "");
//			}
//			
//			if(player == null || !configFile.getPermission(player))
//			{
//				log.warn("Player " + player.getDisplayName() + " requested ConfigFile " + configFile.getFileName() + " to change with no permission!");
//				return;
//			}
//			configFile.clearAllConfigSections();
//
//			stream.readUTF();
//			stream.readInt();
//			int descriptionLength = stream.readInt();
//			String[] description = new String[descriptionLength];
//
//			for (int i = 0; i < descriptionLength; i++)
//				description[i] = stream.readUTF();
//
//			configFile.setDescription(description);
//			configFile.setCustomSectionStarter(stream.readUTF());
//			configFile.setCustomSectionHeadEnder(stream.readUTF());
//			configFile.setCustomSectionEnder(stream.readUTF());
//
//			stream.readBoolean();
//			stream.readBoolean();
//			stream.readBoolean();
//		}
//		else
//		{
//			if (!Minecraft.getMinecraft().isSingleplayer() && !(MinecraftServer.getServer() != null && MinecraftServer.getServer().isServerRunning()))
//				config = InitConfigObject.getConfigByName(currentString);
//			configFile = ConfigHandler.getConfigFileFromName(currentString);
//		}
//
//		if (!Minecraft.getMinecraft().isSingleplayer() && !(MinecraftServer.getServer() != null && MinecraftServer.getServer().isServerRunning()))
//			configFile.createFullConfigFileFromBuff(stream, config);
//		else configFile.createFullConfigFileFromBuff(stream, null);
		}
		catch(Exception e)
		{
			if(ID != null)
				ConfigFileMap.remove(ID);
			log.catching(e);
		}
	}
	
	private static void updateDeleteConfig(ByteBufInputStream stream, EntityPlayerMP player) throws IOException
	{
		String currentString = stream.readUTF();
		if(currentString.equals(getSignature(0)))
			currentString = stream.readUTF();
		
		while(currentString != null)
		{
			if(currentString.equals(getSignature(1)) || currentString.equals(getSignature(2)))
				return;
			
			ConfigFile configFile = ConfigHandler.getConfigFileFromName(currentString);
			
			if(configFile == null)
				log.catching(new NullPointerException("Couldn't find ConfigFile " + currentString));
			else
			{
				if(player != null)
					if(!configFile.getPermission(player))
					{
						EventApi.defaultEventBusInstance.raiseEvent(ConfigApiEvents.instance.new OnPreConfigRemoved(configFile));
						ConfigHandler.removeConfigurationFile(currentString);
						EventApi.defaultEventBusInstance.raiseEvent(ConfigApiEvents.instance.new OnPostConfigRemoved(configFile));
					}
					else log.warn("Player " + player.getDisplayName() + " requested to delete ConfigFile " + configFile.getFileName() + " with no permission!");
				else
				{
					EventApi.defaultEventBusInstance.raiseEvent(ConfigApiEvents.instance.new OnPreConfigRemoved(configFile));
					ConfigHandler.removeConfigurationFile(currentString);
					EventApi.defaultEventBusInstance.raiseEvent(ConfigApiEvents.instance.new OnPostConfigRemoved(configFile));
				}
			}
			currentString = stream.readUTF();
		}
	}

	/** @deprecated - Not needed anymore, serves as an example Permission Check only. */
	private static boolean getPermission(EntityPlayerMP player)
	{
		return(MinecraftServer.getServer() != null &&
				(Arrays.asList(MinecraftServer.getServer().getConfigurationManager().func_152606_n()).contains(player.getGameProfile().toString())) ||
				MinecraftServer.getServer().getConfigurationManager().getServerInstance().getServerOwner().equalsIgnoreCase(player.getGameProfile().toString())) ||
				Minecraft.getMinecraft().isSingleplayer();
	}
}
