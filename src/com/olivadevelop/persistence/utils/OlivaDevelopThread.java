package com.olivadevelop.persistence.utils;

import android.content.Context;
import android.widget.ProgressBar;

import com.olivadevelop.rolermaster.tools.Tools;
import com.olivadevelop.rolermaster.olivaobjectpersistence.interfaces.ActionOlivaDevelop;

import static com.google.android.gms.internal.zzahn.runOnUiThread;

/**
 * Copyright OlivaDevelop 2014-2018
 * Created by Oliva on 15/01/2018.
 */

public class OlivaDevelopThread {
    private static final OlivaDevelopThread ourInstance = new OlivaDevelopThread();
    private Context context;

    private OlivaDevelopThread() {
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static OlivaDevelopThread getInstance() {
        return ourInstance;
    }

    /**
     * Llama a @newThread pero sin progressbar
     *
     * @param action acción que va a realizar al concluir
     */
    public void newThread(ActionThread action) {
        newThread(500, action, null);
    }

    /**
     * Llama a @newThread pero sin progressbar
     *
     * @param time   tiempo máximo del hilo
     * @param action acción que va a realizar al concluir
     */
    public void newThread(int time, ActionThread action) {
        newThread(time, action, null);
    }

    /**
     * Crea un hilo secundario para demorar una acción. Se admite un progresbar
     *
     * @param time        tiempo de demora máximo
     * @param action      la acción que va a realizar el hilo al concluir el tiempo
     * @param progressBar la barra de progreso que irá incrementando
     */
    public void newThread(final int time, final ActionThread action, final ProgressBar progressBar) {
        new Thread() {
            @Override
            public void run() {
                try {
                    if (Tools.isNotNull(action)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                action.preRun();
                            }
                        });
                    }
                    if (time > 0) {
                        int waited = 0;
                        while (waited < time) {
                            sleep(100);
                            if (progressBar != null) {
                                progressBar.incrementProgressBy(100);
                            }
                            waited += 100;
                        }
                    }
                } catch (InterruptedException e) {
                    Tools.Logger(context, e);
                } finally {
                    if (Tools.isNotNull(action)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                action.run();
                                action.postRun();
                            }
                        });
                    }
                }
            }
        }.start();
    }

    public static class ActionThread implements ActionOlivaDevelop {
        @Override
        public void run() {
        }

        public void preRun() {
            if (Tools.isNotNull(Alert.getInstance().getLoadingDialog()) && !Alert.getInstance().getLoadingDialog().isShowing()) {
                Alert.getInstance().showLoadingDialog();
            }
        }

        public void postRun() {
            if (Tools.isNotNull(Alert.getInstance().getLoadingDialog()) && Alert.getInstance().getLoadingDialog().isShowing()) {
                Alert.getInstance().hideLoadingDialog();
            }
        }
    }
}
