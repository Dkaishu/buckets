package com.dkaishu.bucketsofgoogle.update;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.dkaishu.bucketsofgoogle.utils.LogUtil;

public class UpdateManager {
    DownloadService mService;
    boolean mBound = false;
    private static final String TAG = "UpdateManager";

    private String url = "http://dldir1.qq.com/qqfile/qq/QQ8.9.1/20453/QQ8.9.1.exe";
    private String fileName = "QQ8.9.1.exe";

    private UpdateManager() {
    }

    public static UpdateManager create() {
        return new UpdateManager();
    }

    protected void onStart(Context mActivity) {
        // Bind to LocalService
        Intent intent = new Intent(mActivity, DownloadService.class);
//        mActivity.startService(intent);
        mActivity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    protected void onStop(Context mActivity) {
        // Unbind from the service
        if (mBound) {
            mActivity.unbindService(mConnection);
            mBound = false;
        }
    }

    public void optionalUpdate() {

    }

    public void forceUpdate(final Context mActivity) {
        Intent intent = new Intent(mActivity, DownloadService.class);
//        mActivity.startService(intent);
        mActivity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

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
                    onStop(mActivity);
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

    /** Called when a button is clicked (the button in the layout file attaches to
     * this method with the android:onClick attribute) */
//    public void onButtonClick(View v) {
//        if (mBound) {
//            // Call a method from the LocalService.
//            // However, if this call were something that might hang, then this request should
//            // occur in a separate thread to avoid slowing down the activity performance.
//            int num = mService.getRandomNumber();
//            Toast.makeText(this, "number: " + num, Toast.LENGTH_SHORT).show();
//        }
//    }

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

}

