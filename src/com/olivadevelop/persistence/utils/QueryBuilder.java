package com.olivadevelop.persistence.utils;

import com.olivadevelop.persistence.annotations.Entity;
import com.olivadevelop.persistence.entities.BasicEntity;

import java.util.List;

import static com.olivadevelop.persistence.utils.OlivaDevelopException.TypeException.PERSISTENCE;

/**
 * Copyright OlivaDevelop 2014-2018
 * Created by Oliva on 23/01/2018.
 */
public abstract class QueryBuilder {

    public static class Query {

        public enum ORDER_BY {
            ASC, DESC
        }

        String fields;
        String from;
        StringBuilder joins;
        StringBuilder where;
        String orderBy;

        public Query find() throws OlivaDevelopException {
            return find(" * ");
        }

        public Query find(String... columns) throws OlivaDevelopException {
            if (Utils.isNull(from)) {
                throw new OlivaDevelopException(PERSISTENCE, "FROM debe ser definido préviamente.");
            }
            this.fields = String.join(",", columns);
            return this;
        }

        public Query distinct() {
            this.fields = " DISTINCT " + this.fields;
            return this;
        }

        public Query count(String column) throws OlivaDevelopException {
            if (Utils.isNull(from)) {
                throw new OlivaDevelopException(PERSISTENCE, "FROM debe ser definido préviamente.");
            }
            this.fields = " COUNT(" + column + ")";
            return this;
        }

        public <T extends BasicEntity> Query from(T opa) throws OlivaDevelopException {
            return from(opa, null);
        }

        public <T extends BasicEntity> Query from(T opa, String alias) throws OlivaDevelopException {
            try {
                if (Utils.isNotNull(opa)) {
                    Entity entity = opa.getClass().getAnnotation(Entity.class);
                    if (Utils.isNotNull(entity)) {
                        if (Utils.isNull(alias)) {
                            alias = entity.table();
                        }
                        this.from = entity.table() + " " + alias;
                    } else {
                        throw new OlivaDevelopException(PERSISTENCE, "La clase no es una entidad OPA");
                    }
                } else {
                    throw new OlivaDevelopException(PERSISTENCE, "La clase no existe");
                }
            } catch (Exception e) {
                throw new OlivaDevelopException(PERSISTENCE, e.getMessage());
            }
            return this;
        }

        public <T extends BasicEntity, E extends BasicEntity> Query join(List<JoinQuery<T, E>> opas) throws OlivaDevelopException {
            if (Utils.isNotEmpty(opas)) {
                for (JoinQuery<T, E> opa : opas) {
                    this.joins.append(opa.toString());
                }
            }
            return this;
        }

        public Query where(String condition) throws OlivaDevelopException {
            if (Utils.isNotNull(condition)) {
                this.where = new StringBuilder();
                this.where.append(condition);
            }
            return this;
        }

        public Query and(String condition) throws OlivaDevelopException {
            if (Utils.isNull(where)) {
                throw new OlivaDevelopException(PERSISTENCE, "WHERE debe ser definido préviamente.");
            }
            this.where.append(" AND ");
            this.where.append(condition);
            return this;
        }

        public Query or(String condition) throws OlivaDevelopException {
            if (Utils.isNull(where)) {
                throw new OlivaDevelopException(PERSISTENCE, "WHERE debe ser definido préviamente.");
            }
            this.where.append(" OR ");
            this.where.append(condition);
            return this;
        }

        public Query orderBy(String orderByColumn, ORDER_BY order) throws OlivaDevelopException {
            this.orderBy = " ORDER BY " + orderByColumn + " " + order.name();
            return this;
        }

        @Override
        public String toString() {
            StringBuilder query = new StringBuilder();
            query.append("SELECT ");
            query.append(this.fields);
            query.append(this.from);
            if (Utils.isNotNull(this.joins)) {
                query.append(this.joins);
            }
            if (Utils.isNotNull(this.where)) {
                query.append(this.where);
            }
            if (Utils.isNotNull(this.orderBy)) {
                query.append(this.orderBy);
            }
            return query.toString();
        }
    }

    public static class Insert {

    }

    public static class Update {

    }

    public static class Delete {

    }

}
