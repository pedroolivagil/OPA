package com.olivadevelop.persistence.managers;

import com.olivadevelop.persistence.entities.BasicEntity;
import com.olivadevelop.persistence.utils.*;
import com.olivadevelop.persistence.utils.parser.StringToTypeParser;
import com.sun.istack.internal.NotNull;
import org.json.JSONObject;

import javax.rmi.CORBA.Util;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.olivadevelop.persistence.utils.OlivaDevelopException.TypeException.PERSISTENCE;

/**
 * Copyright OlivaDevelop 2014-2018
 * Created by Oliva on 23/01/2018.
 * <p>
 * Este servicio se comunica con el servidor simulando un envio de un formulario.
 * <p>
 * El servidor puede ser cualquier tipo de servidor que, pueda recibir datos POST, y devolver un objeto JSON,
 * independientemente de si usamos mysql, db2, ...
 * <p>
 * Será el servicio del servidor quíen se encargará de traducir la query.
 * <p>
 * OPQL - OlivaDevelop Persistence Query Language
 * Un sistema de consultas específico para OPA.
 */
final class Service {

    enum MODE {
        PERSIST, MERGE, REMOVE
    }

    private Logger<Service> logger = new Logger<>(Service.class);
    private RestService restService = new RestService();
    private List<KeyValuePair<BasicEntity, MODE>> entities = new ArrayList<>();

    /**
     * Recive un string con la query y devuelve un JSON con los resultados.
     *
     * @param query
     * @return
     */
    JSONObject execute(String query) {
        JSONObject retorno = null;
        try {
            retorno = restService.run(query);
        } catch (OlivaDevelopException e) {
            logger.error(e);
        }
        return retorno;
    }

    /**
     * Ejecuta las transacciones de Persist, merge y remove. Debe ser el servidor el que gestione las
     */
    void execute() {
        try {
            List<String> queries = new ArrayList<>();
            for (KeyValuePair<BasicEntity, MODE> entity : entities) {
                FieldData<String, Object> pk = Utils.getPkFromEntity(entity.getKey());
                switch (entity.getValue()) {
                    case MERGE:
                        QueryBuilder.Update update = new QueryBuilder.Update();
                        update.from(entity.getKey().getClass());
                        update.values(entity.getKey());
                        update.where(pk.getKey().concat(" = ").concat(pk.getValueAsString()));
                        queries.add(update.toString());
                        break;
                    case REMOVE:
                        QueryBuilder.Delete remove = new QueryBuilder.Delete();
                        remove.from(entity.getKey().getClass());
                        remove.where(pk.getKey().concat(" = ").concat(pk.getValueAsString()));
                        queries.add(remove.toString());
                        break;
                    case PERSIST:
                        QueryBuilder.Insert persist = new QueryBuilder.Insert();
                        persist.from(entity.getKey().getClass());
                        persist.values(entity.getKey());
                        queries.add(persist.toString());
                        break;
                }
            }
            restService.run(queries);
            // Después de ejecutar las transacciones, limpiamos la lista de entidades
            entities.clear();
        } catch (OlivaDevelopException | IllegalAccessException e) {
            logger.error(e);
        }
    }

    <T extends BasicEntity> void add(T entity, MODE mode) throws OlivaDevelopException {
        if (MODE.PERSIST.equals(mode)) {
            try {
                entity = nextVal(entity);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                logger.error(e);
                throw new OlivaDevelopException(PERSISTENCE, "No se pudo generar la PK para la entidad " + entity.getClass().getSimpleName());
            }
        }
        entities.add(new KeyValuePair<>(entity, mode));
    }

    private <T extends BasicEntity> T nextVal(T entity) throws OlivaDevelopException, IllegalAccessException, NoSuchFieldException {
        FieldData<String, Object> pk = Utils.getPkFromEntity(entity);
        if (pk.isInsertable()) {
            StringToTypeParser parser = StringToTypeParser.newBuilder().build();
            StringBuilder query = new StringBuilder();
            query.append("SELECT nextval('");
            query.append(Utils.getSequenceNameFromEntity(entity));
            query.append("') as sequence;");
            JSONObject result = restService.sequence(query.toString());
            Object id = parser.parse(result.getString("sequence"), pk.getType());
            if (Utils.isNotNull(pk)) {
                Field field = entity.getClass().getDeclaredField(pk.getKey());
                if (Utils.isNotNull(field)) {
                    field.setAccessible(true);
                    field.set(entity, id);
                    field.setAccessible(false);
                }
            }
        }
        return entity;
    }
}
