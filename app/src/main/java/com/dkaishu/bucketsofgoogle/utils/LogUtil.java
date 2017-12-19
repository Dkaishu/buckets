package com.dkaishu.bucketsofgoogle.utils;

import android.util.Log;

import com.dkaishu.bucketsofgoogle.BuildConfig;


/**
 * 对 android.util.Log 的简单封装
 * 可使用"debug_"或自定义的 tagName ，过滤定位log
 */
public final class LogUtil {
    public static Boolean isDebug = BuildConfig.DEBUG;
    public static String Debug = "debug_";


    public static void v(String tagName, String message) {
        if (isDebug) {
            Log.v(Debug + tagName, message);
        }
    }

    public static void v(String message) {
        if (isDebug) {
            Log.v(Debug, message);
        }
    }

    public static void v(String tagName, Exception e) {
        if (isDebug) {
            Log.v(Debug + tagName, e.getMessage(), e);
        }
    }

    public static void v(Exception e) {
        if (isDebug) {
            Log.v(Debug, e.getMessage(), e);
        }
    }

    /**
     * 只有debug 时显示日志，除非在gradle.build 中更改"SHOWLOG", 为"true"
     * 无 tagName 时，即调用 d(String message) 时，tagName 默认为"debug_"
     *
     * @param tagName Tag自定义部分，全文是"debug_tagName"
     * @param message 要打印的内容
     */
    public static void d(String tagName, String message) {
        if (isDebug) {
            Log.d(Debug + tagName, message);
        }
    }

    public static void d(String message) {
        if (isDebug) {
            Log.d(Debug, message);
        }
    }

    /**
     * 只有debug 时显示日志，除非在gradle.build 中更改"SHOWLOG", 为"true"
     * 无 tagName 时，即调用 v(String message) 时，tagName 默认为"debug_"
     *
     * @param tagName Tag自定义部分，全文是"debug_tagName"
     * @param e       要打印的 Exception，打印的内容为 e.getMessage()
     */
    public static void d(String tagName, Exception e) {
        if (isDebug) {
            Log.i(Debug + tagName, e.getMessage(), e);
        }
    }

    public static void d(Exception e) {
        if (isDebug) {
            Log.i(Debug, e.getMessage(), e);
        }
    }

    public static void i(String tagName, String message) {
        if (isDebug) {
            Log.i(Debug + tagName, message);
        }
    }

    public static void i(String message) {
        if (isDebug) {
            Log.i(Debug, message);
        }
    }

    public static void i(String tagName, Exception e) {
        if (isDebug) {
            Log.i(Debug + tagName, e.getMessage(), e);
        }
    }

    public static void i(Exception e) {
        if (isDebug) {
            Log.i(Debug, e.getMessage(), e);
        }
    }

    public static void w(String tagName, String message) {
        if (isDebug) {
            Log.w(Debug + tagName, message);
        }
    }

    public static void w(String message) {
        if (isDebug) {
            Log.w(Debug, message);
        }
    }

    public static void w(String tagName, Exception e) {
        if (isDebug) {
            Log.w(Debug + tagName, e.getMessage(), e);
        }
    }

    public static void w(Exception e) {
        if (isDebug) {
            Log.w(Debug, e.getMessage(), e);
        }
    }

    public static void e(String tagName, String message) {
        if (isDebug) {
            Log.e(Debug + tagName, message);
        }
    }

    public static void e(String message) {
        if (isDebug) {
            Log.e(Debug, message);
        }
    }

    public static void e(String tagName, Exception e) {
        if (isDebug) {
            Log.e(Debug + tagName, e.getMessage(), e);
        }
    }

    public static void e(Exception e) {
        if (isDebug) {
            Log.e(Debug, e.getMessage(), e);
        }
    }
}