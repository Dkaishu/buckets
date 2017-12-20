package com.dkaishu.bucketsofgoogle.main.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dkaishu.bucketsofgoogle.R;
import com.dkaishu.bucketsofgoogle.main.bean.Bucket;
import com.dkaishu.bucketsofgoogle.utils.LogUtil;
import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.DownloadStatus;
import com.okhttplib.bean.DownloadFileInfo;
import com.okhttplib.callback.ProgressCallback;

import java.util.List;

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
        vh.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vh.fileInfo == null) {
                    download(vh.fileInfo, list.get(position).getApkURL(), list.get(position).getApkName()
                            , new onDownloadProgress() {
                                @Override
                                public void onProgress(int percent) {
                                    vh.downloadProgress.setProgress(percent);
                                }
                            });
                } else {
                    if (vh.fileInfo.getDownloadStatus().equals(DownloadStatus.COMPLETED)) return;
                    if (vh.fileInfo.getDownloadStatus().equals(DownloadStatus.DOWNLOADING))
                        vh.fileInfo.setDownloadStatus(DownloadStatus.PAUSE);
                    if (vh.fileInfo.getDownloadStatus().equals(DownloadStatus.PAUSE))
                        vh.fileInfo.setDownloadStatus(DownloadStatus.DOWNLOADING);

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
        ImageView iv;
        TextView contentTv;
        ProgressBar downloadProgress;
        DownloadFileInfo fileInfo;

        public VH(View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
            contentTv = itemView.findViewById(R.id.tv_content);
            downloadProgress = itemView.findViewById(R.id.downloadProgress);
        }
    }


    private void download(DownloadFileInfo fileInfo, String url, String saveFileName, final onDownloadProgress listerner) {
        if (null == fileInfo)
            fileInfo = new DownloadFileInfo(url, saveFileName, new ProgressCallback() {
                @Override
                public void onProgressMain(int percent, long bytesWritten, long contentLength, boolean done) {
                    listerner.onProgress(percent);
                    LogUtil.d(TAG, "下载进度：" + percent);
                }

                @Override
                public void onResponseMain(String filePath, HttpInfo info) {
                    if (info.isSuccessful()) {
//                        tvResult.setText(info.getRetDetail()+"\n下载状态："+fileInfo.getDownloadStatus());
                    } else {
                        Toast.makeText(mContext, info.getRetDetail(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        HttpInfo info = HttpInfo.Builder().addDownloadFile(fileInfo).build();
        OkHttpUtil.Builder().setReadTimeout(120).build(this).doDownloadFileAsync(info);
    }

    public interface onDownloadProgress {
        void onProgress(int percent);
    }
}
