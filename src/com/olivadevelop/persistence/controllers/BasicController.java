package com.olivadevelop.persistence.controllers;

import com.olivadevelop.persistence.annotations.PersistenceUnit;
import com.olivadevelop.persistence.entities.BasicEntity;
import com.olivadevelop.persistence.interfaces.ControllerMethods;
import com.olivadevelop.persistence.interfaces.EntityManager;
import com.olivadevelop.persistence.managers.ServiceDAO;
import com.olivadevelop.persistence.utils.OlivaDevelopException;
import com.olivadevelop.persistence.utils.QueryBuilder;
import com.olivadevelop.persistence.utils.Utils;

import java.util.List;

/**
 * Copyright OlivaDevelop 2014-2018
 * Created by Oliva on 23/01/2018.
 */
public class BasicController<T extends BasicEntity> implements ControllerMethods<T> {

    @PersistenceUnit
    private final EntityManager em;
    private Class<T> entityClass;

    public BasicController(Class<T> entityClass) {
        em = new ServiceDAO();
        this.entityClass = entityClass;
    }

    @Override
    public T read(Integer idEntity) throws OlivaDevelopException {
        return em.find(entityClass, idEntity);
    }

    @Override
    public T read(QueryBuilder queryBuilder) {
        return null;
    }

    @Override
    public List<T> readAll(QueryBuilder queryBuilder) {
        return null;
    }

    @Override
    public List<T> readAll() {
        return null;
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

    public void preCreate(T entity) {

    }

    public void preUpdate(T entity) {

    }

    public void preDelete(T entity) {

    }

}
