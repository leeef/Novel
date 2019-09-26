package com.bx.philosopher.net;


import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.model.bean.response.BookBean;
import com.bx.philosopher.model.bean.response.BookDetailBean;
import com.bx.philosopher.model.bean.response.BookMark;
import com.bx.philosopher.model.bean.response.BookMarkPackage;
import com.bx.philosopher.model.bean.response.BookPackage;
import com.bx.philosopher.model.bean.response.CardBean;
import com.bx.philosopher.model.bean.response.ExploreBanner;
import com.bx.philosopher.model.bean.response.GiftCardBean;
import com.bx.philosopher.model.bean.response.IntroduceBean;
import com.bx.philosopher.model.bean.response.LibraryBannerBean;
import com.bx.philosopher.model.bean.response.LoginBean;
import com.bx.philosopher.model.bean.response.PersonInfo;
import com.bx.philosopher.model.bean.response.RecordBean;
import com.bx.philosopher.model.bean.response.SubscribeBean;
import com.bx.philosopher.model.bean.response.UserInfo;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * @Description: 所有的请求接口
 * @Author: leeeef
 * @CreateDate: 2019/5/17 17:07
 */
public interface RequestApi {


    //短信验证接口
    @POST("/api/verification")
    @FormUrlEncoded
    Observable<BaseResponse<String>> getMessageCode(@Field("phone") String phone, @Field("qh") String qh);

    //短信注册接口
    @POST("/api/register")
    @FormUrlEncoded
    Observable<BaseResponse<Object>> loginByCode(@Field("phone") String phone, @Field("code") String code, @Field("password") String password);

    //探索上面卡片请求
    @GET("/api/explorebanner")
    Observable<BaseResponse<ExploreBanner>> getExploreBanner();

    //探索上面卡片more请求  1=>畅销书推荐 2=>新书推荐 3=>今日推荐 4=>礼品卡 5=>收藏量列表
    @POST("/api/explorebannerlist")
    @FormUrlEncoded
    Observable<BaseResponse<List<BookBean>>> getExploreBannerMore(@Field("type") int type);

    @POST("/api/explorebannerlist")
    @FormUrlEncoded
    Observable<BaseResponse<List<CardBean>>> getCardBannerMore(@Field("type") int type);

    //探索页面下边列表请求
    @GET("/api/explorelist")
    Observable<BaseResponse<List<BookPackage>>> getExploreList();

    //探索页面排行详情 type 1 2 3 4
    @POST("/api/exploretop")
    @FormUrlEncoded
    Observable<BaseResponse<List<BookBean>>> getExploreRankList(@Field("type") int type);

    //探索图书详情
    @POST("/api/exploredetails")
    @FormUrlEncoded
    Observable<BaseResponse<BookDetailBean>> getExploreBookDetail(@Field("bid") int bid, @Field("uid") int uid);

    //获取探索书籍  目录接口
    @POST("/api/explorecatalog")
    @FormUrlEncoded
    Observable<BaseResponse<List<String>>> getExploreBookDetailCatalog(@Field("uid") int uid, @Field("bid") int bid);

    //获取探索书籍  章节内容
    @POST("/api/explorechapter")
    @FormUrlEncoded
    Single<BaseResponse<JsonObject>> getExploreBookChapter(@Field("bid") int bid, @Field("chapter") String chapter);

    //获取图书馆书籍  章节内容
    @POST("/api/mlChapter")
    @FormUrlEncoded
    Single<BaseResponse<JsonObject>> getLibraryBookChapter(@Field("id") int id, @Field("chapter") String chapter);

    //获取探索书籍内容接口 试读
    @POST("/api/explorecontent")
    @FormUrlEncoded
    Observable<BaseResponse<JsonElement>> getExploreBookDetailContent(@Field("bid") int bid);

    //探索页面分类列表详情接口
    @POST("/api/explorelistall")
    @FormUrlEncoded
    Observable<BaseResponse<List<BookBean>>> getExploreTypeList(@Field("cid") int cid);

    //获取图书馆页面上面banner数据
    @GET("/api/banner")
    Observable<BaseResponse<List<LibraryBannerBean>>> getLibraryBanner();

    //获取图书馆页面列表数据
    @GET("/api/libraryList")
    Observable<BaseResponse<List<BookPackage>>> getLibraryList();


    //获取图书馆推荐分类列表
    @POST("/api/topList")
    @FormUrlEncoded
    Observable<BaseResponse<List<BookBean>>> getLibraryRecommendList(@Field("id") int id);

    //获取图书馆分类列表接口
    @POST("/api/listDetail")
    @FormUrlEncoded
    Observable<BaseResponse<List<BookBean>>> getLibraryTypeList(@Field("type") int type);

    //获取图书馆书籍详情接口
    @POST("/api/detail")
    @FormUrlEncoded
    Observable<BaseResponse<BookDetailBean>> getLibraryBookDetail(@Field("id") int id, @Field("uid") int uid);

