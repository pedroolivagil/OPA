package com.olivadevelop.persistence.entities;

import com.olivadevelop.persistence.annotations.OneToMany;
import com.olivadevelop.persistence.annotations.OneToOne;
import com.olivadevelop.persistence.annotations.Persistence;
import com.olivadevelop.persistence.utils.Logger;
import com.olivadevelop.persistence.utils.OlivaDevelopException;
import com.olivadevelop.persistence.utils.Utils;
import com.olivadevelop.persistence.utils.parser.StringToTypeParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright OlivaDevelop 2014-2018
 * Created by Oliva on 23/01/2018.
 * <p>
 * Para definir una entidad, debe añadirse la anotación Entity y añadir la tabla relacionada y su secuencia
 */
public class BasicEntity implements Serializable {
    Logger<BasicEntity> logger = new Logger<>(BasicEntity.class);

    public static final String ENTITY = "entity";
    public static final String SERIAL_VERSION_UID = "serialVersionUID";
    public static final String CHANGE_FIELD = "$change";
    public static final String PERSISTED = "_persisted";

    private boolean _persisted = false;

    public BasicEntity() {
    }

    public BasicEntity(JSONObject json) throws JSONException {
        toEntity(json);
    }

    public boolean isPersisted() {
        return _persisted;
    }

    /**
     * Convertimos el JSON a Entity.
     *
     * @param json
     * @throws JSONException
     */
    private void toEntity(JSONObject json) throws JSONException {
        // Se transformará cada valor a su tipo y se asignará a su propiedad correspondiente.
        // Cada KEY del JSONObject principal, corresponderá con el nombre de las columna de la BBDD
        // por lo que habrá que buscar la propiedad que coincida por nombre o por JoinColumn de la
        // RelatedEntity. Si la relación es una OneToMany, una lista, se debe crear un objeto, a
        // través del constructor JSONObject, por cada elemento de la lista e insertarlo a dicha
        // lista. Si es una relación oneToOne, se carga el objeto directamente a través del
        // constructor JSONObject de cada Entity.

        // El proceso debe ser completo, es decir, que debe cargar todas las relaciones, no solo un
        // nivel, ejemplo:
        // Objeto 1 -> relación con Objeto 2 -> relación con Objeto 3
        // El Objeto 1 no tiene por que tener relación con el Objeto 3, pero este depende del Objeto 2.
        if (Utils.isNotNull(json)) {
            try {
                StringToTypeParser parser = StringToTypeParser.newBuilder().build();
                // recuperamos las propiedades
                //Field[] fields = getClass().getDeclaredFields();
                List<Field> fields = Utils.getAllFieldsFromEntity(this);
                if (Utils.isNotNull(fields)) {
                    for (Field field : fields) {
                        field.setAccessible(true);
                        String fName = field.getName();
                        if (!Utils.ignoreField(field, this)) {
                            // Obtenemos el nombre de la propiedad que se le pasa por el JSON,
                            // por defecto será el nombre de la propiedad Java (el nombre en una
                            // lista será el nombre de la clase relacionada en plural)
                            Persistence persistence = field.getAnnotation(Persistence.class);
                            if (Utils.isNotNull(persistence) && Utils.isNotNull(persistence.column())) {
                                fName = persistence.column();
                            }
                            // Obtenemos el valor del JSON, que puede ser de cualquier tipo
                            Object value = parser.parse(String.valueOf(json.get(fName)), field.getType());

                            // Si el valor es alguno de los primitivos, es decir, no es una lista ni una entidad
                            if (value instanceof Boolean
                                    || value instanceof Byte
                                    || value instanceof Integer
                                    || value instanceof Long
                                    || value instanceof Float
                                    || value instanceof Double
                                    || value instanceof String) {
                                field.set(this, value);
                            /*} else if (value instanceof byte[]) {
                            // TODO: los byte[] agregados a la bbdd, se almacenan como strings, por el momento. Valorar si es correcto o buscar una forma más adecuada para hacer el cast como una anotación o algo
                                field.set(this, ImagePicasso.StringTobase64(value);*/
                            } else if (value instanceof JSONArray) {
                                // Si es un array, debemos generar un objeto por cada elemento del array y asignarlo a la lista
                                JSONArray jsonArray = (JSONArray) value;
                                OneToMany oneToMany = field.getAnnotation(OneToMany.class);
                                List<BasicEntity> entities = new ArrayList<>();
                                for (int x = 0; x < jsonArray.length(); x++) {
                                    JSONObject jObj = jsonArray.getJSONObject(x);
                                    BasicEntity elem = (BasicEntity) oneToMany.mappingClass().getConstructor(JSONObject.class).newInstance(jObj);
                                    entities.add(elem);
                                }
                                field.set(this, entities);
                            } else if (value instanceof JSONObject) {
                                OneToOne oneToOne = field.getAnnotation(OneToOne.class);
                                BasicEntity elem = (BasicEntity) oneToOne.mappingClass().getConstructor(JSONObject.class).newInstance(value);
                                field.set(this, elem);
                            }
                        }
                        field.setAccessible(false);
                    }
                }
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException | OlivaDevelopException e) {
                logger.error(e);
            }
        }
    }

}
