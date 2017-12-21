package com.dkaishu.bucketsofgoogle.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.dkaishu.bucketsofgoogle.R;
import com.dkaishu.bucketsofgoogle.app.PermissionsChecker;
import com.dkaishu.bucketsofgoogle.config.PermissionConfig;
import com.dkaishu.bucketsofgoogle.config.PrefConfig;
import com.dkaishu.bucketsofgoogle.net.networkstate.NetInfo;
import com.dkaishu.bucketsofgoogle.net.networkstate.NetworkStateListener;
import com.dkaishu.bucketsofgoogle.net.networkstate.NetworkStateReceiver;
import com.dkaishu.bucketsofgoogle.update.DownloadService;
import com.dkaishu.bucketsofgoogle.utils.LogUtil;
import com.dkaishu.bucketsofgoogle.utils.NetworkUtil;
import com.dkaishu.bucketsofgoogle.utils.PrefUtil;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;

import static com.dkaishu.bucketsofgoogle.app.PermissionsChecker.verifyPermissions;


public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    /**
     * 网络状态监听器
     **/
    private NetworkStateListener networkStateListener;

    protected abstract int initLayout();

    protected abstract void initView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(initLayout());
        ButterKnife.bind(this);
        initView();
        initNetworkStateListener();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isNeedCheckPermission) {
            PermissionsChecker.checkPermissions(this, PERMISSION_REQUEST_CODE, PermissionConfig.permissions);
        }
        MobclickAgent.onResume(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUpdate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
//        ButterKnife.unbind(this);
        //移除网络状态监听
        if (null != networkStateListener) {
            NetworkStateReceiver.removeNetworkStateListener(networkStateListener);
            NetworkStateReceiver.unRegisterNetworkStateReceiver(this);
        }
        //
        if (mService != null && mService.isDownLoading()) {
            mService.onDestroy();
            unbindService(mConnection);
        }
        super.onDestroy();
    }

    /**
     * ****************************************************************************************
     * 初始化网络状态监听器
     */
    private void initNetworkStateListener() {
        NetworkStateReceiver.registerNetworkStateReceiver(this);
        networkStateListener = new NetworkStateListener() {
            @Override
            public void onNetworkState(boolean isNetworkAvailable, NetInfo netInfo) {
                BaseActivity.this.onNetworkState(isNetworkAvailable, netInfo);
            }
        };
        //添加网络状态监听
        NetworkStateReceiver.addNetworkStateListener(networkStateListener);
    }

    /**
     * 网络状态
     *
     * @param isNetworkAvailable 网络是否可用
     * @param netInfo            网络信息
     */
    public void onNetworkState(boolean isNetworkAvailable, NetInfo netInfo) {
        //Todo 网络状态
        if (!isNetworkAvailable)
            Toast.makeText(this, "Network is not available", Toast.LENGTH_LONG).show();
    }

    /**
     * ***************************************************************************************
     * 权限检测
     */

    private static final int PERMISSION_REQUEST_CODE = 0;

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheckPermission = true;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                LogUtil.d("", String.valueOf(paramArrayOfInt));
                showMissingPermissionDialog();
                isNeedCheckPermission = false;
            }
        }
    }

    /**
     * 显示提示信息
     */
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_permission_ok_notifyTitle);
        builder.setMessage(R.string.dialog_permission_ok_notifyMsg);
        // 拒绝, 退出
        builder.setNegativeButton(R.string.dialog_permission_ok_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        builder.setPositiveButton(R.string.dialog_permission_ok_setting,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }

    /**
     * 启动应用的设置
     */
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }


    /**
     * ***************************************************************************************
     * 版本跟新
     */
    private DownloadService mService;
    private boolean mBound = false;
    private String url = "http://dldir1.qq.com/qqfile/qq/QQ8.9.1/20453/QQ8.9.1.exe";
    private String fileName = "QQ8.9.1.exe";


    private void checkUpdate() {
        if (!NetworkUtil.isNetworkAvailable(this)) return;
        if (PrefUtil.getBoolean(this, PrefConfig.PREF_NEED_VERSION_UPDATE, false))
            showUpdateDialog();
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_permission_ok_notifyTitle);
        builder.setMessage(R.string.dialog_update_notifyMsg);
        // 拒绝, 退出
        builder.setNegativeButton(R.string.dialog_update_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
                    }
                });

        builder.setPositiveButton(R.string.dialog_update_setting,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        update();
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            DownloadService.LocalBinder binder = (DownloadService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public void update() {
        Intent intent = new Intent(this, DownloadService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        if (mBound) {
            mService.setDownLoadListener(new DownloadService.DownloadListener() {
                @Override
                public void begain() {
                    LogUtil.d(TAG, "update begain");
                }

                @Override
                public void pause() {
                    LogUtil.d(TAG, "update pause");
                }

                @Override
                public void inProgress(int percent, long bytesWritten, long contentLength, boolean done) {
                    LogUtil.d(TAG, "percent :  " + percent);
                }

                @Override
                public void downloadSuccess(String filePath) {
                    LogUtil.d(TAG, "downloadSuccess :  " + filePath);
                    LogUtil.d(TAG, "mBound :  " + mBound);
                }

                @Override
                public void downloadFailed(String err) {
                    LogUtil.d(TAG, "downloadFailed :  " + err);
                }
            });
            mService.download(url, fileName);
        }
    }
}
