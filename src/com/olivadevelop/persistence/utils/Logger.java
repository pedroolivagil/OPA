package com.olivadevelop.persistence.utils;

public class Logger<T> {

    private Class<T> clase;

    public Logger(Class<T> clase) {
        this.clase = clase;
    }

    public void print(String text) {
        System.out.print(clase.getSimpleName());
        System.out.print(": ");
        System.out.print(text);
        System.out.println();
    }
}
