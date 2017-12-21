package com.dkaishu.bucketsofgoogle.base;

import android.app.Application;

import com.dkaishu.bucketsofgoogle.utils.Utils;
import com.umeng.analytics.MobclickAgent;


public class BaseApplication extends Application {
    public static BaseApplication baseApplication;

    public static BaseApplication getApplication() {
        return baseApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
        //LeakCanary.install(this);
        //        DBManager.init(this);
//        ZXingLib.initDisplayOpinion(this);
        Utils.init(this);
        MobclickAgent.setDebugMode( true );
    }

}
