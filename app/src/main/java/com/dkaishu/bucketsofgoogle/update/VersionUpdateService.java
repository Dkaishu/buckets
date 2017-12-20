package com.dkaishu.bucketsofgoogle.update;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.dkaishu.bucketsofgoogle.R;
import com.dkaishu.bucketsofgoogle.utils.LogUtil;
import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.DownloadStatus;
import com.okhttplib.bean.DownloadFileInfo;
import com.okhttplib.callback.ProgressCallback;

import java.io.File;

public class VersionUpdateService extends Service {
    private static final String TAG = VersionUpdateService.class.getSimpleName();
    private LocalBinder binder = new LocalBinder();

    private DownloadFileInfo fileInfo;

    private DownLoadListener downLoadListener;
    private boolean downLoading;
    private boolean isForceUpdate;
    private int progress;

    private NotificationManager mNotificationManager;
    private NotificationUpdaterThread notificationUpdaterThread;
    private Notification.Builder notificationBuilder;
    private final int NOTIFICATION_ID = 100;

    private String saveFileDir;
//    private VersionUpdateModel versionUpdateModel;

    public VersionUpdateService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "onCreate called");
    }

    //    @IntDef(value = {Service.START_FLAG_REDELIVERY, Service.START_FLAG_RETRY}
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String url = intent.getStringExtra("url");
        String saveFileName = intent.getStringExtra("saveFileName");
        LogUtil.d(TAG, "onStartCommand  " + url + "    name：  " + saveFileName);

        if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(saveFileName)) {
            doDownLoadTask(url, saveFileName);

            isForceUpdate = false;
            LogUtil.d(TAG, "onStartCommand  ");
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy called");
        setDownLoadListener(null);
//        setCheckVersionCallBack(null);
        stopDownLoadForground();
        if (mNotificationManager != null)
            mNotificationManager.cancelAll();
        downLoading = false;
    }


    public void setDownLoadListener(DownLoadListener downLoadListener) {
        this.downLoadListener = downLoadListener;
    }

    public interface DownLoadListener {
        void begain();

        void pause();

        void inProgress(int percent, long bytesWritten, long contentLength, boolean done);

        void downloadSuccess(String filePath);

        void downloadFailed(String err);
    }

//    public interface CheckVersionCallBack {
//        void onSuccess();
//
//        void onError();
//    }
//
//    private CheckVersionCallBack checkVersionCallBack;
//
//    public void setCheckVersionCallBack(CheckVersionCallBack checkVersionCallBack) {
//        this.checkVersionCallBack = checkVersionCallBack;
//    }

    private class NotificationUpdaterThread extends Thread {
        @Override
        public void run() {
            while (true) {
                notificationBuilder.setContentTitle("正在下载更新" + progress + "%"); // the label of the entry
                notificationBuilder.setProgress(100, progress, false);
                mNotificationManager.notify(NOTIFICATION_ID, notificationBuilder.getNotification());
                if (progress >= 100) {
                    break;
                }
            }
        }
    }

    public boolean isDownLoading() {
        return downLoading;
    }

    public void setDownLoading(boolean downLoading) {
        this.downLoading = downLoading;
    }

    /**
     * 让Service保持活跃,避免出现:
     * 如果启动此服务的前台Activity意外终止时Service出现的异常(也将意外终止)
     */
    private void starDownLoadForground() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = "下载中,请稍后...";
        // The PendingIntent to launch our activity if the user selects this notification
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                new Intent(this, StartVpnActivity.class), 0);
        notificationBuilder = new Notification.Builder(this);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);  // the status icon
        notificationBuilder.setTicker(text);  // the status text
        notificationBuilder.setWhen(System.currentTimeMillis());  // the time stamp
        notificationBuilder.setContentText(text);  // the contents of the entry
//        notificationBuilder.setContentIntent(contentIntent);  // The intent to send when the entry is clicked
        notificationBuilder.setContentTitle("正在下载更新" + 0 + "%"); // the label of the entry
        notificationBuilder.setProgress(100, 0, false);
        notificationBuilder.setOngoing(true);
        notificationBuilder.setAutoCancel(true);
        Notification notification = notificationBuilder.getNotification();
        startForeground(NOTIFICATION_ID, notification);
    }

    private void stopDownLoadForground() {
        stopForeground(true);
    }

