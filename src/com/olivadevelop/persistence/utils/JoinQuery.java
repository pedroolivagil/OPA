package com.olivadevelop.persistence.utils;

import com.olivadevelop.persistence.annotations.Entity;
import com.olivadevelop.persistence.entities.BasicEntity;

import static com.olivadevelop.persistence.utils.OlivaDevelopException.TypeException.PERSISTENCE;

/**
 * Copyright OlivaDevelop 2014-2018
 * Created by Oliva on 23/03/2018.
 * RolerMaster
 */
class JoinQuery<T extends BasicEntity, E extends BasicEntity> {

    private Class<T> entity;
    private Class<E> entityJoin;
    private String table;
    private String tableJoin;
    private String alias;
    private String aliasJoin;
    private KeyValuePair<String, Object> id;
    private KeyValuePair<String, Object> idJoin;

    public JoinQuery() {
    }

    public JoinQuery(Class<T> entity, Class<E> entityJoin) throws OlivaDevelopException, IllegalAccessException, InstantiationException {
        setEntity(entity);
        setEntityJoin(entityJoin);
    }

    public Class<E> getEntityJoin() {
        return entityJoin;
    }

    public Class<T> getEntity() {
        return entity;
    }

    public void setEntity(Class<T> entity) throws OlivaDevelopException, IllegalAccessException, InstantiationException {
        Entity entityAn = entity.getClass().getAnnotation(Entity.class);
        if (Utils.isNotNull(entityAn)) {
            this.entity = entity;
            this.table = entityAn.table();
            this.alias = entityAn.table();
            this.id = Utils.getPkFromEntity(entity.newInstance());
        } else {
            throw new OlivaDevelopException(PERSISTENCE, "La clase no es una entidad OPA");
        }
    }

    public void setEntityJoin(Class<E> entityJoin) throws OlivaDevelopException, IllegalAccessException, InstantiationException {
        Entity entityAn = entityJoin.getClass().getAnnotation(Entity.class);
        if (Utils.isNotNull(entityAn)) {
            this.entityJoin = entityJoin;
            this.tableJoin = entityAn.table();
            this.aliasJoin = entityAn.table();
            this.idJoin = Utils.getPkFromEntity(entityJoin.newInstance());
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
