package com.dkaishu.bucketsofgoogle.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * this fragment will not preLoad by viewPager.
 * it would load data every time when we switch to this page.
 * you also set reload or not.
 * Created by Administrator on 2017/8/15.
 */

public abstract class LazyLoadFragment extends Fragment {
    protected BaseActivity context;
    protected View savedView;
    protected boolean isVisible = false, isPrepared = false, isloaded = false, reload = false;

    public boolean isReload() {
        return reload;
    }

    /**
     *
     * @param reload 可见时，是否重新 load
     */
    public void setReload(boolean reload) {
        this.reload = reload;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (this.context == null) {
            this.context = (BaseActivity) context;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (this.context == null) {
            this.context = (BaseActivity) activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedView != null) return savedView;
        View view = inflater.inflate(setLayout(), container, false);
//        ButterKnife.bind(this, view);
        isPrepared = true;
        savedView = view;
        if (getUserVisibleHint()){
            lazyLoad();
            isloaded = true;
        }
        return savedView;
    }

    // This method may be called outside of the fragment lifecycle.
    // and thus has no ordering guarantees with regard to fragment lifecycle method calls.
    //note :会在onCreateView之前调用
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isPrepared) {
            isVisible = true;
            if (!isloaded || reload){
                lazyLoad();
                isloaded = true;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
//        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        isPrepared = false;
    }

    /**
     * @return your fragment layout resource id .
     */
    protected abstract int setLayout();

    /**
     *
     * put your load function here.
     */
    protected abstract void lazyLoad();
}
