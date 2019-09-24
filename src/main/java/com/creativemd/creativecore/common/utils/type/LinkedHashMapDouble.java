package com.creativemd.creativecore.common.utils.type;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class LinkedHashMapDouble<K> extends LinkedHashMap<K, Double> {
	
	public LinkedHashMapDouble() {
		super();
	}
	
	public LinkedHashMapDouble(Map<? extends K, ? extends Double> paramMap) {
		super(paramMap);
	}
	
	@Override
	public Double put(K paramK, Double paramV) {
		Double value = get(paramK);
		if (value != null)
			value += paramV;
		else
			value = paramV;
		return super.put(paramK, value);
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends Double> paramMap) {
		for (Entry<? extends K, ? extends Double> entry : paramMap.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}
}
