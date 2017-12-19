package com.dkaishu.bucketsofgoogle.app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AppCrashHandler implements Thread.UncaughtExceptionHandler {
    private static AppCrashHandler handler = new AppCrashHandler();
    private Thread.UncaughtExceptionHandler defaultHandler;
    private File saveSpacePath;
    private File localErrorSave;
    private Context context;
    private StringBuilder sb = new StringBuilder();

    private AppCrashHandler() {
    }

    public static AppCrashHandler getInstance() {
        return handler;
    }

    public void init(Context context) {
        this.context = context;
        int lableInfo = context.getApplicationInfo().labelRes;
        String AppName = context.getString(lableInfo);
        saveSpacePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/" + AppName);
        localErrorSave = new File(saveSpacePath, AppName + "Error.txt");
        if (!saveSpacePath.exists()) {
            saveSpacePath.mkdirs();
        }
        if (!localErrorSave.exists()) {
            try {
                localErrorSave.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(final Thread t, Throwable e) {
        upLoadException(t);
        writeErrorToLocal(t, e);

    }


    /***
     * 上传异常信息到服务器
     * @param t1
     */
    private void upLoadException(Thread t1) {

    }

    private void writeErrorToLocal(Thread t, Throwable e) {
        try {
            BufferedWriter fos = new BufferedWriter(new FileWriter(localErrorSave, true));
            PackageManager packageManager = context.getPackageManager();
            String line = "\n----------------------------------------------------------------------------------------\n";
            sb.append(line);
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            sb.append(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())) + "<---->" +
                    "包名::" + packageInfo.packageName + "<---->版本名::" + packageInfo.versionName + "<---->版本号::" + packageInfo.versionCode + "\n");
            sb.append("手机制造商::" + Build.MANUFACTURER + "\n");
            sb.append("手机型号::" + Build.MODEL + "\n");
            sb.append("CPU架构::" + Build.CPU_ABI + "\n");
            sb.append(e.toString() + "\n");
            StackTraceElement[] trace = e.getStackTrace();
            for (StackTraceElement traceElement : trace)
                sb.append("\n\tat " + traceElement);
            sb.append("\n");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Throwable[] suppressed = e.getSuppressed();
                for (Throwable se : suppressed)
                    sb.append("\tat " + se.getMessage());
            }
            fos.write(sb.toString());
            fos.close();


        } catch (IOException e1) {
            e1.printStackTrace();
            defaultHandler.uncaughtException(t, e1);
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
            defaultHandler.uncaughtException(t, e1);
        }
    }
}
