package com.olivadevelop.persistence.managers;

import com.olivadevelop.persistence.entities.BasicEntity;
import com.olivadevelop.persistence.interfaces.EntityManager;
import com.olivadevelop.persistence.utils.*;
import org.json.JSONObject;

import java.util.List;

/**
 * Copyright OlivaDevelop 2014-2018
 * Created by Oliva on 23/01/2018.
 */
public final class ServiceDAO implements EntityManager {

    Logger<EntityManager> logger = new Logger<>(EntityManager.class);

    private Service service;

    public ServiceDAO() {
        this.service = new Service();
    }


    @Override
    public <T extends BasicEntity> T singleQuery(String query, Class<T> entity) {
        JSONPersistence<T> jsonPersistence = new JSONPersistence<>(entity);
        JSONObject retorno = service.execute(query);
        return jsonPersistence.getEntity(retorno);
    }

    @Override
    public <T extends BasicEntity> List<T> createQuery(String query, Class<T> entity) {
        JSONPersistence<T> jsonPersistence = new JSONPersistence<>(entity);
        JSONObject retorno = service.execute(query);
        return jsonPersistence.getListEntities(retorno);
    }

    @Override
    public <T extends BasicEntity> T find(Class<T> entity, Object id) {
        JSONObject retorno = null;
        JSONPersistence<T> jsonPersistence = new JSONPersistence<>(entity);
        try {
            QueryBuilder.Query query = new QueryBuilder.Query();
            KeyValuePair<String, Object> field = Utils.getPkFromEntity(entity);
            query.find();
            query.from(entity);
            query.where(field.getKey() + " = " + id);
            query.orderBy(field.getKey(), QueryBuilder.Query.ORDER_BY.ASC);
            retorno = service.execute(query.toString());
        } catch (OlivaDevelopException | IllegalAccessException e) {
            logger.error(e);
        }
        return jsonPersistence.getEntity(retorno);
    }

    @Override
    public <T extends BasicEntity> T persist(T entity) {
        service.add(entity, Service.MODE.PERSIST);
        return entity;
    }

    @Override
    public <T extends BasicEntity> T merge(T entity) {
        service.add(entity, Service.MODE.MERGE);
        return entity;
    }

    @Override
    public <T extends BasicEntity> T remove(T entity) {
        service.add(entity, Service.MODE.REMOVE);
        return entity;
    }

    /**
     * Ejecuta las operaciones en la BBDD.
     * <p>
     * Inicia una transacción e intenta persistir, actualizar o borrar las entidades que tenga el entity manager.
     * Es necesario llamar a este método para ejecutar los cambios en BBDD
     */
    @Override
    public void flush() {
        service.execute();
    }

}
