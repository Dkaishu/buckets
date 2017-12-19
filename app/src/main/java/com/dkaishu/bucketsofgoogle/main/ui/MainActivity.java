package com.dkaishu.bucketsofgoogle.main.ui;

import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dkaishu.bucketsofgoogle.R;
import com.dkaishu.bucketsofgoogle.base.BaseActivity;
import com.dkaishu.bucketsofgoogle.main.adapter.BucketAdapter;
import com.dkaishu.bucketsofgoogle.main.bean.Bucket;
import com.dkaishu.bucketsofgoogle.utils.LogUtil;
import com.dkaishu.scrolltextview.ScrollTextView;
import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.callback.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class MainActivity extends BaseActivity {

    @BindView(R.id.rv_image_flow)
    RecyclerView rvImageFlow;
    @BindView(R.id.srl_image_flow)
    SwipeRefreshLayout srlImageFlow;
    @BindView(R.id.stv_tip)
    ScrollTextView stvTip;

    List<String> textList = new ArrayList<>();
    private List<Bucket.App> apps = new ArrayList<>();
    private List<Bucket.Tip> tips = new ArrayList<>();
    private BucketAdapter adapter;
    private Bucket.VersionInfo versionInfo;


    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        adapter = new BucketAdapter(this, apps);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvImageFlow.setLayoutManager(mLayoutManager);
        rvImageFlow.setAdapter(adapter);
        query();

    }

    String url = "";

    private void query() {
        srlImageFlow.setRefreshing(true);
        OkHttpUtil.getDefault(this).doGetAsync(
                HttpInfo.Builder().setUrl(url).build(),
                new Callback() {
                    @Override
                    public void onFailure(HttpInfo info) throws IOException {
                        srlImageFlow.setRefreshing(false);
                        String result = info.getRetDetail();
                        LogUtil.e("ParamForm: " + info.getParamForm() + "\n" + "RetDetail" + result);
                    }

                    @Override
                    public void onSuccess(HttpInfo info) throws IOException {
                        srlImageFlow.setRefreshing(false);
                        String result = info.getRetDetail();
                        //GSon解析
                        Bucket bucket = info.getRetDetail(Bucket.class);
                        apps.clear();
                        apps.addAll(bucket.getApps());
                        tips.clear();
                        tips.addAll(bucket.getTips());
                        versionInfo = bucket.getVersionInfo();
                        setTipContent();
                        adapter.notifyDataSetChanged();
                    }
                });
    }


    private void setTipContent() {
        for (int i = 0; i < tips.size(); i++) {
            textList.add(tips.get(i).getTipString());
        }
        stvTip.setScrollTime(500);//ms
        stvTip.setSpanTime(10000);//ms
        stvTip.setTextContent(textList);
    }

    static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
}
