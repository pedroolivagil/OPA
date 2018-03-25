package com.olivadevelop.persistence.utils;

import com.olivadevelop.persistence.annotations.Entity;
import com.olivadevelop.persistence.annotations.Id;
import com.olivadevelop.persistence.entities.BasicEntity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.olivadevelop.persistence.utils.OlivaDevelopException.TypeException.*;

/**
 * Copyright OlivaDevelop 2014-2018
 * Created by Oliva on 22/02/2018.
 */
public class Utils {

    public static boolean isNull(Object object) {
        return object == null || (object instanceof String && object.toString().trim().equals(""));
    }

    public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }

    public static boolean isNotEmpty(Collection<?> obj) {
        return !isNull(obj) && !obj.isEmpty();
    }


    public static <T> List<Field> getAllFieldsFromEntity(T entity) {
        List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(entity.getClass().getDeclaredFields()));
        Class<?> e = entity.getClass().getSuperclass();
        boolean next = true;
        do {
            Entity ent = e.getAnnotation(Entity.class);
            if (isNotNull(ent)) {
                fields.addAll(Arrays.asList(e.getDeclaredFields()));
                e = e.getSuperclass();
            } else {
                next = false;
            }
        } while (next);
        return fields;
    }

    public static <T> KeyValuePair<String, Object> getPkFromEntity(T entity) throws IllegalAccessException {
        KeyValuePair<String, Object> retorno = null;
        for (Field field : getAllFieldsFromEntity(entity)) {
            Id pk = field.getAnnotation(Id.class);
            if (Utils.isNotNull(pk)) {
                retorno = new KeyValuePair<>(field.getName(), field.get(entity));
            }
        }
        return retorno;
    }

    /**
     * Devuelve true si el nombre o el valor de la propiedad coincide con los que queremos omitir
     *
     * @param field
     * @param entity
     * @return
     * @throws IllegalAccessException
     */
    public static boolean ignoreField(Field field, BasicEntity entity) throws IllegalAccessException {
        boolean retorno = false;
        List<Boolean> list = new ArrayList<>();
        list.add(BasicEntity.CHANGE_FIELD.equals(field.getName()));
        list.add(BasicEntity.CHANGE_FIELD.equals(field.get(entity)));
        list.add(BasicEntity.SERIAL_VERSION_UID.equals(field.getName()));
        list.add(BasicEntity.SERIAL_VERSION_UID.equals(field.get(entity)));
        list.add(BasicEntity.ENTITY.equals(field.getName()));
        list.add(BasicEntity.ENTITY.equals(field.get(entity)));
        list.add(BasicEntity.PERSISTED.equals(field.get(entity)));
        for (Boolean bool : list) {
            if (bool) {
                retorno = true;
                break;
            }
        }
        return retorno;
    }

    public static boolean isNumeric(Object o) {
        return o != null && String.valueOf(o).matches("[-+]?\\d*\\.?\\d+");
    }

    public static boolean isBoolean(Object o) {
        return o != null && (o.equals(true) || o.equals(false));
    }

    public static String parseBoolean(Object o) {
        String retorno = null;
        if (o != null) {
            if (o.equals(true)) {
                retorno = "1";
            } else if (o.equals(false)) {
                retorno = "0";
            }
        }
        return retorno;
    }
}
