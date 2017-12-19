package com.dkaishu.bucketsofgoogle.config;

import android.os.Environment;

/**
 * Created by Administrator on 2017/9/18.
 */

public class PathConfig {

    public static final String DOWNLOAD_FILE_DIR = Environment.getExternalStorageDirectory().getPath()+"/okHttp_download/";
    public static final String CACHE_DIR = Environment.getExternalStorageDirectory().getPath()+"/okHttp_cache";

}
