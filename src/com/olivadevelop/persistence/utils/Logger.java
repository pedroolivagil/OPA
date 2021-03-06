package com.olivadevelop.persistence.utils;

public class Logger<T> {

    private Class<T> clase;

    public Logger(Class<T> clase) {
        this.clase = clase;
    }

    public void print(String text) {
        System.out.print(clase.getName());
        System.out.print(": ");
        System.out.print(text);
        System.out.println();
    }

    public void error(Exception e) {
        System.err.print(clase.getName());
        System.err.print(": ");
        System.err.print(e.getMessage());
        System.err.println();
        e.printStackTrace();
    }
}
