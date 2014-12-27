package com.stuffsels.muzei.deviantart.helpers;

public class UrlHelper {
    public static Boolean isImage(String url) {
        if (url == null) { return false; }
        //Documentation: Sets the artwork's image URI, which must resolve to a JPEG or PNG image, ideally under 5MB.
        return url.endsWith("jpg") || url.endsWith("jpeg") || url.endsWith("png");
    }
}
