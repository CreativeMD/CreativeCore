package com.creativemd.creativecore.common.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class HashMapInteger<K> extends HashMap<K, Integer> {
	
	public HashMapInteger() {
		super();
	}
	
	public HashMapInteger(Map<? extends K, ? extends Integer> paramMap)
	{
		super(paramMap);
	}
	
	@Override
	public Integer put(K paramK, Integer paramV) {
		Integer value = get(paramK);
		if(value != null)
			value += paramV;
		return super.put(paramK, value);
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends Integer> paramMap) {
		for (Entry<? extends K, ? extends Integer> entry : paramMap.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}
}
