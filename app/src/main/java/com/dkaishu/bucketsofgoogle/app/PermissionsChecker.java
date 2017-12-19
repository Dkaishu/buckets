package com.dkaishu.bucketsofgoogle.app;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.dkaishu.bucketsofgoogle.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 检查权限的工具类
 *
 */
public class PermissionsChecker {

    /**
     * 检测权限
     */
    public static void checkPermissions(Activity context,int requestCode, String... permissions) {
        List<String> needRequestPermissionList = findDeniedPermissions(context,permissions);
        if (null != needRequestPermissionList && needRequestPermissionList.size() > 0) {
            ActivityCompat.requestPermissions(context,
                    needRequestPermissionList.toArray(new String[needRequestPermissionList.size()]),requestCode);
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     */
    private static List<String> findDeniedPermissions(Activity context, String[] permissions) {
        List<String> needRequestPermissionList = new ArrayList<String>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(context,
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    context, perm)) {
                needRequestPermissionList.add(perm);
                LogUtil.d("",perm);
            }
        }
        return needRequestPermissionList;
    }

    /**
     * 检测是否所有的权限都已经授权
     *
     */
    public static boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

/*    // 判断权限集合
    public static boolean lacksPermissions(Context context,String... permissions) {
        for (String permission : permissions) {
            if (lacksPermission(context,permission)) {
                LogUtil.d("check",permission);
                return true;
            }
        }
        return false;
    }

    // 判断是否缺少权限
    private static boolean lacksPermission(Context context,String permission) {
        return ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_DENIED;
    }*/
}
