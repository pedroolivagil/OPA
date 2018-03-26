package com.olivadevelop.persistence.managers;

import com.olivadevelop.persistence.utils.Constant;
import com.olivadevelop.persistence.utils.Logger;
import com.olivadevelop.persistence.utils.Utils;
import okhttp3.*;
import org.json.JSONObject;

import java.net.URL;
import java.util.List;

import static com.olivadevelop.persistence.utils.Constant.TIMEOUT;

class RestService {
    Logger<Service> logger = new Logger<>(Service.class);
    JSONObject retorno = new JSONObject();

    void run(final List<String> queries) {
        Thread thread = new Thread(() -> {
            synchronized (retorno) {
                try {
                    FormBody.Builder formbody = new FormBody.Builder();
                    for (String query : queries) {
                        formbody.add("query[]", query);
                    }
                    URL url = new URL(Constant.SERVICE_URL + "db_manager.php");
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)
                            .post(formbody.build())
                            .build();
                    Response response = client.newCall(request).execute();
                    ResponseBody body = response.body();
                    if (Utils.isNotNull(body)) {
                        String result = body.string();
                        retorno = new JSONObject(result);
                        retorno.notifyAll();
                    }
                    response.close();
                } catch (Exception e) {
                    /*logger.error(e);*/
                }
            }
        });
        thread.start();
        onPreExecute();
        onPostExecute();
    }

    void onPreExecute() {
        logger.print("Receiving data from service...");
    }

    void onPostExecute() {
        try {
            synchronized (retorno) {
                if (Utils.isNull(retorno)) {
                    retorno.wait(TIMEOUT);
                }
                logger.print(retorno.toString());
            }
        } catch (InterruptedException e) {
            /*logger.error(e);*/
        }
    }
}
