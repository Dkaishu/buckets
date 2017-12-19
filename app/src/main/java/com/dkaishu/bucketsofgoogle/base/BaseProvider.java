package com.dkaishu.bucketsofgoogle.base;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.dkaishu.bucketsofgoogle.app.AppCrashHandler;


/**
 *
 * Description : 采用占位方式初始化第三方组件（onCreate比Application的onCreate先执行）
 */
public class BaseProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        //获取Application上下文
        com.dkaishu.bucketsofgoogle.net.httpUtil.init(this.getContext());
        AppCrashHandler.getInstance().init(this.getContext());

        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