//    public void doCheckUpdateTask() {
//        final int currentBuild = AppUtil.getVersionCode(this);
//        String client = "android";
//        String q = "needUpgrade";
//        ApiManager.getInstance().versionApi.upgradeRecords(q, currentBuild, client, new RequestCallBack() {
//            @Override
//            public void onSuccess(Headers headers, String response) {
//                try {
//                    versionUpdateModel = JSON.parseObject(response, VersionUpdateModel.class);
//                    if (versionUpdateModel.getBuild() < currentBuild) {
//                        versionUpdateModel.setNeedUpgrade(false);
//                    }
//                    //TEST DATA
//                    versionUpdateModel.setNeedUpgrade(true);
//
//                    MainApplication.getInstance().setVersionUpdateModelCache(versionUpdateModel);
//                    if (checkVersionCallBack != null)


//        checkVersionCallBack.onSuccess();


//                } catch (Exception e) {
//                    ToastUtil.toast(VersionUpdateService.this, "获取版本信息失败");
//                }
//            }
//
//            @Override
//            public void onError(int code, String response) {
//                if (checkVersionCallBack != null) {
//                    checkVersionCallBack.onError();
//                }
//            }
//        });
//    }

    public void doDownLoadTask(String url, String saveFileName) {
        if (!isForceUpdate) {
            if (mNotificationManager == null)
                mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

//        starDownLoadForground();
            CharSequence text = "下载中,请稍后...";

            notificationBuilder = new Notification.Builder(this);
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);  // the status icon
            notificationBuilder.setTicker(text);  // the status text
            notificationBuilder.setWhen(System.currentTimeMillis());  // the time stamp
            notificationBuilder.setContentText(text);  // the contents of the entry
//        notificationBuilder.setContentIntent(contentIntent);  // The intent to send when the entry is clicked
            notificationBuilder.setContentTitle("正在下载更新" + 0 + "%"); // the label of the entry
            notificationBuilder.setProgress(100, 0, false);
            notificationBuilder.setOngoing(true);
            notificationBuilder.setAutoCancel(true);
            final Notification notification = notificationBuilder.getNotification();
            startForeground(NOTIFICATION_ID, notification);
        }


        downLoading = true;

        if (null == fileInfo) {
            fileInfo = new DownloadFileInfo(url, saveFileName, new ProgressCallback() {
                @Override
                public void onProgressMain(int percent, long bytesWritten, long contentLength, boolean done) {
                    LogUtil.d(TAG, "下载进度：" + percent);
                    if (!isForceUpdate) {

                        notificationBuilder.setContentTitle("正在下载更新" + percent + "%"); // the label of the entry
                        notificationBuilder.setProgress(100, percent, false);
                        mNotificationManager.notify(NOTIFICATION_ID, notificationBuilder.getNotification());
                    } else downLoadListener.inProgress(percent, bytesWritten, contentLength, done);


                }

                @Override
                public void onResponseMain(String filePath, HttpInfo info) {
                    if (info.isSuccessful()) {
                        String downloadStatus = fileInfo.getDownloadStatus();
                        if (isForceUpdate&&downloadStatus.equals(DownloadStatus.INIT)) downLoadListener.begain();
                        if (isForceUpdate&&downloadStatus.equals(DownloadStatus.PAUSE)) downLoadListener.pause();
                        if (downloadStatus.equals(DownloadStatus.COMPLETED)) {
                            if (isForceUpdate)downLoadListener.downloadSuccess(filePath);
                            LogUtil.d(TAG,"filePath::"+info.getDownloadFiles().get(0).getSaveFileDir());

                            String dir = info.getDownloadFiles().get(0).getSaveFileDir();
                            String name  = info.getDownloadFiles().get(0).getSaveFileName();
                            LogUtil.d(TAG,"fileName::"+name);
                            installApk(new File(dir,name), VersionUpdateService.this);
                            stopSelf();
                        }
                    } else {
                        if (isForceUpdate)downLoadListener.downloadFailed(info.getRetDetail());
                        stopSelf();

                    }
                }
            });
            if (!TextUtils.isEmpty(saveFileDir)) fileInfo.setSaveFileDir(saveFileDir);
            LogUtil.d(TAG, "saveFileDir" + saveFileDir);
        }


        HttpInfo info = HttpInfo.Builder().addDownloadFile(fileInfo).build();
        OkHttpUtil.Builder().setReadTimeout(120).build(this).doDownloadFileAsync(info);
    }


    //安装apk
    public void installApk(File file, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(Build.VERSION.SDK_INT>=24) { //判读版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri =
                    FileProvider.getUriForFile(context, "com.dkaishu.bucketsofgoogle.fileprovider", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }else{
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
        }
        context.startActivity(intent);

    }

    @Override
    public IBinder onBind(Intent intent) {
        isForceUpdate = true;
        return binder;
    }

    public class LocalBinder extends Binder {
        public VersionUpdateService getService() {
            return VersionUpdateService.this;
        }
    }
}
