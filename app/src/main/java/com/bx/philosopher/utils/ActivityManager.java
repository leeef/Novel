package com.bx.philosopher.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: ActivityManager
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/7/24 14:46
 */
public class ActivityManager {
    public ActivityManager() {
    }

    private static ActivityManager instance = new ActivityManager();
    private static List<Activity> activityStack = new ArrayList<>();

    public static ActivityManager getInstance() {
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new ArrayList<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 移除指定的Activity
     */
    public void removeActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity = null;
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 结束所有Activity
     */
    public void clearTop() {
        for (int i = 0, size = activityStack.size(); i < size - 1; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
    }
}
