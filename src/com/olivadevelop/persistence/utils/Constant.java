package com.olivadevelop.persistence.utils;

/**
 * Copyright OlivaDevelop 2014-2018
 * Created by Oliva on 01/02/2018.
 * RolerMaster
 */
public abstract class Constant {

    public static final long TIMEOUT = 9000;
    public static final int REQUEST_CODE = 1;

    public static final int INFO_CODE = 1;
    public static final int WARN_CODE = 2;
    public static final int ERRO_CODE = 3;

    //public static final String SERVER = "10.0.3.2";    //Genymotion
    public static final String SERVER = "localhost";    //Genymotion
    public static final String HOSTNAME = "http://" + SERVER + "/rolermaster/";
    public static final String SERVICE_URL = HOSTNAME + "www/php/";

    public static boolean FLAG_ACTIVE = true;
    public static boolean FLAG_INACTIVE = false;
}
