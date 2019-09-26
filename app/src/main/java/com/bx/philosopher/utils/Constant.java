package com.bx.philosopher.utils;

import com.bx.philosopher.R;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by newbiechen on 17-4-16.
 */

public class Constant {

    public static final String TAG = "philosopher_log";

    public static final String CLIENT_URL = "https://www.jiawenjie.com";
    public static final String CLIENT_HEAD = "http";

    public static final long CONNECT_TIME = 30;

    public static final String EXPLORE_RANK_NAME = "Ranking List";

    public static final String SEARCHKEY = "search_history";


    public static final boolean SUBCRIBE = true;

    //卡片分享地址
    //uid=28&uname=zs&cid=10&money=100
    public static String CARD_SHARE = CLIENT_URL + "/hare/#/?";

    //132
    //探索图书分享
    public static String EXPLORE_SHARE = CLIENT_URL + "/hare/#/detail?id=";

    //120
    //图书馆图书分享
    public static String LIBRARY_SHARE = CLIENT_URL + "/hare/#/library?id=";


    /**
     * 验证码倒数时间
     */
    public static final int VERIFY_CODE_COUNT = 60;


    public static final String FORMAT_TIME = "HH:mm";
    //Book Date Convert Format
    public static final String FORMAT_BOOK_DATE = "yyyy-MM-dd'T'HH:mm:ss";
    //BookCachePath (因为getCachePath引用了Context，所以必须是静态变量，不能够是静态常量)
    public static String BOOK_CACHE_PATH = FileUtils.getCachePath() + File.separator
            + "book_cache" + File.separator;
    public static final int[] book_covers = {R.drawable.book_1, R.drawable.book_2, R.drawable.book_3, R.drawable.book_4,};

    public static int getCardCover(String money) {
        try {
            return getCoverMap().get(money);
        } catch (Exception e) {
            return R.drawable.card;
        }
    }

    public static int getCardCoverGray(String money) {
        try {
            return getCoverMapGray().get(money);
        } catch (Exception e) {
            return R.drawable.card;
        }
    }

    private static Map<String, Integer> getCoverMap() {
        Map<String, Integer> coverMap = new HashMap<>();
        coverMap.put("5.00", R.drawable.card5);
        coverMap.put("10.00", R.drawable.card10);
        coverMap.put("30.00", R.drawable.card30);
        coverMap.put("50.00", R.drawable.card50);
        coverMap.put("100.00", R.drawable.card100);
        coverMap.put("300.00", R.drawable.card10);
        return coverMap;
    }

    private static Map<String, Integer> getCoverMapGray() {
        Map<String, Integer> coverMap = new HashMap<>();
        coverMap.put("5.00", R.drawable.card5);
        coverMap.put("10.00", R.drawable.card10);
        coverMap.put("30.00", R.drawable.card30);
        coverMap.put("50.00", R.drawable.card50);
        coverMap.put("100.00", R.drawable.card100);
        coverMap.put("300.00", R.drawable.card10);
        return coverMap;
    }


}
