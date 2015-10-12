package com.creativemd.creativecore.common.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class HashMapList<K, V> {

	private ArrayList<K> keys;
	private ArrayList<ArrayList<V>> values;
	
	public HashMapList()
	{
		this.keys = new ArrayList<K>();
		this.values = new ArrayList<ArrayList<V>>();
	}
	
	public ArrayList<V> getValues(int index)
	{
		return values.get(index);
	}
	
	public ArrayList<V> getValues(K key)
	{
		int index = indexOfKey(key);
		if(index != -1)
			return values.get(index);
		return null;
	}
	
	public K getKey(int index)
	{
		return keys.get(index);
	}
	
	public K getLast()
	{
		if(keys.size() > 0)
			return keys.get(keys.size()-1);
		return null;
	}
	
	public K getKey(V value)
	{
		for (int i = 0; i < values.size(); i++) {
			int index = values.get(i).indexOf(value);
			if(index != -1)
				return getKey(index);
		}
		return null;
	}
	
	
	public ArrayList<K> getKeys()
	{
		return keys;
	}
	
	public void add(K key, V[] values)
	{
		add(key, new ArrayList<V>(Arrays.asList(values)));
	}
	
	public void add(K key, ArrayList<V> values)
	{
		int index = indexOfKey(key);
		if(index != -1)
			this.values.get(index).addAll(values);
		else
		{
			keys.add(key);
			ArrayList<V> newList = new ArrayList<V>();
			newList.addAll(values);
			this.values.add(newList);
		}
	}
	
	public void add(K key, V value)
	{
		int index = indexOfKey(key);
		if(index != -1)
			values.get(index).add(value);
		else
		{
			keys.add(key);
			ArrayList<V> newList = new ArrayList<V>();
			newList.add(value);
			values.add(newList);
		}
	}
	
	public boolean removeKey(int key)
	{
		if(key != -1)
		{
			keys.remove(key);
			values.remove(key);
			return true;
		}
		return false;
	}
	
	public boolean removeKey(K key)
	{
		return removeKey(indexOfKey(key));
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
		for (int i = 0; i < values.size(); i++) {
			if(values.get(i).remove(value))
				return true;
		}
		return false;
	}
	
	public int indexOfKey(K key)
	{
		return keys.indexOf(key);
	}
	
	public int size()
	{
		return keys.size();
	}
	
}