    //获取图书馆书籍详情分类列表接口
    @POST("/api/libraryType")
    @FormUrlEncoded
    Observable<BaseResponse<List<BookBean>>> getLibraryBookDetailTypeList(@Field("type") int type);

    //图书馆书籍  want_to_see
    @POST("/api/libraryCollect")
    @FormUrlEncoded
    Observable<BaseResponse<String>> getLibraryBookDetailWantSee(@Field("id") int id, @Field("uid") int uid);

    //图书馆书籍目录
    @POST("/api/catalogue")
    @FormUrlEncoded
    Observable<BaseResponse<List<String>>> getLibraryBookCatelog(@Field("id") int id);


    //获取图书馆所有分类接口
    @GET("/api/classify")
    Observable<BaseResponse<List<BookPackage>>> getAllCategory();

    //获取图书馆搜索接口 搜索(书名、作者)
    @POST("/api/search")
    @FormUrlEncoded
    Observable<BaseResponse<List<BookBean>>> getLibrarySearch(@Field("title") String title);

    //获取书架
    @POST("/api/mybookshelf")
    @FormUrlEncoded
    Observable<BaseResponse<List<BookBean>>> getBookShelf(@Field("uid") int uid);

    //获取书架
    @POST("/api/bookshelf")
    @FormUrlEncoded
    Observable<BaseResponse<Object>> addBookToShelf(@Field("uid") int uid, @Field("bid") int bid);

    //书架删除 mid 书籍id（数组格式） uid 用户id
    @POST("/api/delbookshelf")
    @FormUrlEncoded
    Observable<BaseResponse<Integer>> deleteBookFromBookShelf(@Field("uid") int uid, @Field("bid[]") List<Integer> bid);

    //平台介绍
    @GET("/api/platform")
    Observable<BaseResponse<IntroduceBean>> getIntroduce();

    //    //平台建议 uid int content string phone string	 cover string
    @POST("/api/proposal")
    @FormUrlEncoded
    Observable<BaseResponse<JsonElement>> proposal(@Field("uid") int uid, @Field("content") String content, @Field("phone") String phone, @Field("cover") String cover);


    //添加书签
    // uid	是	int	用户id
    //bid	是	int	书籍id
    //chapter	是	string	书籍目录名称
    //site	是	int	书签标记位置
    //content	是	string	书签标记内容
    @POST("/api/bookmark")
    @FormUrlEncoded
    Observable<BaseResponse<Boolean>> setBookMark(@Field("uid") int uid, @Field("bid") int bid, @Field("chapter") String chapter
            , @Field("site") int site, @Field("content") String content);

    //自动书签
    // uid	是	int	用户id
    //bid	是	int	书籍id
    //chapter	是	string	书籍目录名称
    //site	是	int	书签标记位置
    //content	是	string	书签标记内容
    @POST("/api/autobookmark")
    @FormUrlEncoded
    Observable<BaseResponse<Boolean>> setAutoBookMark(@Field("uid") int uid, @Field("bid") int bid, @Field("chapter") String chapter
            , @Field("site") int site, @Field("content") String content);

    //添加笔记
//    uid	是	int	用户id
//    bid	是	int	书籍id
//    chapter	是	string	章节名称
//    startsite	是	int	笔记标示开始位置
//    endsite	是	int	笔记标示结束位置
//    content	是	string	笔记标示内容
//    note	是	string	笔记内容
    @POST("/api/note")
    @FormUrlEncoded
    Observable<BaseResponse<Boolean>> setBookNote(@Field("uid") int uid, @Field("bid") int bid, @Field("chapter") String chapter
            , @Field("startsite") int startsite, @Field("endsite") int endsite, @Field("content") String content, @Field("note") String note);

    //删除书签
    @POST("/api/bookmarkdel")
    @FormUrlEncoded
    Observable<BaseResponse<Integer>> deleteBookMark(@Field("uid") int uid, @Field("mid[]") List<Integer> mid);

    //删除笔记
    @POST("/api/notedel")
    @FormUrlEncoded
    Observable<BaseResponse<Integer>> deleteBookNote(@Field("uid") int uid, @Field("nid") int nid);

    //获取书签
    // uid	是	int	用户id
    //bid	是	int	书籍id
    @POST("/api/bookmarkdetails")
    @FormUrlEncoded
    Observable<BaseResponse<List<BookMarkPackage>>> getBookMark(@Field("uid") int uid, @Field("bid") int bid);

    //获取笔记
    // uid	是	int	用户id
    //bid	是	int	书籍id
    @POST("/api/notedetails")
    @FormUrlEncoded
    Observable<BaseResponse<List<BookMarkPackage>>> getBookNote(@Field("uid") int uid, @Field("bid") int bid);

