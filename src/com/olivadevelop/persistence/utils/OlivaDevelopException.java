package com.olivadevelop.persistence.utils;

/**
 * Copyright OlivaDevelop 2014-2018
 * Created by Oliva on 19/02/2018.
 */

public class OlivaDevelopException extends Exception {
    public enum TypeException {
        PERSISTENCE, RELATIONSHIP, UNIQUE_NOT_NULL
    }

    private TypeException typeException;
    private String errorText;

    public OlivaDevelopException(TypeException typeException) {
        this.typeException = typeException;
        if (TypeException.UNIQUE_NOT_NULL.equals(typeException)) {
            errorText = "Hay un campo unico que no puede ser nulo.";
        }
    }

    public OlivaDevelopException(TypeException typeException, String message) {
        super(message);
        this.typeException = typeException;
    }

    public OlivaDevelopException(String message, TypeException typeException) {
        super(message);
        this.typeException = typeException;
    }

    public OlivaDevelopException(String message, Throwable cause, TypeException typeException) {
        super(message, cause);
        this.typeException = typeException;
    }

    public OlivaDevelopException(Throwable cause, TypeException typeException) {
        super(cause);
        this.typeException = typeException;
    }

    @Override
    public String getMessage() {
        return getErrorText().concat(" ").concat(String.valueOf(super.getMessage()));
    }

    public String getErrorText() {
        if (Utils.isNull(errorText)) {
            errorText = "";
        }
        return errorText;
    }

    public String getTypeException() {
        return typeException.toString();
    }
}
