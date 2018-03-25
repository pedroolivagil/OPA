package com.olivadevelop.persistence.utils;

import com.olivadevelop.persistence.annotations.Entity;
import com.olivadevelop.persistence.annotations.Id;

import static com.olivadevelop.persistence.utils.OlivaDevelopException.TypeException.PERSISTENCE;

/**
 * Copyright OlivaDevelop 2014-2018
 * Created by Oliva on 23/03/2018.
 * RolerMaster
 */
class JoinQuery<T, E> {

    private T entity;
    private E entityJoin;
    private String table;
    private String tableJoin;
    private String alias;
    private String aliasJoin;
    private KeyValuePair<String, Object> id;
    private KeyValuePair<String, Object> idJoin;

    public JoinQuery() {
    }

    public JoinQuery(T entity, E entityJoin) throws OlivaDevelopException, IllegalAccessException {
        setEntity(entity);
        setEntityJoin(entityJoin);
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) throws OlivaDevelopException, IllegalAccessException {
        Entity entityAn = entity.getClass().getAnnotation(Entity.class);
        if (Utils.isNotNull(entityAn)) {
            this.entity = entity;
            this.table = entityAn.table();
            this.id = Utils.getPkFromEntity(entity);
        } else {
            throw new OlivaDevelopException(PERSISTENCE, "La clase no es una entidad OPA");
        }
    }

    public E getEntityJoin() {
        return entityJoin;
    }

    public void setEntityJoin(E entityJoin) throws OlivaDevelopException, IllegalAccessException {
        Entity entityAn = entityJoin.getClass().getAnnotation(Entity.class);
        if (Utils.isNotNull(entityAn)) {
            this.entityJoin = entityJoin;
            this.tableJoin = entityAn.table();
            this.idJoin = Utils.getPkFromEntity(entityJoin);
        } else {
            throw new OlivaDevelopException(PERSISTENCE, "La clase no es una entidad OPA");
        }
    }

    @Override
    public String toString() {
        StringBuilder retorno = new StringBuilder();
        retorno.append(" LEFT JOIN ");
        retorno.append(this.tableJoin);
        retorno.append(" ").append(this.aliasJoin);
        retorno.append(" ON ");
        retorno.append(this.aliasJoin).append(".").append(this.idJoin);
        retorno.append(" = ");
        retorno.append(this.alias).append(".").append(this.id);
        return retorno.toString();
    }
}
