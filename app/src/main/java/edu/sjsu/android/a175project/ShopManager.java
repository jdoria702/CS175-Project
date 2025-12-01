package edu.sjsu.android.a175project;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class ShopManager {

    private static final String PREFS_NAME = "shop_prefs";
    private static final String KEY_COINS = "coins";
    private static final String KEY_OWNED = "owned_characters";
    private static final String KEY_SELECTED = "selected_character";

    private static final String DEFAULT_CHARACTER = "Blue";
    private static final int DEFAULT_COINS = 300;

    private ShopManager() {
        // no instance
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static int getCoins(Context context) {
        return prefs(context).getInt(KEY_COINS, DEFAULT_COINS);
    }

    public static void setCoins(Context context, int coins) {
        prefs(context).edit().putInt(KEY_COINS, Math.max(0, coins)).apply();
    }

    public static void addCoins(Context context, int delta) {
        setCoins(context, getCoins(context) + delta);
    }

    public static Set<String> getOwnedCharacters(Context context) {
        Set<String> owned = new HashSet<>(prefs(context).getStringSet(KEY_OWNED, new HashSet<>()));
        owned.add(DEFAULT_CHARACTER);
        return owned;
    }

    public static boolean isOwned(Context context, String character) {
        return getOwnedCharacters(context).contains(character);
    }

    public static void unlockCharacter(Context context, String character) {
        Set<String> owned = getOwnedCharacters(context);
        owned.add(character);
        prefs(context).edit().putStringSet(KEY_OWNED, owned).apply();
        if (getSelectedCharacter(context) == null) {
            setSelectedCharacter(context, character);
        }
    }

    public static boolean purchaseCharacter(Context context, String character, int price) {
        if (isOwned(context, character)) {
            return true;
        }
        int coins = getCoins(context);
        if (coins < price) {
            return false;
        }
        setCoins(context, coins - price);
        unlockCharacter(context, character);
        return true;
    }

    public static String getSelectedCharacter(Context context) {
        String selected = prefs(context).getString(KEY_SELECTED, null);
        if (selected == null) {
            selected = DEFAULT_CHARACTER;
            setSelectedCharacter(context, selected);
        }
        return selected;
    }

    public static void setSelectedCharacter(Context context, String character) {
        prefs(context).edit().putString(KEY_SELECTED, character).apply();
    }

    // Map character names to drawable resources for UI (lives, minigame, etc.)
    public static int getCharacterDrawable(String characterName) {
        if (characterName == null) return R.drawable.char_blue;
        switch (characterName) {
            case "Johnny":
                return R.drawable.char_johnny;
            case "Leafy":
                return R.drawable.char_leafy;
            case "Shadow":
                return R.drawable.char_shadow;
            case "Blue":
            default:
                return R.drawable.char_blue;
        }
    }
}
