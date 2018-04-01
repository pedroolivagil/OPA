package com.olivadevelop.persistence.utils;

import com.olivadevelop.persistence.annotations.Entity;
import com.olivadevelop.persistence.annotations.Id;
import com.olivadevelop.persistence.entities.BasicEntity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.olivadevelop.persistence.utils.OlivaDevelopException.TypeException.PERSISTENCE;

/**
 * Copyright OlivaDevelop 2014-2018
 * Created by Oliva on 23/01/2018.
 */
@SuppressWarnings("Duplicates")
public abstract class QueryBuilder {

    public static final class Query {

        public enum ORDER_BY {
            ASC, DESC
        }

        String fields;
        String from;
        StringBuilder joins;
        StringBuilder where;
        String orderBy;

        public Query find() throws OlivaDevelopException {
            return find("* ");
        }

        private Query find(String... columns) throws OlivaDevelopException {
            if (Utils.isNull(from)) {
                throw new OlivaDevelopException(PERSISTENCE, "FROM debe ser definido préviamente.");
            }
            this.fields = " " + String.join(",", columns);
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

        public <T extends BasicEntity> Query from(Class<T> opa) throws OlivaDevelopException {
            try {
                if (Utils.isNotNull(opa)) {
                    Entity entity = opa.getAnnotation(Entity.class);
                    if (Utils.isNotNull(entity)) {
                        if (Utils.isNotNull(entity.table())) {
                            this.from = " FROM " + entity.table() + " " + entity.table();
                        } else {
                            throw new OlivaDevelopException(PERSISTENCE, "La entidad no tiene una tabla relacionada");
                        }
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
                this.where.append(" WHERE ");
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
            query.append("SELECT");
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
            query.append(";");
            return query.toString();
        }
    }

    public static final class Insert {

        private String from;
        private List<String> columns;
        private List<Object> values;

        public <T extends BasicEntity> Insert values(T data) throws OlivaDevelopException, IllegalAccessException {
            if (Utils.isNull(from)) {
                throw new OlivaDevelopException(PERSISTENCE, "FROM debe ser definido préviamente.");
            }
            columns = new ArrayList<>();
            values = new ArrayList<>();
            for (Field field : Utils.getAllFieldsFromEntity(data)) {
                field.setAccessible(true);
                columns.add(field.getName());
                values.add(field.get(data));
                field.setAccessible(false);
            }
            return this;
        }

        public <T extends BasicEntity> Insert from(Class<T> opa) throws OlivaDevelopException {
            try {
                if (Utils.isNotNull(opa)) {
                    Entity entity = opa.getAnnotation(Entity.class);
                    if (Utils.isNotNull(entity)) {
                        this.from = entity.table();
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

        @Override
        public String toString() {
            List<String> vals = new ArrayList<>();
            for (Object val : this.values) {
                if (Utils.isNumeric(val)) {
                    vals.add(String.valueOf(val));
                } else if (Utils.isBoolean(val)) {
                    vals.add(Utils.parseBoolean(val));
                } else {
                    vals.add("\"" + String.valueOf(val) + "\"");
                }
            }
            StringBuilder insert = new StringBuilder();
            insert.append("INSERT INTO ");
            insert.append(this.from);
            insert.append(" (");
            insert.append(String.join(", ", this.columns));
            insert.append(") VALUES (");
            insert.append(String.join(", ", vals));
            insert.append(");");
            return insert.toString();
        }
    }

    public static final class Update {

        private String from;
        private List<String> values;
        private StringBuilder where;

        public <T extends BasicEntity> Update values(T data) throws OlivaDevelopException, IllegalAccessException {
            if (Utils.isNull(from)) {
                throw new OlivaDevelopException(PERSISTENCE, "FROM debe ser definido préviamente.");
            }
            values = new ArrayList<>();
            for (Field field : Utils.getAllFieldsFromEntity(data)) {
                field.setAccessible(true);
                if (field.getAnnotation(Id.class) == null) {
                    Object val = field.get(data);
                    if (Utils.isNumeric(val)) {
                        val = String.valueOf(val);
                    } else if (Utils.isBoolean(val)) {
                        val = Utils.parseBoolean(val);
                    } else {
                        val = "\"" + String.valueOf(val) + "\"";
                    }
                    values.add(field.getName() + " = " + val);
                }
                field.setAccessible(false);
            }
            return this;
        }

        public <T extends BasicEntity> Update from(Class<T> opa) throws OlivaDevelopException {
            try {
                if (Utils.isNotNull(opa)) {
                    Entity entity = opa.getAnnotation(Entity.class);
                    if (Utils.isNotNull(entity)) {
                        this.from = entity.table();
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

        public Update where(String condition) throws OlivaDevelopException {
            if (Utils.isNotNull(condition)) {
                this.where = new StringBuilder();
                this.where.append(" WHERE ");
                this.where.append(condition);
            }
            return this;
        }

        public Update and(String condition) throws OlivaDevelopException {
            if (Utils.isNull(where)) {
                throw new OlivaDevelopException(PERSISTENCE, "WHERE debe ser definido préviamente.");
            }
            this.where.append(" AND ");
            this.where.append(condition);
            return this;
        }

        public Update or(String condition) throws OlivaDevelopException {
            if (Utils.isNull(where)) {
                throw new OlivaDevelopException(PERSISTENCE, "WHERE debe ser definido préviamente.");
            }
            this.where.append(" OR ");
            this.where.append(condition);
            return this;
        }

        @Override
        public String toString() {
            StringBuilder update = new StringBuilder();
            update.append("UPDATE ");
            update.append(this.from);
            update.append(" SET ");
            update.append(String.join(", ", this.values));
            if (Utils.isNotNull(this.where)) {
                update.append(this.where);
            }
            update.append(";");
            return update.toString();
        }
    }

    public static final class Delete {

        private String from;
        private StringBuilder where;

        public <T extends BasicEntity> Delete from(Class<T> opa) throws OlivaDevelopException {
            try {
                if (Utils.isNotNull(opa)) {
                    Entity entity = opa.getAnnotation(Entity.class);
                    if (Utils.isNotNull(entity)) {
                        this.from = entity.table();
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

        public Delete where(String condition) throws OlivaDevelopException {
            if (Utils.isNotNull(condition)) {
                this.where = new StringBuilder();
                this.where.append(" WHERE ");
                this.where.append(condition);
            }
            return this;
        }

        public Delete and(String condition) throws OlivaDevelopException {
            if (Utils.isNull(where)) {
                throw new OlivaDevelopException(PERSISTENCE, "WHERE debe ser definido préviamente.");
            }
            this.where.append(" AND ");
            this.where.append(condition);
            return this;
        }

        public Delete or(String condition) throws OlivaDevelopException {
            if (Utils.isNull(where)) {
                throw new OlivaDevelopException(PERSISTENCE, "WHERE debe ser definido préviamente.");
            }
            this.where.append(" OR ");
            this.where.append(condition);
            return this;
        }

        @Override
        public String toString() {
            StringBuilder delete = new StringBuilder();
            delete.append("DELETE FROM ");
            delete.append(this.from);
            if (Utils.isNotNull(this.where)) {
                delete.append(this.where);
            }
            delete.append(";");
            return delete.toString();
        }
    }

}
