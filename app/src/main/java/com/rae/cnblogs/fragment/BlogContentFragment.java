package com.rae.cnblogs.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.rae.cnblogs.AppRoute;
import com.rae.cnblogs.AppUI;
import com.rae.cnblogs.R;
import com.rae.cnblogs.RaeAnim;
import com.rae.cnblogs.dialog.impl.HintCardDialog;
import com.rae.cnblogs.presenter.CnblogsPresenterFactory;
import com.rae.cnblogs.presenter.IBlogContentPresenter;
import com.rae.cnblogs.sdk.AppGson;
import com.rae.cnblogs.sdk.bean.BlogBean;
import com.rae.cnblogs.sdk.bean.BlogType;
import com.rae.cnblogs.sdk.db.model.UserBlogInfo;
import com.rae.cnblogs.widget.ImageLoadingView;
import com.rae.cnblogs.widget.PlaceholderView;
import com.rae.cnblogs.widget.webclient.RaeJavaScriptBridge;
import com.rae.cnblogs.widget.webclient.RaeWebViewClient;

import butterknife.BindView;


/**
 * 博文内容
 * Created by ChenRui on 2016/12/6 23:39.
 */
public class BlogContentFragment extends WebViewFragment implements IBlogContentPresenter.IBlogContentView, View.OnClickListener {

    @BindView(R.id.view_holder)
    PlaceholderView mPlaceholderView;


    private TextView mLikeView;
    private ImageLoadingView mBookmarksView;
    private ImageLoadingView mLikeAnimView; // 点赞做动画的视图
    private BlogType mBlogType;

    public static BlogContentFragment newInstance(BlogBean blog, BlogType type) {
        Bundle args = new Bundle();
        args.putParcelable("blog", blog);
        args.putString("type", type.getTypeName());
        BlogContentFragment fragment = new BlogContentFragment();
        fragment.setArguments(args);
        return fragment;
    }


    private BlogBean mBlog;
    private IBlogContentPresenter mContentPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.fm_blog_content;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBlog = getArguments().getParcelable("blog");
            mBlogType = BlogType.typeOf(getArguments().getString("type"));
            mContentPresenter = CnblogsPresenterFactory.getBlogContentPresenter(getContext(), mBlogType, this);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    }

    @Override
    public Object getJavascriptApi() {
        return new RaeJavaScriptBridge() {

            @JavascriptInterface
            public String getBlog() {
                return AppGson.get().toJson(mBlog);
            }
        };
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mBlog == null) return;
        mLikeView = (TextView) getActivity().findViewById(R.id.tv_like_badge);
        mBookmarksView = (ImageLoadingView) getActivity().findViewById(R.id.img_content_bookmarks);
        mLikeAnimView = (ImageLoadingView) getActivity().findViewById(R.id.img_content_like);
        getActivity().findViewById(R.id.ll_like).setOnClickListener(this); // 点赞布局
        getActivity().findViewById(R.id.ll_content_bookmarks).setOnClickListener(this); // 收藏布局
        mContentPresenter.loadContent();
    }

    @Override
    public BlogBean getBlog() {
        return mBlog;
    }

    @Override
    public void onLoadContentSuccess(BlogBean blog) {
        mPlaceholderView.dismiss();
        mWebView.loadUrl("file:///android_asset/view.html");
    }

    @Override
    public void onLoadContentFailed(String msg) {
        mPlaceholderView.empty();
    }

    @Override
    public void onLikeError(boolean isCancel, String msg) {
        mLikeView.setEnabled(true);
        mLikeAnimView.stop();
        mLikeView.setVisibility(View.VISIBLE);
        mLikeAnimView.setVisibility(View.GONE);

        RaeAnim.scaleIn(mLikeView);
        AppUI.toastInCenter(getContext(), msg);
    }

    @Override
    public void onLikeSuccess(boolean isCancel) {
        mLikeView.setEnabled(true);
        mLikeView.setSelected(!isCancel);
        if (mLikeView.isSelected()) {
            mLikeAnimView.anim(new Runnable() {
                @Override
                public void run() {
                    mLikeAnimView.setVisibility(View.GONE);
                    mLikeView.setVisibility(View.VISIBLE);
                }
            });
        } else {
            mLikeView.setVisibility(View.VISIBLE);
            mLikeAnimView.setVisibility(View.GONE);
        }


        if (!config().hasLikeGuide()) {
            HintCardDialog dialog = new HintCardDialog(getContext());
            dialog.setMessage(getString(R.string.dialog_tips_post_like));
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    config().likeGuide();
                }
            });
            dialog.show();
        }
    }

    @Override
    public void onBookmarksError(boolean isCancel, String msg) {
        mBookmarksView.setEnabled(true);
        mBookmarksView.stop();
        RaeAnim.scaleIn(mBookmarksView);
        AppUI.toastInCenter(getContext(), msg);
    }

    @Override
    public void onBookmarksSuccess(boolean isCancel) {
        mBookmarksView.setEnabled(true);
        mBookmarksView.setSelected(!isCancel);
        mBookmarksView.anim();
//        RaeAnim.scaleIn(mBookmarksView);
    }

    @Override
    public void onNeedLogin() {
        mLikeView.setEnabled(true);
        mBookmarksView.setEnabled(true);
        mBookmarksView.stop();
        mLikeAnimView.stop();
        mLikeAnimView.setVisibility(View.GONE);
        mLikeView.setVisibility(View.VISIBLE);
        AppRoute.jumpToLogin(getContext());
    }

    @Override
    public void onLoadBlogInfoSuccess(UserBlogInfo infoModel) {
        // 角标处理
        mLikeView.setSelected(infoModel.isLiked());
        mBookmarksView.setSelected(infoModel.isBookmarks());

        // 判断是否为自己的
    }

    @Override
    public BlogType getBlogType() {
        return mBlogType;
    }

    @Override
    public WebViewClient getWebViewClient() {
        return new RaeWebViewClient(mProgressBar) {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                AppRoute.jumpToWebNewTask(view.getContext(), url);
                return true;
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_like:  // 点赞
                mLikeView.setEnabled(false);
                mLikeView.setVisibility(View.GONE);
                mLikeAnimView.setVisibility(View.VISIBLE);
                mLikeAnimView.loading();
                mContentPresenter.doLike(v.isSelected());
                break;

            case R.id.ll_content_bookmarks:  // 收藏
                mBookmarksView.setEnabled(false);
                mBookmarksView.loading();
                mContentPresenter.doBookmarks(v.isSelected());
                break;
        }
    }


    /**
     * 滚动到顶部
     */
    public void scrollToTop() {
        mWebView.scrollTo(0, 0);
    }
}
