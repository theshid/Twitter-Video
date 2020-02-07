package com.shid.twittervideo.util;

import android.webkit.URLUtil;

public class LinkChecker {

    public static boolean isValidLink(String link){
        boolean isValid = URLUtil.isValidUrl( link ) && link.contains("twitter.com/");
        return isValid;
    }
}
