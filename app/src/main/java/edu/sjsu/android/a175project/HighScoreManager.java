package edu.sjsu.android.a175project;

import android.content.Context;
import android.content.SharedPreferences;

public class HighScoreManager {
    private static final String PREFS_NAME = "stats_prefs";
    private static final String KEY_HIGH_SCORE = "high_score";

    private HighScoreManager() {}

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static int getHighScore(Context context) {
        return prefs(context).getInt(KEY_HIGH_SCORE, 0);
    }

    public static void setHighScore(Context context, int score) {
        prefs(context).edit().putInt(KEY_HIGH_SCORE, Math.max(0, score)).apply();
    }
}
