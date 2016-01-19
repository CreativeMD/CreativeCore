package com.n247s.N2ConfigApi.api.core;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.Logger;

import scala.actors.threadpool.Arrays;

import com.n247s.N2ConfigApi.api.N2ConfigApi;
import com.n247s.N2ConfigApi.api.core.InitConfigObjectManager.Config;

/**
 * @author N247S
 * An ingame ConfigFile Manager
 */

public class ConfigSection implements Serializable
{
	
	private static final long serialVersionUID = -864529735488611585L;
	
	private static final Logger log = N2ConfigApi.log;
	/**
	 * Also function as sectionID
	 */
	private String sectionName;
	private String[] description = new String[]{};
	private Object defaultValue;
	private SectionType sectionType;
	private boolean seperateLines;
	private boolean hideOutLines;
	private boolean changedDefaultValue;
	
	/**
	 * Default sectionStarter
	 */
	private String sectionStarter = "-----------------------------------------------------------------------------------------------------";
	/**
	 * Default sectionHeadEnder is the same as SectionStarter
	 */
	private String SectionHeadEnder = "";
	/**
	 * Default sectionEnder
	 */
	private String SectionEnder   = "-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-";
	
	/**
	 * This will create a new ConfigSection. Note, this won't be saved automatically!
	 * @param sectionName - This will be displayed before the Value inside the ConfigurationFile. This will also function as ID!
	 * @param description - This will be displayed above the section Input. Each String represents a new Line.
	 * @param defaultValue - This will be printed if the ConfigurationFile is created/regenerated.
	 * @param sectionType - This will define which type of data you wan't to add.
	 * @param separateLines - If the sectionType is an ArrayType, this will cause each defaultValue to be printed on a separate line.
	 * @param hideOutLines - If the Outlines should be printed.
	 */
	public ConfigSection(String sectionName, String[] description, Object defaultValue, SectionType sectionType, boolean separateLines, boolean hideOutLines)
	{
		this.sectionName = sectionName;
		this.description = description;
		this.sectionType = sectionType;
		this.defaultValue = defaultValue;
		this.seperateLines = separateLines;
		this.hideOutLines = hideOutLines;
		this.changedDefaultValue = true;
	}

	/**
	 * This will create a new ConfigSection. Note, this won't be saved automatically!
	 * @param sectionName - This will be displayed before the Value inside the ConfigurationFile. This will also function as ID!
	 * @param description - This will be displayed above the section Input. Each String represents a new Line.
	 * @param defaultValue - This will be printed if the ConfigurationFile is created/regenerated.
	 * @param sectionType - This will define which type of data you wan't to add.
	 * @param hideOutLines - If the sectionType is an StringArray, this will cause each String to be printed on a separate line.
	 */
	public ConfigSection(String sectionName, String[] description, Object defaultValue, SectionType sectionType, boolean hideOutLines)
	{
		this(sectionName, description, defaultValue, sectionType, false, hideOutLines);
	}
	
	/**
	 * This will create a new ConfigSection. Note, this won't be saved automatically!
	 * @param sectionName - This will be displayed before the Value inside the ConfigurationFile. This will also function as ID!
	 * @param description - This will be displayed above the section Input. Each String represents a new Line.
	 * @param defaultValue - This will be printed if the ConfigurationFile is created/regenerated.
	 * @param sectionType - This will define which type of data you wan't to add.
	 */
	public ConfigSection(String sectionName, String[] description, Object defaultValue, SectionType sectionType)
	{
		this(sectionName, description, defaultValue, sectionType, false, false);
	}
	
	 /**
	  * This will create a new ConfigSection. Note, this won't be saved automatically!
	  * @param sectionName - This will be displayed before the Value inside the ConfigurationFile. This will also function as ID!
	  * @param description - This will be displayed above the section Input. Each String represents a new Line.
	  * @param sectionType - This will define which type of data you wan't to add.
	  */
	public ConfigSection(String sectionName, String[] description, SectionType sectionType)
	{
		this(sectionName, description, null, sectionType, false, false);
	}
	
	public final String getSectionName()
	{
		return this.sectionName;
	}
	
	public final String[] getDescription()
	{
		return this.description;
	}
	
	public final Object getDefaultValue()
	{
		return this.defaultValue;
	}
	
	public final SectionType getSectionType()
	{
		return this.sectionType;
	}
	
	public final boolean getSeparteLines()
	{
		return this.seperateLines;
	}
	
