package com.forasterisk.ilkeok.utils;

import android.content.Context;

/**
 * Created by yearnning on 15. 10. 18..
 */
public class IlkeokResultManager {

    /**
     * @return episode number return;
     */
    public static int getEpisodeNumber(String current_url) {
        int number = -1;

        try {
            if (current_url == null) {
                return -1;
            }

            if (current_url.contains("no=")) {
                String number_str = current_url.substring(current_url.indexOf("no=") + 3);

                if (number_str.contains("&")) {
                    number_str = number_str.substring(0, number_str.indexOf("&"));
                }

                number = Integer.parseInt(number_str);
            }
        } catch (Exception e) {
            e.printStackTrace();
            number = -1;
        }

        return number;
    }

    private static String getPreferenceKey(int episode_number) {
        if (episode_number < 0) {
            return null;
        }
        return Argument.PREFS_EPISODE_NAVER_LUCKY + episode_number;
    }

    /**
     * @param url
     * @param star
     */
    public static void setResult(Context context, String url, int star) {
        int episode_number = getEpisodeNumber(url);
        if (episode_number < 0) {
            return;
        }

        String key = getPreferenceKey(episode_number);
        PreferenceManager.put(context, key, star);
    }

    public static boolean isTen(String url) {
        int number = IlkeokResultManager.getEpisodeNumber(url);
        for (int n : Argument.ten_nos) {
            if (n == number) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param context
     * @param url
     * @return
     */
    public static int getResult(Context context, String url) {
        int episode_number = getEpisodeNumber(url);
        if (episode_number < 0) {
            return -1; // ERROR
        }

        String key = getPreferenceKey(episode_number);
        return PreferenceManager.get(context, key, 0);
    }
}
