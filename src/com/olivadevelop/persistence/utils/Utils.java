package com.olivadevelop.persistence.utils;

import com.olivadevelop.persistence.annotations.Entity;
import com.olivadevelop.persistence.annotations.Id;
import com.olivadevelop.persistence.entities.BasicEntity;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.olivadevelop.persistence.utils.OlivaDevelopException.TypeException.PERSISTENCE;

/**
 * Copyright OlivaDevelop 2014-2018
 * Created by Oliva on 22/02/2018.
 */
public class Utils {

    public static boolean isNull(Object object) {
        return object == null || (object instanceof String && object.toString().trim().equals(""));
    }

    public static boolean isNull(JSONObject object) {
        return object == null || (object.toString().trim().equals("{}"));
    }

    public static boolean isNotNull(JSONObject object) {
        return !isNull(object);
    }

    public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }

    public static boolean isNotEmpty(Collection<?> obj) {
        return !isNull(obj) && !obj.isEmpty();
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

    public static <T extends BasicEntity> List<Field> getAllFieldsFromEntity(T entity) throws OlivaDevelopException {
        List<Field> fields = new ArrayList<>();
        try {
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
        } catch (NullPointerException npe) {
            throw new OlivaDevelopException(PERSISTENCE, "La entidad está nula");
        }
        return fields;
    }

    public static <T extends BasicEntity> FieldData<String, Object> getPkFromEntity(T entity) throws IllegalAccessException, OlivaDevelopException {
        FieldData<String, Object> retorno = null;
        for (Field field : getAllFieldsFromEntity(entity)) {
            field.setAccessible(true);
            Id pk = field.getAnnotation(Id.class);
            if (Utils.isNotNull(pk)) {
                retorno = new FieldData<>(field.getName(), field.get(entity), field.getType(), pk.insertable());
            }
            field.setAccessible(false);
        }
        return retorno;
    }

    public static <T extends BasicEntity> String getSequenceNameFromEntity(T entity) throws OlivaDevelopException {
        String retorno;
        Entity ent = entity.getClass().getAnnotation(Entity.class);
        if (isNotNull(ent)) {
            retorno = ent.sequenceName();
            if (isNull(retorno)) {
                retorno = ent.table();
            }
            if (isNull(retorno)) {
                throw new OlivaDevelopException(PERSISTENCE, "La entidad no tiene una tabla y/o secuencia definidas");
            }
        } else {
            throw new OlivaDevelopException(PERSISTENCE, "La entidad no es una entidad. Debe definirse una entidad usando @Entity y extendiendo de BasicEntity");
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

    public static <T extends BasicEntity> boolean ignoreField(Field field, T entity) throws IllegalAccessException {
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
}
