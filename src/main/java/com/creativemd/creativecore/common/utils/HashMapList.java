package com.creativemd.creativecore.common.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class HashMapList<K, V> {

	private HashMap<K, ArrayList<V>> keys;
	
	public HashMapList()
	{
		this.keys = new HashMap<>();
	}
	
	/*public ArrayList<V> getValues(int index)
	{
		return values.get(index);
	}*/
	
	public ArrayList<V> getValues(K key)
	{
		return keys.get(key);
	}
	
	/*public K getKey(int index)
	{
		return keys.get(index);
	}
	
	public K getLast()
	{
		if(keys.size() > 0)
			return keys.get(keys.size()-1);
		return null;
	}*/
	
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
			return values.remove(value);
		return false;
	}
	
	public boolean removeValue(V value)
	{
		for (ArrayList<V> values : keys.values()) {
			if(values.remove(value))
				return true;
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
	
}
