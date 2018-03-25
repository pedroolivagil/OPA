package com.olivadevelop.persistence.interfaces;

import com.olivadevelop.persistence.entities.BasicEntity;
import com.olivadevelop.persistence.utils.QueryBuilder;

import java.util.List;

/**
 * Copyright OlivaDevelop 2014-2018
 * Created by Oliva on 23/01/2018.
 */
public interface EntityManager {

    <T extends BasicEntity> T query(Class<T> entity, String query);

    <T extends BasicEntity> List<T> query(String query, Class<T> entity);

    <T extends BasicEntity> T find(Class<T> entity, Object id);

    <T extends BasicEntity> T persist(T entity);

    <T extends BasicEntity> T merge(T entity);

    <T extends BasicEntity> T remove(T entity);

    void flush();
}
