package com.dkaishu.bucketsofgoogle.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.dkaishu.bucketsofgoogle.utils.LogUtil;
import com.dkaishu.bucketsofgoogle.widget.LoadingDialog;
import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.DownloadStatus;
import com.okhttplib.bean.DownloadFileInfo;
import com.okhttplib.callback.ProgressCallback;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2017/9/20.
 */

public class DownloadService extends Service {

    private DownloadFileInfo fileInfo;
    private String url = "http://dldir1.qq.com/qqfile/qq/QQ8.9.1/20453/QQ8.9.1.exe";

    public DownloadService() {
    }

//    @Override
//    protected void onHandleIntent(Intent intent) {
//        String urlToDownload = intent.getStringExtra("url");
//        String fileDestination = intent.getStringExtra("dest");
//        download();
//    }


    private LocalBinder binder = new LocalBinder();

    private DownloadListener downLoadListener;
    private boolean downLoading;
    private int progress;

    private NotificationManager mNotificationManager;
    //    private NotificationUpdaterThread notificationUpdaterThread;
    private Notification.Builder notificationBuilder;
    private final int NOTIFICATION_ID = 100;

//    private VersionUpdateModel versionUpdateModel;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "onCreate called");
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy called");
        setDownLoadListener(null);
//        setCheckVersionCallBack(null);
//        stopDownLoadForground();
//        if (mNotificationManager != null)
//            mNotificationManager.cancelAll();
        downLoading = false;
    }


    public void download(String url,String saveFileName) {
        if (null == fileInfo)
            fileInfo = new DownloadFileInfo(url, saveFileName, new ProgressCallback() {
                @Override
                public void onProgressMain(int percent, long bytesWritten, long contentLength, boolean done) {
                    LogUtil.d(TAG, "下载进度：" + percent);
                    downLoadListener.inProgress(percent, bytesWritten, contentLength, done);
                }

                @Override
                public void onResponseMain(String filePath, HttpInfo info) {
                    if (info.isSuccessful()) {
                        String downloadStatus = fileInfo.getDownloadStatus();
                        if (downloadStatus.equals(DownloadStatus.INIT)) downLoadListener.begain();
                        if (downloadStatus.equals(DownloadStatus.PAUSE)) downLoadListener.pause();
                        if (downloadStatus.equals(DownloadStatus.COMPLETED))
                            downLoadListener.downloadSuccess(filePath);
                    } else {
                        downLoadListener.downloadFailed(info.getRetDetail());
                    }
                }
            });

        HttpInfo info = HttpInfo.Builder().addDownloadFile(fileInfo).build();
        OkHttpUtil.Builder().setReadTimeout(120).build(this).doDownloadFileAsync(info);
    }

    public void showUpdateDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        // builder.setIcon(R.drawable.icon);
//        builder.setTitle("有新版本");
//        builder.setMessage(mAppVersion.getUpdateMessage());
//        builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                downLoadApk();
//            }
//        });
//        builder.setNegativeButton("忽略", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//
//            }
//        });
//        builder.show();
        LoadingDialog dialog = new LoadingDialog(this);
        dialog.showDialog();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }

    public boolean isDownLoading() {
        return downLoading;
    }

    public void setDownLoading(boolean downLoading) {
        this.downLoading = downLoading;
    }

    public interface DownloadListener {
        void begain();

        void pause();

        void inProgress(int percent, long bytesWritten, long contentLength, boolean done);

        void downloadSuccess(String filePath);

        void downloadFailed(String err);
    }

    public void setDownLoadListener(DownloadListener downLoadListener) {
        this.downLoadListener = downLoadListener;
    }

}
