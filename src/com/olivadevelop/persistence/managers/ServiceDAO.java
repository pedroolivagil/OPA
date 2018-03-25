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

    private Service service;

    public ServiceDAO() {
        this.service = new Service();
    }

    @Override
    public <T extends BasicEntity> T query(Class<T> entity, String query) {
        JSONObject retorno = service.execute(query);
        JSONPersistence<T> jsonPersistence = new JSONPersistence<>(entity);
        return jsonPersistence.getEntity(retorno);
    }

    @Override
    public <T extends BasicEntity> List<T> query(String query, Class<T> entity) {
        JSONObject retorno = service.execute(query);
        JSONPersistence<T> jsonPersistence = new JSONPersistence<>(entity);
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
            query.from(entity.newInstance());
            query.where(field.getKey() + " = " + id);
            query.orderBy(field.getKey(), QueryBuilder.Query.ORDER_BY.ASC);
            retorno = service.execute(query.toString());
        } catch (OlivaDevelopException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return jsonPersistence.getEntity(retorno);
    }

    @Override
    public <T extends BasicEntity> T persist(T entity) {
        return null;
    }

    @Override
    public <T extends BasicEntity> T merge(T entity) {
        return null;
    }

    @Override
    public <T extends BasicEntity> T remove(T entity) {
        return null;
    }
}
