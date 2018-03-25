package com.olivadevelop.persistence.managers;

import com.olivadevelop.persistence.entities.BasicEntity;
import com.olivadevelop.persistence.interfaces.EntityManager;
import com.olivadevelop.persistence.utils.*;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    Logger<Service> logger = new Logger<>(Service.class);
    private List<KeyValuePair<BasicEntity, MODE>> entities = new ArrayList<>();

    /**
     * Recive un string con la query y devuelve un JSON con los resultados.
     *
     * @param query
     * @return
     */
    JSONObject execute(String query) {
        return null;
    }

    /**
     * Ejecuta las transacciones de Persist, merge y remove. Debe ser el servidor el que gestione las
     */
    void execute() {
        try {
            List<String> persistQueries = new ArrayList<>();
            List<String> mergeQueries = new ArrayList<>();
            List<String> removeQueries = new ArrayList<>();
            for (KeyValuePair<BasicEntity, MODE> entity : entities) {
                switch (entity.getValue()) {
                    case MERGE:
                        KeyValuePair<String, Object> pk = Utils.getPkFromEntity(entity.getKey());
                        QueryBuilder.Update update = new QueryBuilder.Update();
                        update.from(entity.getKey().getClass());
                        update.values(entity.getKey());
                        update.where(pk.getKey().concat(" = ").concat(pk.getValueAsString()));
                        mergeQueries.add(update.toString());
                        break;
                    case REMOVE:
                        break;
                    case PERSIST:
                        break;
                }
            }
            // Después de ejecutar las transacciones, limpiamos la lista de entidades
            entities.clear();
        } catch (OlivaDevelopException | IllegalAccessException e) {
            logger.error(e);
        }
    }

    <T extends BasicEntity> void add(T entity, MODE mode) {
        entities.add(new KeyValuePair<>(entity, mode));
    }
}
