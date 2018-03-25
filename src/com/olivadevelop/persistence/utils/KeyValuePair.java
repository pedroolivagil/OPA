package com.olivadevelop.persistence.utils;

/**
 * Copyright OlivaDevelop 2014-2018
 * Created by Oliva on 03/01/2018.
 */

public class KeyValuePair<K, V> {

    private K key;
    private V value;

    public KeyValuePair() {
    }

    public KeyValuePair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public String getValueAsString() {
        return String.valueOf(value);
    }
}
