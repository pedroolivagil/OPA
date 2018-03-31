package com.olivadevelop.persistence.utils;

/**
 * Copyright OlivaDevelop 2014-2018
 * Created by Oliva on 03/01/2018.
 */

public class KeyValuePair<K, V> {

    private K key;
    private V value;
    private Class type;

    public KeyValuePair() {
    }

    public KeyValuePair(K key, V value) {
        this(key, value, null);
    }

    public KeyValuePair(K key, V value, Class type) {
        this.key = key;
        this.value = value;
        this.type = type;
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

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }
}
