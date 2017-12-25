package com.dkaishu.bucketsofgoogle.main.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

/**
 * Created by dks on 2017/12/20.
 */
public class AppInstallReceiver extends BroadcastReceiver {
    public interface OnAppInstallListerner{
        void onAdded(String pkg);
        void onRemoved(String pkg);
        void onReplaced(String pkg);
    }

    private OnAppInstallListerner listerner;

    public void setListerner(OnAppInstallListerner listerner) {
        this.listerner = listerner;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        PackageManager manager = context.getPackageManager();
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
           if (null!= listerner)listerner.onAdded(packageName);
            Toast.makeText(context, "安装成功"+packageName, Toast.LENGTH_LONG).show();
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            if (null!= listerner)listerner.onRemoved(packageName);
            Toast.makeText(context, "卸载成功"+packageName, Toast.LENGTH_LONG).show();
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            if (null!= listerner)listerner.onReplaced(packageName);
            Toast.makeText(context, "替换成功"+packageName, Toast.LENGTH_LONG).show();
        }
    }
}