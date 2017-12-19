package com.dkaishu.bucketsofgoogle.main.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dkaishu.bucketsofgoogle.R;
import com.dkaishu.bucketsofgoogle.main.bean.Bucket;

import java.util.List;

/**
 * Created by dks on 2017/12/19.
 */

public class BucketAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView contentTv;
        ProgressBar downloadProgress;

        public VH(View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
            contentTv = itemView.findViewById(R.id.tv_content);
            downloadProgress = itemView.findViewById(R.id.downloadProgress);
        }
    }
}
