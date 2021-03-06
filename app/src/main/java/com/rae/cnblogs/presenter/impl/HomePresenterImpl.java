package com.rae.cnblogs.presenter.impl;

import android.content.Context;

import com.rae.cnblogs.presenter.IHomePresenter;
import com.rae.cnblogs.sdk.ApiDefaultObserver;
import com.rae.cnblogs.sdk.bean.CategoryBean;
import com.rae.swift.Rx;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页处理
 * Created by ChenRui on 2016/12/2 00:25.
 */
public class HomePresenterImpl extends BasePresenter<IHomePresenter.IHomeView> implements IHomePresenter {

    private List<CategoryBean> mCategoryData;


    public HomePresenterImpl(Context context, IHomeView view) {
        super(context, view);
    }

    @Override
    public void start() {

        // 加载分类
        createObservable(getApiProvider().getCategoriesApi().getHomeCategories())
                .subscribe(new ApiDefaultObserver<List<CategoryBean>>() {
                    @Override
                    protected void onError(String message) {
                        // 发生错误至少加载首页这个分类
                        List<CategoryBean> data = new ArrayList<>();

                        CategoryBean home = new CategoryBean();
                        home.setCategoryId("808");
                        home.setParentId("0");
                        home.setName("首页");
                        home.setType("SiteHome");

                        CategoryBean recommend = new CategoryBean();
                        recommend.setCategoryId("-2");
                        recommend.setParentId("0");
                        recommend.setName("推荐");
                        recommend.setType("Picked");

                        data.add(home);
                        data.add(recommend);

                        mView.onLoadCategory(data);
                    }

                    @Override
                    protected void accept(List<CategoryBean> data) {
                        mCategoryData = data;
                        mView.onLoadCategory(data);
                    }
                });

        // 加载热门搜索
        createObservable(getApiProvider().getSearchApi().hotSearch())
                .subscribe(new ApiDefaultObserver<List<String>>() {
                    @Override
                    protected void onError(String message) {

                    }

                    @Override
                    protected void accept(List<String> data) {
                        if (!Rx.isEmpty(data)) {
                            mView.onLoadHotSearchData(data.get(0));
                        }
                    }
                });
    }
}
