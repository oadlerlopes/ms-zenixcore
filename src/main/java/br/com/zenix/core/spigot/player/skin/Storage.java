package br.com.zenix.core.spigot.player.skin;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Storage<K, V, SV> {
	private List<K> keys;
	private List<V> values;
	private List<SV> subValues;

	public Storage() {
		keys = new ArrayList<K>();
		values = new ArrayList<V>();
		subValues = new ArrayList<SV>();
	}

	public void put(K key, V value, SV subValue) {
		keys.add(key);
		values.add(value);
		subValues.add(subValue);
	}

	public void remove(K key, V value, SV subValue) {
		keys.remove(key);
		values.remove(value);
		subValues.remove(subValue);
	}

	public V getValue(K key) {
		return (V) values.get(keys.indexOf(key));
	}

	public K searchKeyFromValue(V value) {
		return (K) keys.get(values.indexOf(value));
	}

	public SV getSubValue(K key) {
		return (SV) subValues.get(keys.indexOf(key));
	}

	public boolean containsKey(K key) {
		return keys.contains(key);
	}

	public boolean containsValue(V value) {
		return values.contains(value);
	}

	public boolean containsSubValue(SV subValue) {
		return subValues.contains(subValue);
	}

	public Object[] keyToArray() {
		return keys.toArray();
	}

	public Object[] valueToArray() {
		return values.toArray();
	}

	public Object[] subValueToArray() {
		return subValues.toArray();
	}

	public void clear() {
		keys.clear();
		values.clear();
		subValues.clear();
	}
}