	public final boolean getHideOutLines()
	{
		return this.hideOutLines;
	}
	
	public final boolean getChangedDefaultValue()
	{
		return this.changedDefaultValue;
	}
	
	public final String getStarter()
	{
		return this.sectionStarter;
	}
	
	public final String getHeadEnder()
	{
		if(this.SectionHeadEnder != null)
			if(this.SectionHeadEnder.length() == 0)
				return this.sectionStarter;
		return this.SectionHeadEnder;
	}
	
	public final String getEnder()
	{
		return this.SectionEnder;
	}
	
	/**
	 * this will set a custom SectionStarter, this can be done for each individual section!
	 * Symbols that might cause a conflict: '{ } ; ,'
	 * @param sectionStarter
	 */
	public final ConfigSection setCustomSectionStarter(String sectionStarter)
	{
		this.sectionStarter = sectionStarter;
		return this;
	}
	
	/**
	 * this will set a custom SectionHeadEnder, this can be done for each individual section!
	 * Symbols that might cause a conflict: '{ } ; ,'
	 * @param sectionHeadEnder
	 */
	public final ConfigSection setCustomSectionHeadEnder(String sectionHeadEnder)
	{
		this.SectionHeadEnder = sectionHeadEnder;
		this.hideOutLines = false;
		return this;
	}
	
	/**
	 * this will set a custom SectionStarter, this can be done for each individual section!
	 * Symbols that might cause a conflict: '{ } ; ,'
	 * @param sectionEnder
	 */
	public final ConfigSection setCustomSectionEnder(String sectionEnder)
	{
		this.SectionEnder = sectionEnder;
		return this;
	}
	
	public final ConfigSection setDescription(String[] description)
	{
		this.description = description;
		return this;
	}
	
	public final ConfigSection setDefaultValue(Object defaultvalue)
	{
		this.defaultValue = defaultvalue;
		this.changedDefaultValue = true;
		return this;
	}
	
	public final ConfigSection setChangedDefaultValue(boolean changedDefaultValue)
	{
		this.changedDefaultValue = changedDefaultValue;
		return this;
	}
	
	protected final void writeFullSectionToByteBuf(ByteBufOutputStream stream, Object value) throws IOException
	{
		stream.writeUTF(this.sectionName);
		stream.writeInt(this.sectionType.index);
		stream.writeInt(this.description.length);
		for(String currentLine : this.description)
			stream.writeUTF(currentLine);
		stream.writeUTF(this.sectionStarter);
		stream.writeUTF(this.SectionHeadEnder);
		stream.writeUTF(this.SectionEnder);
		writeValueToByteBuf(stream, value);
		
		if (this.sectionType.isArray)
		{
			if (this.sectionType == SectionType.SectionHead)
				stream.writeInt((int) value);
			else
			{
				List valueList = Arrays.asList((Object[]) value);
				stream.writeInt(valueList.size());
				for (Object currentValue : valueList)
					writeValueToByteBuf(stream, currentValue);
			}
		}
		else writeValueToByteBuf(stream, value);
		
		stream.writeBoolean(this.seperateLines);
		stream.writeBoolean(this.hideOutLines);
		stream.writeBoolean(this.changedDefaultValue);
	}
	
	protected final void WriteSectionValuesToByteBuf(ByteBufOutputStream stream, Object value) throws IOException
	{
		if(this.sectionType.equals(SectionType.Text) || this.sectionType.equals(SectionType.SectionHead))
			return;
		
		stream.writeUTF(this.sectionName);
		stream.writeInt(this.sectionType.index);

		if (this.sectionType.isArray)
		{
			List valueList = Arrays.asList((Object[]) value);
			stream.writeInt(valueList.size());
			for (Object currentValue : valueList)
				writeValueToByteBuf(stream, currentValue);
		}
		else writeValueToByteBuf(stream, value);

		stream.flush();
	}
	
