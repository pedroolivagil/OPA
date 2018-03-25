package com.olivadevelop.persistence.managers;

import org.json.JSONObject;

/**
 * Copyright OlivaDevelop 2014-2018
 * Created by Oliva on 23/01/2018.
 *
 * Este servicio se comunica con el servidor simulando un envio de un formulario.
 * <p>
 * El servidor puede ser cualquier tipo de servidor que, pueda recibir datos POST, y devolver un objeto JSON,
 * independientemente de si usamos mysql, db2, ...
 * <p>
 * Será el servicio del servidor quíen se encargará de traducir la query.
 * <p>
 * OPQL - OlivaDevelop Persistence Query Language
 * Un sistema de consultas específico para OPA.
 */
final class Service {


    /**
     * Recive un string con la query.
     *
     * @param query
     * @return
     */
    public JSONObject execute(String query) {
        return null;
    }
}
