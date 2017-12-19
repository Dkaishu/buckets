package com.dkaishu.bucketsofgoogle.config;

import android.Manifest;

/**
 * Created by Administrator on 2017/9/20.
 */

public class PermissionConfig {
    /**
     * 全局的权限列表，默认每个 Activity 都会检测
     * 此处的权限必须在 AndroidManifest 中声明，
     */
    public static final String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
    };
}
