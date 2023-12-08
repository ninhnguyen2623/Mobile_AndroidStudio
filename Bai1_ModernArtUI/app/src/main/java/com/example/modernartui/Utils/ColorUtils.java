package com.example.modernartui.Utils;

import android.graphics.Color;
import android.view.View;


public class ColorUtils {
    public static void updateColorRectangles(int progress, View... rectangles) {
        String[][] colorTables = {
                {"#FFFF00", "#FFD700", "#FFC125", "#FFB90F"},
                {"#F0FFF0", "#ADFF2F", "#7FFF00", "#32CD32", "#00FF00"},
                {"#B0E0E6", "#ADD8E6", "#87CEEB", "#1E90FF", "#0000FF"},
                {"#FFA07A", "#FF6347", "#FF4500", "#FF0000", "#DC143C"},
                {"#0D301D", "#17472F", "#1E5E42", "#277753", "#2F8A66"}
        };

        if (progress > 10) {
            int colorIndex = (int) (progress / 100.0 * (colorTables[0].length - 1));

            for (int i = 0; i < rectangles.length; i++) {
                int color = Color.parseColor(colorTables[i][colorIndex]);

                int newColor = Color.rgb(Color.red(color), Color.green(color), Color.blue(color));

                rectangles[i].setBackgroundColor(newColor);
            }
        } else {
            String[] colorDeflt = {
                    "#FFFF00",
                    "#0D301D",
                    "#00FF00",
                    "#0000FF",
                    "#FF0000"
            };
            for (int i = 0; i < rectangles.length; i++) {
                int color = Color.parseColor(colorDeflt[i]);

                rectangles[i].setBackgroundColor(color);
            }
        }
    }
}
