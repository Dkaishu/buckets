package com.dkaishu.bucketsofgoogle.main.bean;

import android.support.annotation.Keep;

import java.util.List;

@Keep
public class Bucket {
    private List<Tip> tips;
    private List<App> apps;
    private VersionInfo versionInfo;

    public List<Tip> getTips() {
        return tips;
    }

    public void setTips(List<Tip> tips) {
        this.tips = tips;
    }

    public List<App> getApps() {
        return apps;
    }

    public void setApps(List<App> apps) {
        this.apps = apps;
    }

    public VersionInfo getVersionInfo() {
        return versionInfo;
    }

    public void setVersionInfo(VersionInfo versionInfo) {
        this.versionInfo = versionInfo;
    }
    @Keep
    public static class Tip {
        String tipString;

        public String getTipString() {
            return tipString;
        }

        public void setTipString(String tipString) {
            this.tipString = tipString;
        }
    }

    @Keep
    public static class App{
        private String title;
        private String content;
        private String apkURL;
        private String apkName;
        private String pkg;
        private String imageUrl;

        public String getPkg() {
            return pkg;
        }

        public void setPkg(String pkg) {
            this.pkg = pkg;
        }

        public String getApkName() {
            return apkName;
        }

        public void setApkName(String apkName) {
            this.apkName = apkName;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getApkURL() {
            return apkURL;
        }

        public void setApkURL(String apkURL) {
            this.apkURL = apkURL;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }


    public static class VersionInfo{
        private String versionCode;
        private String forceUpdate;
        private String updateUrl;

        public String getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(String versionCode) {
            this.versionCode = versionCode;
        }

        public String getForceUpdate() {
            return forceUpdate;
        }

        public void setForceUpdate(String forceUpdate) {
            this.forceUpdate = forceUpdate;
        }

        public String getUpdateUrl() {
            return updateUrl;
        }

        public void setUpdateUrl(String updateUrl) {
            this.updateUrl = updateUrl;
        }
    }
}
