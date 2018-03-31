package com.olivadevelop.persistence.interfaces;

public interface TypeParser<T> {
    T parse(String value);
}