package com.olivadevelop.persistence.utils;

import com.olivadevelop.persistence.annotations.*;
import com.olivadevelop.persistence.entities.BasicEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static com.olivadevelop.persistence.utils.OlivaDevelopException.TypeException.UNIQUE_NOT_NULL;


/**
 * Copyright OlivaDevelop 2014-2018
 * Created by Oliva on 19/02/2018.
 */
public class JSONPersistence<T extends BasicEntity> {
    private static final String ENTITY = "entity";

    public enum REQUEST {
        CODE_200(200), CODE_404(404);
        int code;

        REQUEST(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    private Class<T> entityClass;

    //TODO: para el test lo dejamos publico
    public JSONPersistence(Class<T> entity) {
        this.entityClass = entity;
    }

    /**
     * Obtiene el nombre de persistencia asignado a una propiedad de una entidad
     *
     * @param fieldName
     * @return
     */
    public String getPersistenceFieldName(String fieldName) {
        String retorno = fieldName;
        try {
            if (Utils.isNotNull(this.entityClass)) {
                Field field = this.entityClass.getDeclaredField(fieldName);
                Persistence persistence = field.getAnnotation(Persistence.class);
                if (Utils.isNotNull(persistence)) {
                    String fn = persistence.column();
                    if (Utils.isNotNull(fn)) {
                        retorno = fn;
                    } else {
                        if (Utils.isNotNull(field.getName())) {
                            retorno = field.getName();
                        }
                    }
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return retorno;
    }

    /**
     * Persistimos o actualizamos una sola entidad, sin importar las entidades relacionadas, solo
     * guardaremos sus respectivos ID para relacionarlos
     *
     * @param entity
     * @return
     * @throws OlivaDevelopException
     */
    public JSONObject persistenceJSONObject(T entity) throws OlivaDevelopException {
        JSONObject retorno = new JSONObject();
        try {
            List<Field> fields = Utils.getAllFieldsFromEntity(entity);
            if (Utils.isNotNull(fields) && fields.size() > 0) {
                Entity entityAn = entity.getClass().getAnnotation(Entity.class);
                Persistence persistenceClass = entity.getClass().getAnnotation(Persistence.class);
                String className = entity.getClass().getSimpleName();
                if (Utils.isNotNull(persistenceClass) && Utils.isNotNull(entityAn.table())) {
                    className = entityAn.table();
                }
                // Inicio de objeto JSON
                // le pasamos la entidad como parámetro de JSON
                retorno.put(ENTITY, className);
                retorno.put(className, transformToJSON(entity));
            }
        } catch (IllegalAccessException | JSONException e) {
            e.printStackTrace();
        }
        return retorno;
    }

    /**
     * Complementa el método persistenceJSONObject para mejor visibilidad. Transforma la entidad a
     * JSON
     *
     * @param entity
     * @return
     * @throws IllegalAccessException
     * @throws JSONException
     * @throws OlivaDevelopException
     */
    private JSONObject transformToJSON(T entity) throws IllegalAccessException, JSONException, OlivaDevelopException {
        JSONObject retorno = new JSONObject();
        for (Field field : Utils.getAllFieldsFromEntity(entity)) {
            field.setAccessible(true);
            KeyValuePair<String, Object> fieldValue = getValueFromField(field, entity);
            if (fieldValue != null) {
                retorno.put(fieldValue.getKey(), fieldValue.getValue());
            }
            field.setAccessible(false);
        }
        return retorno;
    }

    /**
     * Complementa el método persistenceJSONObject para mejor visibilidad. Obtiene el valor de una
     * propiedad de la entidad y, en caso de que sea una entidad relacionada enlazamos el id
     *
     * @param field
     * @param entityMaster
     * @return
     * @throws IllegalAccessException
     * @throws OlivaDevelopException
     */
    private KeyValuePair<String, Object> getValueFromField(Field field, T entityMaster) throws IllegalAccessException, OlivaDevelopException {
        KeyValuePair<String, Object> retorno = null;
        if (!Utils.ignoreField(field, entityMaster)) {
            // comprobamos si es único y si está vacío
            Object value = field.get(entityMaster);
            Persistence persistenceField = field.getAnnotation(Persistence.class);
            if (Utils.isNotNull(persistenceField)) {
                if (Utils.isNotNull(persistenceField.unique())) {
                    if (Utils.isNull(value)) {
                        // Si es único, no puede ser nulo, cancelamos la operación
                        throw new OlivaDevelopException(UNIQUE_NOT_NULL);
                    }
                }
            }
            //Buscamos la relación
            RelatedEntity relatedEntity = field.getAnnotation(RelatedEntity.class);
            if (Utils.isNotNull(relatedEntity)) {
                // Ahora debemos obtener el tipo de relación.
                OneToOne oneToOne = field.getAnnotation(OneToOne.class);
                OneToMany oneToMany = field.getAnnotation(OneToMany.class);
                ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
                ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
                if (Utils.isNotNull(oneToOne)) {
                    // Si es una relación uno a uno o uno a muchos, directamente podemos transformar en una Entity para obtener su identificador
                    BasicEntity entity = (BasicEntity) value;
                    if (Utils.isNotNull(entity)) {
                        retorno = new KeyValuePair<>();
                        if (Utils.isNotNull(relatedEntity.joinColumn())) {
                            retorno.setKey(relatedEntity.joinColumn());
                        } else {
                            retorno.setKey(field.getName());
                        }
                        for (Field fieldRelated : entity.getClass().getDeclaredFields()) {
                            fieldRelated.setAccessible(true);
                            if (!Utils.ignoreField(fieldRelated, entity)) {
                                Id pk = fieldRelated.getAnnotation(Id.class);
                                if (Utils.isNotNull(pk)) {
                                    retorno.setValue(fieldRelated.get(entity));
                                    break;
                                }
                            }
                            fieldRelated.setAccessible(false);
                        }
                    }
           /* } else if (Utils.isNotNull(oneToMany)) {
                // no hacemos nada, es decir, se omite la relación puesto que es la entidad relacionada quien tendrá el identificador del padre
            } else if (Utils.isNotNull(manyToOne)) {
                // no hacemos nada, es decir, se omite la relación puesto que es la entidad relacionada (padre) quien tendrá el identificador del hijo
            } else if (Utils.isNotNull(manyToMany)) {
                // Si es una relación muchos a uno o muchos a muchos, transformarmamos en una List<Entity> para asignar el identificador del padre a cada entidad.
                List<Entity> lista = (List<Entity>) value;
                for (Entity ent : lista) {
                    BasicEntity entity = (BasicEntity) ent;
                }*/
                }
            } else {
                // Si no hay relatedEntity, es un valor primitivo, por lo que lo añadimos tal cual
                retorno = new KeyValuePair<>();
                Persistence persistence = field.getAnnotation(Persistence.class);
                if (Utils.isNotNull(persistence)) {
                    retorno.setKey(persistence.column());
                } else {
                    retorno.setKey(field.getName());
                }
                retorno.setValue(value);
            }
        }
        return retorno;
    }

    /**
     * Transforma un JSON a una entidad
     *
     * @param json
     * @return
     * @throws JSONException
     */
    public T getEntity(JSONObject json) throws JSONException {
        return parseJsonToEntity(json, this.entityClass);
    }

    /**
     * Transforma el resultado JSON a la entidad correspondiente.
     *
     * @param json   result from service
     * @param entity entity class to parse it
     * @return entity object
     */
    private T parseJsonToEntity(JSONObject json, Class<T> entity) throws JSONException {
        T retorno = null;
        if (Utils.isNotNull(json)) {
            int code = json.getInt("result");
            if (REQUEST.CODE_200.getCode() == code) {
                JSONArray array = json.getJSONArray("entities");
                if (Utils.isNotNull(array)) {
                    retorno = constructObject(array, entity, 0);
                }
            }
        }
        return retorno;
    }

    /**
     * Transforma un JSON a una lista de entidades
     *
     * @param json
     * @return
     * @throws JSONException
     */
    public List<T> getListEntities(JSONObject json) throws JSONException {
        return parseJsonToListEntity(json, this.entityClass);
    }

    /**
     * Transforma un resultado jsonObject a una lista de entidades pasada por parámetro
     *
     * @param json
     * @param entity
     * @return
     * @throws JSONException
     */
    private List<T> parseJsonToListEntity(JSONObject json, Class<T> entity) throws JSONException {
        List<T> retorno = new ArrayList<>();
        if (Utils.isNotNull(json)) {
            int code = json.getInt("result");
            if (REQUEST.CODE_200.getCode() == code) {
                JSONArray array = json.getJSONArray("entities");
                if (Utils.isNotNull(array)) {
                    for (int x = 0; x < array.length(); x++) {
                        retorno.add(constructObject(array, entity, x));
                    }
                }
            }
        }
        return retorno;
    }

    private T constructObject(JSONArray array, Class<T> entity, int pos) {
        T retorno = null;
        if (Utils.isNotNull(array)) {
            try {
                retorno = entity.getConstructor(JSONObject.class).newInstance(array.getJSONObject(pos));
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | JSONException e) {
                e.printStackTrace();
            }
        }
        return retorno;
    }
}
