package com.creativemd.creativecore.common.utils.type;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class LinkedHashMapInteger<K> extends LinkedHashMap<K, Integer> {
	
	public LinkedHashMapInteger() {
		super();
	}
	
	public LinkedHashMapInteger(Map<? extends K, ? extends Integer> paramMap) {
		super(paramMap);
	}
	
	@Override
	public Integer put(K paramK, Integer paramV) {
		Integer value = get(paramK);
		if (value != null)
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
