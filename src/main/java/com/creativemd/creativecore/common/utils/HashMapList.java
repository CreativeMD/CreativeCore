package com.creativemd.creativecore.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class HashMapList<K, V> {
	
	private HashMap<K, ArrayList<V>> keys;
	
	public HashMapList()
	{
		this.keys = new HashMap<>();
	}
	
	public HashMapList(HashMapList<K, V> object)
	{
		this();
		for (Iterator<Entry<K, ArrayList<V>>> iterator = object.entrySet().iterator(); iterator.hasNext();) {
			Entry<K, ArrayList<V>> entry = iterator.next();
			keys.put(entry.getKey(), new ArrayList<>(entry.getValue()));
		}
	}
	
	public ArrayList<V> getValues(K key)
	{
		return keys.get(key);
	}
	
	public K getKey(V search)
	{
		for (Entry<K, ArrayList<V>> entry : keys.entrySet()) {
			if(entry.getValue().contains(search))
				return entry.getKey();
		}
		return null;
	}
	
	
	public Set<K> getKeys()
	{
		return keys.keySet();
	}
	
	public Collection<ArrayList<V>> getValues()
	{
		return keys.values();
	}
	
	public Set<Entry<K, ArrayList<V>>> entrySet()
	{
		return keys.entrySet();
	}
	
	public boolean contains(V value)
	{
		for (Iterator<ArrayList<V>> iterator = getValues().iterator(); iterator.hasNext();) {
			ArrayList<V> type = iterator.next();
			if(type.contains(value))
				return true;
		}
		return false;
	}
	
	public boolean contains(K key, V value)
	{
		ArrayList<V> list = getValues(key);
		if(list != null)
			return list.contains(value);
		return false;
	}
	
	public void add(K key, V[] values)
	{
		add(key, new ArrayList<V>(Arrays.asList(values)));
	}
	
	public void add(K key, ArrayList<V> values)
	{
		ArrayList<V> list = getValues(key);
		if(list == null)
			keys.put(key, new ArrayList<>(values));
		else
			list.addAll(values);
	}
	
	public void add(K key, V value)
	{
		ArrayList<V> list = getValues(key);
		if(list == null)
		{
			list = new ArrayList<>();
			list.add(value);
			keys.put(key, list);
		}
		else
			list.add(value);
	}
	
	public boolean removeKey(K key)
	{
		return keys.remove(key) != null;
	}
	
	public boolean removeValue(K key, V value)
	{
		ArrayList<V> values = getValues(key);
		if(values != null)
			if(values.remove(value))
			{
				if(values.isEmpty())
					removeKey(key);
				return true;
			}
		
		return false;
	}
	
	public boolean removeValue(V value)
	{
		for (Iterator<ArrayList<V>> iterator = keys.values().iterator(); iterator.hasNext();) {
			ArrayList<V> values = iterator.next();
			if(values.remove(value))
			{
				if(values.isEmpty())
					iterator.remove();
				return true;
			}
		}
		return false;
	}
	
	public int sizeOfValues()
	{
		int size = 0;
		for (ArrayList<V> values : keys.values()) {
			size += values.size();
		}
		return size;
	}
	
	public int size()
	{
		return keys.size();
	}
	
	public void clear()
	{
		keys.clear();
	}
	
	@Override
	public String toString()
	{
		return keys.toString();
	}

	public boolean isEmpty() {
		return sizeOfValues() == 0;
	}

	public V getFirst() {
		if(size() > 0)
		{
			for (Iterator<ArrayList<V>> iterator = getValues().iterator(); iterator.hasNext();) {
				ArrayList<V> list = iterator.next();
				if(list.size() > 0)
					return list.get(0);
			}
		}
		return null;
	}
	
	public Iterator<V> iterator()
	{
		return new Iterator<V>() {
			
			int index = 0;
			
			Iterator<ArrayList<V>> iterator = getValues().iterator();
			
			ArrayList<V> currentList;
			
			@Override
			public boolean hasNext() {
				while(currentList == null || currentList.size() <= index)
				{
					if(iterator.hasNext())
					{
						currentList = iterator.next();
						index = 0;
					}
					else
						return false;
				}
				
				return true;
			}

			@Override
			public V next() {
				V value = currentList.get(index);
				index++;
				return value;
			}
			
			@Override
			public void remove() {
				currentList.remove(index-1);
			}
		};
	}
	
}
