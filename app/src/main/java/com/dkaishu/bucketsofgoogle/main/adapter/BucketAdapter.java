package com.dkaishu.bucketsofgoogle.main.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dkaishu.bucketsofgoogle.R;
import com.dkaishu.bucketsofgoogle.main.bean.Bucket;
import com.dkaishu.bucketsofgoogle.main.main.BucketUtils;
import com.dkaishu.bucketsofgoogle.utils.FileUtils;
import com.dkaishu.bucketsofgoogle.utils.LogUtil;
import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.DownloadStatus;
import com.okhttplib.bean.DownloadFileInfo;
import com.okhttplib.callback.ProgressCallback;

import java.io.File;
import java.util.List;

import static com.dkaishu.bucketsofgoogle.config.PathConfig.SERVER_URL;
import static com.dkaishu.bucketsofgoogle.utils.FileUtils.installApk;

/**
 * Created by dks on 2017/12/19.
 */
public class BucketAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "BucketAdapter";
    private List<Bucket.App> list;
    private Context mContext;

    public BucketAdapter(Context mContext, List<Bucket.App> list) {
        this.list = list;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recycler_app, null);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final VH vh = (VH) holder;
        vh.contentTv.setText(list.get(position).getTitle());
        String imageURL = BucketUtils.getURL(SERVER_URL + "image/" + list.get(position).getImageUrl());
        LogUtil.e("imageURL : position:  " + position + "****" + imageURL);
        Glide.with(mContext).load(imageURL).into(vh.iv);
        boolean isInstalled = FileUtils.isAppInstalled(mContext, list.get(position).getPkg());
        Glide.with(mContext).load(isInstalled ? R.drawable.ic_download_done : R.drawable.ic_download_start).into(vh.indicater);
        vh.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String apkURL = BucketUtils.getURL(SERVER_URL + "app/" + list.get(position).getApkURL());
                if (vh.fileInfo == null) {
                    Log.e(TAG, list.get(position).getImageUrl());
                    Log.e(TAG, apkURL);
                    download(vh, apkURL, list.get(position).getApkName()
                            , new onDownloadProgress() {
                                @Override
                                public void onProgress(int percent) {
                                    vh.downloadProgress.setProgress(percent);
                                }

                                @Override
                                public void onSuccessful(String filePath, HttpInfo info) {
                                    String dir = info.getDownloadFiles().get(0).getSaveFileDir();
                                    String name = info.getDownloadFiles().get(0).getSaveFileName();
                                    installApk(new File(dir, name), mContext);
                                }
                            });
                    Glide.with(mContext).load(R.drawable.ic_download_going).into(vh.indicater);
                } else {
                    if (vh.fileInfo.getDownloadStatus().equals(DownloadStatus.COMPLETED)) {
                        Glide.with(mContext).load(R.drawable.ic_download_done).into(vh.indicater);
                        return;
                    }
                    if (vh.fileInfo.getDownloadStatus().equals(DownloadStatus.DOWNLOADING)) {
                        vh.fileInfo.setDownloadStatus(DownloadStatus.PAUSE);
                        Glide.with(mContext).load(R.drawable.ic_download_pause).into(vh.indicater);
                        return;
                    }
                    if (vh.fileInfo.getDownloadStatus().equals(DownloadStatus.PAUSE)) {
                        vh.fileInfo.setDownloadStatus(DownloadStatus.DOWNLOADING);
                        Glide.with(mContext).load(R.drawable.ic_download_going).into(vh.indicater);
                    }

                    HttpInfo info = HttpInfo.Builder().addDownloadFile(vh.fileInfo).build();
                    OkHttpUtil.Builder().setReadTimeout(120).build(this).doDownloadFileAsync(info);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView iv, indicater;
        TextView contentTv;
        ProgressBar downloadProgress;
        DownloadFileInfo fileInfo;

        VH(View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
            contentTv = itemView.findViewById(R.id.tv_content);
            downloadProgress = itemView.findViewById(R.id.downloadProgress);
            indicater = itemView.findViewById(R.id.iv_download_indicater);
        }
    }


    private void download(VH vh, String url, String saveFileName, final onDownloadProgress listerner) {
        if (null == vh.fileInfo)
            vh.fileInfo = new DownloadFileInfo(url, saveFileName, new ProgressCallback() {
                @Override
                public void onProgressMain(int percent, long bytesWritten, long contentLength, boolean done) {
                    listerner.onProgress(percent);
                    LogUtil.d(TAG, "下载进度：" + percent);
                }

                @Override
                public void onResponseMain(String filePath, HttpInfo info) {
                    if (info.isSuccessful()) {
                        listerner.onSuccessful(filePath, info);
//                        tvResult.setText(info.getRetDetail()+"\n下载状态："+fileInfo.getDownloadStatus());
                    } else {
                        Toast.makeText(mContext, info.getRetDetail(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        HttpInfo info = HttpInfo.Builder().addDownloadFile(vh.fileInfo).build();
        OkHttpUtil.Builder().setReadTimeout(120).build(url).doDownloadFileAsync(info);
    }

    public interface onDownloadProgress {
        void onProgress(int percent);

        void onSuccessful(String filePath, HttpInfo info);
    }
}
