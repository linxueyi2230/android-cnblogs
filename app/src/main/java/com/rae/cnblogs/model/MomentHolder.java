package com.rae.cnblogs.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rae.cnblogs.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 闪存
 * Created by ChenRui on 2017/10/27 0027 10:50.
 */
public class MomentHolder extends SimpleViewHolder {

//    @BindView(R.id.ll_blog_author_layout)
//    public View authorLayout;

    @BindView(R.id.img_blog_avatar)
    public ImageView avatarView;

    @BindView(R.id.tv_blog_author)
    public TextView authorView;

    @BindView(R.id.tv_blog_summary)
    public TextView summaryView;

    @BindView(R.id.tv_blog_date)
    public TextView dateView;


    public TextView commentView;

    @BindView(R.id.recycler_view)
    public RecyclerView mRecyclerView;

    public View deleteView; // 删除
    public Button followView; // 关注
    public ImageView thumbView; // 单张图片
    public View androidTagView; // 客户端标志


//    @BindView(R.id.img_blog_list_large_thumb)
//    public ImageView largeThumbView;
//
//    @BindView(R.id.layout_blog_list_thumb)
//    public View thumbLayout;
//
//    @BindView(R.id.img_blog_list_thumb_one)
//    public ImageView thumbOneView;
//
//    @BindView(R.id.img_blog_list_thumb_two)
//    public ImageView thumbTwoView;
//
//    @BindView(R.id.img_blog_list_thumb_three)
//    public ImageView thumbThreeView;

    public MomentHolder(View itemView) {
        super(itemView);
        deleteView = itemView.findViewById(R.id.img_close);
        thumbView = itemView.findViewById(R.id.img_thumb);
        followView = itemView.findViewById(R.id.btn_blogger_follow);
        androidTagView = itemView.findViewById(R.id.tv_android_tag);
        commentView = itemView.findViewById(R.id.tv_blog_comment);
        ButterKnife.bind(this, itemView);
    }
}
