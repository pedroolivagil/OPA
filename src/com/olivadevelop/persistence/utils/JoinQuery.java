package com.olivadevelop.persistence.utils;

import com.olivadevelop.persistence.annotations.Entity;
import com.olivadevelop.persistence.annotations.OneToMany;
import com.olivadevelop.persistence.annotations.OneToOne;
import com.olivadevelop.persistence.annotations.Persistence;
import com.olivadevelop.persistence.entities.BasicEntity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.olivadevelop.persistence.utils.OlivaDevelopException.TypeException.PERSISTENCE;

/**
 * Copyright OlivaDevelop 2014-2018
 * Created by Oliva on 03/01/2018.
 */

public class JoinQuery {
    Logger<JoinQuery> logger = new Logger<>(JoinQuery.class);

    private List<KeyValuePair<Class<?>, Class<?>>> joins;
    private List<String> fields;

    public JoinQuery() {
        this.fields = new ArrayList<>();
    }

    public void addJoin(Class<?> from, Class<?> to) {
        this.getJoins().add(new KeyValuePair<>(from, to));
    }

    private List<KeyValuePair<Class<?>, Class<?>>> getJoins() {
        if (Utils.isNull(this.joins)) {
            this.joins = new ArrayList<>();
        }
        return joins;
    }

    public List<String> getFields() {
        return fields;
    }

    public String toJoin() throws OlivaDevelopException {
        StringBuilder retorno = new StringBuilder();
        try {
            for (KeyValuePair<Class<?>, Class<?>> join : getJoins()) {
                Entity entity = join.getKey().getAnnotation(Entity.class);
                Entity entityJoin = join.getValue().getAnnotation(Entity.class);
                if (Utils.isNotNull(entity) && Utils.isNotNull(entityJoin)) {

                    for (String field : Utils.getFieldAliasFromEntity((BasicEntity) join.getKey().newInstance())) {
                        if (!fields.contains(field)) {
                            fields.add(field);
                        }
                    }
                    for (String field : Utils.getFieldAliasFromEntity((BasicEntity) join.getValue().newInstance())) {
                        if (!fields.contains(field)) {
                            fields.add(field);
                        }
                    }

                    String alias = entity.table();
                    String id = Utils.getPkFromEntity((BasicEntity) join.getKey().newInstance()).getKey();
                    String tableJoin = entityJoin.table();
                    String aliasJoin = entityJoin.table();
                    String idJoin = Utils.getPkFromEntity((BasicEntity) join.getValue().newInstance()).getKey();

                    retorno.append(" LEFT JOIN ");
                    retorno.append(tableJoin);
                    retorno.append(" ").append(aliasJoin);
                    retorno.append(" ON ");
                    retorno.append(aliasJoin).append(".").append(idJoin);
                    retorno.append(" = ");
                    retorno.append(alias).append(".").append(id);
                } else {
                    throw new OlivaDevelopException(PERSISTENCE, "No se ha podido crear el join, las clases no son entidades OPA");
                }
            }
        } catch (IllegalAccessException | InstantiationException e) {
            logger.error(e);
        }
        return retorno.toString();
    }
}
