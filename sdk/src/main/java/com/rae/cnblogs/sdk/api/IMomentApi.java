package com.rae.cnblogs.sdk.api;

import com.rae.cnblogs.sdk.Empty;
import com.rae.cnblogs.sdk.JsonBody;
import com.rae.cnblogs.sdk.Parser;
import com.rae.cnblogs.sdk.bean.MomentBean;
import com.rae.cnblogs.sdk.bean.MomentCommentBean;
import com.rae.cnblogs.sdk.parser.MomentCommentParser;
import com.rae.cnblogs.sdk.parser.MomentParser;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 闪存接口
 * Created by ChenRui on 2017/9/25 0025 17:01.
 */
public interface IMomentApi {

    /**
     * 全站闪存
     */
    String MOMENT_TYPE_ALL = "All";

    /**
     * 关注的闪存
     */
    String MOMENT_TYPE_FOLLOWING = "following";

    /**
     * 我自己的闪存
     */
    String MOMENT_TYPE_MY = "my";

    /**
     * 发布闪存
     * [官方不支持图文模式]
     *
     * @param content    发表内容
     * @param publicFlag 取值：1为公开，0为私有
     * @return
     */

    @POST(ApiUrls.API_MOMENT_PUBLISH)
    @FormUrlEncoded
    @Headers({JsonBody.XHR})
    Observable<Empty> publish(@Field("content") String content, @Field("publicFlag") int publicFlag);

    /**
     * 获取闪存列表
     *
     * @param type      类型，参考静态变量MOMENT_TYPE_*，如：{@link #MOMENT_TYPE_ALL}
     * @param page      页码
     * @param timestamp 传当前的时间戳
     */
    @GET(ApiUrls.API_MOMENT_LIST)
    @Headers({JsonBody.XHR})
    @Parser(MomentParser.class)
    Observable<List<MomentBean>> getMoments(@Query("IngListType") String type, @Query("PageIndex") int page, @Query("_") long timestamp);

    /**
     * 获取闪存评论
     *
     * @param ingId     闪存ID
     * @param userAlias 用户ID
     * @param timestamp 传当前的时间戳
     * @return
     */
    @GET(ApiUrls.API_MOMENT_SINGLE_COMMENTS)
    @Headers({JsonBody.XHR})
    @Parser(MomentCommentParser.class)
    Observable<List<MomentCommentBean>> getMomentSingleComments(@Query("ingId") String ingId, @Query("userAlias") String userAlias, @Query("_") long timestamp);


}
