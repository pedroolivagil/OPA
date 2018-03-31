package com.olivadevelop.persistence.controllers;

import com.olivadevelop.persistence.entities.BasicEntity;

/**
 * Copyright OlivaDevelop 2014-2018
 * Created by Oliva on 23/01/2018.
 * <p>
 * Para crear controladores, extender de esta clase y usar sus métodos.
 * <p>
 * El controlador administra las entidades relacionadas con dicho contralador y las envía al servicio REST
 *
 * @param <T> Tipo de entidad
 */
public class BasicController<T extends BasicEntity> extends Controller<T> {

    public BasicController(Class<T> entityClass) {
        super(entityClass);
    }
}