    //获取自动书签
    // uid	是	int	用户id
    //bid	是	int	书籍id
    @POST("/api/autobookmarkdetails")
    @FormUrlEncoded
    Observable<BaseResponse<BookMark>> getAutoBookmark(@Field("uid") int uid, @Field("bid") int bid);

    //文件上传
    @POST("/api/upload")
    @FormUrlEncoded
    Observable<BaseResponse<List<String>>> upLoadImage(@Field("cover") String String);

    //添加下载
    @POST("/api/adddownload")
    @FormUrlEncoded
    Observable<BaseResponse<JsonElement>> addMyDownLoad(@Field("uid") int uid, @Field("bid") int bid);

    //我的下载
    @POST("/api/download")
    @FormUrlEncoded
    Observable<BaseResponse<List<BookBean>>> getMyDownLoad(@Field("uid") int uid);

    //删除下载
    @POST("/api/downloaddel")
    @FormUrlEncoded
    Observable<BaseResponse<Integer>> deleteMyDownLoad(@Field("uid") int uid, @Field("bid") int bid);

    //微博登录
    @POST("/api/microblog")
//    nickname  名称
//    headimg  头像
//    phone   手机号
//    openid  用户id
    @FormUrlEncoded
    Observable<BaseResponse<UserInfo>> weiboLogin(@FieldMap Map<String, String> param);

    //购买书籍
//    uid	是	int	用户id
//    bid	是	int	书籍id
//    total	是	string	金额
//    nickname	是	string	用户名
    @POST("/api/buybook")
    @FormUrlEncoded
    Observable<BaseResponse<JsonElement>> buyBook(@Field("uid") int uid, @Field("bid") int bid, @Field("total") String total, @Field("nickname") String nickname);

    //生成订单
//    uid	是	int	用户id
//    total	是	int	订单金额
//    payway	是	string	支付方式
//    nickname	是	string	用户名
    @POST("/api/order")
    @FormUrlEncoded
    Observable<BaseResponse<String>> orderForm(@Field("uid") int uid, @Field("total") int total, @Field("payway") String payway, @Field("nickname") String nickname);


    //获取订阅状态
    @POST("/api/mysubscription")
    @FormUrlEncoded
    Observable<BaseResponse<SubscribeBean>> getSubscribeInfo(@Field("uid") int uid);


    //订阅
    @POST("/api/subscription")
    @FormUrlEncoded
    Observable<BaseResponse<JsonElement>> subscribe(@Field("uid") int uid, @Field("nickname") String nickname, @Field("total") String total);

    @GET("/api/libraryTop")
    Observable<BaseResponse<List<BookBean>>> getLibraryTop();

    @POST("/api/listTop ")
    @FormUrlEncoded
    Observable<BaseResponse<List<BookBean>>> getLibraryTopList(@Field("id") int id);

    //消费记录
    @POST("/api/expend")
    @FormUrlEncoded
    Observable<BaseResponse<List<RecordBean>>> getRecord(@Field("uid") int uid);

    //个人信息
    @POST("/api/personal")
    @FormUrlEncoded
    Observable<BaseResponse<PersonInfo>> getPersonInfo(@Field("uid") int uid);

    //卡片购买
    @POST("/api/gift")
    @FormUrlEncoded
    Observable<BaseResponse<JsonElement>> buyCard(@Field("uid") int uid, @Field("total") String total);

    //卡片列表
    @POST("/api/mygift")
//    type	是	int	类型 1自己购买 2他人赠送
    @FormUrlEncoded
    Observable<BaseResponse<List<GiftCardBean>>> getCardList(@Field("uid") int uid, @Field("type") int type);

    //勋章
    @POST("/api/mymedal")
    @FormUrlEncoded
    Observable<BaseResponse<List<String>>> getMedalList(@Field("uid") int uid);

    //登录
    @POST("/api/doLogin")
    @FormUrlEncoded
    Observable<BaseResponse<LoginBean>> login(@Field("phone") String phone, @Field("password") String password);


    @POST("/api/amend")
    @FormUrlEncoded
    Observable<BaseResponse<Object>> updateHead(@Field("uid") int uid, @Field("headimg") String headimg);

    @POST("/api/amendNickname")
    @FormUrlEncoded
    Observable<BaseResponse<Object>> updateName(@Field("uid") int uid, @Field("nickname") String nickname);

    @POST("/api/upPwd")
    @FormUrlEncoded
    Observable<BaseResponse<Object>> updatePassword(@FieldMap Map<String, String> param);

    @POST("/api/upPhone")
    @FormUrlEncoded
    Observable<BaseResponse<Object>> updatePhone(@FieldMap Map<String, String> param);

    @POST("/api/version")
    @FormUrlEncoded
    Observable<BaseResponse<Object>> checkVersion(@Field("version") String version);

    @POST("/api/card_use")
    @FormUrlEncoded
    Observable<BaseResponse<Object>> useCard(@Field("uid") int uid, @Field("cid") int cid);

}
