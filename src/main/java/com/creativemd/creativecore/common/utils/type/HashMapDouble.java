package com.creativemd.creativecore.common.utils.type;

import java.util.HashMap;
import java.util.Map;

public class HashMapDouble<K> extends HashMap<K, Double> {

	public HashMapDouble() {
		super();
	}

	public HashMapDouble(Map<? extends K, ? extends Double> paramMap) {
		super(paramMap);
	}

	@Override
	public Double put(K paramK, Double paramV) {
		Double value = get(paramK);
		if (value != null)
			value += paramV;
		return super.put(paramK, value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends Double> paramMap) {
		for (Entry<? extends K, ? extends Double> entry : paramMap.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}
}
