package com.olivadevelop.persistence.interfaces;


import com.olivadevelop.persistence.entities.BasicEntity;
import com.olivadevelop.persistence.utils.OlivaDevelopException;
import com.olivadevelop.persistence.utils.QueryBuilder;

import java.util.List;

/**
 * Copyright OlivaDevelop 2014-2018
 * Created by Oliva on 22/01/2018.
 */

public interface ControllerMethods<T extends BasicEntity> {

    T read(Integer idEntity) throws OlivaDevelopException;

    T read(QueryBuilder.Query queryBuilder) throws OlivaDevelopException;

    List<T> readAll(QueryBuilder.Query queryBuilder) throws OlivaDevelopException;

    List<T> readAll() throws OlivaDevelopException;

    void create(T entity) throws OlivaDevelopException;

    T update(T entity) throws OlivaDevelopException;

    void delete(T entity) throws OlivaDevelopException;
}
