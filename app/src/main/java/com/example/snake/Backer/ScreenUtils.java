package com.example.snake.Backer;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by 陈明 on 2017/7/23.
 */

public class ScreenUtils {
    private static int screenWidth;
    private static int screenHeight;

    public static void init(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    public static int getScreenWidth(Context context) {
        if (screenWidth <= 0) {
            init(context);
        }
        return screenWidth;
    }

    public static int getScreenHeight(Context context) {
        if (screenHeight <= 0) {
            init(context);
        }
        return screenHeight;
    }
}
