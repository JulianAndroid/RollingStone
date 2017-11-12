package io.julian.rollingstone.util;

import android.content.res.Resources;

/**
 * @author Zhu Liang
 */

public class SizeUtils {

    public static int dpToPx(int dpi) {
        return (int) (Resources.getSystem().getDisplayMetrics().density * dpi);
    }

    public static int spToPx(float spValue) {
        final float fontScale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