	protected final void createFullSectionFromBuf(ByteBufInputStream stream, Config config) throws IOException
	{
		int descriptionLength = stream.readInt();
		String[] description = new String[descriptionLength];
		for(int i = 0; i < descriptionLength; i++)
			description[i] = stream.readUTF();
		this.description = description;
		this.sectionStarter = stream.readUTF();
		this.SectionHeadEnder = stream.readUTF();
		this.SectionEnder = stream.readUTF();
		if(this.sectionType.isArray)
		{
			int arrayLength = stream.readInt();
			Object[] array = new Object[arrayLength];
			if(this.sectionType != SectionType.SectionHead)
			{
				for(int i = 0; i < arrayLength; i++)
				{
					if(this.sectionType != SectionType.Text)
						array[i] = readValueFromByteBuf(stream);
				}
				this.defaultValue = Arrays.copyOf(array, arrayLength, this.sectionType.getValueClass());
			}
			else this.defaultValue = arrayLength;
		}
		else this.defaultValue = readValueFromByteBuf(stream);
		this.seperateLines = stream.readBoolean();
		this.hideOutLines = stream.readBoolean();
		this.changedDefaultValue = stream.readBoolean();
		
		if(config != null)
			InitConfigObjectManager.addField(config, this.sectionName, this.defaultValue);
	}
	
	protected final void createSectionValuesFromBuf(ByteBufInputStream stream, Config config) throws IOException
	{
		if(this.sectionType != SectionType.values()[stream.readInt()])
		{
			log.catching(new Exception("ConfigSection " + this.sectionName + " has a discrepancy from the recieved information!"));
			return;
		}
		
		if (this.sectionType.isArray)
		{
			int arrayLength = stream.readInt();
			Object[] array = new Object[arrayLength];
			if (this.sectionType != SectionType.SectionHead)
			{
				for (int i = 0; i < arrayLength; i++)
					if (this.sectionType != SectionType.Text)
						array[i] = readValueFromByteBuf(stream);
				this.defaultValue = Arrays.copyOf(array, arrayLength, this.sectionType.getValueClass());
			}
			else this.defaultValue = arrayLength;
		}
		else this.defaultValue = readValueFromByteBuf(stream);
	}
	
	private void writeValueToByteBuf(ByteBufOutputStream stream, Object value)
	{
		try
		{
			switch(this.sectionType)
			{
				case Short:
				case ShortArray: stream.writeShort((int) value);
					break;
				case Integer:
				case IntegerArray: stream.writeInt((int) value);
					break;
				case Long:
				case LongArray: stream.writeLong((long) value); 
					break;
				case Float:
				case FloatArray: stream.writeFloat((float) value);
					break;
				case Double:
				case DoubleArray: stream.writeDouble((double) value);
					break;
				case Boolean:
				case BooleanArray: stream.writeBoolean((boolean) value);
					break;
				case String:
				case StringArray: stream.writeUTF((String) value);
				case SectionHead:
				case Text: return;
			}
		}
		catch(Exception e)
		{
			log.catching(e);
		}
	}
	
	public Object readValueFromByteBuf(ByteBufInputStream stream)
	{
		try
		{
			switch(this.sectionType)
			{
				case Short:
				case ShortArray: return stream.readShort();
				case Integer:
				case IntegerArray: return stream.readInt();
				case Long:
				case LongArray: return stream.readLong(); 
				case Float:
				case FloatArray: return stream.readFloat();
				case Double:
				case DoubleArray: return stream.readDouble();
				case Boolean:
				case BooleanArray: return stream.readBoolean();
				case String:
				case StringArray: return stream.readUTF();
				case SectionHead:
				case Text: return null;
			}
		}
		catch(Exception e)
		{
			log.catching(e);
		}
		return null;
	}

	public enum SectionType
	{
		Short(false, 0, Short.class),
		ShortArray(true, 1, Short[].class),
		Integer(false, 2, Integer.class),
		IntegerArray(true, 3, Integer[].class),
		Long(false, 4, Long.class), 
		LongArray(true, 5, Long[].class),
		Float(false, 6, Float.class),
		FloatArray(true, 7, Float[].class),
		Double(false, 8, Double.class),
		DoubleArray(true, 9, Double[].class),
		Boolean(false, 10, Boolean.class),
		BooleanArray(true, 11, Boolean[].class),
		String(false, 12, String.class),
		StringArray(true, 13, String[].class),
		Text(false, 14, null),
		SectionHead(true, 15, null);
		
		private final boolean isArray;
		private final int index;
		private final Class valueClassType;
		
		private SectionType(final boolean isArray, final int index, final Class valueClassType)
		{
			this.isArray = isArray;
			this.index = index;
			this.valueClassType = valueClassType;
		}
		
		public boolean isArray()
		{
			return this.isArray;
		}
		
		public int getIndex()
		{
			return this.index;
		}
		
		public Class getValueClass()
		{
			return this.valueClassType;
		}
	}
}
