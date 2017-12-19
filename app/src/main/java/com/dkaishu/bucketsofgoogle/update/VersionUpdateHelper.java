package com.dkaishu.bucketsofgoogle.update;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;

import com.dkaishu.bucketsofgoogle.utils.LogUtil;
import com.dkaishu.bucketsofgoogle.utils.NetworkUtil;


public class VersionUpdateHelper implements ServiceConnection {
    private Context context;
    private com.dkaishu.bucketsofgoogle.update.VersionUpdateService service;
    private AlertDialog waitForUpdateDialog;
    private ProgressDialog progressDialog;

    private static boolean isCanceled;

    private static boolean isForceUpdate;

    private boolean showDialogOnStart;
    private boolean toastInfo;

    public static final int NEED_UPDATE = 2;
    public static final int DONOT_NEED_UPDATE = 1;
    public static final int CHECK_FAILD = -1;
    public static final int USER_CANCELED = 0;


//    private String url = "http://dldir1.qq.com/qqfile/qq/QQ8.9.1/20453/QQ8.9.1.exe";
    private String url = "http://imtt.dd.qq.com/16891/1A33D1D9F95EE33F4761DC82C474315E.apk?fsname=com.alex.lookwifipassword_3.1.3_49.apk&csr=1bbd";
    private String fileName = "qq.apk";


    private CheckCallBack checkCallBack;

    public interface CheckCallBack {
        void callBack(int code);
    }

    private VersionUpdateHelper(Context context) {
        this.context = context;
    }
    public static VersionUpdateHelper create(Context context){
        return new VersionUpdateHelper(context);

    }

    public void setCheckCallBack(CheckCallBack checkCallBack) {
        this.checkCallBack = checkCallBack;
    }

    public static void resetCancelFlag() {
        isCanceled = false;
    }

    /**
     * 设置非强制更新时,是否显示更新对话框
     *
     * @param showDialogOnStart
     */
    public void setShowDialogOnStart(boolean showDialogOnStart) {
        this.showDialogOnStart = showDialogOnStart;
    }

    /**
     * 是否吐司更新消息
     *
     * @param toastInfo
     */
    public void setToastInfo(boolean toastInfo) {
        this.toastInfo = toastInfo;
    }

    public void startForceUpdateVersion() {
        if (isCanceled)
            return;
        if (isWaitForUpdate() || isWaitForDownload()) {
            return;
        }
        if (service == null && context != null) {
            isForceUpdate = true;
            context.bindService(new Intent(context, com.dkaishu.bucketsofgoogle.update.VersionUpdateService.class), this, Context.BIND_AUTO_CREATE);
            LogUtil.d("VersionUpdateService", "bindService");
        }
    }

    public void startOptionalUpdateVersion(){
        if (isCanceled)
            return;
        if (isWaitForUpdate() || isWaitForDownload()) {
            return;
        }
        if (context!=null ){
            isForceUpdate = false;
            startUpdateDialog();
        }

    }

    private void startUpdateDialog(){
        final AlertDialog.Builder builer = new AlertDialog.Builder(context);
        builer.setTitle("版本升级");
        builer.setMessage("版本升级");
        //当点确定按钮时从服务器上下载新的apk 然后安装
        builer.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (NetworkUtil.isWifiConnected(context)) {
                    if (isForceUpdate){
                        service.doDownLoadTask(url, fileName);

                    }else {
                        Intent intent = new Intent(context, com.dkaishu.bucketsofgoogle.update.VersionUpdateService.class);
                        intent.putExtra("url", url);
                        intent.putExtra("saveFileName", fileName);
                        context.startService(intent);
                    }
                } else {
                    showNotWifiDownloadDialog();
                }
            }
        });

        //当点取消按钮时进行登录
//                if (!versionUpdateModel.isMustUpgrade()) {
        builer.setNegativeButton("稍后更新", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                cancel();
                if (checkCallBack != null) {
                    checkCallBack.callBack(USER_CANCELED);
                }
            }
        });
//                }
        builer.setCancelable(false);
        waitForUpdateDialog = builer.create();
        waitForUpdateDialog.show();

    }

    public void stopUpdateVersion() {
        LogUtil.d("VersionUpdateService", "stopUpdateVersion");
        unBindService();
    }

    private void cancel() {
        isCanceled = true;
        if (isForceUpdate) unBindService();
    }

    private void unBindService() {
        if (isWaitForUpdate() || isWaitForDownload()) {
            return;
        }
        if (service != null && !service.isDownLoading()) {
            LogUtil.d("VersionUpdateService", "unBindService");
            context.unbindService(this);
            service = null;
        }
    }

    private boolean isWaitForUpdate() {
        return waitForUpdateDialog != null && waitForUpdateDialog.isShowing();
    }

    private boolean isWaitForDownload() {
        return progressDialog != null && progressDialog.isShowing();
    }

    private void showNotWifiDownloadDialog() {
        final AlertDialog.Builder builer = new AlertDialog.Builder(context);
        builer.setTitle("下载新版本");
        builer.setMessage("检查到您的网络处于非wifi状态,下载新版本将消耗一定的流量,是否继续下载?");
        builer.setNegativeButton(isForceUpdate ? "退出" : "以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //exit app
//                boolean mustUpdate = service.getVersionUpdateModel().isMustUpgrade();
                dialog.cancel();
                if (isForceUpdate) {
                    //Todo tuichu
                } else unBindService();
//                if (mustUpdate) {
//                    MainApplication.getInstance().exitApp();
//                }
            }
        });
        builer.setPositiveButton("继续下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (isForceUpdate) service.doDownLoadTask(url, fileName);
                else {
                    Intent intent = new Intent(context, com.dkaishu.bucketsofgoogle.update.VersionUpdateService.class);
                    intent.putExtra("url", url);
                    intent.putExtra("fileName", fileName);
                    context.startService(intent);
                }
            }
        });
        builer.setCancelable(false);
        builer.show();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((com.dkaishu.bucketsofgoogle.update.VersionUpdateService.LocalBinder) binder).getService();
        startUpdateDialog();

        service.setDownLoadListener(new com.dkaishu.bucketsofgoogle.update.VersionUpdateService.DownLoadListener() {
            @Override
            public void begain() {
//                if (isForceUpdate) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("正在下载更新");
                progressDialog.show();
//                }
            }

            @Override
            public void pause() {

            }

            @Override
            public void inProgress(int percent, long bytesWritten, long contentLength, boolean done) {
                if (percent > 0 && null == progressDialog) {
                    progressDialog = new ProgressDialog(context);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setMessage("正在下载更新");
                    progressDialog.show();
                }

                if (progressDialog != null) {
                    progressDialog.setMax(100);
                    progressDialog.setProgress(percent);
                    LogUtil.d("+++", String.valueOf(percent));
                }
                LogUtil.d("+555+", String.valueOf(percent));

            }

            @Override
            public void downloadSuccess(String filePath) {
                if (progressDialog != null)
                    progressDialog.cancel();
                service.setDownLoading(false);
                unBindService();
            }

            @Override
            public void downloadFailed(String err) {
                if (progressDialog != null)
                    progressDialog.cancel();
                service.setDownLoading(false);
                unBindService();
            }
        });
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.cancel();

        if (waitForUpdateDialog != null && waitForUpdateDialog.isShowing())
            waitForUpdateDialog.cancel();

        if (service != null) {
            service.setDownLoadListener(null);
//            service.setCheckVersionCallBack(null);
        }
        service = null;
        context = null;
    }

}
