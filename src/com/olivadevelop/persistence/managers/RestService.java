package com.olivadevelop.persistence.managers;

import com.olivadevelop.persistence.utils.Constant;
import com.olivadevelop.persistence.utils.Logger;
import com.olivadevelop.persistence.utils.OlivaDevelopException;
import com.olivadevelop.persistence.utils.Utils;
import okhttp3.*;
import org.json.JSONObject;

import javax.rmi.CORBA.Util;
import java.io.IOException;
import java.net.URL;
import java.util.DuplicateFormatFlagsException;
import java.util.List;

import static com.olivadevelop.persistence.utils.Constant.TIMEOUT;
import static com.olivadevelop.persistence.utils.JSONPersistence.REQUEST.CODE_200;
import static com.olivadevelop.persistence.utils.OlivaDevelopException.TypeException.PERSISTENCE;

final class RestService {
    private Logger<Service> logger = new Logger<>(Service.class);
    private JSONObject retorno;
    private OlivaDevelopException exception;

    private void setResult(ResponseBody body) throws IOException {
        if (Utils.isNotNull(body)) {
            String result = body.string();
            retorno = new JSONObject(result);
            retorno.notify();
            if (Utils.isNotNull(retorno)) {
                if (CODE_200.getCode() != retorno.getInt("result")) {
                    exception = new OlivaDevelopException(PERSISTENCE, retorno.getString("message"));
                }
            }
        }
    }

    private enum PATH {
        EXECUTE("db_manager.php"),
        FIND("read.php"),
        SEQUENCE("sequence.php");

        String v;

        PATH(String v) {
            this.v = v;
        }

        public String v() {
            return v;
        }
    }

    /**
     * Queries pueden ser, INSERT, DELETE, UPDATE
     *
     * @param queries
     */
    void run(final List<String> queries) throws OlivaDevelopException {
        retorno = new JSONObject();
        Thread thread = new Thread(() -> {
            synchronized (retorno) {
                try {
                    FormBody.Builder formbody = new FormBody.Builder();
                    for (String query : queries) {
                        formbody.add("query[]", query);
                    }
                    URL url = new URL(Constant.SERVICE_URL + PATH.EXECUTE.v());
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)
                            .post(formbody.build())
                            .build();
                    Response response = client.newCall(request).execute();
                    ResponseBody body = response.body();
                    setResult(body);
                    response.close();
                } catch (Exception ignored) {
                }
            }
        });
        thread.start();
        onPreExecute(queries);
        onPostExecute();
    }

    /**
     * Para las consultas SELECT
     *
     * @param query
     * @return
     */
    JSONObject run(final String query) throws OlivaDevelopException {
        retorno = new JSONObject();
        Thread thread = new Thread(() -> {
            synchronized (retorno) {
                try {
                    FormBody.Builder formbody = new FormBody.Builder();
                    formbody.add("query", query);
                    URL url = new URL(Constant.SERVICE_URL + PATH.FIND.v());
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)
                            .post(formbody.build())
                            .build();
                    Response response = client.newCall(request).execute();
                    ResponseBody body = response.body();
                    setResult(body);
                    response.close();
                } catch (Exception ignored) {
                }
            }
        });
        thread.start();
        onPreExecute(query);
        return onPostExecute();
    }

    /**
     * Para las consultas SELECT
     *
     * @param query
     * @return
     */
    JSONObject sequence(final String query) throws OlivaDevelopException {
        retorno = new JSONObject();
        Thread thread = new Thread(() -> {
            synchronized (retorno) {
                try {
                    FormBody.Builder formbody = new FormBody.Builder();
                    formbody.add("query", query);
                    URL url = new URL(Constant.SERVICE_URL + PATH.SEQUENCE.v());
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)
                            .post(formbody.build())
                            .build();
                    Response response = client.newCall(request).execute();
                    ResponseBody body = response.body();
                    setResult(body);
                    response.close();
                } catch (Exception ignored) {
                }
            }
        });
        thread.start();
        onPreExecute(query);
        return onPostExecute();
    }

    private void onPreExecute(List<String> queries) {
        onPreExecute(String.join(",", queries));
    }

    private void onPreExecute(String query) {
        logger.print("Receiving/Sending data from/to service... (Queries = {" + query + "})");
    }

    private JSONObject onPostExecute() throws OlivaDevelopException {
        try {
            synchronized (retorno) {
                if (Utils.isNull(retorno)) {
                    retorno.wait(TIMEOUT);
                }
                if (Utils.isNotNull(exception)) {
                    throw exception;
                }
            }
        } catch (InterruptedException ignored) {
        }
        return retorno;
    }
}
