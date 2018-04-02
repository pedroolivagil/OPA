package com.olivadevelop.persistence.controllers;

import com.olivadevelop.persistence.entities.BasicEntity;
import com.olivadevelop.persistence.interfaces.ControllerMethods;
import com.olivadevelop.persistence.interfaces.EntityManager;
import com.olivadevelop.persistence.managers.ServiceDAO;
import com.olivadevelop.persistence.utils.OlivaDevelopException;
import com.olivadevelop.persistence.utils.QueryBuilder;

import java.util.List;

/**
 * Copyright OlivaDevelop 2014-2018
 * Created by Oliva on 23/01/2018.
 */
class Controller<T extends BasicEntity> implements ControllerMethods<T> {

    private final EntityManager em;
    private Class<T> entityClass;

    public Controller(Class<T> entityClass) {
        em = new ServiceDAO();
        this.entityClass = entityClass;
    }

    @Override
    public T read(Integer idEntity) throws OlivaDevelopException {
        return em.find(entityClass, idEntity);
    }

    @Override
    public T read(QueryBuilder.Query queryBuilder) throws OlivaDevelopException {
        return em.singleQuery(queryBuilder.toString(), entityClass);
    }

    @Override
    public List<T> readAll(QueryBuilder.Query queryBuilder) throws OlivaDevelopException {
        return em.createQuery(queryBuilder.toString(), entityClass);
    }

    @Override
    public List<T> readAll() throws OlivaDevelopException {
        QueryBuilder.Query query = new QueryBuilder.Query();
        query.from(entityClass).distinct();
        return em.createQuery(query.toString(), entityClass);
    }

    @Override
    public void create(T entity) throws OlivaDevelopException {
        preCreate(entity);
        em.persist(entity);
        em.flush();
    }

    @Override
    public T update(T entity) throws OlivaDevelopException {
        preUpdate(entity);
        entity = em.merge(entity);
        em.flush();
        return entity;
    }

    @Override
    public void delete(T entity) throws OlivaDevelopException {
        preDelete(entity);
        em.remove(entity);
        em.flush();
    }

    protected void preCreate(T entity) {

    }

    protected void preUpdate(T entity) {

    }

    protected void preDelete(T entity) {

    }

}
