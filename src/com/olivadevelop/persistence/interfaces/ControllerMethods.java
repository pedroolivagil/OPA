package com.olivadevelop.persistence.interfaces;


import com.olivadevelop.persistence.entities.BasicEntity;
import com.olivadevelop.persistence.utils.QueryBuilder;

import java.util.List;

/**
 * Copyright OlivaDevelop 2014-2018
 * Created by Oliva on 22/01/2018.
 */

public interface ControllerMethods<T extends BasicEntity> {

    T read(Integer idEntity);

    T read(T entity);

    T read(QueryBuilder queryBuilder);

    List<T> readAll(QueryBuilder queryBuilder);

    List<T> readAll();

    boolean create(T entity);

    boolean update(T entity);

    boolean delete(T entity);
}
